Phần 1 - Phân tích logic
Lỗi hệ thống trả về 403 Forbidden xuất phát từ sự bất đồng bộ trong luồng xử lý (Sequence Flow) giữa JwtAuthenticationFilter và JwtTokenProvider.

1. Bản chất của lỗi "Con gà và Quả trứng"
   Trong đoạn mã lỗi, bạn thực hiện kiểm tra điều kiện:

Java
if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt, null))
Vấn đề: Để hàm validateToken(jwt, userDetails) kiểm tra xem token đó có thuộc về đúng người dùng đang thao tác hay không, nó bắt buộc phải có đối tượng UserDetails để thực hiện lệnh so sánh: username.equals(userDetails.getUsername()).

Hậu quả: Do bạn truyền vào null, hàm validateToken bên trong JwtTokenProvider sẽ kích hoạt lỗi NullPointerException (hoặc trả về false tùy cách bắt exception). Điều này khiến luồng xử lý bị nhảy thẳng xuống khối catch hoặc bỏ qua khối lệnh if.

2. Thiếu bước thiết lập Ngữ cảnh Bảo mật (Security Context)
   Ngay cả khi điều kiện if có đúng, đoạn mã cũ chỉ dừng lại ở việc gọi userDetailsService.loadUserByUsername(username) mà không hề tạo đối tượng UsernamePasswordAuthenticationToken và không nạp nó vào SecurityContextHolder.

 Nguyên lý của Spring Security: Nếu SecurityContextHolder trống (không chứa thông tin Authentication), Spring Security sẽ coi đây là một request vô danh (Anonymous) và chặn đứng lập tức bằng lỗi 403 Forbidden tại các API cần phân quyền.