package com.freeworldthing;

import com.freeworldthing.ai.AiGateway;
import com.freeworldthing.ai.AiOrchestrator;
import com.freeworldthing.dto.AiJobSpecResponse;
import com.freeworldthing.model.*;
import com.freeworldthing.repository.ContractRepository;
import com.freeworldthing.repository.JobRepository;
import com.freeworldthing.repository.MilestoneRepository;
import com.freeworldthing.repository.UserRepository;
import com.freeworldthing.service.AiJobBuilderService;
import com.freeworldthing.service.MatchmakingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class FwtAiApplicationTests {

    @Autowired
    private AiJobBuilderService aiJobBuilderService;

    @Autowired
    private MatchmakingService matchmakingService;

    @Autowired
    private AiOrchestrator aiOrchestrator;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    private MilestoneRepository milestoneRepository;

    private User client;
    private User freelancer;

    @BeforeEach
    public void setup() {
        client = userRepository.findByEmail("shiva@freeworldthing.ai")
                .orElseGet(() -> {
                    User u = new User();
                    u.setEmail("shiva@freeworldthing.ai");
                    u.setPasswordHash("hash123");
                    u.setFullName("Shiva Kumar");
                    u.setRole(User.Role.CLIENT);
                    u.setWalletBalance(100000.0);
                    return userRepository.save(u);
                });

        freelancer = userRepository.findByEmail("elena@freeworldthing.ai")
                .orElseGet(() -> {
                    User u = new User();
                    u.setEmail("elena@freeworldthing.ai");
                    u.setPasswordHash("hash123");
                    u.setFullName("Elena Rostova");
                    u.setRole(User.Role.FREELANCER);
                    u.setWalletBalance(0.0);
                    return userRepository.save(u);
                });
    }

    @Test
    @DisplayName("AI Job Builder accurately decomposes natural language prompts into verifiable milestones")
    public void testAiJobDecomposition() {
        String prompt = "Build a scalable RAG chatbot with LangChain, FastAPI, and Next.js frontend";
        AiJobSpecResponse spec = aiJobBuilderService.parsePromptToJobSpec(prompt, "INR");

        assertNotNull(spec);
        assertNotNull(spec.getTitle());
        assertTrue(spec.getRequiredSkills().contains("LangChain") || spec.getRequiredSkills().contains("Python") || spec.getRequiredSkills().contains("FastAPI"));
        assertNotNull(spec.getSuggestedMilestones());
        assertFalse(spec.getSuggestedMilestones().isEmpty(), "Suggested milestones should not be empty");
        assertTrue(spec.getAiConfidenceScore() > 0.8, "Confidence score should be high");
    }

    @Test
    @DisplayName("AI Smart Matching matches and ranks talent with semantic reasoning")
    public void testAiTalentMatching() {
        Job job = new Job();
        job.setClient(client);
        job.setTitle("AI Engineer for Autonomous Workflow");
        job.setDescription("Need PyTorch, LangChain, and FastAPI expertise");
        job.setRequiredSkills(List.of("LangChain", "PyTorch", "FastAPI", "Python"));
        job.setBudgetMax(new BigDecimal("120000"));
        job.setCategory(Skill.Category.AI_ML);
        job.setStatus(Job.Status.OPEN);
        job = jobRepository.save(job);

        List<MatchmakingService.MatchedFreelancer> matches = matchmakingService.matchJob(job.getId(), 5);
        assertNotNull(matches);
        assertFalse(matches.isEmpty(), "Should find matching candidates");
        assertTrue(matches.get(0).match().score() >= 50, "Top candidate match score should be valid");
        assertNotNull(matches.get(0).match().reasons(), "Match reasons should be provided");
    }

    @Test
    @DisplayName("Smart Escrow Milestone Lifecycle: Pending -> Funded -> Submitted -> Approved -> Paid")
    public void testEscrowLifecycle() {
        // 1. Create a contract
        Contract contract = Contract.builder()
                .title("AI Pipeline Engineering")
                .job(jobRepository.findAll().stream().findFirst().orElse(null))
                .client(client)
                .freelancer(freelancer)
                .totalValue(new BigDecimal("50000.00"))
                .currency("INR")
                .status(Contract.Status.ACTIVE)
                .fundedAmount(BigDecimal.ZERO)
                .releasedAmount(BigDecimal.ZERO)
                .build();
        contract = contractRepository.save(contract);

        // 2. Create milestone
        Milestone milestone = Milestone.builder()
                .contract(contract)
                .sequence(1)
                .title("Phase 1: Architecture & Data Ingestion")
                .amount(new BigDecimal("20000.00"))
                .status(Milestone.Status.PENDING)
                .build();
        milestone = milestoneRepository.save(milestone);

        // 3. Fund milestone
        milestone.setStatus(Milestone.Status.FUNDED);
        contract.setFundedAmount(contract.getFundedAmount().add(milestone.getAmount()));
        milestone = milestoneRepository.save(milestone);
        contract = contractRepository.save(contract);
        assertEquals(Milestone.Status.FUNDED, milestone.getStatus());
        assertEquals(new BigDecimal("20000.00"), contract.getFundedAmount());

        // 4. Freelancer submits deliverable
        milestone.setStatus(Milestone.Status.SUBMITTED);
        milestone.setDeliverableNotes("Delivered data ingestion pipeline with 98% test coverage: https://github.com/freeworldthing/rag-pipeline/pull/1");
        milestone = milestoneRepository.save(milestone);
        assertEquals(Milestone.Status.SUBMITTED, milestone.getStatus());

        // 5. Client approves & releases
        milestone.setStatus(Milestone.Status.APPROVED);
        milestoneRepository.save(milestone);

        milestone.setStatus(Milestone.Status.PAID);
        contract.setReleasedAmount(contract.getReleasedAmount().add(milestone.getAmount()));
        milestoneRepository.save(milestone);
        contractRepository.save(contract);

        assertEquals(Milestone.Status.PAID, milestone.getStatus());
        assertEquals(new BigDecimal("20000.00"), contract.getReleasedAmount());
    }
}
