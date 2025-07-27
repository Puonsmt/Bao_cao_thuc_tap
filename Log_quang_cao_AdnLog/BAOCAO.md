# Hướng Dẫn Triển Khai và Triển Khai Dự Án AdnLog API

## Tổng Quan Bài Toán
**Yêu cầu**: Xây dựng server API trả về số lượng user view/click cho campaign/banner theo khoảng thời gian, với thời gian phản hồi < 1 phút.

**Đầu vào**: 
- Log quảng cáo với các trường: `guid` (ID người dùng), `campaignId`, `bannerId`, `click_or_view` (false=view, true=click), `time_create`.

**Đầu ra**: 
- Endpoint API trả về số lượng user unique đã view/click campaign/banner trong khoảng thời gian đã cho.

---

## Kiến Trúc Giải Pháp
```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Flask API     │───▶│  Apache Spark   │──▶│ Nguồn Dữ Liệu   │
│   (REST API)    │    │  (Xử lý)        │    │ (HDFS/Mẫu)      │
└─────────────────┘    └─────────────────┘    └─────────────────┘
        │                       │                       │
        │                       │                       │
    Gunicorn              Bộ Nhớ Cache            File Parquet
   (Sản xuất)           (Hiệu suất)            (Lưu trữ)
```

**Chiến lược chính**: Cache toàn bộ dữ liệu vào bộ nhớ → Thực hiện truy vấn từ bộ nhớ thay vì đĩa → Thời gian phản hồi < 1 giây.

---

## Chi Tiết Triển Khai

### **1. Các File Cốt Lõi của Ứng Dụng**

#### **app.py - Ứng dụng Flask Chính**
```python
from flask import Flask, request, jsonify
from datetime import datetime
from spark_session import get_spark
from data_processor import AdnLogProcessor

app = Flask(__name__)
spark = None
processor = None

def init_app():
    """Khởi tạo Spark session và tải dữ liệu"""
    global spark, processor
    try:
        # Bước 1: Khởi tạo Spark session
        spark = get_spark()

        # Bước 2: Khởi tạo bộ xử lý dữ liệu
        processor = AdnLogProcessor(spark)

        # Bước 3: Tải và cache dữ liệu
        if processor.load_and_cache_data():
            return True
        return False
    except Exception as e:
        logger.error(f"Lỗi khởi tạo: {str(e)}")
        return False

@app.route("/query", methods=["GET"])
def query_user_count():
    """Endpoint chính để truy vấn"""
    start_time = datetime.now()

    # Lấy tham số
    id_type = request.args.get("id_type")
    target_id = request.args.get("id")
    mode = request.args.get("mode")
    from_date = request.args.get("from")
    to_date = request.args.get("to")

    # Kiểm tra tham số bắt buộc
    if not all([id_type, target_id, mode, from_date, to_date]):
        return jsonify({"error": "Thiếu tham số bắt buộc"}), 400

    # Kiểm tra định dạng ngày
    try:
        datetime.strptime(from_date, '%Y-%m-%d')
        datetime.strptime(to_date, '%Y-%m-%d')
    except ValueError:
        return jsonify({"error": "Định dạng ngày không hợp lệ. Sử dụng YYYY-MM-DD"}), 400

    # Thực hiện truy vấn Spark
    user_count = processor.query_user_count(id_type, target_id, mode, from_date, to_date)

    # Tính thời gian phản hồi
    end_time = datetime.now()
    query_time = (end_time - start_time).total_seconds()

    return jsonify({
        "success": True,
        "data": {
            "id_type": id_type,
            "id": target_id,
            "mode": mode,
            "from_date": from_date,
            "to_date": to_date,
            "user_count": user_count
        },
        "meta": {
            "query_time_seconds": round(query_time, 3),
            "timestamp": end_time.isoformat()
        }
    })

if __name__ == "__main__":
    if init_app():
        app.run(host="0.0.0.0", port=5000, debug=False, threaded=True)
```

