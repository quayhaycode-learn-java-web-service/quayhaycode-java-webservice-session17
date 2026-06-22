package com.example.hrm.bai2.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import com.example.hrm.bai2.jwt.JwtTokenProvider;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            // 1. Trích xuất JWT từ request
            String jwt = getJwtFromRequest(request);

            if (StringUtils.hasText(jwt)) {
                // 2. Lấy username từ JWT trước để chuẩn bị load UserDetails
                String username = tokenProvider.getUsernameFromJWT(jwt);

                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    // 3. Tải thông tin người dùng từ cơ sở dữ liệu
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                    // 4. Xác thực Token với đầy đủ tham số jwt và userDetails
                    if (tokenProvider.validateToken(jwt, userDetails)) {

                        // 5. Nếu token hợp lệ, tạo đối tượng Authentication của Spring Security
                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                        // 6. Đăng ký thông tin xác thực vào Security Context
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
            }
        } catch (Exception ex) {
            logger.error("Could not set user authentication in security context", ex);
        }

        // Chuyển tiếp request sang Filter tiếp theo trong chuỗi FilterChain
        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}