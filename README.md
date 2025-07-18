# Bao_cao_thuc_tap
# TUẦN 1: Nền tảng cơ bản

## Python

### Kiểu dữ liệu cơ bản

| Kiểu dữ liệu | Mô tả                  | Ví dụ                       |
|--------------|------------------------|-----------------------------|
| `int`        | Số nguyên              | `x = 10`                    |
| `float`      | Số thực                | `pi = 3.14`                 |
| `str`        | Chuỗi                  | `s = "Python"`              |
| `bool`       | Boolean (True/False)   | `flag = True`              |
| `list`       | Danh sách              | `a = [1, 2, 3]`             |
| `tuple`      | Bộ giá trị             | `t = (1, 2)`                |
| `set`        | Tập hợp không trùng    | `s = {1, 2, 3}`             |
| `dict`       | Từ điển (key-value)    | `d = {"a": 1}`              |

---

### Toán tử

- **Toán tử toán học**: `+ - * / // % **`
- **Toán tử so sánh**: `== != >= <= > <`
- **Toán tử logic**: `and or not`

---

### Cấu trúc điều khiển

#### Câu lệnh điều
#### Vòng lặp 

### Hàm
<img width="560" height="151" alt="image" src="https://github.com/user-attachments/assets/d04ee943-1dca-4186-8fc2-66e7a23de12f" />

### List - Tuple - Set - Dict 
| Đặc điểm                  | `list`                        | `tuple`                       | `set`                          | `dict`                              |
|---------------------------|-------------------------------|-------------------------------|---------------------------------|--------------------------------------|
| Ký hiệu khởi tạo          | `[ ]`                         | `( )`                         | `{ }`                           | `{key: value}`                       |
| Có thứ tự (ordered)       | có                            | có                            | có                              | có                                   |
| Thay đổi được (mutable)   | có                             | không                             | có                               | có                                    |
| Cho phép trùng lặp        | có                             | có                             | không                               | Key không / Value có                     |
| Truy cập qua chỉ số       | có                             | có                             | không                               | có (qua key)                         |
| Dùng khi nào              | Danh sách dữ liệu có thứ tự, có thể thay đổi | Dữ liệu cố định, không thay đổi | Tập hợp các giá trị duy nhất    | Ánh xạ giữa key và value            |
| Ví dụ                     | `[1, 2, 3]`                   | `(1, 2, 3)`                   | `{1, 2, 3}`                     | `{"name": "Alice", "age": 25}`      |

### Đọc ghi file
#### Đọc/ ghi file
<img width="614" height="169" alt="image" src="https://github.com/user-attachments/assets/1be2c5a8-a077-4827-ab7c-9fc1cee10bbb" />

#### Đọc/ ghi file bằng pandas 
<img width="611" height="170" alt="image" src="https://github.com/user-attachments/assets/825ae92d-e574-4adc-a59a-4fbabed05c8b" />


# SQL cơ bản 

## Các nhóm lệnh chính trong SQL

### Ngôn ngữ định nghĩa dữ liệu (DDL)

| Lệnh     | Mô tả                                       |
|----------|----------------------------------------------|
| CREATE   | Tạo bảng hoặc cơ sở dữ liệu                  |
| ALTER    | Sửa đổi cấu trúc bảng                        |
| DROP     | Xóa bảng hoặc cơ sở dữ liệu                  |
| TRUNCATE | Xóa toàn bộ dữ liệu trong bảng (nhanh hơn DELETE) |

### Ngôn ngữ thao tác dữ liệu (DML)

| Lệnh   | Mô tả           |
|--------|------------------|
| SELECT | Truy vấn dữ liệu |
| INSERT | Thêm dữ liệu mới |
| UPDATE | Cập nhật dữ liệu |
| DELETE | Xóa dữ liệu      |

## Cấu trúc bảng trong cơ sở dữ liệu

- **Bảng (Table)**: Tập hợp các hàng và cột.
- **Hàng (Row/Record)**: Một bản ghi dữ liệu.
- **Cột (Column/Field)**: Kiểu dữ liệu cụ thể như `VARCHAR`, `INT`, `DATE`, `BOOLEAN`.

## GROUP BY – Nhóm dữ liệu

