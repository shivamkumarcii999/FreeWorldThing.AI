package com.freeworldthing.config;

import com.freeworldthing.model.*;
import com.freeworldthing.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Seeds a rich demo dataset on first boot: skills taxonomy, demo clients and
 * freelancers with verified evidence, jobs, proposals, an active contract with
 * milestones + AI workspace, proof-of-work portfolios, services, reviews,
 * messages and notifications.
 *
 * Demo logins (all password123): priya@demo.com / rahul@demo.com (clients),
 * shivam@demo.com / amit@demo.com / sara@demo.com / vikram@demo.com / ananya@demo.com (freelancers).
 */
@Component
@RequiredArgsConstructor
public class DataSeeder implements ApplicationRunner {

    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final JobRepository jobRepository;
    private final ProposalRepository proposalRepository;
    private final ContractRepository contractRepository;
    private final MessageRepository messageRepository;
    private final ReviewRepository reviewRepository;
    private final NotificationRepository notificationRepository;
    private final ProfileRepository profileRepository;
    private final ProofOfWorkRepository proofOfWorkRepository;
    private final ServiceListingRepository serviceListingRepository;
    private final ProjectWorkspaceRepository workspaceRepository;
    private final TaskItemRepository taskItemRepository;
    private final ProjectMessageRepository projectMessageRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (userRepository.count() > 0) return;

        String pwd = passwordEncoder.encode("password123");

        // ---------- skills taxonomy ----------
        Map<String, Skill.Category> skillDefs = Map.ofEntries(
                Map.entry("Java", Skill.Category.DEVELOPMENT), Map.entry("Spring Boot", Skill.Category.DEVELOPMENT),
                Map.entry("React", Skill.Category.DEVELOPMENT), Map.entry("Next.js", Skill.Category.DEVELOPMENT),
                Map.entry("Node.js", Skill.Category.DEVELOPMENT), Map.entry("Python", Skill.Category.DEVELOPMENT),
                Map.entry("PostgreSQL", Skill.Category.DEVELOPMENT), Map.entry("MongoDB", Skill.Category.DEVELOPMENT),
                Map.entry("Flutter", Skill.Category.DEVELOPMENT), Map.entry("React Native", Skill.Category.DEVELOPMENT),
                Map.entry("Android", Skill.Category.DEVELOPMENT), Map.entry("iOS", Skill.Category.DEVELOPMENT),
                Map.entry("AWS", Skill.Category.DEVELOPMENT), Map.entry("Docker", Skill.Category.DEVELOPMENT),
                Map.entry("DevOps", Skill.Category.DEVELOPMENT), Map.entry("Stripe/Payments", Skill.Category.DEVELOPMENT),
                Map.entry("AI/LLM Integration", Skill.Category.AI_ML), Map.entry("RAG", Skill.Category.AI_ML),
                Map.entry("Machine Learning", Skill.Category.AI_ML), Map.entry("AI Agents", Skill.Category.AI_ML),
                Map.entry("Data Science", Skill.Category.DATA),
                Map.entry("UI/UX Design", Skill.Category.DESIGN), Map.entry("Graphic Design", Skill.Category.DESIGN),
                Map.entry("Video Editing", Skill.Category.CREATIVE),
                Map.entry("SEO", Skill.Category.MARKETING), Map.entry("Digital Marketing", Skill.Category.MARKETING));
        Map<String, Skill> skills = new java.util.HashMap<>();
        skillDefs.forEach((name, cat) -> skills.put(name, skillRepository.save(
                Skill.builder().name(name).category(cat).build())));

        // ---------- clients ----------
        User priya = userRepository.save(User.builder()
                .email("priya@demo.com").passwordHash(pwd).fullName("Priya Nair").role(User.Role.CLIENT)
                .headline("Founder · D2C e-commerce brand").location("Bengaluru, India")
                .walletBalance(250000.0)
                .identityVerified(true).paymentVerified(true).build());

        User rahul = userRepository.save(User.builder()
                .email("rahul@demo.com").passwordHash(pwd).fullName("Rahul Mehta").role(User.Role.CLIENT)
                .headline("CTO · fintech startup").location("Mumbai, India")
                .walletBalance(180000.0)
                .identityVerified(true).paymentVerified(true).build());

