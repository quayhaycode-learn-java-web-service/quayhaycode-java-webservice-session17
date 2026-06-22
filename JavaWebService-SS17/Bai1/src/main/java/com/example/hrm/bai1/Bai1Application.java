package com.example.hrm.bai1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class Bai1Application {

    public static void main(String[] args) {
        // (1) Tạo ra Secret Key (Dùng chung cho cả ký và xác thực)
        Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        // (2) Định nghĩa claims (Thông tin người dùng)
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", 123L);
        claims.put("roles", "USER");
        // (3) Thiết lập thời gian phát hành và hết hạn
        Date now = new Date();
        Date expiration = new Date(now.getTime() + 3600 * 1000);
        // (4) Xây dựng và ký JWT
        String jwtToken = Jwts.builder()
                .setClaims(claims)
                .setSubject("user@movieticket.com")
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        System.out.println("Generated JWT: " + jwtToken);
        System.out.println("--------------------------------------------------");

        try {
            Jws<Claims> claimsJws = Jwts.parserBuilder()
                    .setSigningKey(key) // Sử dụng lại chính xác 'key' ban đầu
                    .build()
                    .parseClaimsJws(jwtToken);

            System.out.println("JWT is valid and verified!");

            Claims body = claimsJws.getBody();
            String subject = body.getSubject();
            // ép kiểu qua Number trước khi lấy giá trị Long để tránh ClassCastException an toàn hơn.
            Long userId = ((Number) body.get("userId")).longValue();
            String roles = (String) body.get("roles");
            // In thông tin ra màn hình console theo yêu cầu
            System.out.println("Subject : " + subject);
            System.out.println("User ID : " + userId);
            System.out.println("Roles   : " + roles);
        } catch (Exception e) {
            System.err.println("Invalid JWT: " + e.getMessage());
        }
    }

}
