## Khởi chạy Spark-shell
```
/data/spark-3.4.3/bin/spark-shell --master yarn --deploy-mode client --num-executors 3 --executor-memory 1G
```
Trong đó
- -- master yarn: chạy bằng yarn
- --deploy-mode client: chạy mode client
- --num-executor 3: Số lượng excutor là 3
- --executor-memory 1G: cấp dung lượng cho bộ nhớ là 1G 

## 1. Đọc dữ liệu từ PageViewApp 
### Lấy dữ liệu từ hdfs về spark-shell 
```
val df = spark.read.parquet("hdfs://adt-platform-dev-106-254:8120/data/Parquet/PageViewApp/*")
```
### Thực hiện import thư viện spark.sql
```
import org.apache.spark.sql.functions._
```

### Thực hiện tính toán 
```
val appUserCountDF = df.select("appId", "userId")
                      .filter(col("appId").isNotNull && col("userId").isNotNull)
                      .groupBy("appId")
                      .agg(countDistinct("userId")
                      .alias("userCount"))
```
Trong đó
- .select("appId", "userId"): Lấy 2 cột *appId* và *userId* từ dataframe df đọc từ hdfs 
- .filter(col("appId").isNotNull && col("userId").isNotNull): loại những bản ghi có trường - appId và userId là null
- .groupBy("appId"): nhóm các bản ghi theo appId
- .agg(countDistinct("userId"): với mỗi nhóm appId ta thực hiện phép tổng hợp đếm số userId khác nhau (chỉ xuất hiện 1 lần) 
- .alias("userCount")): đặt tên cho cột tổng hợp là * userCount * 

### Lấy top 10 appId có lượt truy cập nhiều nhất 
```
val top10AppIds = appUserCountDF.orderBy(desc("userCount")).limit(10)
top10AppIds.show(truncate = false)
```
- Kết quả:
<img width="444" height="332" alt="image" src="https://github.com/user-attachments/assets/79f79cce-51fe-4ab7-9225-89801cccf640" />

## 2. Đọc dữ liệu từ PageViewApp 
### Lấy dữ liệu từ hdfs về spark-shell 
```
val df = spark.read.parquet("hdfs://adt-platform-dev-106-254:8120/data/Parquet/PageViewApp/*")
```
### Thực hiện import thư viện spark.sql
```
import org.apache.spark.sql.functions._
```

### Thực hiện tính toán 
```
val appUserCountDF = df.select("appId", "userId")
                      .filter(col("appId").isNotNull && col("userId").isNotNull)
                      .groupBy("appId")
                      .agg(countDistinct("userId")
                      .alias("userCount"))
```
Trong đó
- .select("appId", "userId"): Lấy 2 cột *appId* và *userId* từ dataframe df đọc từ hdfs 
- .filter(col("appId").isNotNull && col("userId").isNotNull): loại những bản ghi có trường - appId và userId là null
- .groupBy("appId"): nhóm các bản ghi theo appId
- .agg(countDistinct("userId"): với mỗi nhóm appId ta thực hiện phép tổng hợp đếm số userId khác nhau (chỉ xuất hiện 1 lần) 
- .alias("userCount")): đặt tên cho cột tổng hợp là * userCount * 

### Lấy top 10 appId có lượt truy cập nhiều nhất 
```
val top10AppIds = appUserCountDF.orderBy(desc("userCount")).limit(10)
top10AppIds.show(truncate = false)
```
- Kết quả:
<img width="444" height="332" alt="image" src="https://github.com/user-attachments/assets/79f79cce-51fe-4ab7-9225-89801cccf640" />

