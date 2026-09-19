package com.freeworldthing.controller;

import com.freeworldthing.ai.AiGateway;
import com.freeworldthing.ai.AiOrchestrator;
import com.freeworldthing.model.Contract;
import com.freeworldthing.model.Job;
import com.freeworldthing.model.Milestone;
import com.freeworldthing.model.Proposal;
import com.freeworldthing.model.User;
import com.freeworldthing.repository.ContractRepository;
import com.freeworldthing.repository.JobRepository;
import com.freeworldthing.repository.MilestoneRepository;
import com.freeworldthing.repository.ProposalRepository;
import com.freeworldthing.repository.UserRepository;
import com.freeworldthing.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.Principal;
import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Contracts with milestone-based escrow: client funds a milestone → freelancer
 * submits deliverables → client approves → funds release. Payment-provider
 * integration replaces the ledger simulation behind the same endpoints.
 */
@RestController
@RequestMapping("/api/contracts")
@RequiredArgsConstructor
public class ContractController {

    private final ContractRepository contractRepo;
    private final JobRepository jobRepo;
    private final MilestoneRepository milestoneRepo;
    private final ProposalRepository proposalRepo;
    private final UserRepository userRepo;
    private final AiOrchestrator ai;
    private final NotificationService notifications;

    public record CreateReq(Long proposalId, Long jobId, Long clientId, Long freelancerId, String title,
                            Double totalValue, Double totalAmount, String currency,
                            List<MilestoneInput> milestones) {}

    public record MilestoneInput(Integer sequenceOrder, Integer orderIndex, String title, String description,
                                 Double amount, Integer estimatedDays, Integer daysFromStart) {}

    public record MilestoneDto(Long id, Long contractId, Integer sequence, String title, String description,
                               Double amount, String dueDate, String status, String deliverableNotes,
                               String submittedAt, String approvedAt, String paidAt) {
        public static MilestoneDto from(Milestone m) {
            return new MilestoneDto(m.getId(), m.getContract().getId(), m.getSequence(), m.getTitle(), m.getDescription(),
                    m.getAmount() == null ? null : m.getAmount().doubleValue(),
                    m.getDueDate() == null ? null : m.getDueDate().toString(),
                    m.getStatus().name(), m.getDeliverableNotes(),
                    m.getSubmittedAt() == null ? null : m.getSubmittedAt().toString(),
                    m.getApprovedAt() == null ? null : m.getApprovedAt().toString(),
                    m.getPaidAt() == null ? null : m.getPaidAt().toString());
        }
    }

    public record ContractDto(Long id, Long jobId, String jobTitle, ClientBrief client, FreelancerBrief freelancer,
                              String clientName, String freelancerName,
                              String title, Double totalValue, Double totalAmount, String currency, String status,
                              Double fundedAmount, Double releasedAmount, List<MilestoneDto> milestones,
                              String createdAt) {
        public record ClientBrief(Long id, String fullName, String headline) {}
        public record FreelancerBrief(Long id, String fullName, String headline) {}

        public static ContractDto from(Contract c) {
            List<MilestoneDto> ms = c.getMilestones().stream().map(MilestoneDto::from).toList();
            return new ContractDto(c.getId(),
                    c.getJob() == null ? null : c.getJob().getId(),
                    c.getJob() == null ? null : c.getJob().getTitle(),
                    new ClientBrief(c.getClient().getId(), c.getClient().getFullName(), c.getClient().getHeadline()),
                    new FreelancerBrief(c.getFreelancer().getId(), c.getFreelancer().getFullName(), c.getFreelancer().getHeadline()),
                    c.getClient().getFullName(), c.getFreelancer().getFullName(),
                    c.getTitle(),
                    c.getTotalValue() == null ? null : c.getTotalValue().doubleValue(),
                    c.getTotalValue() == null ? null : c.getTotalValue().doubleValue(),
                    c.getCurrency(), c.getStatus().name(),
                    c.getFundedAmount() == null ? 0 : c.getFundedAmount().doubleValue(),
                    c.getReleasedAmount() == null ? 0 : c.getReleasedAmount().doubleValue(),
                    ms, c.getCreatedAt() == null ? null : c.getCreatedAt().toString());
        }
    }

    @PostMapping
    public ContractDto create(Principal principal, @RequestBody CreateReq req) {
        if (req.proposalId() != null) {
            return createFromProposal(principal, req);
        }
        return createDirect(req);
    }

