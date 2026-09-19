package com.freeworldthing.controller;

import com.freeworldthing.dto.*;
import com.freeworldthing.service.AiJobBuilderService;
import com.freeworldthing.service.AiProjectManagerService;
import com.freeworldthing.service.AiProposalAssistantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
@CrossOrigin
public class AiStudioController {

    private final AiJobBuilderService aiJobBuilderService;
    private final AiProposalAssistantService aiProposalAssistantService;
    private final AiProjectManagerService aiProjectManagerService;

    @PostMapping("/job-spec")
    public ResponseEntity<AiJobSpecResponse> generateJobSpec(@RequestBody AiJobPromptRequest request) {
        AiJobSpecResponse response = aiJobBuilderService.parsePromptToJobSpec(request.getPrompt(), request.getPreferredCurrency());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/proposal-draft")
    public ResponseEntity<AiProposalDraftResponse> generateProposalDraft(@RequestBody AiProposalDraftRequest request) {
        AiProposalDraftResponse response = aiProposalAssistantService.generateProposalDraft(
                request.getJobId(),
                request.getFreelancerId(),
                request.getCustomNote()
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/workspace-health/{contractId}")
    public ResponseEntity<AiProjectHealthReport> getWorkspaceHealth(@PathVariable Long contractId) {
        AiProjectHealthReport report = aiProjectManagerService.scanAndAnalyzeProject(contractId);
        return ResponseEntity.ok(report);
    }
}