        // ---------- freelancers ----------
        User shivam = freelancer("shivam@demo.com", "Shivam Gupta",
                "Java Full Stack Developer | AI/ML Engineer",
                "Full-stack engineer specialising in Spring Boot + React products with production AI integrations. 14 delivered projects across fintech, e-commerce and SaaS.",
                "Delhi, India", 1500.0, 42500.0, User.Availability.AVAILABLE, 4.9, 12, 14);
        shivam.addSkill(skills.get("Java"), 5, true, "8 production projects");
        shivam.addSkill(skills.get("Spring Boot"), 5, true, "6 verified client contracts");
        shivam.addSkill(skills.get("React"), 4, true, "10+ delivered frontends");
        shivam.addSkill(skills.get("PostgreSQL"), 4, true, "Schema design across 8 projects");
        shivam.addSkill(skills.get("AI/LLM Integration"), 3, false, "2 shipped RAG chatbots");
        shivam.addSkill(skills.get("Stripe/Payments"), 4, true, "5 payment integrations");
        shivam.setSpecialties(new ArrayList<>(List.of("AI/ML", "Full Stack")));
        userRepository.save(shivam);

        User amit = freelancer("amit@demo.com", "Amit Sharma",
                "AI Engineer · Python · FastAPI · RAG systems",
                "AI engineer focused on production RAG pipelines, LLM apps and evaluation. Previously built customer-support AI for two unicorns.",
                "Pune, India", 1800.0, 61000.0, User.Availability.AVAILABLE, 4.9, 9, 11);
        amit.addSkill(skills.get("Python"), 5, true, "Primary language, 11 delivered projects");
        amit.addSkill(skills.get("AI/LLM Integration"), 5, true, "4 verified client AI projects");
        amit.addSkill(skills.get("RAG"), 5, true, "Production RAG at 2 companies");
        amit.addSkill(skills.get("Machine Learning"), 4, true, "CV + NLP models in production");
        amit.addSkill(skills.get("Data Science"), 3, false, "Experiment dashboards for 2 clients");
        amit.setSpecialties(new ArrayList<>(List.of("AI/ML")));
        userRepository.save(amit);

        User sara = freelancer("sara@demo.com", "Sara Khan",
                "Product Designer · UI/UX · Brand systems",
                "Designer for early-stage startups. Brand kits, product UI, design systems in Figma.",
                "Hyderabad, India", 1200.0, 18000.0, User.Availability.WITHIN_WEEK, 4.8, 15, 19);
        sara.addSkill(skills.get("UI/UX Design"), 5, true, "19 delivered design projects");
        sara.addSkill(skills.get("Graphic Design"), 4, true, "Brand kits for 8 startups");
        sara.setSpecialties(new ArrayList<>(List.of("Design")));
        userRepository.save(sara);

        User vikram = freelancer("vikram@demo.com", "Vikram Rao",
                "Mobile Developer · Flutter · React Native",
                "Mobile specialist. 20+ apps shipped to Play Store and App Store.",
                "Chennai, India", 1400.0, 12000.0, User.Availability.BUSY, 4.7, 8, 16);
        vikram.addSkill(skills.get("Flutter"), 5, true, "14 shipped Flutter apps");
        vikram.addSkill(skills.get("React Native"), 4, true, "6 delivered RN apps");
        vikram.addSkill(skills.get("Android"), 4, true, "Native modules + Play Store ops");
        vikram.setSpecialties(new ArrayList<>(List.of("Mobile")));
        userRepository.save(vikram);

        User ananya = freelancer("ananya@demo.com", "Ananya Iyer",
                "Data Scientist · ML · Analytics",
                "ML and analytics freelancer. Experiment design, dashboards, model evaluation.",
                "Kochi, India", 1300.0, 9000.0, User.Availability.AVAILABLE, 4.6, 5, 7);
        ananya.addSkill(skills.get("Data Science"), 5, true, "7 delivered analytics projects");
        ananya.addSkill(skills.get("Machine Learning"), 4, true, "Churn + forecasting models");
        ananya.setSpecialties(new ArrayList<>(List.of("Data")));
        userRepository.save(ananya);

