package com.example.hrm.bai5.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/games")
public class GameController {
    
    @Secured({"ROLE_GAME_MODERATOR", "ROLE_ADMIN"})
    @PostMapping
    public ResponseEntity<String> createNewGame() {
        return ResponseEntity.ok("Tạo game mới thành công!");
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'GAME_MODERATOR') or @commentService.isOwner(#commentId, authentication.name)")
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<String> deleteComment(@PathVariable Long commentId) {
        return ResponseEntity.ok("Xóa bình luận thành công!");
    }
}