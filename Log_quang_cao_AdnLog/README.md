# Yêu cầu 
## Log quảng cáo: AdnLog
Log này là log quảng cáo có chưa một số thông tin cơ bản sau:
- Guid: định danh user
- CampaignID: ID chiến dich
- BannerID
- ClickOrView: false là view
Y/C Hãy xây dựng một server có thể trả dữ liệu số user view/click đối với một campaign hoặc banner theo thời gian cho trước, từ ngày A -> ngày B. Thời gian phản hồi không được phép quá 1 min.

# Các bước thực hiện 
- Kết nối với server và thực hiện theo các bước sau 
## Tạo thư mục chứa dự án 
```bash
mkdir -p ~/adnlog-api
cd ~/adnlog-api
pwd
```
- Copy đường dẫn từ terminal
- Chọn **Upload File** ở góc phải trên
- Sau đó paste đường dẫn vào **Upload Destination**: /home/phuongtl/adnlog-api/
- Upfile chứa code thực hiện yêu cầu **Drag your files here**:adnlog-api-complete.zip
- Sau khi load thành công ta thực hiện giải nén
```bash
unzip adnlog-api-complete.zip
```

## Setup môi trường 
```bash
export SPARK_HOME=/data/spark-3.4.3
export PATH=$PATH:$SPARK_HOME/bin:$SPARK_HOME/sbin
export PYSPARK_PYTHON=python3
export PYTHONPATH=$SPARK_HOME/python:$SPARK_HOME/python/lib/py4j-0.10.9.7-src.zip:$PYTHONPATH
```

## Cấp quyền thực thi 
```bash
chmod +x *.sh
./setup_environment.sh
```

## Chạy chương trình 
```bash
./start_server.sh
```
# Đọc kết quả 
- Mở một terminal khác thực hiện truy vấn và xem kết quả

