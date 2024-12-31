ResponseDTO: 
- dùng để trả dữ liệu từ server về cho client
- Dữ liệu cần để thực hiện hành động (input).
- Thường bao gồm các ràng buộc để kiểm tra dữ liệu đầu vào.
- Có thể bao gồm dữ liệu nhạy cảm (như mật khẩu).
- Tối thiểu các trường cần để server xử lý.

RequestDTO: 
- dùng để nhận dữ liệu từ client gửi tới server
- Dữ liệu kết quả của hành động (output).
- Thường không có ràng buộc.
- Loại bỏ các trường nhạy cảm (như mật khẩu).
- Tối thiểu các trường cần để client hiển thị hoặc sử dụng.