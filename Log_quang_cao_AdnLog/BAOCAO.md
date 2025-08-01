# Xử lý dữ liệu 
## Khởi chạy spark-shell
```
/data/spark-3.4.3/bin/spark-shell --master yarn --deploy-mode client --num-executors 3 --executor-memory 1G
```
## Lấy dữ liệu từ hdfs 
```
val df = spark.read.parquet("hdfs://adt-platform-dev-106-254:8120/data/Parquet/AdnLog/*")
```
## Xử lý dữ liệu 
```
val dfProcessed = df.withColumn("event_date",from_unixtime(col("time_group.time_create") / 1000, "yyyy-MM-dd")).select("campaignId", "click_or_view", "event_date", "guid").na.drop(Seq("campaignId", "click_or_view", "event_date", "guid")).dropDuplicates(Seq("campaignId", "click_or_view", "event_date", "guid"))
```

## Lưu trữ dữ liệu đã xử lý vào hdfs
```
dfProcessed.write.mode("overwrite").option("header", "true").csv("hdfs://adt-platform-dev-106-254:8120/user/phuongtl/CampaignId_Guid")
```
- Sau đó kết thúc spark-shell

## Kiểm tra dữ liệu trên hdfs và tải dữ liệu về local 
- Kiểm tra dữ liệu
```
hdfs dfs -ls hdfs://adt-platform-dev-106-254:8120/user/phuongtl/CampaignId_Guid
```
- Tải dữ liệu về local
```
hdfs dfs -get hdfs://adt-platform-dev-106-254:8120/user/phuongtl/CampaignId_Guid
```
# Lưu trữ dữ liệu 

# Tạo API và truy vấn