```sql
-- Đếm số nhân viên theo phòng ban
SELECT phong_ban, COUNT(*) AS so_nhan_vien
FROM nhan_vien
GROUP BY phong_ban;

-- Lương trung bình theo phòng ban
SELECT phong_ban, AVG(luong) AS luong_tb
FROM nhan_vien
GROUP BY phong_ban;
```

- Hàm tổng hợp: `COUNT()`, `SUM()`, `AVG()`, `MAX()`, `MIN()`

## HAVING – Lọc sau khi nhóm

```sql
-- Phòng ban có > 5 nhân viên
SELECT phong_ban, COUNT(*) AS so_nv
FROM nhan_vien
GROUP BY phong_ban
HAVING COUNT(*) > 5;
```

## ORDER BY – Sắp xếp

```sql
-- Sắp xếp tăng dần
SELECT * FROM nhan_vien ORDER BY luong ASC;

-- Sắp xếp giảm dần
SELECT * FROM nhan_vien ORDER BY luong DESC;

-- Sắp xếp nhiều cột
SELECT * FROM nhan_vien 
ORDER BY phong_ban ASC, luong DESC;
```

## JOIN – Kết hợp nhiều bảng

- `INNER JOIN`: Lấy giao nhau của 2 bảng
- `LEFT JOIN`: Lấy đầy đủ bảng trái và dữ liệu giao nhau
- `RIGHT JOIN`: Ngược lại với LEFT JOIN
- `FULL OUTER JOIN`: Lấy tất cả dữ liệu từ cả hai bảng

---

# Linux Command Line

## Quản lý file và thư mục

| Câu lệnh         | Mô tả                                |
|------------------|----------------------------------------|
| pwd              | Hiển thị thư mục hiện tại             |
| ls               | Liệt kê nội dung thư mục              |
| ls -l            | Hiển thị chi tiết dạng danh sách      |
| cd <dir>         | Di chuyển tới thư mục                  |
| cd ..            | Quay về thư mục cha                    |
| mkdir <dir>      | Tạo thư mục mới                        |
| touch <file>     | Tạo file rỗng                          |
| cp <src> <dest>  | Sao chép file/thư mục                  |
| mv <src> <dest>  | Di chuyển hoặc đổi tên                 |
| rm <file>        | Xóa file                               |
| rm -r <dir>      | Xóa thư mục và toàn bộ nội dung bên trong |

## Xem và chỉnh sửa nội dung

| Câu lệnh      | Mô tả                                |
|---------------|----------------------------------------|
| cat <file>    | Hiển thị nội dung file                |
| less <file>   | Xem nội dung file theo trang          |
| head <file>   | Hiển thị dòng đầu tiên trong file     |
| tail <file>   | Hiển thị dòng cuối của file           |
| nano <file>   | Mở trình chỉnh sửa văn bản nano       |

## Quyền truy cập và người dùng

| Câu lệnh                       | Mô tả                               |
|--------------------------------|--------------------------------------|
| chmod <mode> <file>            | Thay đổi quyền truy cập            |
| chown <user>:<group> <file>    | Thay đổi chủ sở hữu file           |
| whoami                         | Hiển thị người dùng hiện tại       |
| sudo <command>                 | Thực thi với quyền admin           |
| su                             | Chuyển sang người dùng khác        |

## Nén và giải nén

| Câu lệnh                     | Mô tả                         |
|------------------------------|-------------------------------|
| tar -cvf archive.tar folder  | Nén folder thành file tar     |
| tar xvf archive.tar          | Giải nén file tar             |


# TUẦN 2: Kiến trúc dữ liệu

## OLTP vs OLAP

**OLTP (Online Transaction Processing)** là hệ thống phục vụ cho các giao dịch trực tuyến, được thiết kế để quản lý và thực hiện các giao dịch kinh doanh hằng ngày của một tổ chức.  
**OLAP (Online Analytical Processing)** là hệ thống chuyên dùng cho các công việc phân tích trực tuyến, được tạo ra để hỗ trợ việc phân tích chiều sâu và đa chiều của dữ liệu. OLAP tập trung vào việc tổ chức dữ liệu để tối ưu hóa quá trình truy vấn và phân tích từ nhiều góc độ khác nhau. 

### So sánh OLTP vs OLAP