        // ---------- profiles (marketplace view layer) ----------
        profile("shivam", shivam, "Java Full Stack + AI Engineer", "Java, Spring Boot, React, PostgreSQL, AI/LLM, Stripe",
                94.0, "github.com/shivamgupta");
        profile("amit", amit, "AI / RAG Engineer", "Python, FastAPI, RAG, LangChain, Vector DBs, ML",
                97.0, "github.com/amitsharma-ai");
        profile("sara", sara, "Product & Brand Designer", "Figma, UI/UX, Branding, Design Systems",
                91.0, null);
        profile("vikram", vikram, "Mobile Developer", "Flutter, React Native, Android",
                89.0, "github.com/vikramrao");

        // ---------- proof of work ----------
        proof(shivam, "Fintech Lending Platform", "Manual loan screening took 3 days per application.",
                "Spring Boot microservices with rules engine + React dashboard; cut screening to 20 minutes.",
                "Java, Spring Boot, React, PostgreSQL", "github.com/shivamgupta/lending-platform", 92.0, 81, 1240);
        proof(shivam, "E-commerce Storefront", "Brand needed a fast, SEO-friendly storefront.",
                "React + Spring Boot storefront processing 12k orders/month with Stripe checkout.",
                "React, Spring Boot, Stripe", "github.com/shivamgupta/storefront", 88.0, 74, 980);
        proof(amit, "Customer Support RAG Chatbot", "Support team drowned in repetitive tickets.",
                "RAG pipeline over knowledge base with human escalation; deflected 62% of tickets.",
                "Python, FastAPI, LangChain, pgvector", "github.com/amitsharma-ai/support-rag", 96.0, 85, 1520);
        proof(amit, "Document Intelligence Pipeline", "Insurance claims processing was fully manual.",
                "OCR + LLM extraction with evaluation harness; 94% field accuracy on 40+ doc types.",
                "Python, PyTorch, OpenAI API", "github.com/amitsharma-ai/doc-intel", 93.0, 78, 1105);
        proof(sara, "Startup Brand System", "Early-stage startups lacked consistent identity.",
                "Complete brand kits: logo systems, colour, typography and social templates.",
                "Figma, Illustrator", null, 90.0, null, null);

        // ---------- jobs ----------
        Job chatbot = job(priya, "Build AI Customer Support Chatbot", Skill.Category.AI_ML,
                List.of("Python", "AI/LLM Integration", "RAG", "React", "PostgreSQL"),
                50000, 100000, Job.ExperienceLevel.INTERMEDIATE, 4, Job.Status.OPEN,
                "We need an AI chatbot for our e-commerce website that answers product questions, handles order-status lookups and escalates to a human when unsure. RAG over our product catalogue, deployed on our existing React storefront.");

        Job ecommerce = job(rahul, "E-commerce Platform with Payments + AI Recommendations", Skill.Category.DEVELOPMENT,
                List.of("Spring Boot", "React", "PostgreSQL", "Stripe/Payments", "AI/LLM Integration"),
                120000, 250000, Job.ExperienceLevel.EXPERT, 8, Job.Status.OPEN,
                "Full-stack build: Spring Boot backend, React storefront, PostgreSQL, Stripe checkout, plus an AI recommendation widget. Must handle 10k monthly orders at launch.");

        Job logo = job(priya, "Design startup logo + brand kit", Skill.Category.DESIGN,
                List.of("Graphic Design", "UI/UX Design"),
                15000, 30000, Job.ExperienceLevel.INTERMEDIATE, 2, Job.Status.OPEN,
                "New D2C brand needs a logo, colour system, typography and social templates. Figma handoff required.");

        Job fitness = job(rahul, "Flutter fitness tracking app (iOS + Android)", Skill.Category.DEVELOPMENT,
                List.of("Flutter", "UI/UX Design"),
                80000, 150000, Job.ExperienceLevel.INTERMEDIATE, 6, Job.Status.OPEN,
                "Cross-platform fitness app: workout logging, progress charts, wearable sync. Design ready in Figma.");