#### **spark_session.py - Cấu Hình Spark**
```python
from pyspark.sql import SparkSession
import os

def get_spark():
    """Tạo SparkSession với cấu hình tối ưu"""
    mode = os.environ.get('SPARK_MODE', 'remote')

    if mode == 'remote':
        # Kết nối với Spark cluster
        spark = SparkSession.builder \
            .appName("AdnLogAPI-Remote") \
            .master("spark://adt-platform-dev-106-254:7077") \
            .config("spark.hadoop.fs.defaultFS", "hdfs://adt-platform-dev-106-254:8120") \
            .config("spark.sql.adaptive.enabled", "true") \
            .config("spark.sql.adaptive.coalescePartitions.enabled", "true") \
            .config("spark.serializer", "org.apache.spark.serializer.KryoSerializer") \
            .config("spark.sql.execution.arrow.pyspark.enabled", "true") \
            .config("spark.executor.memory", "2g") \
            .config("spark.executor.cores", "2") \
            .getOrCreate()
    elif mode == 'yarn':
        # Sản xuất với YARN
        spark = SparkSession.builder \
            .appName("AdnLogAPI-YARN") \
            .master("yarn") \
            .config("spark.hadoop.fs.defaultFS", "hdfs://adt-platform-dev-106-254:8120") \
            .config("spark.sql.adaptive.enabled", "true") \
            .config("spark.serializer", "org.apache.spark.serializer.KryoSerializer") \
            .getOrCreate()
    else:
        # Local mode cho phát triển
        spark = SparkSession.builder \
            .appName("AdnLogAPI-Local") \
            .master("local[*]") \
            .config("spark.sql.adaptive.enabled", "true") \
            .config("spark.serializer", "org.apache.spark.serializer.KryoSerializer") \
            .getOrCreate()

    spark.sparkContext.setLogLevel("WARN")
    return spark
```

#### **data_processor.py - Logic Xử Lý Dữ Liệu**
```python
from pyspark.sql.functions import from_unixtime, col, countDistinct, to_date
from pyspark.sql.types import StructType, StructField, StringType, BooleanType, LongType
import os

class AdnLogProcessor:
    def __init__(self, spark):
        self.spark = spark
        self.df = None

    def create_sample_data(self):
        """Tạo dữ liệu mẫu cho phát triển"""
        schema = StructType([
            StructField("guid", StringType(), True),
            StructField("campaignId", StringType(), True),
            StructField("bannerId", StringType(), True),
            StructField("click_or_view", BooleanType(), True),
            StructField("time_create", LongType(), True)
        ])

        # Dữ liệu mẫu với các test cases
        sample_data = [
            ("user1", "12345", "banner1", False, 1720137600000),  # 2024-07-05 view
            ("user2", "12345", "banner1", True, 1720137600000),   # 2024-07-05 click
            ("user3", "12345", "banner2", False, 1720051200000),  # 2024-07-04 view
            ("user1", "67890", "banner3", False, 1720051200000),  # 2024-07-04 view
            ("user4", "12345", "banner1", False, 1719964800000),  # 2024-07-03 view
            ("user5", "12345", "banner1", True, 1719964800000),   # 2024-07-03 click
        ]

        df = self.spark.createDataFrame(sample_data, schema)

        # Chuyển đổi timestamp
        df_processed = df.withColumn(
            "event_time",
            from_unixtime(col("time_create") / 1000).cast("timestamp")
        ).withColumn(
            "event_date",
            to_date(col("event_time"))
        )

        return df_processed

    def load_and_cache_data(self):
        """Tải và cache dữ liệu"""
        try:
            mode = os.environ.get('SPARK_MODE', 'remote')

            if mode in ['remote', 'yarn']:
                # Sản xuất: Đọc từ HDFS
                df = self.spark.read.parquet("hdfs://adt-platform-dev-106-254:8120/data/Parquet/AdnLog/*")
                df_processed = df.select(
                    "guid", "campaignId", "bannerId", "click_or_view",
                    col("time_group.time_create").alias("time_create")
                ).withColumn(
                    "event_time",
                    from_unixtime(col("time_create") / 1000).cast("timestamp")
                ).withColumn(
                    "event_date",
                    to_date(col("event_time"))
                )
            else:
                # Phát triển: Dữ liệu mẫu
                df_processed = self.create_sample_data()

            # Cache vào bộ nhớ để truy vấn nhanh
            self.df = df_processed.cache()

            # Kích hoạt action để cache thực sự
            count = self.df.count()
            logger.info(f"Cached {count} records successfully")

            return True
        except Exception as e:
            logger.error(f"Lỗi tải dữ liệu: {str(e)}")
            return False

    def query_user_count(self, id_type, target_id, mode, from_date, to_date):
        """Truy vấn số lượng user unique"""
        try:
            # Kiểm tra tham số
            if id_type not in ["campaignId", "bannerId"]:
                raise ValueError("id_type phải là 'campaignId' hoặc 'bannerId'")

            if mode not in ["click", "view"]:
                raise ValueError("mode phải là 'click' hoặc 'view'")

            # Chuyển mode thành boolean (click=true, view=false)
            is_click = (mode == "click")

            # Xây dựng chuỗi truy vấn hiệu quả
            result = self.df.filter(col(id_type) == target_id) \
                           .filter(col("click_or_view") == is_click) \
                           .filter(col("event_date").between(from_date, to_date)) \
                           .agg(countDistinct("guid").alias("user_count")) \
                           .collect()

            return int(result[0]["user_count"]) if result else 0

        except Exception as e:
            logger.error(f"Lỗi truy vấn: {str(e)}")
            raise
```

