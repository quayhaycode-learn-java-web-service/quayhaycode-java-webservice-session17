package com.example.hrm.bai5.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @PreAuthorize("#username == authentication.name")
    @PutMapping("/{username}/profile")
    public ResponseEntity<String> updateProfile(@PathVariable String username) {
        return ResponseEntity.ok("Cập nhật profile thành công cho user: " + username);
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/system-stats")
    public ResponseEntity<String> getSystemStats() {
        return ResponseEntity.ok("Dữ liệu thống kê hệ thống nhạy cảm!");
    }
}