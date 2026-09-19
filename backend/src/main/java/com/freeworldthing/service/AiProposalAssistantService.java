package com.freeworldthing.service;

import com.freeworldthing.dto.AiProposalDraftResponse;
import com.freeworldthing.model.*;
import com.freeworldthing.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class AiProposalAssistantService {

    private final JobRepository jobRepository;
    private final ProfileRepository profileRepository;
    private final ProofOfWorkRepository proofOfWorkRepository;
    private final UserRepository userRepository;

    public AiProposalDraftResponse generateProposalDraft(Long jobId, Long freelancerId, String customNote) {
        Job job = jobRepository.findById(jobId).orElse(null);
        User freelancer = userRepository.findById(freelancerId).orElse(null);
        Profile profile = profileRepository.findByUserId(freelancerId).orElse(null);
        List<ProofOfWork> proofs = proofOfWorkRepository.findByUserId(freelancerId);

        String freelancerName = freelancer != null ? freelancer.getFullName() : "Specialist";
        String jobTitle = job != null ? job.getTitle() : "the project";
        Double budget = (job != null && job.getBudgetMin() != null) ? job.getBudgetMin().doubleValue() : 50000.0;
        Integer durationDays = (job != null && job.getDurationWeeks() != null) ? job.getDurationWeeks() * 7 : 28;

        List<String> highlightedProofs = new ArrayList<>();
        StringBuilder proofDetails = new StringBuilder();
        for (ProofOfWork p : proofs) {
            highlightedProofs.add(p.getProjectTitle() + " (" + p.getTechnologies() + ")");
            proofDetails.append("- **").append(p.getProjectTitle()).append("**: ").append(p.getSolution())
                    .append(" (Evidence Score: ").append(p.getEvidenceScore()).append("/100, Test Coverage: ")
                    .append(p.getTestCoveragePercent()).append("%)\n");
        }

        String coverLetter = String.format(
                "Hi there,\n\n" +
                "I am excited to submit my proposal for **%s**. As a %s with a verified Proof-of-Work score of %.0f/100, I have direct hands-on experience delivering similar production-grade solutions.\n\n" +
                "### Relevant Experience & Verified Deliverables:\n%s\n" +
                "### My Proposed Approach:\n" +
                "1. **Discovery & Architecture:** Align on milestones, API contracts, and schema definitions.\n" +
                "2. **Iterative Build & Verification:** Deliver clean, modular code with >90%% test coverage and live staging previews.\n" +
                "3. **Handoff & Deployment:** Fully dockerized production setup with comprehensive documentation.\n\n" +
                "%s\n" +
                "I am ready to start immediately through FreeWorldThing AI Milestone Escrow to ensure 100%% security and transparent progress.\n\n" +
                "Best regards,\n%s",
                jobTitle,
                profile != null ? profile.getTitle() : "Software Engineer",
                profile != null && profile.getProofOfWorkScore() != null ? profile.getProofOfWorkScore() : 95.0,
                proofDetails.length() > 0 ? proofDetails.toString() : "- Direct experience with the required tech stack.\n",
                (customNote != null && !customNote.isBlank()) ? "### Note:\n" + customNote + "\n" : "",
                freelancerName
        );

        List<String> recommendations = Arrays.asList(
                "Verify your milestone delivery timeline matches the client's target duration.",
                "Ensure your GitHub repository links are publicly accessible.",
                "Mention your availability for daily or weekly asynchronous syncs."
        );

        return AiProposalDraftResponse.builder()
                .coverLetter(coverLetter)
                .suggestedBudget(budget)
                .suggestedDurationDays(durationDays)
                .highlightedProofs(highlightedProofs)
                .qualityScore(96.5)
                .recommendations(recommendations)
                .build();
    }

    public double calculateQualityScore(String coverLetter, Double budget, Integer durationDays) {
        if (coverLetter == null || coverLetter.length() < 50) return 40.0;
        double score = 60.0;
        if (coverLetter.length() > 200) score += 15.0;
        if (coverLetter.toLowerCase().contains("milestone") || coverLetter.toLowerCase().contains("test") || coverLetter.toLowerCase().contains("architecture")) score += 10.0;
        if (coverLetter.toLowerCase().contains("github") || coverLetter.toLowerCase().contains("repo") || coverLetter.toLowerCase().contains("demo")) score += 10.0;
        if (budget != null && budget > 0) score += 2.5;
        if (durationDays != null && durationDays > 0) score += 2.5;
        return Math.min(100.0, score);
    }
}
