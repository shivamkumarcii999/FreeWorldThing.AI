package com.freeworldthing.service;

import com.freeworldthing.dto.AiProjectHealthReport;
import com.freeworldthing.model.*;
import com.freeworldthing.model.Milestone.Status;
import com.freeworldthing.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AiProjectManagerService {

    private final ContractRepository contractRepository;
    private final MilestoneRepository milestoneRepository;
    private final TaskItemRepository taskItemRepository;
    private final ProjectWorkspaceRepository workspaceRepository;

    public AiProjectHealthReport scanAndAnalyzeProject(Long contractId) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new RuntimeException("Contract not found: " + contractId));

        List<Milestone> milestones = milestoneRepository.findByContractIdOrderBySequenceAsc(contractId);
        ProjectWorkspace workspace = workspaceRepository.findByContractId(contractId).orElse(null);
        List<TaskItem> tasks = workspace != null ? taskItemRepository.findByWorkspaceId(workspace.getId()) : Collections.emptyList();

        long totalMilestones = milestones.size();
        long completedMilestones = milestones.stream()
                .filter(m -> m.getStatus() == Status.APPROVED || m.getStatus() == Status.PAID)
                .count();

        long inProgressMilestones = milestones.stream()
                .filter(m -> m.getStatus() == Status.FUNDED)
                .count();

        long deliveredMilestones = milestones.stream()
                .filter(m -> m.getStatus() == Status.SUBMITTED)
                .count();

        int completionPercent = totalMilestones > 0 ? (int) Math.round(((double) completedMilestones / totalMilestones) * 100) : 0;

        List<String> blockers = new ArrayList<>();
        List<String> nextSteps = new ArrayList<>();
        String healthStatus = "ON_TRACK";

        if (contract.getStatus() == Contract.Status.COMPLETED) {
            healthStatus = "COMPLETED";
            nextSteps.add("All deliverables approved and payments released. Leave a review to finalize reputation badges.");
        } else {
            if (deliveredMilestones > 0) {
                blockers.add(deliveredMilestones + " milestone deliverable(s) awaiting client review and payment release.");
                nextSteps.add("Client: Inspect submitted deliverable repository & demo, then click 'Approve & Release'.");
            }

            long unfundedNext = milestones.stream()
                    .filter(m -> m.getStatus() == Status.PENDING)
                    .count();

            if (inProgressMilestones == 0 && deliveredMilestones == 0 && unfundedNext > 0) {
                blockers.add("Next milestone is unfunded. Freelancer cannot commence work until escrow is secured.");
                nextSteps.add("Client: Fund the upcoming milestone to activate the workspace sprint.");
                healthStatus = "AT_RISK";
            } else if (inProgressMilestones > 0) {
                nextSteps.add("Freelancer: Currently executing active milestone sprint. Push daily commits to linked repository.");
            }

            if (tasks.stream().anyMatch(t -> t.getStatus() == TaskItem.TaskStatus.TODO && "URGENT".equalsIgnoreCase(t.getPriority()))) {
                blockers.add("Urgent backlog tasks detected without active assignee progress.");
                healthStatus = "AT_RISK";
            }
        }

        String summary = String.format(
                "Project '%s' is currently %s at %d%% completion (%d/%d milestones finalized). %s",
                contract.getTitle(),
                healthStatus.replace("_", " "),
                completionPercent,
                completedMilestones,
                totalMilestones,
                blockers.isEmpty() ? "All development streams are progressing smoothly on schedule." : "Action required to clear active blockers."
        );

        if (workspace != null) {
            workspace.setHealthStatus(healthStatus);
            workspace.setAiExecutiveSummary(summary);
            workspace.setLastAiScanAt(LocalDateTime.now());
            workspaceRepository.save(workspace);
        }

        return AiProjectHealthReport.builder()
                .healthStatus(healthStatus)
                .executiveSummary(summary)
                .completionPercent(completionPercent)
                .blockers(blockers)
                .nextSteps(nextSteps)
                .confidenceScore(0.96)
                .build();
    }
}