| Tiêu chí | OLTP | OLAP |
|---------|------|------|
| Mục đích | Hướng tới xử lý các lượng lớn dữ liệu giao dịch trong thời gian thực, hỗ trợ các quá trình ra quyết định | Chủ yếu để phân tích dữ liệu chi tiết qua nhiều chiều để hỗ trợ quyết định và giải quyết vấn đề |
| Nguồn dữ liệu | Lưu trữ dữ liệu giao dịch trong cơ sở dữ liệu quan hệ được tối ưu hóa | Sử dụng mô hình dữ liệu đa chiều từ nhiều bộ dữ liệu khác nhau |
| Cấu trúc dữ liệu | Cơ sở dữ liệu quan hệ | Cơ sở dữ liệu đa chiều hoặc quan hệ |
| Mô hình dữ liệu | Mô hình chuẩn hóa hoặc phi chuẩn hóa | Lược đồ hình sao, bông tuyết, mô hình phân tích |
| Cập nhật và sao lưu | Cập nhật thời gian thực, sao lưu thường xuyên | Cập nhật hàng giờ - hàng ngày, sao lưu ít hơn |
| Tốc độ | Phản hồi trong mili giây | Phản hồi từ giây đến giờ |
| Dung lượng lưu trữ | Tương đối, chủ yếu lưu trữ dữ liệu giao dịch | Lớn, do tổng hợp dữ liệu phục vụ phân tích |
| Ứng dụng | Xử lý thanh toán, quản lý đơn hàng, khách hàng | Phân tích xu hướng, dự đoán hành vi |
| DBMS phù hợp | MySQL, SQL Server, Oracle, PostgreSQL | BigQuery, Snowflake (RDBMS ít phù hợp) |

---

## ETL vs ELT

- **ETL** (Extract - Transform - Load):ETL là tên viết tắt của Extract, Transform và Load. Trong quá trình này, một công cụ ETL trích xuất dữ liệu từ các hệ thống nguồn RDBMS khác nhau sauddos chuyển đổi dữ liệu như áp dụng các phép biến đổi dữ liệu (tính toán, nối chuỗi,v.v…) và sau đó tải dữ liệu vào hệ thống Data Warehouse, ETL là những luồng từ “nguồn” tới “đích”.
- **ELT** (Extract - Load - Transform): ELT là một phương pháp khác để tiếp cận công cụ chuyển động dữ liệu. Thay vì chuyển đổi dữ liệu trước khi viết, ELT cho phép “hệ thống đích” chuyển đổi trước. Dữ liệu đầu tiên được sao chép vào “đích” sau đó được chuyển đổi tại đó. 

### So sánh ETL vs ELT

| Tiêu chí | ETL | ELT |
|---------|-----|-----|
| Thứ tự xử lý | Extract -> Transform -> Load | Extract -> Load -> Transform |
| Nơi xử lý Transform | Ngoài Data Warehouse | Trong Data Warehouse |
| Công nghệ phổ biến | Talend, Apache Nifi | BigQuery, Snowflake |
| Hiệu suất xử lý dữ liệu lớn | Giới hạn | Tốt hơn |
| Phù hợp với | Hệ thống truyền thống, dữ liệu nhỏ | Cloud-based, dữ liệu lớn |
| Linh hoạt & mở rộng | Hạn chế | Rất tốt |
| Thời gian thực | Kém | Tốt hơn khi kết hợp streaming |
| Bảo mật dữ liệu thô | Không lưu lâu | Có thể lưu trữ để truy vết |

---

## CAP Theorem

**CAP** là viết tắt của:

- **C - Consistency (Tính nhất quán):** Tất cả các node trong hệ thống đều có cùng một dữ liệu tại một thời điểm. Khi người dùng ghi dữ liệu, các truy vấn sau đó sẽ nhận được giá trị mới nhất hoặc lỗi.
- **A - Availability (Tính khả dụng):** Mỗi yêu cầu  (đọc/ghi) gửi tới hệ thống đều nhận được phản hồi, dù có thể là cũ hoặc mới - nhưng  bị treo hoặc từ chối.
- **P - Partition tolerance (Chịu phân vùng):** Hệ thống tiếp tục hoạt động được ngay cả khi xảy ra partition (sự cố mất kết nối giữa các node)