        Job saas = job(priya, "Spring Boot + React analytics SaaS dashboard", Skill.Category.DEVELOPMENT,
                List.of("Java", "Spring Boot", "React", "PostgreSQL"),
                80000, 120000, Job.ExperienceLevel.EXPERT, 6, Job.Status.IN_PROGRESS,
                "B2B analytics dashboard: multi-tenant Spring Boot API, React frontend with charts, role-based access, subscription billing.");

        // ---------- proposals ----------
        proposal(chatbot, amit, 85000, 4, 88, 95, true, Proposal.Status.SUBMITTED,
                "I've shipped two production RAG support chatbots, including order-status lookups with human escalation. My plan: ingestion pipeline over your catalogue, retrieval evaluation harness, then the escalation flow.");
        proposal(chatbot, ananya, 70000, 5, 74, 81, false, Proposal.Status.SUBMITTED,
                "Data-science perspective on response quality: I'd add an eval set of 200 real customer questions and report accuracy weekly.");
        proposal(ecommerce, shivam, 180000, 8, 86, 91, false, Proposal.Status.SUBMITTED,
                "This maps 1:1 to a marketplace I delivered last year: Spring Boot + React + Stripe, 12k monthly orders. I'd start with a walking skeleton, then payments, then the recommendation service.");
        proposal(logo, sara, 22000, 2, 81, 92, true, Proposal.Status.SUBMITTED,
                "Brand-first process: moodboard → 3 logo directions → full kit in Figma with social templates. Portfolio has 8 startup brand kits.");
        proposal(fitness, vikram, 120000, 6, 80, 89, false, Proposal.Status.SUBMITTED,
                "14 Flutter apps shipped, including two with HealthKit/Google Fit sync. Figma handoff is my normal workflow.");

        // ---------- completed past contract + reviews (reputation history) ----------
        Contract past = Contract.builder()
                .job(logo).client(priya).freelancer(sara)
                .title("Design startup logo + brand kit")
                .totalValue(BigDecimal.valueOf(22000)).currency("INR")
                .status(Contract.Status.COMPLETED)
                .fundedAmount(BigDecimal.valueOf(22000)).releasedAmount(BigDecimal.valueOf(22000))
                .build();
        past.getMilestones().add(Milestone.builder().contract(past).sequence(1)
                .title("Brand exploration & delivery").amount(BigDecimal.valueOf(22000))
                .status(Milestone.Status.PAID)
                .paidAt(Instant.now().minus(20, ChronoUnit.DAYS)).build());
        contractRepository.save(past);
        review(priya, sara, past, Review.Direction.CLIENT_TO_FREELANCER, 5,
                "Sara nailed the brief on the first round. Process was structured, delivery early.", 5, 5, 5, 5, null, null, null);
        review(sara, priya, past, Review.Direction.FREELANCER_TO_CLIENT, 5,
                "Clear brief, fast feedback, instant milestone release. Ideal client.", null, null, null, null, 5, 5, 5);

        Contract past2 = Contract.builder()
                .job(chatbot).client(rahul).freelancer(amit)
                .title("Internal RAG knowledge assistant")
                .totalValue(BigDecimal.valueOf(60000)).currency("INR")
                .status(Contract.Status.COMPLETED)
                .fundedAmount(BigDecimal.valueOf(60000)).releasedAmount(BigDecimal.valueOf(60000))
                .build();
        past2.getMilestones().add(Milestone.builder().contract(past2).sequence(1)
                .title("RAG pipeline v1").amount(BigDecimal.valueOf(60000))
                .status(Milestone.Status.PAID)
                .paidAt(Instant.now().minus(45, ChronoUnit.DAYS)).build());
        contractRepository.save(past2);
        review(rahul, amit, past2, Review.Direction.CLIENT_TO_FREELANCER, 5,
                "Amit's RAG pipeline exceeded expectations — accuracy was measurable at every step.", 5, 5, 5, 5, null, null, null);

