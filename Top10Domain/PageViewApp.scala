{
import org.apache.spark.sql.{SparkSession, DataFrame}
import org.apache.spark.sql.functions._
import org.apache.hadoop.fs.{FileSystem, Path}

val spark = SparkSession.builder().getOrCreate()
import spark.implicits._

val baseDir = "hdfs://adt-platform-dev-106-254:8120/data/Parquet/PageViewApp/"
val fs = FileSystem.get(spark.sparkContext.hadoopConfiguration)

// Lấy danh sách thư mục ngày
val dayDirs = fs.listStatus(new Path(baseDir))
  .map(_.getPath)
  .filter(p => p.getName.matches("\\d{4}_\\d{2}_\\d{2}"))
  .sortBy(_.getName)

var runningTotalDF: DataFrame = spark.emptyDataFrame

for (dayPath <- dayDirs) {
  val day = dayPath.getName
  println(s"\n🟢 Đang xử lý ngày: $day")

  val parquetFiles = fs.listStatus(dayPath)
    .map(_.getPath.toString)
    .filter(_.endsWith(".parquet"))

  if (parquetFiles.isEmpty) {
    println(s"⚠️ Không có file parquet trong $day")
  } else {
    val batchSize = 1
    val fileGroups = parquetFiles.grouped(batchSize).toList

    var dayCounts = scala.collection.mutable.Map[String, Long]()

    for ((group, idx) <- fileGroups.zipWithIndex) {
      println(s"   📦 Nhóm ${idx + 1}/${fileGroups.size}")
      val df = spark.read.parquet(group: _*)
        .select("appId")
        .groupBy("appId")
        .agg(count("*").as("count"))
        .collect()

      df.foreach { row =>
        val appId = row.getString(0)
        val count = row.getLong(1)
        dayCounts(appId) = dayCounts.getOrElse(appId, 0L) + count
      }
    }

    // Tạo DataFrame cho ngày đó từ Map
    val reducedDayDF = dayCounts.toSeq.toDF("appId", "count")

    println(s"📊 Kết quả cho ngày $day:")
    reducedDayDF.orderBy(desc("count")).show(truncate = false)

    // Cộng dồn với tổng
    if (runningTotalDF.isEmpty) {
      runningTotalDF = reducedDayDF
    } else {
      // convert về Map để cộng dễ hơn
      val runningMap = runningTotalDF.collect().map(r => r.getString(0) -> r.getLong(1)).toMap
      val newDayMap = reducedDayDF.collect().map(r => r.getString(0) -> r.getLong(1)).toMap

      val merged = (runningMap.keySet ++ newDayMap.keySet).map { appId =>
        appId -> (runningMap.getOrElse(appId, 0L) + newDayMap.getOrElse(appId, 0L))
      }.toSeq

      runningTotalDF = merged.toDF("appId", "count")
    }
  }
}

// Kết quả cuối cùng
if (!runningTotalDF.isEmpty) {
  println("\n🏁 Kết quả tổng hợp toàn bộ:")
  runningTotalDF.orderBy(desc("count")).show(10, truncate = false)
} else {
  println("🚫 Không có dữ liệu được xử lý.")
}
}
