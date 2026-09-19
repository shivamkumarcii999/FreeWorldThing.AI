package com.freeworldthing.service;

import com.freeworldthing.ai.AiGateway;
import com.freeworldthing.ai.AiOrchestrator;
import com.freeworldthing.model.Job;
import com.freeworldthing.model.User;
import com.freeworldthing.repository.JobRepository;
import com.freeworldthing.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.List;

/**
 * The matching engine pipeline: candidates → AI scoring → trust filters → ranked matches.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MatchmakingService {

    private final UserRepository userRepo;
    private final JobRepository jobRepo;
    private final AiOrchestrator ai;

    public AiGateway.FreelancerCard toCard(User u) {
        List<AiGateway.SkillEvidence> skills = u.getSkills().stream()
                .map(us -> new AiGateway.SkillEvidence(us.getSkill().getName(), us.getLevel(), us.isVerified(), us.getEvidence()))
                .toList();
        return new AiGateway.FreelancerCard(u.getId(), u.getFullName(), u.getHeadline(), u.getLocation(),
                u.getHourlyRate(), u.getRatingAvg(), u.getRatingCount(), u.getCompletedProjects(),
                u.getAvailability() == null ? "UNKNOWN" : u.getAvailability().name(), skills);
    }

    public AiGateway.JobSpec toJobSpec(Job j) {
        return new AiGateway.JobSpec(j.getTitle(), j.getDescription(), j.getCategory() == null ? null : j.getCategory().name(),
                j.getRequiredSkills(),
                j.getBudgetMin() == null ? null : j.getBudgetMin().doubleValue(),
                j.getBudgetMax() == null ? null : j.getBudgetMax().doubleValue(),
                j.getCurrency(), j.getProjectType() == null ? "FIXED" : j.getProjectType().name(),
                j.getExperienceLevel() == null ? "INTERMEDIATE" : j.getExperienceLevel().name(),
                j.getDurationWeeks(), null, List.of(), List.of());
    }

    public record MatchedFreelancer(AiGateway.FreelancerCard freelancer, AiGateway.MatchResult match) {}

    /** Rank all freelancers against a job and return the top matches with explanations. */
    public List<MatchedFreelancer> matchJob(Long jobId, int limit) {
        Job job = jobRepo.findById(jobId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Job not found"));
        AiGateway.JobSpec spec = toJobSpec(job);

        return userRepo.findAll().stream()
                .filter(u -> u.getRole() == User.Role.FREELANCER)
                .map(this::toCard)
                .map(card -> new MatchedFreelancer(card, ai.gateway().matchFreelancer(card, spec)))
                .sorted(Comparator.comparingInt((MatchedFreelancer m) -> m.match().score()).reversed())
                .limit(limit)
                .toList();
    }
}
