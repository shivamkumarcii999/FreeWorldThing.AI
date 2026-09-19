package com.freeworldthing.controller;

import com.freeworldthing.dto.*;
import com.freeworldthing.model.ProjectMessage;
import com.freeworldthing.model.TaskItem;
import com.freeworldthing.service.WorkspaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/workspace")
@RequiredArgsConstructor
@CrossOrigin
public class WorkspaceController {

    private final WorkspaceService workspaceService;

    @GetMapping("/{contractId}")
    public ResponseEntity<Map<String, Object>> getWorkspace(@PathVariable Long contractId) {
        return ResponseEntity.ok(workspaceService.getWorkspaceDetails(contractId));
    }

    @PostMapping("/tasks")
    public ResponseEntity<TaskItem> createTask(@RequestBody CreateTaskRequest request) {
        return ResponseEntity.ok(workspaceService.createTask(request));
    }

    @PatchMapping("/tasks/{taskId}/status")
    public ResponseEntity<TaskItem> updateTaskStatus(
            @PathVariable Long taskId,
            @RequestParam TaskItem.TaskStatus status) {
        return ResponseEntity.ok(workspaceService.updateTaskStatus(taskId, status));
    }

    @PostMapping("/messages")
    public ResponseEntity<ProjectMessage> sendMessage(@RequestBody SendMessageRequest request) {
        return ResponseEntity.ok(workspaceService.sendMessage(request));
    }

    @GetMapping("/{contractId}/ai-health")
    public ResponseEntity<AiProjectHealthReport> getAiHealthScan(@PathVariable Long contractId) {
        return ResponseEntity.ok(workspaceService.runAiHealthScan(contractId));
    }
}