**Kết luận CAP:** Trong một hệ thống phân tán hoặc kho dữ liệu nào cũng chỉ có thể đồng thời cung cấp hai trong ba đảm bảo: tính nhất quán, tính khả dụng và khả năng chịu đựng phân vùng

---

## ACID

Tập các thuộc tính đảm bảo giao dịch an toàn, chính xác:

- **A - Atomicity (Nguyên tử):** Một giao dịch (transactions) là một khối thao tác, hoặc là thực hiện toàn bộ, hoặc là không thực hiện gì cả. VD: Nếu có lỗi xảy ra ở giữa giao dịch, mọi thay đổi trước đó đều phải được hoàn tác (rollback).
- **C - Consistency (Nhất quán):** Giao dịch phải giữ cho dữ liệu nhất quán trước và sau khi thực hiện. Hệ thống phải tuân thủ tất cả các ràng buộc (constraints) như khóa ngoại, định dạng, quy tắc nghiệp vụ,...VD: Không thể có một hóa đơn liên kết với một khách hàng không tồn tại  

- **I - Isolation (Độc lập):** Các giao dịch chạy đồng thời phải không ảnh hưởng đến nhau. Kết quả cuối cùng của các giao dịch đồng thời phải giống như khi chúng được thực hiện tuần tự. VD: Nếu hai người cùng đặt hàng một sản phẩm cùng lúc, hệ thống phải xử lý sao cho không bị lỗi số lượng tồn kho.

- **D - Durability (Bền vững):** Sau khi giao dịch được commit, mọi thay đổi dữ liệu phải được lưu trữ vĩnh viễn, ngay cả khi hệ thống gặp sự cố (mất điện, lỗi phần cứng,...). VD: Nếu lưu một đơn hàng và hệ thống báo “thành công” thì dù mất điện ngay sau đó, đơn hàng đó vẫn phải tồn tại khi hệ thống khởi động lại.


---

## BASE

Mô hình thiết kế hệ thống phân tán, nhấn mạnh vào khả dụng và mở rộng:

- **BA - Basically Available:** 
  - Hệ thống luôn đáp ứng được yêu cầu truy cập dữ liệu, ngay cả khi một phần hệ thống bị lỗi
  - Không nhất thiết phải trả về  dữ liệu đúng hoặc mới nhất, nhưng luôn có phản hồi.
  - VD: Khi vào trang thương mại điện tử như Shopee hay Tiki lúc hệ thống đang bị quá tải. Trang sản phẩm vẫn hiển thị được thông tin, dù có thể giá hiển thị cũ hơn giá thật một chút (chưa cập nhật). Nhưng hệ thống vẫn không bị sập, vẫn hoạt động 

- **S - Soft state:**
  - Trạng thái của hệ thống có thể thay đổi theo thời gian mà không cần người dùng can thiệp
  - Dữ liệu giữa các node có thể đang không đồng bộ hoàn toàn, vì hệ thống chấp nhận cập nhật dần dần
  - VD: Hệ thống có 3 bản sao dữ liệu người dùng. Khi cập nhật tên người dùng: Node 1 có thể đã cập nhật. Node 2, 3 thì chưa, nhưng sẽ được cập nhật tự động sau vài giây

- **E - Eventually Consistent:**
  - Sau một khoảng thời gian (vài giây hoặc phút), tất cả các bản sao dữ liệu trong hệ thống sẽ đồng nhất 
  - Không cần nhất quán ngay lập tức như trong ACID
  - VD: Khi nhắn tin qua ứng dụng chat (như zalo): Khi gửi “Hello”. Tin nhắn này sẽ hiện ngay bên người gửi. Nhưng người nhận thấy sau 1-2 giây vì  tin nhắn đang được đồng bộ giữa các node -> cuối cùng thì cả hai vẫn thấy tin nhắn giống nhau, nhưng không phải ngay lập tức.

# TUẦN 3: Big Data - Batch Processing 

## Apache Spark 

