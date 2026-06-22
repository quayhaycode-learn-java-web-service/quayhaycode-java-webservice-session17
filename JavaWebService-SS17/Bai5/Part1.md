Phần 1 - Phân tích logic: Chiến lược phối hợp bảo mật
Để đạt được mức độ bảo mật tối ưu và linh hoạt, chúng ta không chọn một trong hai mà sẽ áp dụng chiến lược Phòng thủ chiều sâu (Defense in Depth) bằng cách kết hợp cả Phân quyền dựa trên URL và Phân quyền cấp phương thức.

1. Phân quyền dựa trên URL (requestMatchers trong SecurityConfig)
   Bản chất: Đóng vai trò như một "Cổng an ninh tòa nhà". Nó lọc các request ngay khi vừa chạm vào hệ thống dựa trên cấu trúc URL Pattern (vùng kiểm soát thô - Coarse-grained).

Ngữ cảnh áp dụng: * Các phân vùng API có ranh giới rõ ràng dành riêng cho một hoặc một nhóm vai trò (Ví dụ: Tất cả các pattern /admin/ bắt buộc phải là ADMIN, /moderator/ phải là GAME_MODERATOR hoặc ADMIN).

Cấu hình các API công khai (Public API) không cần token như /api/v1/auth/, hoặc chỉ cho xem danh sách game công khai.

Lý do lựa chọn: Tốc độ xử lý nhanh (chặn ngay từ tầng Filter Chain trước khi vào Controller), dễ quản lý tổng quan cấu trúc bảo mật hệ thống tại một nơi duy nhất.

2. Phân quyền cấp phương thức (@PreAuthorize, @Secured trong Controller)
   Bản chất: Đóng vai trò như "Khóa vân tay của từng phòng". Nó kiểm tra quyền truy cập ngay trước khi logic của hàm được thực thi (vùng kiểm soát mịn - Fine-grained).

Ngữ cảnh áp dụng:

@Secured: Dùng cho các logic đơn giản, chỉ kiểm tra xem user có sở hữu một Role cụ thể hay không mà không quan tâm đến dữ liệu đầu vào.

@PreAuthorize (kèm SpEL): Dùng cho các nghiệp vụ phức tạp, nơi quyền truy cập phụ thuộc vào ngữ cảnh dữ liệu (Context-aware). Ví dụ: Người dùng chỉ có quyền xóa/sửa bình luận của CHÍNH HỌ, trừ khi họ là MODERATOR hoặc ADMIN.

Lý do lựa chọn: Cho phép viết các điều kiện logic động cực kỳ linh hoạt mà cấu hình URL không thể làm được (vì URL không thể tự động kiểm tra xem ID của vật phẩm/bình luận đó có thuộc về người dùng đang đăng nhập hay không).