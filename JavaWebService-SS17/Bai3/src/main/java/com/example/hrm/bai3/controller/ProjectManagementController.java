package com.example.hrm.bai3.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class ProjectManagementController {

     // private final TaskService taskService;

    // Quy tắc 1: Chỉ PO mới có thể tạo dự án
    @PostMapping("/projects")
    @PreAuthorize("hasRole('PO')")
    public String createProject() {
        return "Dự án mới đã được tạo bởi Product Owner.";
    }

    // Quy tắc 2: Chỉ SM mới có thể quản lý sprint
    @PutMapping("/sprints/{id}")
    @PreAuthorize("hasRole('SM')")
    public String manageSprint(@PathVariable UUID id) {
        return "Sprint " + id + " đã được quản lý bởi Scrum Master.";
    }

    // Quy tắc 3: Chỉ DEV hoặc QA mới có thể cập nhật trạng thái tác vụ của CHÍNH HỌ
    @PutMapping("/tasks/{taskId}/status")
    @PreAuthorize("(hasRole('DEV') or hasRole('QA')) and @taskService.isTaskOwner(#taskId, authentication.name)")
    public String updateTaskStatus(@PathVariable UUID taskId) {
        // Logic cập nhật trạng thái task
        return "Trạng thái tác vụ " + taskId + " đã được cập nhật.";
    }

    // Quy tắc 4: Chỉ người tạo task hoặc PO mới được xóa task
    @DeleteMapping("/tasks/{taskId}")
    @PreAuthorize("hasRole('PO') or @taskService.getTaskCreator(#taskId) == authentication.name")
    public String deleteTask(@PathVariable UUID taskId) {
        return "Tác vụ " + taskId + " đã được xóa.";
    }
}