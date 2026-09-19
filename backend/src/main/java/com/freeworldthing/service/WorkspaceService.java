package com.freeworldthing.service;

import com.freeworldthing.dto.AiProjectHealthReport;
import com.freeworldthing.dto.CreateTaskRequest;
import com.freeworldthing.dto.SendMessageRequest;
import com.freeworldthing.model.*;
import com.freeworldthing.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class WorkspaceService {

    private final ProjectWorkspaceRepository workspaceRepository;
    private final ContractRepository contractRepository;
    private final MilestoneRepository milestoneRepository;
    private final TaskItemRepository taskItemRepository;
    private final ProjectMessageRepository messageRepository;
    private final AiProjectManagerService aiProjectManagerService;

    public Map<String, Object> getWorkspaceDetails(Long contractId) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new RuntimeException("Contract not found: " + contractId));

        ProjectWorkspace workspace = workspaceRepository.findByContractId(contractId)
                .orElseGet(() -> {
                    ProjectWorkspace newWs = ProjectWorkspace.builder()
                            .contractId(contractId)
                            .healthStatus("ON_TRACK")
                            .aiExecutiveSummary("Workspace initialized.")
                            .lastAiScanAt(LocalDateTime.now())
                            .build();
                    return workspaceRepository.save(newWs);
                });

        List<Milestone> milestones = milestoneRepository.findByContractIdOrderBySequenceAsc(contractId);
        List<TaskItem> tasks = taskItemRepository.findByWorkspaceId(workspace.getId());
        List<ProjectMessage> messages = messageRepository.findByWorkspaceIdOrderByCreatedAtAsc(workspace.getId());

        Map<String, Object> result = new HashMap<>();
        result.put("workspace", workspace);
        result.put("contract", contract);
        result.put("milestones", milestones);
        result.put("tasks", tasks);
        result.put("messages", messages);
        return result;
    }

    @Transactional
    public TaskItem createTask(CreateTaskRequest request) {
        TaskItem task = TaskItem.builder()
                .workspaceId(request.getWorkspaceId())
                .milestoneId(request.getMilestoneId())
                .title(request.getTitle())
                .description(request.getDescription())
                .priority(request.getPriority() != null ? request.getPriority() : "MEDIUM")
                .assignedToName(request.getAssignedToName() != null ? request.getAssignedToName() : "Lead Dev")
                .status(TaskItem.TaskStatus.TODO)
                .build();
        return taskItemRepository.save(task);
    }

    @Transactional
    public TaskItem updateTaskStatus(Long taskId, TaskItem.TaskStatus status) {
        TaskItem task = taskItemRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found: " + taskId));
        task.setStatus(status);
        return taskItemRepository.save(task);
    }

    @Transactional
    public ProjectMessage sendMessage(SendMessageRequest request) {
        ProjectMessage msg = ProjectMessage.builder()
                .workspaceId(request.getWorkspaceId())
                .senderId(request.getSenderId())
                .senderName(request.getSenderName())
                .senderRole(request.getSenderRole() != null ? request.getSenderRole() : "CLIENT")
                .content(request.getContent())
                .messageType(request.getMessageType() != null ? request.getMessageType() : "TEXT")
                .build();
        return messageRepository.save(msg);
    }

    public AiProjectHealthReport runAiHealthScan(Long contractId) {
        return aiProjectManagerService.scanAndAnalyzeProject(contractId);
    }
}
