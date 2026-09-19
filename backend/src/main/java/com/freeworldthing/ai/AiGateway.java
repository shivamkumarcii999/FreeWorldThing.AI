package com.freeworldthing.ai;

import java.util.List;
import java.util.Map;

/**
 * Provider-agnostic AI Gateway. All AI features go through this interface so the
 * marketplace never locks onto a single LLM vendor. The current implementation is
 * a deterministic mock engine (works offline, zero cost); swapping in OpenAI,
 * Anthropic or a self-hosted model means adding one implementation of this interface.
 */
public interface AiGateway {

    /** Analyze a raw idea and return a structured job specification. */
    JobSpec analyzeJobIdea(String idea, Map<String, Object> hints);

    /** Score 0-100 how well a freelancer matches a job, with an explanation. */
    MatchResult matchFreelancer(FreelancerCard freelancer, JobSpec job);

    /** Draft a proposal cover letter for a freelancer applying to a job. */
    String generateProposal(FreelancerCard freelancer, JobSpec job, String extraContext);

    /** Evaluate a proposal draft and return actionable quality feedback. */
    ProposalReview reviewProposal(String jobDescription, String proposalText);

    /** Generate a milestone plan for a contract. */
    List<MilestonePlan> planProject(String title, String description, List<String> skills);

    // ----- transport types -----

    record JobSpec(
            String title,
            String description,
            String category,
            List<String> requiredSkills,
            Double budgetMin,
            Double budgetMax,
            String currency,
            String projectType,      // FIXED | HOURLY
            String experienceLevel,  // ENTRY | INTERMEDIATE | EXPERT
            Integer durationWeeks,
            String complexity,       // LOW | MEDIUM | HIGH
            List<MilestonePlan> suggestedMilestones,
            List<String> clarifyingQuestions
    ) {}

    record MilestonePlan(String title, String description, double shareOfBudget) {}

    record FreelancerCard(
            Long id,
            String fullName,
            String headline,
            String location,
            Double hourlyRate,
            Double ratingAvg,
            Integer ratingCount,
            Integer completedProjects,
            String availability,
            List<SkillEvidence> skills
    ) {}

    record SkillEvidence(String name, int level, boolean verified, String evidence) {}

    record MatchResult(
            Long freelancerId,
            int score,               // 0-100
            String skillMatchPct,
            List<String> reasons,
            List<String> gaps
    ) {}

    record ProposalReview(
            int score,               // 0-100
            List<String> strengths,
            List<String> improvements
    ) {}
}
