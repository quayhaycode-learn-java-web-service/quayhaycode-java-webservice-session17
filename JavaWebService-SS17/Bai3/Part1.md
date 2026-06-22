# Báo cáo: Vận dụng chuyên sâu - Phân quyền nâng cao trong Hệ thống Quản lý Dự án

## Phần 1 - Phân tích logic Phân quyền dựa trên đối tượng (Object-Level Security)

Đối với yêu cầu nghiệp vụ: **"Chỉ người tạo ra một tác vụ (Task) mới được phép xóa tác vụ đó"**, chúng ta cần thực hiện việc phân quyền ở cấp độ đối tượng/dữ liệu động (Contextual / Object-level authorization).

Trong Spring Security, giữa hai Annotation `@Secured` và `@PreAuthorize`, chúng ta **bắt buộc phải ưu tiên lựa chọn `@PreAuthorize`**.

### 1. Sự khác biệt giữa `@Secured` và `@PreAuthorize`

| Tiêu chí | `@Secured` | `@PreAuthorize` |
| :--- | :--- | :--- |
| **Cơ chế hoạt động** | Là Annotation truyền thống (legacy) của Spring Security. Chỉ chấp nhận một mảng các chuỗi ký tự đại diện cho cấu hình Role/Authority cố định. | Là Annotation hiện đại, hoạt động dựa trên cơ chế **Expression-Based Access Control** (Kiểm soát truy cập dựa trên biểu thức). |
| **Hỗ trợ SpEL** | **Không hỗ trợ** Spring Expression Language (SpEL). | **Hỗ trợ toàn diện SpEL**, cho phép viết các biểu thức logic phức tạp. |
| **Khả năng truy cập tham số** | Không thể đọc hoặc liên kết với các tham số đầu vào của phương thức (như `taskId`, `userId`). | Cho phép truy cập trực tiếp vào tham số của phương thức bằng ký tự `#` (Ví dụ: `#taskId`). |
| **Khả năng gọi Spring Bean** | Không thể tương tác với các tầng dữ liệu hoặc Business Service khác tại thời điểm runtime. | Cho phép gọi trực tiếp các Spring Bean/Service được quản lý trong ApplicationContext thông qua cú pháp `@beanName`. |

### 2. Lý do lựa chọn `@PreAuthorize` cho bài toán này

Quy tắc nghiệp vụ xóa task không thể giải quyết bằng cách kiểm tra chức danh (Role-based) đơn thuần, bởi vì hệ thống không thể biết trước User A có phải là người tạo ra Task X hay không nếu chỉ nhìn vào Role `DEVELOPER` của họ. Việc này đòi hỏi:
1. Phải lấy được thông tin `taskId` từ tham số truyền vào phương thức.
2. Phải gọi xuống Database hoặc Service (`TaskService`) để truy vấn xem ai là người tạo ra tác vụ đó.
3. So sánh tên người tạo với tên của người dùng đang đăng nhập hiện tại (`authentication.name`).

`@Secured` hoàn toàn bất lực trước bài toán này vì nó không hỗ