#### **wsgi.py - Điểm Nhập cho Sản Xuất**
```python
# Giao diện WSGI cho Gunicorn
# Khởi tạo ứng dụng với xử lý lỗi
# Cấu hình logging cho sản xuất
```

---

### **2. Quy Trình Triển Khai**

#### **Bước 1: Chuẩn Bị Môi Trường Server**

##### **1.1 Yêu cầu hệ thống:**
- **Hệ điều hành**: Linux (Ubuntu/CentOS)
- **Python**: 3.8+
- **Java**: 8+ (cho Spark)
- **Apache Spark**: 3.4.3+
- **Bộ nhớ**: 2GB+ RAM

##### **1.2 Kiểm tra môi trường:**
```bash
python3 --version  # Phải >= 3.8
java -version      # Phải >= 8
free -h           # Kiểm tra RAM
df -h             # Kiểm tra dung lượng đĩa
```

##### **1.3 Tạo thư mục dự án:**
```bash
mkdir -p ~/adnlog-api
cd ~/adnlog-api
pwd
```

##### **1.4 Tải file qua Teleport:**
- Trong giao diện web Teleport, tìm nút "Files" hoặc "Upload".
- Tải lên các file:
  - `adnlog-api-complete.zip`

##### **1.5 Giải nén file:**
```bash
cd ~/adnlog-api
unzip adnlog-api-complete.zip
```

##### **1.6 Cấp quyền thực thi:**
```bash
chmod +x *.sh
```

---

#### **Bước 2: Cài Đặt Môi Trường**

##### **2.1 Cấu hình biến môi trường:**
```bash
export SPARK_HOME=/data/spark-3.4.3
export PATH=$PATH:$SPARK_HOME/bin:$SPARK_HOME/sbin
export PYSPARK_PYTHON=python3
```

##### **2.2 Chạy Spark ở Local Mode**
- Tạo file test:
  ```bash
  cat > spark_test_local.py << 'EOF'
  from pyspark.sql import SparkSession

  # Tạo Spark session với local mode
  spark = SparkSession.builder \
      .appName("ADNLogTest") \
      .master("local[*]") \
      .config("spark.sql.warehouse.dir", "/tmp/spark-warehouse") \
      .getOrCreate()

  print("✅ Spark session created!")
  print(f"Spark version: {spark.version}")
  print(f"Master: {spark.sparkContext.master}")

  df = spark.range(10)
  print(f"Test count: {df.count()}")

  spark.stop()
  print("✅ Test completed!")
  EOF
  ```
- Chạy test:
  ```bash
  spark-submit --master local[*] spark_test_local.py
  ```

##### **2.3 Cấu hình PYTHONPATH:**
```bash
export PYTHONPATH=$SPARK_HOME/python:$SPARK_HOME/python/lib/py4j-0.10.9.7-src.zip:$PYTHONPATH
```
- Kiểm tra PySpark:
  ```bash
  python3 -c "from pyspark.sql import SparkSession; print('PySpark OK')"
  ```

---

#### **Bước 3: Chạy Ứng Dụng**

##### **3.1 Cách 1: Chạy thủ công với thiết lập môi trường**
```bash
cd ~/adnlog-api
export SPARK_HOME=/data/spark-3.4.3
export PATH=$PATH:$SPARK_HOME/bin:$SPARK_HOME/sbin
export PYTHONPATH=$SPARK_HOME/python:$SPARK_HOME/python/lib/py4j-0.10.9.7-src.zip:$PYTHONPATH
export PYSPARK_PYTHON=python3
export SPARK_MODE=local
python3 app.py
```

##### **3.2 Cách 2: Tạo script tự động**
- Tạo `run_app.sh`:
  ```bash
  cat > run_app.sh << 'EOF'
  #!/bin/bash

  # Thiết lập môi trường
  export SPARK_HOME=/data/spark-3.4.3
  export PATH=$PATH:$SPARK_HOME/bin:$SPARK_HOME/sbin
  export PYTHONPATH=$SPARK_HOME/python:$SPARK_HOME/python/lib/py4j-0.10.9.7-src.zip:$PYTHONPATH
  export PYSPARK_PYTHON=python3
  export SPARK_MODE=local

  # Chạy ứng dụng
  python3 app.py
  EOF
  ```
- Cấp quyền và chạy:
  ```bash
  chmod +x run_app.sh
  ./run_app.sh
  ```

##### **3.3 Chạy với `spark-submit` (khuyến nghị):**
```bash
spark-submit --master local[*] app.py
```

---

#### **Bước 4: Chạy Server với Gunicorn**