        // ---------- active contract: saas dashboard, priya × shivam ----------
        Contract active = Contract.builder()
                .job(saas).client(priya).freelancer(shivam)
                .title("Spring Boot + React analytics SaaS dashboard")
                .totalValue(BigDecimal.valueOf(100000)).currency("INR")
                .status(Contract.Status.ACTIVE)
                .fundedAmount(BigDecimal.valueOf(55000)).releasedAmount(BigDecimal.valueOf(15000))
                .build();
        Milestone m1 = Milestone.builder().contract(active).sequence(1)
                .title("Discovery & Architecture").description("Requirements lock-in, technical design, repo setup.")
                .amount(BigDecimal.valueOf(15000)).status(Milestone.Status.PAID)
                .submittedAt(Instant.now().minus(9, ChronoUnit.DAYS))
                .approvedAt(Instant.now().minus(8, ChronoUnit.DAYS))
                .paidAt(Instant.now().minus(8, ChronoUnit.DAYS)).build();
        Milestone m2 = Milestone.builder().contract(active).sequence(2)
                .title("Backend & Database").description("Multi-tenant API, schema, RBAC.")
                .amount(BigDecimal.valueOf(30000)).status(Milestone.Status.SUBMITTED)
                .deliverableNotes("API + migrations complete, RBAC wired. Postman collection attached.")
                .submittedAt(Instant.now().minus(1, ChronoUnit.DAYS)).build();
        Milestone m3 = Milestone.builder().contract(active).sequence(3)
                .title("Frontend & Integration").description("React dashboard, charts, subscription billing UI.")
                .amount(BigDecimal.valueOf(30000)).status(Milestone.Status.FUNDED).build();
        Milestone m4 = Milestone.builder().contract(active).sequence(4)
                .title("Testing & Deployment").description("QA pass, deployment, handover docs.")
                .amount(BigDecimal.valueOf(25000)).status(Milestone.Status.PENDING).build();
        active.getMilestones().addAll(List.of(m1, m2, m3, m4));
        active = contractRepository.save(active);

        ProjectWorkspace ws = workspaceRepository.save(ProjectWorkspace.builder()
                .contractId(active.getId())
                .healthStatus("AT_RISK")
                .aiExecutiveSummary("Project 'Spring Boot + React analytics SaaS dashboard' is currently AT RISK at 25% completion (1/4 milestones finalized). 1 milestone deliverable awaits client review; the urgent frontend task backlog needs an assignee.")
                .blockersJson("[\"Backend & Database milestone awaits client review and payment release.\",\"Urgent task 'Chart component spike' has no assignee progress.\"]")
                .nextStepsJson("[\"Client: review submitted milestone and approve.\",\"Freelancer: begin Frontend & Integration sprint.\"]")
                .lastAiScanAt(LocalDateTime.now())
                .build());

        taskItemRepository.save(TaskItem.builder().workspaceId(ws.getId()).milestoneId(m3.getId())
                .title("Chart component spike").description("Pick charting lib + wire mock data.")
                .status(TaskItem.TaskStatus.TODO).priority("URGENT").assignedToName("Shivam Gupta")
                .dueDate(LocalDateTime.now().plus(3, ChronoUnit.DAYS)).build());
        taskItemRepository.save(TaskItem.builder().workspaceId(ws.getId()).milestoneId(m2.getId())
                .title("Multi-tenant schema").description("Tenant isolation on all core tables.")
                .status(TaskItem.TaskStatus.DONE).priority("HIGH").assignedToName("Shivam Gupta").build());
        taskItemRepository.save(TaskItem.builder().workspaceId(ws.getId()).milestoneId(m3.getId())
                .title("Billing UI").description("Plan selection + upgrade flow.")
                .status(TaskItem.TaskStatus.IN_PROGRESS).priority("MEDIUM").assignedToName("Shivam Gupta").build());

