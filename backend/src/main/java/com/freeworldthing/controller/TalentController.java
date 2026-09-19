package com.freeworldthing.controller;

import com.freeworldthing.ai.AiGateway;
import com.freeworldthing.ai.AiOrchestrator;
import com.freeworldthing.model.ProofOfWork;
import com.freeworldthing.model.Review;
import com.freeworldthing.model.User;
import com.freeworldthing.repository.ProofOfWorkRepository;
import com.freeworldthing.repository.ReviewRepository;
import com.freeworldthing.repository.UserRepository;
import com.freeworldthing.service.MatchmakingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/talent")
@RequiredArgsConstructor
public class TalentController {

    private final UserRepository userRepo;
    private final ReviewRepository reviewRepo;
    private final ProofOfWorkRepository proofRepo;
    private final MatchmakingService matchmaking;
    private final AiOrchestrator ai;

    public record TalentCard(
            Long id, String fullName, String headline, String bio, String location, Double hourlyRate,
            String availability, Double ratingAvg, Integer ratingCount, Integer completedProjects,
            boolean identityVerified, boolean paymentVerified,
            List<SkillLine> skills, List<ReviewBrief> reviews) {
        public record SkillLine(String name, int level, boolean verified, String evidence) {}
        public record ReviewBrief(int overall, String comment, String author, String createdAt) {}
    }

    @GetMapping
    public List<TalentCard> list(@RequestParam(required = false) String q) {
        return userRepo.findAll().stream()
                .filter(u -> u.getRole() == User.Role.FREELANCER)
                .filter(u -> q == null || q.isBlank()
                        || (u.getHeadline() != null && u.getHeadline().toLowerCase().contains(q.toLowerCase()))
                        || u.getFullName().toLowerCase().contains(q.toLowerCase())
                        || u.getSkills().stream().anyMatch(s -> s.getSkill().getName().toLowerCase().contains(q.toLowerCase())))
                .map(this::toCard)
                .toList();
    }

    @GetMapping("/{id}")
    public TalentCard get(@PathVariable Long id) {
        User u = userRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Freelancer not found"));
        return toCard(u);
    }

    /** Verified proof-of-work projects for a freelancer. */
    @GetMapping("/{id}/proofs")
    public List<ProofOfWork> proofs(@PathVariable Long id) {
        return proofRepo.findByUserId(id);
    }

    /** AI-readiness preview: how this freelancer would score against a job idea. */
    public record MatchPreviewReq(String idea, List<String> skills) {}

    @PostMapping("/{id}/match-preview")
    public AiGateway.MatchResult matchPreview(@PathVariable Long id, @RequestBody MatchPreviewReq req) {
        User u = userRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Freelancer not found"));
        var card = matchmaking.toCard(u);
        var spec = new AiGateway.JobSpec("Preview", req.idea() == null ? "" : req.idea(), null,
                req.skills() == null ? List.of() : req.skills(),
                null, null, "INR", "FIXED", "INTERMEDIATE", 4, null, List.of(), List.of());
        return ai.gateway().matchFreelancer(card, spec);
    }

    private TalentCard toCard(User u) {
        List<TalentCard.SkillLine> skills = u.getSkills().stream()
                .map(us -> new TalentCard.SkillLine(us.getSkill().getName(), us.getLevel(), us.isVerified(), us.getEvidence()))
                .toList();
        List<TalentCard.ReviewBrief> reviews = reviewRepo.findBySubjectIdOrderByCreatedAtDesc(u.getId()).stream()
                .limit(5)
                .map(r -> new TalentCard.ReviewBrief(r.getOverall(), r.getComment(), r.getAuthor().getFullName(),
                        r.getCreatedAt() == null ? null : r.getCreatedAt().toString()))
                .toList();
        return new TalentCard(u.getId(), u.getFullName(), u.getHeadline(), u.getBio(), u.getLocation(),
                u.getHourlyRate(), u.getAvailability() == null ? null : u.getAvailability().name(),
                u.getRatingAvg(), u.getRatingCount(), u.getCompletedProjects(),
                u.isIdentityVerified(), u.isPaymentVerified(), skills, reviews);
    }
}