### Apache Spark là gì?
- Apache Spark là một nền tảng tính toán cụm mã nguồn mở dành cho xử lý hàng loại (batch processing) và phát trực tuyến (streaming), được thiết kế để xử lý dữ liệu trong bộ nhớ nhanh chóng. 
- Spark sử dụng điện toán trong bộ nhớ để giữ dữ liệu trong RAM trong suốt quá trình tính toán và chỉ trần vào đĩa khi cần thiết.
- Spark cung cấp API dễ sử dụng và hỗ trợ nhiều ngôn ngữ lập trình khác nhau như Python, Scala, và Java

### Các thành phần chính của Apache Spark 
- **Spark Core**: Cung cấp các chức năng nền tảng như quản lý bộ nhớ, lịch trình task
- **Spark SQL**: Xử lý dữ liệu có cấu trúc bằng SQL hoặc DataFrame, Dataset
- **Spark Streaming**: Xử lý dữ liệu thời gian thực dạng stream (theo batch nhỏ)
- **MLlib**: Thư viện học máy (machine learning)
- **GraphX**: Thư viện xử lý đồ thị 

### Kiến trúc vật lý 
Apache Spark chạy theo mô hình Master - Slave (chủ tớ) bao gồm: 
- **Driver Program** (chạy trên Master Node): Là chương trình chính điều khiển toàn bộ ứng dụng Spark 
- **Cluster Manager**: Quản lý tài nguyên và phân phối executor 
- **Executor** (chạy trên Worker Nodes): Tiến trình chạy trên Worker Node để thực hiện task
<img width="322" height="202" alt="image" src="https://github.com/user-attachments/assets/9587a0d8-6cfd-4528-8fd1-8f0ea8971a7d" />

- Apache Spark tuân theo kiểu kiến trúc master-salve.  
- Chương trình Driver đóng vai trò là chủ và điều phối tạo hoặc loại bỏ các Executor.  
- Trình điều khiển phân vùng dữ liệu và chuyển từng phân vùng cho một executor để xử lý phân tán song song.  
- Mỗi Executor thực hiện tính toán thực tế trên phân vùng dữ liệu của nó.  
- Executor truyền lại kết quả cho Driver.

### Kiến trúc logic
- **Người dùng (User layer)**: Thực hiện viết mã bằng RDD API, DataFrame API hoặc SQL API bằng các ngôn ngữ như Scala, Python, Java, R  
- **Catalyst Optimizer (Logic Plan + Optimization)**: Là bộ tối ưu hóa logic dùng cho DataFrame/Dataset, thực hiện chuyển đổi mã người dùng thành Logical Plan, sau đó tối ưu (dự đoán, sắp xếp lại phép toán,...)  
- **Physical Plan**: Khi Catalyst sinh ra một hoặc nhiều physical plan từ logical plan thì Spark sẽ lựa chọn 1 physical plan tối ưu nhất để thực thi  
- **DAG Scheduler**: Biến physical plan thành đồ thị DAG gồm các Stages (mỗi Stage là tập hợp các task có thể thực thi song song)  
- **Task Scheduler**: Gửi các task của mỗi stage đến các executor để xử lý dữ liệu.  

### RDD
**RDD - Resilient Distributed Dataset**: là một tập hợp các đối tượng phân tán, trải rộng trên các nút cụm để xử lý song song.  
Mỗi RDD đều mang siêu dữ liệu riêng để cho phép chịu lỗi và thực thi phân tán

- **Phân vùng**: các khối dữ liệu được phân phối trên các nút cụm. Một phân vùng = một đơn vị song song  
- **Phụ thuộc**: thông tin dòng dõi, danh sách RDD cha và lịch sử chuyển đổi, tạo thành biểu đồ dòng dõi. Điều này cho phép Spark tính toán lại dữ liệu bị mất mà không cần kiểm tra điểm  
- **Tính toán**: hàm chuyển đổi được áp dụng cho RDD cha  
- **Vị trí ưu tiên**: gợi ý nơi lưu trữ phân vùng, cho phép thực thi dữ liệu cục bộ  
- **Trình phân vùng**: xác định cách dữ liệu được chia thành các phân vùng  

RDD là bất biến, không bao giờ có thể thay đổi RDD và chỉ có thể tạo RDD mới từ các phép biến đổi.  
Những thứ sẽ làm với Spark sẽ bắt đầu theo quy trình: tạo một RDD (vd: đọc từ tệp), chuyển đổi nó, sau đó thực hiện điều gì đó hữu ích với nó (như ghi vào HDFS, gửi đến Kafka hoặc đưa vào đường ống ML)

