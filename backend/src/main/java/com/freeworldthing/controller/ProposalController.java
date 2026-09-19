package com.freeworldthing.controller;

import com.freeworldthing.ai.AiGateway;
import com.freeworldthing.ai.AiOrchestrator;
import com.freeworldthing.model.Job;
import com.freeworldthing.model.Proposal;
import com.freeworldthing.model.User;
import com.freeworldthing.repository.JobRepository;
import com.freeworldthing.repository.ProposalRepository;
import com.freeworldthing.repository.UserRepository;
import com.freeworldthing.service.MatchmakingService;
import com.freeworldthing.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ProposalController {

    private final ProposalRepository proposalRepo;
    private final JobRepository jobRepo;
    private final UserRepository userRepo;
    private final AiOrchestrator ai;
    private final MatchmakingService matchmaking;
    private final NotificationService notifications;

    public record SubmitReq(String coverLetter, Double bidAmount, Double proposedBudget,
                            Integer durationWeeks, Integer proposedDurationDays, Boolean aiDrafted) {}

    public record ProposalDto(Long id, Long jobId, String jobTitle, Long freelancerId,
                              String freelancerName, String freelancerHeadline, Double freelancerRating,
                              Integer freelancerProjects, String coverLetter, Double bidAmount,
                              Integer durationWeeks, String status, Integer aiQualityScore,
                              Integer matchScore, boolean aiDrafted, String createdAt) {}

    public static ProposalDto toDto(Proposal p) {
        return new ProposalDto(p.getId(), p.getJob().getId(), p.getJob().getTitle(),
                p.getFreelancer().getId(), p.getFreelancer().getFullName(), p.getFreelancer().getHeadline(),
                p.getFreelancer().getRatingAvg(), p.getFreelancer().getCompletedProjects(),
                p.getCoverLetter(),
                p.getBidAmount() == null ? null : p.getBidAmount().doubleValue(),
                p.getDurationWeeks(), p.getStatus().name(), p.getAiQualityScore(), p.getMatchScore(),
                p.isAiDrafted(), p.getCreatedAt() == null ? null : p.getCreatedAt().toString());
    }

    @PostMapping("/api/jobs/{jobId}/proposals")
    public ProposalDto submit(Principal principal, @PathVariable Long jobId, @RequestBody SubmitReq req) {
        User me = userRepo.findByEmail(principal.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        if (me.getRole() != User.Role.FREELANCER) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only freelancers can submit proposals");
        }
        Job job = jobRepo.findById(jobId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Job not found"));
        if (job.getStatus() != Job.Status.OPEN) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "This job is no longer open");
        }
        if (proposalRepo.findByJobIdAndFreelancerId(jobId, me.getId()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "You already applied to this job");
        }

        // AI scores computed at submission: match snapshot + proposal quality
        var spec = matchmaking.toJobSpec(job);
        var card = matchmaking.toCard(me);
        AiGateway.MatchResult match = ai.gateway().matchFreelancer(card, spec);
        AiGateway.ProposalReview review = ai.gateway().reviewProposal(job.getDescription(), req.coverLetter());

        Proposal p = Proposal.builder()
                .job(job)
                .freelancer(me)
                .coverLetter(req.coverLetter())
                .bidAmount(req.bidAmount() != null ? BigDecimal.valueOf(req.bidAmount())
                        : req.proposedBudget() != null ? BigDecimal.valueOf(req.proposedBudget()) : null)
                .durationWeeks(req.durationWeeks() != null ? req.durationWeeks()
                        : req.proposedDurationDays() != null ? (int) Math.ceil(req.proposedDurationDays() / 7.0) : null)
                .status(Proposal.Status.SUBMITTED)
                .aiQualityScore(review.score())
                .matchScore(match.score())
                .aiDrafted(Boolean.TRUE.equals(req.aiDrafted()))
                .build();
        p = proposalRepo.save(p);

        notifications.notify(job.getClient(), "PROPOSAL_RECEIVED",
                me.getFullName() + " applied to \"" + job.getTitle() + "\" — " + match.score() + "% match",
                "/jobs/" + jobId);
        return toDto(p);
    }

    /** Proposals on a job — public read in the demo, owner sees everything. */
    @GetMapping("/api/jobs/{jobId}/proposals")
    public List<ProposalDto> forJob(Principal principal, @PathVariable Long jobId) {
        Job job = jobRepo.findById(jobId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Job not found"));
        return proposalRepo.findByJobIdOrderByAiQualityScoreDesc(jobId).stream()
                .map(ProposalController::toDto)
                .toList();
    }

    @GetMapping("/api/proposals/mine")
    public List<ProposalDto> mine(Principal principal) {
        User me = userRepo.findByEmail(principal.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        return proposalRepo.findByFreelancerIdOrderByCreatedAtDesc(me.getId()).stream()
                .map(ProposalController::toDto)
                .toList();
    }

    @PutMapping("/api/proposals/{id}/status")
    public ProposalDto setStatus(Principal principal, @PathVariable Long id, @RequestParam String status) {
        User me = userRepo.findByEmail(principal.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        Proposal p = proposalRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Proposal not found"));
        if (!p.getJob().getClient().getId().equals(me.getId()) && me.getRole() != User.Role.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the job owner can manage proposals");
        }
        Proposal.Status s;
        try {
            s = Proposal.Status.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid status");
        }
        p.setStatus(s);
        if (s == Proposal.Status.ACCEPTED) {
            p.getJob().setStatus(Job.Status.IN_PROGRESS);
            jobRepo.save(p.getJob());
        }
        p = proposalRepo.save(p);
        notifications.notify(p.getFreelancer(), "PROPOSAL_UPDATE",
                "Your proposal for \"" + p.getJob().getTitle() + "\" was " + s.name().toLowerCase(),
                "/proposals");
        return toDto(p);
    }
}
