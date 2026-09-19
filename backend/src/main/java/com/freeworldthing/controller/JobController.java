package com.freeworldthing.controller;

import com.freeworldthing.ai.AiOrchestrator;
import com.freeworldthing.ai.AiGateway;
import com.freeworldthing.model.Job;
import com.freeworldthing.model.Profile;
import com.freeworldthing.model.ProofOfWork;
import com.freeworldthing.model.Skill;
import com.freeworldthing.model.User;
import com.freeworldthing.repository.JobRepository;
import com.freeworldthing.repository.ProfileRepository;
import com.freeworldthing.repository.ProofOfWorkRepository;
import com.freeworldthing.repository.ProposalRepository;
import com.freeworldthing.repository.UserRepository;
import com.freeworldthing.service.MatchmakingService;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
public class JobController {

    private final JobRepository jobRepo;
    private final UserRepository userRepo;
    private final ProposalRepository proposalRepo;
    private final ProfileRepository profileRepo;
    private final ProofOfWorkRepository proofRepo;
    private final AiOrchestrator ai;
    private final MatchmakingService matchmaking;

    /** Freelancer match card in the shape the marketplace UI consumes. */
    public record MatchCard(Long userId, String fullName, String avatarUrl, String title, String location,
                            Double hourlyRate, String currency, Double rating, Integer reviewCount,
                            Integer completedProjects, Double proofOfWorkScore, boolean verifiedBadge,
                            boolean availableNow, List<String> skills, int matchScore,
                            List<String> matchingSkills, String matchReasoning, String topVerifiedProject) {
        public static MatchCard of(AiGateway.FreelancerCard card, AiGateway.MatchResult match,
                                   Profile p, List<ProofOfWork> proofs) {
            String top = proofs.isEmpty() ? "Verified project history"
                    : proofs.get(0).getProjectTitle() + " (" + proofs.get(0).getTechnologies() + ")";
            return new MatchCard(card.id(), card.fullName(), null, p != null && p.getTitle() != null ? p.getTitle() : card.headline(),
                    p != null && p.getLocation() != null ? p.getLocation() : card.location(),
                    card.hourlyRate(), "INR",
                    p != null && p.getRating() != null ? p.getRating() : card.ratingAvg(),
                    p != null && p.getReviewCount() != null ? p.getReviewCount() : card.ratingCount(),
                    card.completedProjects(),
                    p != null && p.getProofOfWorkScore() != null ? p.getProofOfWorkScore() : 90.0,
                    p == null || p.getVerifiedBadge() == null || p.getVerifiedBadge(),
                    p != null && Boolean.TRUE.equals(p.getAvailableNow()),
                    card.skills().stream().map(AiGateway.SkillEvidence::name).toList(),
                    match.score(),
                    card.skills().stream().map(AiGateway.SkillEvidence::name)
                            .filter(s -> jobSkillList(match).isEmpty() || jobSkillList(match).contains(s)).toList(),
                    String.join(" · ", match.reasons()),
                    top);
        }

        private static List<String> jobSkillList(AiGateway.MatchResult match) { return List.of(); }
    }

    public record CreateJobReq(@NotBlank String title, @NotBlank String description, String category,
                               List<String> requiredSkills, Double budgetMin, Double budgetMax,
                               Double minBudget, Double maxBudget, String budgetCurrency,
                               Integer estimatedDurationWeeks, String complexity, String projectType,
                               String suggestedMilestonesJson, Long clientId, String clientName,
                               String currency, String experienceLevel,
                               Integer durationWeeks, Boolean remote) {}

    public record JobDto(
            Long id, String title, String description, String category, List<String> requiredSkills,
            Double budgetMin, Double budgetMax, String currency, String projectType, String experienceLevel,
            Integer durationWeeks, Boolean remote, String status, String aiSpec, String createdAt,
            ClientBrief client, long proposalCount) {
        public record ClientBrief(Long id, String fullName, String headline, String location,
                                  boolean paymentVerified, boolean identityVerified) {}
    }

    public static JobDto toDto(Job j) {
        return new JobDto(j.getId(), j.getTitle(), j.getDescription(),
                j.getCategory() == null ? null : j.getCategory().name(),
                j.getRequiredSkills(),
                j.getBudgetMin() == null ? null : j.getBudgetMin().doubleValue(),
                j.getBudgetMax() == null ? null : j.getBudgetMax().doubleValue(),
                j.getCurrency(),
                j.getProjectType() == null ? null : j.getProjectType().name(),
                j.getExperienceLevel() == null ? null : j.getExperienceLevel().name(),
                j.getDurationWeeks(), j.isRemote(), j.getStatus().name(), j.getAiSpec(),
                j.getCreatedAt() == null ? null : j.getCreatedAt().toString(),
                new JobDto.ClientBrief(j.getClient().getId(), j.getClient().getFullName(),
                        j.getClient().getHeadline(), j.getClient().getLocation(),
                        j.getClient().isPaymentVerified(), j.getClient().isIdentityVerified()),
                0);
    }

    @GetMapping
    public List<JobDto> list(@RequestParam(required = false) String q,
                             @RequestParam(required = false) String category) {
        List<Job> jobs;
        if (q != null && !q.isBlank()) {
            jobs = jobRepo.search(q.trim(), PageRequest.of(0, 100, Sort.by("createdAt").descending())).getContent();
        } else {
            jobs = jobRepo.findByStatusOrderByCreatedAtDesc(Job.Status.OPEN, PageRequest.of(0, 100)).getContent();
        }
        return jobs.stream()
                .filter(j -> category == null || category.isBlank() || category.equalsIgnoreCase("all")
                        || j.getCategory() != null && j.getCategory().name().equalsIgnoreCase(category))
                .map(j -> {
                    JobDto dto = toDto(j);
                    long count = proposalRepo.countByJobId(j.getId());
                    return new JobDto(dto.id(), dto.title(), dto.description(), dto.category(), dto.requiredSkills(),
                            dto.budgetMin(), dto.budgetMax(), dto.currency(), dto.projectType(), dto.experienceLevel(),
                            dto.durationWeeks(), dto.remote(), dto.status(), dto.aiSpec(), dto.createdAt(),
                            dto.client(), count);
                })
                .toList();
    }

