package com.freeworldthing.controller;

import com.freeworldthing.ai.AiGateway;
import com.freeworldthing.ai.AiOrchestrator;
import com.freeworldthing.model.Job;
import com.freeworldthing.model.User;
import com.freeworldthing.repository.JobRepository;
import com.freeworldthing.repository.UserRepository;
import com.freeworldthing.service.MatchmakingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiOrchestrator ai;
    private final MatchmakingService matchmaking;
    private final UserRepository userRepo;
    private final JobRepository jobRepo;

    public record AnalyzeReq(String idea, Map<String, Object> hints) {}

    public record GenerateProposalReq(Long jobId, String extraContext) {}

    public record ReviewProposalReq(String jobDescription, String proposalText) {}

    public record PlanReq(String title, String description, List<String> skills, Double totalBudget) {}

    @PostMapping("/job-analyze")
    public AiGateway.JobSpec analyze(@RequestBody AnalyzeReq req) {
        return ai.gateway().analyzeJobIdea(req.idea(), req.hints() == null ? Map.of() : req.hints());
    }

    @PostMapping("/generate-proposal")
    public Map<String, String> generateProposal(Principal principal, @RequestBody GenerateProposalReq req) {
        User me = userRepo.findByEmail(principal.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        Job job = jobRepo.findById(req.jobId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Job not found"));
        var card = matchmaking.toCard(me);
        var spec = matchmaking.toJobSpec(job);
        String draft = ai.gateway().generateProposal(card, spec, req.extraContext());
        return Map.of("draft", draft);
    }

    @PostMapping("/review-proposal")
    public AiGateway.ProposalReview reviewProposal(@RequestBody ReviewProposalReq req) {
        return ai.gateway().reviewProposal(req.jobDescription(), req.proposalText());
    }

    @PostMapping("/project-plan")
    public List<AiGateway.MilestonePlan> plan(@RequestBody PlanReq req) {
        return ai.gateway().planProject(req.title(), req.description(), req.skills());
    }
}