### DataFrame và Dataset
- **DataFrames**: Biểu diễn dữ liệu theo định dạng giống bảng với các cột được đặt tên (giống như bảng SQL). Chúng tận dụng Catalyst Optimizer của Spark để lập kế hoạch và thực hiện truy vấn hiệu quả  
- **Datasets**: Cung cấp các lợi ích của DataFrame với tính an toàn về kiểu, khiến chúng trở nên lý tưởng cho các ngôn ngữ có kiểu tĩnh như Scala và Java

# Hadoop HDFS & Columnar Storage Formats (ORC, Parquet)

## Hadoop HDFS

**HDFS (Hadoop Distributed File System)** là hệ thống file phân tán, được thiết kế để:

- Lưu trữ dữ liệu lớn trên nhiều máy chủ (nodes).
- Chịu lỗi cao (fault-tolerant).
- Hiệu suất cao trong đọc/ghi tuần tự dữ liệu.

### Kiến trúc

HDFS sử dụng kiến trúc **Master/Slave** gồm:

- **NameNode** (Master): NameNode là trung tâm điều khiển của HDFS và lưu trữ thông tin về vị trí và trạng thái các khối dữ liệu.
- **DataNode** (Worker): DataNode là các nút lưu trữ dữ liệu thực sự và phân tán dữ liệu trên các nút khác nhau.

- NameNode quản lý các Namespace Filesystem. Nó quản lý một Filesystem Tree và các metadata cho tất cả file và thư mục trên tree. Thông tin này được lưu trữ trên đĩa vật lý dưới dạng không gian tên ảnh và nhật ký (edit log). NameNode còn quản lý thông tin các khối (block) của một tập tin được lưu trên những DataNodes nào.
- HDFS đưa ra một không gian tên cho phép dữ liệu được lưu trên tập tin. Trong đó một tập tin được chia ra thanh một hay nhiều khối (block) và các block được lưu trên một tập các DataNode. NameNode thực thi các hoạt động trên hệ thống quản trị không gian tên tập tin như mở, đóng đổi tên tập tin và thư mục. Các DataNode có tính năng xử lý các yêu cầu về đọc ghi từ máy khách. Ngoài ra các DataNode còn thực hiện việc tạo, xóa, lặp các khối theo sự hướng dẫn của DataNode.

---

## ORC (Optimized Row Columnar)

**ORC** là định dạng lưu trữ dạng cột (columnar), tối ưu cho việc nén và xử lý song song.

### Đặc điểm chính

- Dữ liệu được tổ chức thành các **dải (stripe)** độc lập.
- Mỗi dải gồm: `index`, `row data`, và `footer`.
- `Footer` chứa **thống kê** và **siêu dữ liệu** cho mỗi cột → cho phép bỏ qua dữ liệu không cần đọc.

<img width="323" height="402" alt="image" src="https://github.com/user-attachments/assets/b8697f24-c9db-40d1-a0eb-a5600beb8976" />
<img width="328" height="369" alt="image" src="https://github.com/user-attachments/assets/ba6ea0e3-629a-4702-9b38-4eff27730f44" />


## Parquet

**Parquet** là định dạng lưu trữ dữ liệu dạng cột được tối ưu cho hệ sinh thái Hadoop.

### Cấu trúc

- Tập tin Parquet gồm: `row groups`, `header`, và `footer`.
- Mỗi **row group** chứa các cột được lưu trữ cùng nhau để hỗ trợ nén và truy cập hiệu quả.

<img width="586" height="308" alt="image" src="https://github.com/user-attachments/assets/83448f31-5204-4abd-9232-29072cab5562" />


### Tính năng nổi bật

- Tối ưu cho dữ liệu **có cấu trúc lồng nhau**.
- Hỗ trợ **nén cột**, mã hóa theo từng cột.
- Có khả năng mở rộng để hỗ trợ cơ chế mã hóa tương lai.
---

# TUẦN 4: 

## Apache Kafka  

## Spark Streaming 

## Kiến trúc Lambda