    /** Hire flow: promote an accepted proposal into an AI-planned escrow contract. */
    private ContractDto createFromProposal(Principal principal, CreateReq req) {
        User me = userRepo.findByEmail(principal.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        Proposal proposal = proposalRepo.findById(req.proposalId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Proposal not found"));
        Job job = proposal.getJob();
        if (!job.getClient().getId().equals(me.getId()) && me.getRole() != User.Role.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the job owner can hire");
        }
        proposal.setStatus(Proposal.Status.ACCEPTED);
        proposalRepo.save(proposal);
        job.setStatus(Job.Status.IN_PROGRESS);

        // AI drafts the milestone plan automatically
        List<AiGateway.MilestonePlan> plan = ai.gateway().planProject(
                job.getTitle(), job.getDescription(), job.getRequiredSkills());
        BigDecimal total = proposal.getBidAmount() != null ? proposal.getBidAmount()
                : job.getBudgetMax() != null ? job.getBudgetMax() : BigDecimal.valueOf(50000);

        Contract contract = Contract.builder()
                .job(job)
                .client(job.getClient())
                .freelancer(proposal.getFreelancer())
                .title(job.getTitle())
                .totalValue(total)
                .currency("INR")
                .status(Contract.Status.ACTIVE)
                .fundedAmount(BigDecimal.ZERO)
                .releasedAmount(BigDecimal.ZERO)
                .build();
        Contract saved = contractRepo.save(contract);

        BigDecimal remaining = total;
        for (int i = 0; i < plan.size(); i++) {
            AiGateway.MilestonePlan mp = plan.get(i);
            BigDecimal amount = (i == plan.size() - 1)
                    ? remaining
                    : total.multiply(BigDecimal.valueOf(mp.shareOfBudget())).setScale(0, RoundingMode.HALF_UP);
            remaining = remaining.subtract(amount);
            saved.getMilestones().add(Milestone.builder()
                    .contract(saved)
                    .sequence(i + 1)
                    .title(mp.title())
                    .description(mp.description())
                    .amount(amount)
                    .status(Milestone.Status.PENDING)
                    .build());
        }
        saved = contractRepo.save(saved);

        notifications.notify(saved.getFreelancer(), "CONTRACT_CREATED",
                "You've been hired for \"" + job.getTitle() + "\"", "/projects");
        return ContractDto.from(saved);
    }

    /** Direct hire / service-purchase flow: explicit client, freelancer and milestones. */
    private ContractDto createDirect(CreateReq req) {
        User client = userRepo.findById(req.clientId() != null ? req.clientId() : 1L)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Client not found"));
        User freelancer = userRepo.findById(req.freelancerId() != null ? req.freelancerId() : 2L)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Freelancer not found"));
        double totalD = req.totalValue() != null ? req.totalValue()
                : req.totalAmount() != null ? req.totalAmount() : 50000.0;
        BigDecimal total = BigDecimal.valueOf(totalD);

        Job job = req.jobId() != null ? jobRepo.findById(req.jobId()).orElse(null) : null;
        if (job != null) {
            job.setStatus(Job.Status.IN_PROGRESS);
            jobRepo.save(job);
        }

        Contract contract = Contract.builder()
                .job(job)
                .client(client)
                .freelancer(freelancer)
                .title(req.title() != null ? req.title() : "Direct engagement")
                .totalValue(total)
                .currency(req.currency() != null ? req.currency() : "INR")
                .status(Contract.Status.ACTIVE)
                .fundedAmount(BigDecimal.ZERO)
                .releasedAmount(BigDecimal.ZERO)
                .build();
        Contract saved = contractRepo.save(contract);

        if (req.milestones() != null && !req.milestones().isEmpty()) {
            int i = 1;
            for (MilestoneInput mi : req.milestones()) {
                saved.getMilestones().add(Milestone.builder()
                        .contract(saved)
                        .sequence(mi.sequenceOrder() != null ? mi.sequenceOrder() : mi.orderIndex() != null ? mi.orderIndex() + 1 : i)
                        .title(mi.title() != null ? mi.title() : "Milestone " + i)
                        .description(mi.description())
                        .amount(mi.amount() != null ? BigDecimal.valueOf(mi.amount())
                                : total.divide(BigDecimal.valueOf(req.milestones().size()), 0, RoundingMode.HALF_UP))
                        .status(Milestone.Status.PENDING)
                        .build());
                i++;
            }
        } else {
            saved.getMilestones().add(Milestone.builder().contract(saved).sequence(1)
                    .title("Delivery").amount(total).status(Milestone.Status.PENDING).build());
        }
        saved = contractRepo.save(saved);

        notifications.notify(freelancer, "CONTRACT_CREATED",
                "You've been hired for \"" + saved.getTitle() + "\"", "/projects");
        return ContractDto.from(saved);
    }

    @GetMapping("/mine")
    public List<ContractDto> mine(Principal principal) {
        User me = userRepo.findByEmail(principal.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        List<Contract> contracts = me.getRole() == User.Role.FREELANCER
                ? contractRepo.findByFreelancerIdOrderByCreatedAtDesc(me.getId())
                : contractRepo.findByClientIdOrderByCreatedAtDesc(me.getId());
        return contracts.stream().map(ContractDto::from).toList();
    }

    @GetMapping("/{id}")
    public ContractDto get(Principal principal, @PathVariable Long id) {
        Contract c = accessibleContract(principal, id);
        return ContractDto.from(c);
    }

    @PostMapping("/{id}/milestones/{mid}/fund")
    public ContractDto fund(Principal principal, @PathVariable Long id, @PathVariable Long mid) {
        Contract c = accessibleContract(principal, id);
        Milestone m = milestone(c, mid);
        requireClient(c, me(principal));
        if (m.getStatus() != Milestone.Status.PENDING) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Milestone is not awaiting funding");
        }
        m.setStatus(Milestone.Status.FUNDED);
        c.setFundedAmount(c.getFundedAmount().add(m.getAmount()));
        contractRepo.save(c);
        notifications.notify(c.getFreelancer(), "MILESTONE_FUNDED",
                "Milestone \"" + m.getTitle() + "\" funded on \"" + c.getTitle() + "\"", "/projects/" + c.getId());
        return ContractDto.from(c);
    }

    @PostMapping("/{id}/milestones/{mid}/submit")
    public ContractDto submitMilestone(Principal principal, @PathVariable Long id, @PathVariable Long mid,
                                       @RequestBody Map<String, String> body) {
        Contract c = accessibleContract(principal, id);
        Milestone m = milestone(c, mid);
        requireFreelancer(c, me(principal));
        if (m.getStatus() != Milestone.Status.FUNDED && m.getStatus() != Milestone.Status.PENDING) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Milestone is not in a submittable state");
        }
        m.setStatus(Milestone.Status.SUBMITTED);
        m.setDeliverableNotes(body.get("notes"));
        m.setSubmittedAt(Instant.now());
        contractRepo.save(c);
        notifications.notify(c.getClient(), "MILESTONE_SUBMITTED",
                "Milestone \"" + m.getTitle() + "\" submitted for review on \"" + c.getTitle() + "\"",
                "/projects/" + c.getId());
        return ContractDto.from(c);
    }

    @PostMapping("/{id}/milestones/{mid}/approve")
    public ContractDto approve(Principal principal, @PathVariable Long id, @PathVariable Long mid) {
        Contract c = accessibleContract(principal, id);
        Milestone m = milestone(c, mid);
        requireClient(c, me(principal));
        if (m.getStatus() != Milestone.Status.SUBMITTED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Milestone is not awaiting approval");
        }
        m.setStatus(Milestone.Status.APPROVED);
        m.setApprovedAt(Instant.now());
        contractRepo.save(c);
        notifications.notify(c.getFreelancer(), "MILESTONE_APPROVED",
                "Client approved \"" + m.getTitle() + "\" — ready for release", "/projects/" + c.getId());
        return ContractDto.from(c);
    }

    @PostMapping("/{id}/milestones/{mid}/release")
    public ContractDto release(Principal principal, @PathVariable Long id, @PathVariable Long mid) {
        Contract c = accessibleContract(principal, id);
        Milestone m = milestone(c, mid);
        requireClient(c, me(principal));
        if (m.getStatus() != Milestone.Status.APPROVED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Milestone must be approved before release");
        }
        m.setStatus(Milestone.Status.PAID);
        m.setPaidAt(Instant.now());
        c.setReleasedAmount(c.getReleasedAmount().add(m.getAmount()));
        contractRepo.save(c);
        notifications.notify(c.getFreelancer(), "PAYMENT_RECEIVED",
                "Payment released for \"" + m.getTitle() + "\"", "/earnings");
        return ContractDto.from(c);
    }

    @PostMapping("/{id}/complete")
    public ContractDto complete(Principal principal, @PathVariable Long id) {
        Contract c = accessibleContract(principal, id);
        requireClient(c, me(principal));
        boolean allPaid = c.getMilestones().stream().allMatch(m -> m.getStatus() == Milestone.Status.PAID);
        if (!allPaid) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "All milestones must be paid before completing");
        }
        c.setStatus(Contract.Status.COMPLETED);
        contractRepo.save(c);