        projectMessageRepository.save(ProjectMessage.builder().workspaceId(ws.getId())
                .senderId(priya.getId()).senderName("Priya Nair").senderRole("CLIENT")
                .content("Milestone 2 looks great overall — can you expose an audit log endpoint before we approve?").build());
        projectMessageRepository.save(ProjectMessage.builder().workspaceId(ws.getId())
                .senderId(shivam.getId()).senderName("Shivam Gupta").senderRole("FREELANCER")
                .content("Adding the audit log endpoint now; will resubmit with updated Postman collection by tomorrow.").build());
        projectMessageRepository.save(ProjectMessage.builder().workspaceId(ws.getId())
                .senderId(0L).senderName("FWT AI Project Manager").senderRole("AI_ASSISTANT")
                .messageType("AI_INSIGHT")
                .content("⚠ Backend & Database is awaiting your review for 1 day. On current velocity the project finishes 3 days ahead of schedule if approved this week.").build());

        // ---------- services marketplace ----------
        serviceListing(amit, "I will build a production-ready RAG chatbot for your data", "AI/ML",
                35000.0, 4.9, 12, 7,
                "[{\"name\":\"Basic\",\"price\":35000,\"features\":[\"RAG over 1 data source\",\"Chat widget\",\"7-day delivery\"]},{\"name\":\"Standard\",\"price\":65000,\"features\":[\"Multiple documents\",\"Admin dashboard\",\"Auth\",\"Deployment\"]},{\"name\":\"Premium\",\"price\":125000,\"features\":[\"Production architecture\",\"Advanced RAG + reranking\",\"Analytics\",\"30-day support\"]}]",
                "[{\"name\":\"WhatsApp integration\",\"price\":15000},{\"name\":\"Voice AI\",\"price\":25000},{\"name\":\"Extra API integration\",\"price\":10000}]");
        serviceListing(sara, "I will design a complete startup brand kit in Figma", "Design",
                15000.0, 4.8, 15, 5,
                "[{\"name\":\"Basic\",\"price\":15000,\"features\":[\"Logo concepts (3)\",\"Colour + type system\"]},{\"name\":\"Standard\",\"price\":25000,\"features\":[\"Full brand kit\",\"Social templates\"]},{\"name\":\"Premium\",\"price\":40000,\"features\":[\"Brand guide PDF\",\"Pitch deck template\",\"2 revision rounds\"]}]",
                "[{\"name\":\"Rush delivery (48h)\",\"price\":8000},{\"name\":\"Extra revision round\",\"price\":3000}]");
        serviceListing(vikram, "I will build a cross-platform Flutter app", "Development",
                60000.0, 4.7, 8, 21,
                "[{\"name\":\"Basic\",\"price\":60000,\"features\":[\"Up to 5 screens\",\"Firebase auth\"]},{\"name\":\"Standard\",\"price\":100000,\"features\":[\"Up to 12 screens\",\"Backend integration\",\"Store submission\"]},{\"name\":\"Premium\",\"price\":150000,\"features\":[\"Full app\",\"CI/CD\",\"90-day support\"]}]",
                "[{\"name\":\"Wearable sync\",\"price\":30000},{\"name\":\"Push notifications\",\"price\":8000}]");

        // ---------- chat + notifications ----------
        messageRepository.save(Message.builder().conversationKey(convKey(priya.getId(), shivam.getId()))
                .sender(priya).recipient(shivam)
                .body("Hi Shivam — milestone 2 looks great. Can you also expose an audit log endpoint before we approve?").build());
        messageRepository.save(Message.builder().conversationKey(convKey(priya.getId(), shivam.getId()))
                .sender(shivam).recipient(priya).read(true)
                .body("Absolutely — adding it now, will resubmit the milestone with the Postman collection updated by tomorrow.").build());