Kiến trúc Lambda là một kiến trúc xử lý dữ liệu được thiết kế để xử lý lượng dữ liệu khổng lồ bằng cách tận dụng cả phương pháp **xử lý hàng loạt (batch)** và **xử lý luồng (stream)**.
<img width="786" height="193" alt="image" src="https://github.com/user-attachments/assets/59cde0e7-cc31-4ad0-8d2c-fbeb64997463" />


### Mục tiêu

- **Cân bằng giữa độ trễ, thông lượng và khả năng chịu lỗi.**
- Kết hợp batch để cung cấp chế độ xem toàn diện và chính xác cho dữ liệu.
- Kết hợp stream để cung cấp dữ liệu thời gian thực.

### Thành phần

1. **Lớp xử lý dữ liệu batch**  
   - Tính toán trước kết quả sử dụng hệ thống xử lý phân tán.  
   - Hướng đến **độ chính xác cao**, xử lý trên toàn bộ tập dữ liệu.  
   - Có thể sửa lỗi bằng cách tính toán lại trên toàn bộ dữ liệu.  
   - Kết quả thường được lưu ở **cơ sở dữ liệu chỉ đọc** và thay thế dữ liệu cũ.

2. **Lớp xử lý dữ liệu speed (real-time)**  
   - Xử lý dữ liệu **trong thời gian thực**, không yêu cầu sửa chữa hoặc hoàn thiện.  
   - **Hy sinh thông lượng** để giảm thiểu độ trễ.  
   - Dữ liệu có thể không chính xác hoặc đầy đủ như lớp batch.  
   - Được **thay thế bởi dữ liệu batch** khi batch hoàn tất.  
   - **Không nên nhầm với micro-batch**, vì lớp speed xử lý **từng dòng dữ liệu**.

3. **Lớp serving (phục vụ dữ liệu)**  
   - Dữ liệu từ batch và speed được lưu trữ tại đây.  
   - Dùng để **đáp ứng các truy vấn** bằng cách trả về dữ liệu đã xử lý sẵn.  

### Ưu điểm và ứng dụng

- **Phổ biến** nhờ sự phát triển của dữ liệu lớn và nhu cầu phân tích thời gian thực.
- Giảm thiểu độ trễ trong xử lý dữ liệu map-reduce truyền thống.

---

## Kiến trúc Kappa

Mặc dù kiến trúc Lambda rất mạnh mẽ, nó cũng có một số **hạn chế**:

- Cần duy trì **hai cơ sở mã khác nhau**: một cho batch, một cho real-time.
- Phức tạp trong việc đồng bộ logic nghiệp vụ giữa hai luồng xử lý.
<img width="769" height="190" alt="image" src="https://github.com/user-attachments/assets/a5bf997c-116e-4bb2-806a-76fcf9b344f0" />


### Giải pháp: Kiến trúc Kappa

**Được đề xuất bởi Jay Kreps** – đồng sáng lập Apache Kafka.

### Ý tưởng chính

- **Lưu toàn bộ dữ liệu nguồn** (có cấu trúc) vào Kafka (hoặc hệ thống lưu trữ tương tự).
- Tái sử dụng **cùng một logic xử lý** (stream) để chạy lại trên dữ liệu cũ.
- Tránh việc tách riêng xử lý batch và stream → **thống nhất mã nguồn**.

### Đặc điểm

- Có thể lưu trữ dữ liệu nhiều năm trong Kafka để thay thế cho lớp batch.
- Phù hợp với **các hệ thống xử lý luồng yêu cầu xử lý dữ liệu lịch sử** lớn.

---

>  **Ghi chú:**  
> Kiến trúc Kappa **không loại bỏ hoàn toàn** các yêu cầu xử lý batch, nhưng nó **giảm thiểu độ phức tạp** bằng cách sử dụng lại logic xử lý stream trên dữ liệu cũ.

---

## So sánh nhanh

| Tiêu chí               | Lambda                             | Kappa                                  |
|------------------------|------------------------------------|----------------------------------------|
| Số lượng hệ thống xử lý| 2 (batch + stream)                 | 1 (chỉ stream)                         |
| Mã nguồn xử lý         | Riêng biệt                         | Thống nhất                            |
| Phức tạp               | Cao                                | Thấp hơn                              |
| Hỗ trợ dữ liệu lịch sử| Có (batch)                         | Có (lưu dữ liệu trong Kafka lâu dài) |