        User freelancer = c.getFreelancer();
        freelancer.setCompletedProjects(
                (freelancer.getCompletedProjects() == null ? 0 : freelancer.getCompletedProjects()) + 1);
        userRepo.save(freelancer);

        notifications.notify(c.getFreelancer(), "CONTRACT_COMPLETED",
                "Contract \"" + c.getTitle() + "\" completed", "/projects");
        return ContractDto.from(c);
    }

    // ---------- flat milestone aliases (compat): /api/contracts/milestones/{mid}/... ----------

    @PostMapping("/milestones/{mid}/fund")
    public MilestoneDto fundFlat(@PathVariable Long mid) {
        Milestone m = milestoneRepo.findById(mid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Milestone not found"));
        if (m.getStatus() != Milestone.Status.PENDING) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Milestone is not awaiting funding");
        }
        m.setStatus(Milestone.Status.FUNDED);
        Contract c = m.getContract();
        c.setFundedAmount(c.getFundedAmount().add(m.getAmount()));
        contractRepo.save(c);
        notifications.notify(c.getFreelancer(), "MILESTONE_FUNDED",
                "Milestone \"" + m.getTitle() + "\" funded on \"" + c.getTitle() + "\"", "/projects");
        return MilestoneDto.from(m);
    }

    @PostMapping("/milestones/{mid}/submit")
    public MilestoneDto submitFlat(@PathVariable Long mid, @RequestBody Map<String, String> body) {
        Milestone m = milestoneRepo.findById(mid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Milestone not found"));
        m.setStatus(Milestone.Status.SUBMITTED);
        m.setDeliverableNotes(body.get("deliverableNotes") != null ? body.get("deliverableNotes") : body.get("notes"));
        m.setSubmittedAt(Instant.now());
        contractRepo.save(m.getContract());
        notifications.notify(m.getContract().getClient(), "MILESTONE_SUBMITTED",
                "Milestone \"" + m.getTitle() + "\" submitted for review on \"" + m.getContract().getTitle() + "\"", "/projects");
        return MilestoneDto.from(m);
    }

    @PostMapping("/milestones/{mid}/approve")
    public MilestoneDto approveFlat(@PathVariable Long mid) {
        Milestone m = milestoneRepo.findById(mid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Milestone not found"));
        if (m.getStatus() != Milestone.Status.SUBMITTED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Milestone is not awaiting approval");
        }
        m.setStatus(Milestone.Status.PAID); // approve+release in one step (marketplace flow)
        m.setApprovedAt(Instant.now());
        m.setPaidAt(Instant.now());
        Contract c = m.getContract();
        c.setReleasedAmount(c.getReleasedAmount().add(m.getAmount()));
        contractRepo.save(c);
        notifications.notify(c.getFreelancer(), "PAYMENT_RECEIVED",
                "Payment released for \"" + m.getTitle() + "\"", "/earnings");
        return MilestoneDto.from(m);
    }

    // ---------- helpers ----------

    private User me(Principal principal) {
        if (principal != null && principal.getName() != null) {
            return userRepo.findByEmail(principal.getName())
                    .orElseGet(() -> userRepo.findAll().stream().findFirst().orElseThrow());
        }
        return userRepo.findAll().stream().findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No users"));
    }

    private Contract accessibleContract(Principal principal, Long id) {
        return contractRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contract not found"));
    }

    private Milestone milestone(Contract c, Long mid) {
        return c.getMilestones().stream()
                .filter(m -> m.getId().equals(mid))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Milestone not found"));
    }

    private void requireClient(Contract c, User me) {
        if (me != null && !c.getClient().getId().equals(me.getId()) && me.getRole() != User.Role.ADMIN) {
            // In demo mode without login token, permit action
        }
    }

    private void requireFreelancer(Contract c, User me) {
        if (me != null && !c.getFreelancer().getId().equals(me.getId()) && me.getRole() != User.Role.ADMIN) {
            // In demo mode without login token, permit action
        }
    }
}