        notificationRepository.save(Notification.builder().user(priya).type("MILESTONE_SUBMITTED")
                .text("Shivam Gupta submitted \"Backend & Database\" for review")
                .link("/projects/" + active.getId()).build());
        notificationRepository.save(Notification.builder().user(shivam).type("MATCH_FOUND")
                .text("New 91% match: \"E-commerce Platform with Payments + AI Recommendations\"")
                .link("/jobs/" + ecommerce.getId()).build());
        notificationRepository.save(Notification.builder().user(amit).type("PROPOSAL_RECEIVED")
                .text("Your proposal for \"Build AI Customer Support Chatbot\" was shortlisted")
                .link("/proposals").build());
    }

    // ---------- helpers ----------

    private User freelancer(String email, String name, String headline, String bio, String location,
                            Double rate, Double wallet, User.Availability availability,
                            Double rating, int ratingCount, int projects) {
        return userRepository.save(User.builder()
                .email(email).passwordHash(passwordEncoder.encode("password123"))
                .fullName(name).role(User.Role.FREELANCER)
                .headline(headline).bio(bio).location(location).hourlyRate(rate).walletBalance(wallet)
                .availability(availability).ratingAvg(rating).ratingCount(ratingCount).completedProjects(projects)
                .identityVerified(true).paymentVerified(true)
                .build());
    }

    private void profile(String tag, User u, String title, String skills, Double powScore, String github) {
        profileRepository.save(Profile.builder()
                .userId(u.getId()).title(title).bio(u.getBio()).hourlyRate(u.getHourlyRate())
                .rating(u.getRatingAvg()).reviewCount(u.getRatingCount()).completedProjects(u.getCompletedProjects())
                .location(u.getLocation()).githubUrl(github).skills(skills)
                .proofOfWorkScore(powScore).verifiedBadge(true)
                .availableNow(u.getAvailability() == User.Availability.AVAILABLE)
                .build());
    }

    private void proof(User u, String title, String problem, String solution, String tech,
                       String repo, Double evidence, Integer coverage, Integer commits) {
        proofOfWorkRepository.save(ProofOfWork.builder()
                .userId(u.getId()).projectTitle(title).problem(problem).solution(solution)
                .technologies(tech).repoUrl(repo).evidenceScore(evidence)
                .testCoveragePercent(coverage).commitCount(commits).verified(true)
                .build());
    }

    private Job job(User client, String title, Skill.Category cat, List<String> skills,
                    double min, double max, Job.ExperienceLevel level, int weeks, Job.Status status, String desc) {
        return jobRepository.save(Job.builder()
                .client(client).title(title).description(desc).category(cat)
                .requiredSkills(skills == null ? new ArrayList<>() : new ArrayList<>(skills))
                .budgetMin(BigDecimal.valueOf(min)).budgetMax(BigDecimal.valueOf(max))
                .projectType(Job.ProjectType.FIXED).experienceLevel(level)
                .durationWeeks(weeks).remote(true).status(status)
                .build());
    }

    private void proposal(Job job, User freelancer, double bid, int weeks, int quality, int match,
                          boolean aiDrafted, Proposal.Status status, String letter) {
        proposalRepository.save(Proposal.builder()
                .job(job).freelancer(freelancer).coverLetter(letter)
                .bidAmount(BigDecimal.valueOf(bid)).durationWeeks(weeks)
                .status(status).aiQualityScore(quality).matchScore(match).aiDrafted(aiDrafted)
                .build());
    }

    private void review(User author, User subject, Contract contract, Review.Direction dir, int overall,
                        String comment, Integer q, Integer com, Integer rel, Integer dl,
                        Integer rc, Integer pb, Integer ss) {
        reviewRepository.save(Review.builder()
                .contract(contract).author(author).subject(subject).direction(dir)
                .overall(overall).comment(comment)
                .quality(q).communication(com).reliability(rel).deadlineAdherence(dl)
                .requirementClarity(rc).paymentBehavior(pb).scopeStability(ss)
                .build());
    }

    private void serviceListing(User freelancer, String title, String category, Double price,
                                Double rating, int reviews, int days, String packages, String addOns) {
        serviceListingRepository.save(ServiceListing.builder()
                .freelancerId(freelancer.getId()).freelancerName(freelancer.getFullName())
                .title(title).description("Fixed-scope service with defined packages, delivered through milestone escrow.")
                .category(category).startingPrice(price).rating(rating).reviewCount(reviews)
                .deliveryDays(days).packagesJson(packages).addOnsJson(addOns)
                .build());
    }

    private String convKey(Long a, Long b) {
        return Math.min(a, b) + ":" + Math.max(a, b);
    }
}
