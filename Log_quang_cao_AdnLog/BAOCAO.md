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
- Lưu trữ dữ liệu trên Click House với cấu trúc:
- Database: adnlog_db
- Bảng campaign_envents: lưu trữ thông tin campaignId, hoạt động (click/view) của guid tương ứng và thời gian xảy ra hoạt động đó
- Bảng banner_envents: lưu trữ thông tin bannerId, hoạt động (click/view) của guid tương tứng và thời gian xảy ra hoạt động đó
```sql
# Tạo database và các bảng trên click house 
CREATE DATABASE IF NOT EXISTS adnlog_db;

USE adnlog_db;

CREATE TABLE IF NOT EXISTS adnlog_db.campaign_events
(
    campaignId UInt64,          
    click_or_view Boolean,
    event_date Date,
    guid UInt64
)
ENGINE = MergeTree()
ORDER BY (event_date, campaignId);

CREATE TABLE IF NOT EXISTS adnlog_db.banner_events
(
    bannerId UInt64,          
    click_or_view Boolean,
    event_date Date,
    guid UInt64
)
ENGINE = MergeTree()
ORDER BY (event_date, bannerId);
```

# Tạo API và truy vấn
## Test câu truy vấn 
- Để đếm số guid trong khoảng thời gian từ ngày A đến ngày B mà để tránh trình trạng cùng một guid nhưng lại trùng lặp nhiều lần ta có 2 phương án:
1. Thực hiện lọc dữ liệu trong khoảng ngày đã cho và các điều kiện campaignId/bannerId, click/view sau đó thực hiện đếm distince(guid) để có được số guid không trùng lặp
3. Thực hiện áp dụng thuật toán HLL để đếm số guid gần đúng
### Phương pháp lọc distince
#### Truy vấn theo bannerId 
```sql
SELECT  bannerId,
        click_or_view,
        count(DISTINCT guid) AS user_count
FROM banner_events
WHERE event_date BETWEEN '{start_date}' AND '{end_date}'
              AND click_or_view = {click_or_view}
              AND bannerId = {banner_id}
GROUP BY bannerId, click_or_view
```
Trong đó: các giá trị trong ngoặc là giá trị đầu vào

Ưu điểm: Đếm chính xác số lượng guid hoạt động click/view trên từng campaignId 

Nhược điểm: tốc độ tính toán lâu khi xử lý với khối lượng dữ liệu lớn, thời gian truy vấn lâu có thể bị treo máy 
#### Truy vấn theo campaignId
- Tương tự bannerId chỉ cần thay bảng và trường campaignId 
### Phương pháp dùng thuật toán HLL 
#### Nguyên lý của thuật toán HyperLogLog
**1. Sử dụng hàm băm với mỗi phần từ chuyển thành chuỗi nhị phân**
   
Mỗi phần tử ta sẽ sử dụng một hàm băm, gán cho phần tử một giá trị giả lập và chuyển về chuỗi nhị phân.

VD: 'A' gán giá trị là 18 và chuyển thành chuỗi nhị phân là ```00011000``` nếu ta dùng hàm băm 8bit.

**2. Đếm số lượng bit 0 liên tiếp từ trái qua phải (leading zeros)**

Sau khi có chuỗi giá trị nhị phân của từng phân tử, ta thực hiện đếm số lượng bit 0 ở đầu gọi là leading zeros

VD: ```00011000``` sẽ có leading zeros là 3 

**3. Chia nhỏ thành nhiều bucket**

Ta lấy p bit đầu tiên của hash để xác định bucket số m, khi đó:

<img width="326" height="60" alt="image" src="https://github.com/user-attachments/assets/51b48db1-4129-4ab4-8a92-6281cd2746f1" />

VD: ta lấy số bit p = 2 thì số bucket m = 4 (00, 01, 10, 11) 

```00011000``` có 2 ký tự đầu là 00 (bucket 0)

Bỏ 2 ký tự đầu đi ta còn lại ```011000``` khi đó giá trị mới sẽ có leading zeros là 1.

Tương tự với các phần tử khác t sẽ được tập giá trị leading zeros của mỗi bucket, sau đó ta sẽ lấy giá trị lớn nhất (max) của mỗi bucket để tính toán. 

Bucket rỗng thì giá trị tính toán sẽ là 0. 

**4. Dùng công thức tính trung bình với hệ số hiệu chỉnh (phụ thuộc vào số bucket)**

<img width="410" height="138" alt="image" src="https://github.com/user-attachments/assets/b5563507-8e85-4e89-8bd3-45d02abbb3e3" />

Trong đó hệ số alpha sẽ có công thức tính riêng nhưng ta thường dùng một vài giá trị có sẵn để tính toán. Nếu số lượng bucket quá lớn thì sẽ dùng công thức để tính xấp xỉ.

<img width="250" height="89" alt="image" src="https://github.com/user-attachments/assets/1004f598-3dfe-48d7-8a76-0e9e3ee19b0b" />

#### Truy vấn theo bannerId
```sql 
SELECT bannerId,
        click_or_view,
        uniqHLL12(guid) AS estimated_user_count
FROM banner_events
WHERE event_date BETWEEN '{start_date}' AND '{end_date}'
      AND click_or_view = {click_or_view}
      AND bannerId = {banner_id}
GROUP BY bannerId, click_or_view
```
Trong đó: các giá trị trong ngoặc là giá trị đầu vào
Ưu điểm: Tăng tốc độ truy vấn và tính toán kể cả trên khối lượng dữ liệu lớn 
Nhược điểm: số lượng đếm guid có thể thể chỉ là gần đúng mà k phải là chính xác 
#### Truy vấn theo campaignId 
- Tương tự bannerId chỉ cần thay bảng và trường campaignId

## Viết API 
- Chi tiết source code ở thư mục project
- Kết quả truy vấn



