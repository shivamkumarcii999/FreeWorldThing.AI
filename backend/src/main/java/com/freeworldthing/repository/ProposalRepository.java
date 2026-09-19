package com.freeworldthing.repository;

import com.freeworldthing.model.Proposal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProposalRepository extends JpaRepository<Proposal, Long> {
    List<Proposal> findByJobId(Long jobId);
    List<Proposal> findByFreelancerId(Long freelancerId);
    List<Proposal> findByJobIdOrderByAiQualityScoreDesc(Long jobId);
    List<Proposal> findByFreelancerIdOrderByCreatedAtDesc(Long freelancerId);
    Optional<Proposal> findByJobIdAndFreelancerId(Long jobId, Long freelancerId);
    long countByJobId(Long jobId);
}
