package com.example.hrm.bai2.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final String JWT_SECRET = "chuoi_secret_key_sieu_bao_mat_cho_he_thong_ban_le_2026";

    private final long JWT_EXPIRATION = 86400000L;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(JWT_SECRET.getBytes());
    }

    public String getUsernameFromJWT(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return claims.getSubject();
        } catch (Exception e) {
            return null;
        }
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            // 1. Kiểm tra cấu trúc token hợp lệ bằng cách parse thử
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token);

            // 2. Lấy username từ token và so sánh với UserDetails hiện tại
            String username = getUsernameFromJWT(token);
            return (username != null && username.equals(userDetails.getUsername()) && !isTokenExpired(token));

        } catch (JwtException | IllegalArgumentException e) {
            // Token có thể đã hết hạn, sai chữ ký, hoặc trống
            return false;
        }
    }
    // Kiểm tra xem mã Token đã quá hạn hay chưa
    private boolean isTokenExpired(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getExpiration().before(new Date());
    }
}