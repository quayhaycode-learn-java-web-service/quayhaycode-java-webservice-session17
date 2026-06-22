Phần 1 - Phân tích logic
Lý do hệ thống trả về thông báo lỗi 'Invalid JWT' nằm ở nguyên lý hoạt động của thuật toán ký chữ ký số đối xứng (Symmetric Signing) như HMAC-SHA256 (HS256).

1. Bản chất của thuật toán HS256
   Trong cơ chế mã hóa đối xứng, cùng một Secret Key phải được sử dụng cho cả hai quá trình:

Tạo Token (Ký số): Hệ thống dùng Secret Key kết hợp với phần Header và Payload để băm thành chuỗi Signature.

Xác thực Token (Giải mã/Kiểm tra): Hệ thống nhận lại JWT từ client, lấy Header và Payload kết hợp với Secret Key để tự tính lại chuỗi Signature mới. Nếu chuỗi mới này trùng với chuỗi Signature đính kèm trong JWT, token mới được coi là hợp lệ.

2. Lỗi logic trong đoạn mã cũ
   Trong đoạn mã của bạn, hàm Keys.secretKeyFor(SignatureAlgorithm.HS256) được gọi hai lần:

Lần 1 tạo ra key.

Lần 2 tạo ra differentKey.

⚠️ Điểm mấu chốt: Mỗi lần gọi Keys.secretKeyFor(), thư viện JJWT sẽ tạo ra một khóa ngẫu nhiên, hoàn toàn mới và có giá trị byte khác hoàn toàn khóa trước đó. Do đó, differentKey không phải là key.

Khi bạn đưa differentKey vào setSigningKey(), bộ phân tích (Parser) của JJWT sẽ dùng khóa sai này để tính toán lại chữ ký. Chữ ký mới tính ra chắc chắn sẽ lệch so với chữ ký gốc được ký bằng key, dẫn đến việc thư viện ném ra ngoại lệ chứng thực (SignatureException) và rơi vào nhánh catch (Exception e) in ra 'Invalid JWT'.