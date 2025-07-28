## 1. Đọc dữ liệu từ PageViewApp 
- Đọc dữ liệu Parquet từ thư mục `hdfs://adt-platform-dev-106-254:8120/data/Parquet/PageViewApp/` theo từng ngày (dựa trên tên thư mục kiểu `YYYY_MM_DD`).
- Tính số lần xuất hiện (`count`) của mỗi `appId` trong mỗi ngày.
- Cộng dồn kết quả qua các ngày để tạo tổng hợp cuối cùng.
- Hiển thị kết quả từng ngày và tổng hợp cuối cùng theo thứ tự giảm dần của `count`.

## a) Cấu trúc Code
### a.1. Khởi tạo Spark Session
```scala
val spark = SparkSession.builder().getOrCreate()
import spark.implicits._
```
- Tạo hoặc lấy phiên bản Spark Session hiện có để xử lý dữ liệu.
- Import các hàm implict để hỗ trợ thao tác DataFrame.

### a.2. Thiết lập đường dẫn và FileSystem
```scala
val baseDir = "hdfs://adt-platform-dev-106-254:8120/data/Parquet/PageViewApp/"
val fs = FileSystem.get(spark.sparkContext.hadoopConfiguration)
```
- Định nghĩa đường dẫn cơ sở (`baseDir`) đến thư mục HDFS chứa dữ liệu Parquet.
- Lấy đối tượng `FileSystem` để liệt kê các file và thư mục.

### a.3. Lấy danh sách thư mục ngày
```scala
val dayDirs = fs.listStatus(new Path(baseDir))
  .map(_.getPath)
  .filter(p => p.getName.matches("\\d{4}_\\d{2}_\\d{2}"))
  .sortBy(_.getName)
```
- Lấy danh sách các thư mục con trong `baseDir`.
- Lọc các thư mục có tên khớp với định dạng ngày (`YYYY_MM_DD`).
- Sắp xếp theo thứ tự tăng dần của tên ngày.

### a.4. Xử lý từng ngày
```scala
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

    val reducedDayDF = dayCounts.toSeq.toDF("appId", "count")

    println(s"📊 Kết quả cho ngày $day:")
    reducedDayDF.orderBy(desc("count")).show(truncate = false)

    if (runningTotalDF.isEmpty) {
      runningTotalDF = reducedDayDF
    } else {
      val runningMap = runningTotalDF.collect().map(r => r.getString(0) -> r.getLong(1)).toMap
      val newDayMap = reducedDayDF.collect().map(r => r.getString(0) -> r.getLong(1)).toMap

      val merged = (runningMap.keySet ++ newDayMap.keySet).map { appId =>
        appId -> (runningMap.getOrElse(appId, 0L) + newDayMap.getOrElse(appId, 0L))
      }.toSeq

      runningTotalDF = merged.toDF("appId", "count")
    }
  }
}
```
- **Vòng lặp qua các ngày**: Duyệt qua từng thư mục ngày.
- **Lấy file Parquet**: Liệt kê các file `.parquet` trong thư mục ngày.
- **Xử lý theo batch**: Chia file thành các nhóm (batch size = 1), xử lý từng nhóm để tránh tải toàn bộ dữ liệu cùng lúc.
- **Tính count**: Đọc file, chọn cột `appId`, nhóm và tính tổng (`count`), lưu vào `dayCounts` (Map mutable).
- **Tạo DataFrame ngày**: Chuyển `dayCounts` thành DataFrame (`reducedDayDF`) và hiển thị.
- **Cộng dồn**: Nếu `runningTotalDF` rỗng, gán `reducedDayDF`; nếu không, hợp nhất bằng cách cộng các giá trị `count` của `appId` giống nhau.

### a.5. Hiển thị kết quả cuối cùng
```scala
if (!runningTotalDF.isEmpty) {
  println("\n🏁 Kết quả tổng hợp toàn bộ:")
  runningTotalDF.orderBy(desc("count")).show(100, truncate = false)
} else {
  println("🚫 Không có dữ liệu được xử lý.")
}
```
- Hiển thị DataFrame tổng hợp (`runningTotalDF`) theo thứ tự giảm dần của `count`, tối đa 100 dòng.
- Nếu không có dữ liệu, in thông báo.

<img width="467" height="372" alt="image" src="https://github.com/user-attachments/assets/1578ca22-7c05-438b-a27e-873ba6c23a8e" />


## 2. Đọc dữ liệu từ PageViewMobile 
- Đọc dữ liệu Parquet từ thư mục `hdfs://adt-platform-dev-106-254:8120/data/Parquet/PageViewMobile/` theo từng ngày (dựa trên tên thư mục kiểu `YYYY_MM_DD`).
- Tính số lần xuất hiện (`count`) của mỗi `domain` trong mỗi ngày.
- Cộng dồn kết quả qua các ngày để tạo tổng hợp cuối cùng.
- Hiển thị kết quả từng ngày và tổng hợp cuối cùng theo thứ tự giảm dần của `count`.

- Thực hiện tương tự đọc App chỉ cần thay đường dẫn thư mục
  <img width="361" height="377" alt="image" src="https://github.com/user-attachments/assets/0b9ab348-f866-4115-b8c5-7ce1b0f63abd" />


## 3. Đọc dữ liệu từ PageViewV1 (PC)
- Đọc dữ liệu Parquet từ thư mục `hdfs://adt-platform-dev-106-254:8120/data/Parquet/PageViewV1/` theo từng ngày (dựa trên tên thư mục kiểu `YYYY_MM_DD`).
- Tính số lần xuất hiện (`count`) của mỗi `domain` trong mỗi ngày.
- Cộng dồn kết quả qua các ngày để tạo tổng hợp cuối cùng.
- Hiển thị kết quả từng ngày và tổng hợp cuối cùng theo thứ tự giảm dần của `count`.

- Thực hiện tươgn tự đọc App và Mobile chỉ cần thay đường dẫn thư mục
<img width="379" height="375" alt="image" src="https://github.com/user-attachments/assets/e697332c-9052-412e-8c9e-13ff9ae0c4e0" />