##### **4.1 Tạo script khởi động server**
```bash
cd ~/adnlog-api
cat > start_server.sh << 'EOF'
#!/bin/bash
echo "🚀 Khởi động Server AdnLog API..."

# Thiết lập môi trường
export SPARK_HOME=/data/spark-3.4.3
export PATH=$PATH:$SPARK_HOME/bin:$SPARK_HOME/sbin
export PYTHONPATH=$SPARK_HOME/python:$SPARK_HOME/python/lib/py4j-0.10.9.7-src.zip:$PYTHONPATH
export PYSPARK_PYTHON=python3
export SPARK_MODE=local

echo "📍 Server sẽ sẵn sàng tại: http://localhost:5000"
echo "📖 Tài liệu API: http://localhost:5000/"
echo "❤️ Kiểm tra sức khỏe: http://localhost:5000/health"
echo ""

# Khởi động server
gunicorn --bind 0.0.0.0:5000 --workers 2 --timeout 120 wsgi:app
EOF
```

##### **4.2 Tạo script kiểm tra API**
```bash
cat > test_api.sh << 'EOF'
#!/bin/bash
echo "🧪 Kiểm tra AdnLog API..."

echo "1. Kiểm tra sức khỏe:"
curl -s http://localhost:5000/health | python3 -m json.tool

echo -e "\n2. Tài liệu API:"
curl -s http://localhost:5000/ | python3 -m json.tool

echo -e "\n3. Kiểm tra truy vấn Campaign View:"
curl -s "http://localhost:5000/query?id_type=campaignId&id=12345&mode=view&from=2024-07-03&to=2024-07-05" | python3 -m json.tool

echo -e "\n4. Kiểm tra truy vấn Campaign Click:"
curl -s "http://localhost:5000/query?id_type=campaignId&id=12345&mode=click&from=2024-07-03&to=2024-07-05" | python3 -m json.tool

echo -e "\n✅ Hoàn tất tất cả các kiểm tra!"
EOF
```

##### **4.3 Cấp quyền và chạy**
```bash
chmod +x start_server.sh test_api.sh
echo "✅ Scripts đã được tạo thành công!"
./start_server.sh
```

##### **4.4 Thay đổi cổng (tùy chọn)**
- Thay đổi cổng trong `app.py`:
  ```bash
  sed -i 's/port=5000/port=8080/' app.py
  python3 app.py
  ```
- Hoặc dùng lệnh dự phòng:
  ```bash
  gunicorn --bind 0.0.0.0:5000 --workers 2 --timeout 120 wsgi:app
  ```

##### **4.5 Kiểm tra API**
- Mở terminal mới và chạy:
  ```bash
  # Kiểm tra sức khỏe
  curl http://localhost:5000/health

  # Ví dụ truy vấn
  curl "http://localhost:5000/query?id_type=campaignId&id=12345&mode=view&from=2024-07-03&to=2024-07-05"
  ```

---

#### **Bước 5: Triển Khai Tự Động**
```bash
# Tải adnlog-api-complete.zip lên server
unzip adnlog-api-complete.zip
cd adnlog-api-complete/
chmod +x *.sh
./setup_environment.sh
./start_server.sh
```

---

## ✅ Xác Thực Chức Năng
1. **Chức năng cốt lõi**:
   - ✓ Trả về số user view/click: `"user_count": 3`
   - ✓ Hỗ trợ campaign: `"id_type": "campaignId", "id": "12345"`
   - ✓ Hỗ trợ banner: API có endpoint cho `bannerId`
   - ✓ Lọc theo thời gian: `"from_date": "2024-07-03", "to_date": "2024-07-05"`
   - ✓ Phân biệt view/click: `"mode": "view"` (false = view, true = click)

2. **Hiệu suất**:
   - Yêu cầu: < 1 phút
   - Thực tế: `"query_time_seconds": 0.929` (< 1 giây!)
   - Kết quả: Nhanh hơn yêu cầu 60 lần! 🚀

3. **Cấu trúc dữ liệu**:
   - Server hiểu đúng cấu trúc log:
     - `guid` → Định danh user ✓
     - `campaignId` → ID chiến dịch ✓
     - `bannerId` → ID banner ✓
     - `click_or_view` → false=view, true=click ✓

4. **Thiết kế API**:
   - RESTful: Endpoint GET với tham số truy vấn
   - Linh hoạt: Hỗ trợ cả `campaignId` và `bannerId`
   - Rõ ràng: Định dạng phản hồi rõ ràng với metadata
   - Xử lý lỗi: Kiểm tra tham số đầu vào

#### **Trường hợp kiểm tra**
```bash
# Kiểm tra truy vấn banner
curl "http://localhost:5000/query?id_type=bannerId&id=banner1&mode=click&from=2024-07-03&to=2024-07-05"

# Kiểm tra trường hợp biên
curl "http://localhost:5000/query?id_type=campaignId&id=99999&mode=view&from=2024-07-01&to=2024-07-02"
```

---