    @GetMapping("/{id}")
    public JobDto get(@PathVariable Long id) {
        Job j = jobRepo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Job not found"));
        JobDto dto = toDto(j);
        long count = proposalRepo.countByJobId(id);
        return new JobDto(dto.id(), dto.title(), dto.description(), dto.category(), dto.requiredSkills(),
                dto.budgetMin(), dto.budgetMax(), dto.currency(), dto.projectType(), dto.experienceLevel(),
                dto.durationWeeks(), dto.remote(), dto.status(), dto.aiSpec(), dto.createdAt(), dto.client(), count);
    }

    @PostMapping
    public JobDto create(Principal principal, @RequestBody CreateJobReq req) {
        User client;
        if (principal != null && principal.getName() != null) {
            client = userRepo.findByEmail(principal.getName())
                    .orElseGet(() -> userRepo.findAll().stream().findFirst().orElseThrow());
        } else {
            client = userRepo.findAll().stream().filter(u -> u.getRole() == User.Role.CLIENT).findFirst()
                    .orElseGet(() -> userRepo.findAll().stream().findFirst().orElseThrow());
        }
        Double bMin = req.budgetMin() != null ? req.budgetMin() : req.minBudget();
        Double bMax = req.budgetMax() != null ? req.budgetMax() : req.maxBudget();
        Integer weeks = req.durationWeeks() != null ? req.durationWeeks() : req.estimatedDurationWeeks();
        Job j = Job.builder()
                .client(client)
                .title(req.title())
                .description(req.description())
                .requiredSkills(req.requiredSkills() == null ? new ArrayList<>() : new ArrayList<>(req.requiredSkills()))
                .budgetMin(bMin == null ? null : BigDecimal.valueOf(bMin))
                .budgetMax(bMax == null ? null : BigDecimal.valueOf(bMax))
                .currency(req.budgetCurrency() != null ? req.budgetCurrency()
                        : req.currency() != null ? req.currency() : "INR")
                .projectType(parseEnum(req.projectType(), Job.ProjectType.FIXED))
                .experienceLevel(parseEnum(req.experienceLevel(), Job.ExperienceLevel.INTERMEDIATE))
                .durationWeeks(weeks)
                .remote(req.remote() == null || req.remote())
                .status(Job.Status.OPEN)
                .aiSpec(req.suggestedMilestonesJson())
                .build();
        if (req.category() != null) {
            try { j.setCategory(Skill.Category.valueOf(req.category().toUpperCase().replace("/", "_"))); }
            catch (IllegalArgumentException ignored) {}
        }
        return toDto(jobRepo.save(j));
    }

    @PutMapping("/{id}")
    public JobDto update(Principal principal, @PathVariable Long id, @RequestBody CreateJobReq req) {
        Job j = ownedJob(principal, id);
        if (req.title() != null) j.setTitle(req.title());
        if (req.description() != null) j.setDescription(req.description());
        if (req.requiredSkills() != null) j.setRequiredSkills(req.requiredSkills());
        if (req.budgetMin() != null) j.setBudgetMin(BigDecimal.valueOf(req.budgetMin()));
        if (req.budgetMax() != null) j.setBudgetMax(BigDecimal.valueOf(req.budgetMax()));
        if (req.durationWeeks() != null) j.setDurationWeeks(req.durationWeeks());
        return toDto(jobRepo.save(j));
    }

    @DeleteMapping("/{id}")
    public Map<String, String> close(Principal principal, @PathVariable Long id) {
        Job j = ownedJob(principal, id);
        j.setStatus(Job.Status.CLOSED);
        jobRepo.save(j);
        return Map.of("status", "CLOSED");
    }

    /** AI-ranked freelancer suggestions for this job — the differentiator. */
    @GetMapping("/{id}/match")
    public List<MatchCard> matchesCompat(@PathVariable Long id,
                                         @RequestParam(defaultValue = "6") int limit) {
        return matchmaking.matchJob(id, 50).stream()
                .limit(limit)
                .map(m -> {
                    Profile p = profileRepo.findByUserId(m.freelancer().id()).orElse(null);
                    List<ProofOfWork> proofs = proofRepo.findByUserId(m.freelancer().id());
                    return MatchCard.of(m.freelancer(), m.match(), p, proofs);
                })
                .toList();
    }

    /** AI-ranked freelancer suggestions for this job — the differentiator. */
    @GetMapping("/{id}/matches")
    public List<MatchmakingService.MatchedFreelancer> matches(@PathVariable Long id,
                                                              @RequestParam(defaultValue = "6") int limit) {
        return matchmaking.matchJob(id, limit);
    }

    private Job ownedJob(Principal principal, Long id) {
        Job j = jobRepo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Job not found"));
        User u = userRepo.findByEmail(principal.getName()).orElseThrow();
        if (!j.getClient().getId().equals(u.getId()) && u.getRole() != User.Role.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You don't own this job");
        }
        return j;
    }

    private <T extends Enum<T>> T parseEnum(String value, T fallback) {
        if (value == null) return fallback;
        try { return Enum.valueOf(fallback.getDeclaringClass(), value.toUpperCase()); }
        catch (IllegalArgumentException e) { return fallback; }
    }
}
