# Khởi chạy spark-sell

<img width="751" height="628" alt="image" src="https://github.com/user-attachments/assets/da143a47-75ae-4a19-a8e7-797981838b42" />

- Driver được khởi tạo với cluster manager là yarn
- 3 Worker (Excutor) được khởi tạo ngay sau đó -> Yarn hoạt động phân bổ tài nguyên tới những node này
- Scheduling Mode - FIFO: Jobs được xử lý theo thứ tự submit

# Chạy Jobs
<img width="1884" height="424" alt="image" src="https://github.com/user-attachments/assets/44fbae6b-deef-467f-8805-dc7972b00985" />

- Danh sách các job đã chạy thành công
- Trong đó các mỗi job có 1 id, bắt đầu từ id 0
- Mỗi jobs sẽ được chia thành các stages tùy vào độ phức tạo của job đó
- Mỗi Stages được chia thành các tasks 
