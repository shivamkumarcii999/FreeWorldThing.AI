package com.freeworldthing.ai;

import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Deterministic mock AI engine. Implements the AiGateway with keyword extraction,
 * heuristic scoring and template generation so every AI feature works offline.
 * Deterministic outputs keep the product demo-able and testable; a live LLM
 * implementation of AiGateway can be dropped in via configuration later.
 */
@Component
public class MockAiEngine implements AiGateway {

    // ----- knowledge base: skill keyword map -----
    private static final Map<String, List<String>> SKILL_KEYWORDS = Map.ofEntries(
            Map.entry("Java", List.of("java")),
            Map.entry("Spring Boot", List.of("spring boot", "spring", "rest api", "restful")),
            Map.entry("React", List.of("react", "reactjs")),
            Map.entry("Next.js", List.of("next.js", "nextjs")),
            Map.entry("Angular", List.of("angular")),
            Map.entry("Node.js", List.of("node", "nodejs", "express")),
            Map.entry("Python", List.of("python", "django", "fastapi")),
            Map.entry("PostgreSQL", List.of("postgres", "postgresql", "sql database")),
            Map.entry("MongoDB", List.of("mongo")),
            Map.entry("Flutter", List.of("flutter", "dart")),
            Map.entry("React Native", List.of("react native")),
            Map.entry("Android", List.of("android", "kotlin")),
            Map.entry("iOS", List.of("ios", "swift")),
            Map.entry("AWS", List.of("aws", "amazon web services")),
            Map.entry("Docker", List.of("docker", "container", "kubernetes", "k8s")),
            Map.entry("DevOps", List.of("devops", "ci/cd", "ci cd", "github actions")),
            Map.entry("AI/LLM Integration", List.of("ai", "llm", "gpt", "chatbot", "chat bot", "openai", "anthropic", "claude", "gemini")),
            Map.entry("RAG", List.of("rag", "retrieval augmented", "vector database", "pgvector", "embeddings")),
            Map.entry("Machine Learning", List.of("machine learning", "ml model", "scikit", "tensorflow", "pytorch", "computer vision", "nlp")),
            Map.entry("AI Agents", List.of("agent", "agentic", "autonomous ai", "tool calling")),
            Map.entry("UI/UX Design", List.of("ui", "ux", "design system", "wireframe", "figma", "prototype")),
            Map.entry("Graphic Design", List.of("logo", "branding", "graphic", "illustrator", "photoshop")),
            Map.entry("Video Editing", List.of("video editing", "premiere", "after effects", "youtube video", "reels", "shorts")),
            Map.entry("SEO", List.of("seo", "search engine optimization", "keyword research")),
            Map.entry("Digital Marketing", List.of("marketing", "ads", "campaign", "social media")),
            Map.entry("Data Science", List.of("data science", "analytics", "pandas", "data analysis", "dashboard")),
            Map.entry("Stripe/Payments", List.of("stripe", "razorpay", "payment gateway", "paypal", "payment integration")),
            Map.entry("Blockchain", List.of("blockchain", "solidity", "web3", "smart contract")),
            Map.entry("Cybersecurity", List.of("security audit", "pentest", "penetration", "cybersecurity"))
    );

    private static final Map<String, Double> SKILL_RATE = Map.ofEntries(
            Map.entry("AI/LLM Integration", 1.6), Map.entry("RAG", 1.7), Map.entry("Machine Learning", 1.5),
            Map.entry("AI Agents", 1.7), Map.entry("Blockchain", 1.5), Map.entry("Spring Boot", 1.2),
            Map.entry("React", 1.15), Map.entry("Next.js", 1.2), Map.entry("Flutter", 1.15),
            Map.entry("React Native", 1.15), Map.entry("AWS", 1.3), Map.entry("DevOps", 1.25),
            Map.entry("Cybersecurity", 1.4), Map.entry("Stripe/Payments", 1.2), Map.entry("Data Science", 1.3),
            Map.entry("UI/UX Design", 1.0), Map.entry("Graphic Design", 0.8), Map.entry("Video Editing", 0.75),
            Map.entry("SEO", 0.7), Map.entry("Digital Marketing", 0.75)
    );

    @Override
    public JobSpec analyzeJobIdea(String idea, Map<String, Object> hints) {
        String text = (idea == null ? "" : idea.toLowerCase());
        Map<String, Object> h = hints == null ? Map.of() : hints;

        // 1. skill extraction
        LinkedHashSet<String> skills = new LinkedHashSet<>();
        for (var e : SKILL_KEYWORDS.entrySet()) {
            for (String kw : e.getValue()) {
                if (text.contains(kw)) { skills.add(e.getKey()); break; }
            }
        }
        if (skills.isEmpty()) skills.add("General Development");

        // 2. complexity
        int skillWeight = skills.stream().mapToInt(s -> SKILL_RATE.getOrDefault(s, 1.0) >= 1.3 ? 2 : 1).sum();
        String complexity = skillWeight >= 7 ? "HIGH" : skillWeight >= 4 ? "MEDIUM" : "LOW";

        // 3. duration
        Integer duration = hintInt(h, "durationWeeks");
        if (duration == null) {
            duration = switch (complexity) {
                case "HIGH" -> text.contains("30 days") || text.contains("1 month") ? 4 : 6;
                case "MEDIUM" -> 4;
                default -> 2;
            };
        }

        // 4. budget estimation (INR baseline)
        double base = complexity.equals("HIGH") ? 120_000 : complexity.equals("MEDIUM") ? 60_000 : 20_000;
        Double bMin = hintDouble(h, "budgetMin") != null ? hintDouble(h, "budgetMin") : Math.round(base / 10000.0) * 10000;
        Double bMax = hintDouble(h, "budgetMax") != null ? hintDouble(h, "budgetMax") : Math.round(base * 1.8 / 10000.0) * 10000;

        // 5. title + category
        String title = hintStr(h, "title");
        if (title == null || title.isBlank()) {
            title = titleFromIdea(idea, skills);
        }
        String category = categoryFromSkills(skills);

        // 6. experience level
        String level = hintStr(h, "experienceLevel");
        if (level == null) level = complexity.equals("HIGH") ? "EXPERT" : complexity.equals("MEDIUM") ? "INTERMEDIATE" : "ENTRY";

        // 7. clarifying questions the client should answer before publishing
        List<String> questions = new ArrayList<>();
        if (skills.contains("AI/LLM Integration")) {
            questions.add("Which LLM provider do you prefer (OpenAI, Anthropic, open-source)?");
            questions.add("Does the AI feature need to integrate with WhatsApp, CRM, or another system?");
        }
        if (skills.contains("Stripe/Payments")) questions.add("Which payment methods should be supported (cards, UPI, international)?");
        if (skills.contains("React") || skills.contains("Next.js")) questions.add("Do you need the frontend hosted separately (Vercel/CDN)?");
        if (skills.contains("Flutter") || skills.contains("React Native") || skills.contains("Android") || skills.contains("iOS"))
            questions.add("Which platforms matter most — Android, iOS, or both?");
        if (text.contains("ecommerce") || text.contains("e-commerce") || text.contains("shop") || text.contains("store"))
            questions.add("Roughly how many products/SKUs, and expected monthly traffic?");
        questions.add("Is there a hard deadline we should plan milestones around?");
        if (questions.size() > 5) questions = questions.subList(0, 5);

        return new JobSpec(
                title,
                descriptionFromIdea(idea, skills, complexity),
                category,
                List.copyOf(skills),
                bMin, bMax,
                "INR",
                hintStr(h, "projectType") != null ? hintStr(h, "projectType") : "FIXED",
                level,
                duration,
                complexity,
                planProject(title, idea, List.copyOf(skills)),
                questions
        );
    }

    @Override
    public MatchResult matchFreelancer(FreelancerCard f, JobSpec job) {
        Set<String> jobSkills = new HashSet<>(job.requiredSkills());
        Map<String, SkillEvidence> fSkills = f.skills().stream()
                .collect(Collectors.toMap(SkillEvidence::name, s -> s, (a, b) -> a));

        // component 1: skill coverage (0-45)
        List<String> matched = new ArrayList<>(), gaps = new ArrayList<>();
        double covered = 0;
        for (String s : jobSkills) {
            SkillEvidence ev = fSkills.get(s);
            if (ev != null && ev.level() >= 2) { covered++; matched.add(s); }
            else gaps.add(s);
        }
        double coverage = jobSkills.isEmpty() ? 0.5 : covered / jobSkills.size();
        int skillScore = (int) Math.round(coverage * 45);

        // component 2: evidence strength (0-20) — verified skills with real evidence
        double evidence = f.skills().stream()
                .filter(s -> matched.contains(s.name()))
                .mapToInt(s -> s.verified() ? s.level() : s.level() / 2)
                .average().orElse(0);
        int evidenceScore = (int) Math.round(Math.min(evidence / 5.0, 1.0) * 20);

        // component 3: reputation (0-15)
        double rating = f.ratingAvg() == null ? 3.5 : f.ratingAvg();
        int jobs = f.completedProjects() == null ? 0 : f.completedProjects();
        int repScore = (int) Math.round(((rating - 3.0) / 2.0 * 0.7 + Math.min(jobs / 20.0, 1.0) * 0.3) * 15);

        // component 4: availability (0-10)
        int availScore = switch (f.availability() == null ? "" : f.availability()) {
            case "AVAILABLE" -> 10;
            case "WITHIN_WEEK" -> 6;
            default -> 2;
        };

        // component 5: budget compatibility (0-10)
        int budgetScore = 7; // neutral default when rates unknown
        if (f.hourlyRate() != null && job.budgetMin() != null && job.durationWeeks() != null) {
            double estCost = f.hourlyRate() * 25 * job.durationWeeks(); // ~25h/week
            budgetScore = estCost <= job.budgetMax() ? 10 : estCost <= job.budgetMax() * 1.3 ? 6 : 2;
        }

        int total = Math.min(100, skillScore + evidenceScore + repScore + availScore + budgetScore);

        List<String> reasons = new ArrayList<>();
        if (!matched.isEmpty()) reasons.add("Covers " + matched.size() + "/" + jobSkills.size() + " required skills: " + String.join(", ", matched));
        if (evidenceScore >= 12) reasons.add("Verified skill evidence on the exact tech stack");
        if (repScore >= 10) reasons.add(String.format("Strong track record — %.1f★ across %d projects", rating, jobs));
        if (availScore == 10) reasons.add("Available now");
        if (budgetScore == 10) reasons.add("Rate fits the budget");

        return new MatchResult(f.id(), total, Math.round(coverage * 100) + "%", reasons, gaps);
    }

    @Override
    public String generateProposal(FreelancerCard f, JobSpec job, String extraContext) {
        String skills = job.requiredSkills().stream().limit(4).collect(Collectors.joining(", "));
        String myEvidence = f.skills().stream()
                .filter(s -> job.requiredSkills().contains(s.name()) && s.level() >= 3)
                .map(s -> s.name() + (s.verified() ? " (verified)" : ""))
                .collect(Collectors.joining(", "));

        StringBuilder sb = new StringBuilder();
        sb.append("Hi,\n\n");
        sb.append("I read your brief on \"").append(job.title()).append("\" carefully. You're looking for ")
          .append(skills).append(" work delivered in about ").append(job.durationWeeks()).append(" weeks");
        if (job.budgetMax() != null) sb.append(", within your budget range");
        sb.append(".\n\n");
        if (!myEvidence.isEmpty()) {
            sb.append("This is squarely in my wheelhouse: I have hands-on experience with ").append(myEvidence)
              .append(", with completed projects and client work as evidence on my profile.\n\n");
        } else {
            sb.append("My background lines up well with this project — see the relevant work on my profile.\n\n");
        }
        sb.append("My approach:\n");
        sb.append("1. Kick-off & scoping — confirm requirements, define success criteria and milestones.\n");
        sb.append("2. Architecture & setup — repo, environments, and a walking skeleton end-to-end.\n");
        sb.append("3. Core build — deliver in weekly increments you can see and test.\n");
        sb.append("4. Hardening — testing, polish, documentation, and deployment support.\n\n");
        if (extraContext != null && !extraContext.isBlank()) sb.append(extraContext.trim()).append("\n\n");
        sb.append("Timeline: ~").append(job.durationWeeks()).append(" weeks with a milestone review at each stage, so you always know where things stand.\n\n");
        sb.append("Happy to jump on a quick call to walk through the plan.\n\n");
        sb.append("Best,\n").append(f.fullName());
        return sb.toString();
    }

    @Override
    public ProposalReview reviewProposal(String jobDescription, String proposalText) {
        String p = proposalText.toLowerCase();
        String j = jobDescription == null ? "" : jobDescription.toLowerCase();
        List<String> strengths = new ArrayList<>();
        List<String> improvements = new ArrayList<>();

        int score = 40;
        if (p.length() > 300) { score += 8; strengths.add("Good depth — not a generic one-liner."); }
        else improvements.add("Too short. Explain your approach in 2-3 concrete steps.");

        boolean mentionsSkill = SKILL_KEYWORDS.keySet().stream().anyMatch(s -> p.contains(s.toLowerCase()));
        if (mentionsSkill) { score += 12; strengths.add("References relevant skills from the job."); }
        else improvements.add("Name the specific technologies you'd use — clients scan for this.");

        boolean evidence = p.contains("built") || p.contains("delivered") || p.contains("experience") || p.contains("portfolio") || p.contains("project");
        if (evidence) { score += 12; strengths.add("Points to past work as evidence."); }
        else improvements.add("Add proof: link or mention a similar project you've delivered.");

        boolean pricing = p.contains("₹") || p.contains("price") || p.contains("budget") || p.contains("cost") || p.contains("milestone");
        if (pricing) { score += 10; strengths.add("Pricing/milestone expectations addressed."); }
        else improvements.add("Be explicit about price or milestone structure.");

        boolean timeline = p.contains("week") || p.contains("day") || p.contains("timeline") || p.contains("deliver");
        if (timeline) { score += 10; strengths.add("Gives a timeline."); }
        else improvements.add("State an estimated delivery timeline.");

        boolean tooGeneric = p.contains("i am the best") || p.contains("hire me");
        if (tooGeneric) { score -= 10; improvements.add("Remove generic sales lines — lead with substance."); }

        // check overlap with job requirements vocabulary
        long overlap = Arrays.stream(j.split("\\W+")).filter(w -> w.length() > 4 && p.contains(w)).distinct().count();
        if (overlap >= 6) { score += 8; strengths.add("Directly answers the job's requirements."); }
        else improvements.add("Mirror the client's key requirements more explicitly.");

        return new ProposalReview(Math.max(5, Math.min(98, score)), strengths, improvements);
    }

    @Override
    public List<MilestonePlan> planProject(String title, String description, List<String> skills) {
        Set<String> s = new HashSet<>(skills == null ? List.of() : skills);
        List<MilestonePlan> plan = new ArrayList<>();
        plan.add(new MilestonePlan("Discovery & Architecture", "Requirements lock-in, technical design, repo and environment setup.", 0.15));
        if (s.contains("AI/LLM Integration") || s.contains("RAG") || s.contains("Machine Learning") || s.contains("AI Agents"))
            plan.add(new MilestonePlan("AI/Backend Core", "Backend services, data model and the core AI pipeline (prompts, retrieval, evaluation).", 0.30));
        else
            plan.add(new MilestonePlan("Backend & Database", "API layer, database schema, business logic and integrations.", 0.30));
        plan.add(new MilestonePlan("Frontend & Integration", "UI build, API integration, third-party services.", 0.30));
        plan.add(new MilestonePlan("Testing & Deployment", "QA pass, bug fixes, deployment, handover documentation.", 0.25));
        return plan;
    }

    // ---------- helpers ----------

    private String titleFromIdea(String idea, Set<String> skills) {
        String clean = idea == null ? "" : idea.replaceAll("\\s+", " ").trim();
        if (clean.length() > 80) clean = clean.substring(0, 77) + "...";
        String lead = skills.contains("AI/LLM Integration") ? "AI " : "";
        return Character.toUpperCase(clean.charAt(0)) + clean.substring(1) + (clean.toLowerCase().startsWith("build") || clean.toLowerCase().startsWith("create") ? "" : " — " + lead + "Project");
    }

    private String descriptionFromIdea(String idea, Set<String> skills, String complexity) {
        return idea + "\n\n— AI-drafted specification —\n" +
                "Skills identified: " + String.join(", ", skills) + "\n" +
                "Estimated complexity: " + complexity + "\n" +
                "Suggested milestones and budget generated automatically. Review and edit before publishing.";
    }

    private String categoryFromSkills(Set<String> skills) {
        if (skills.contains("AI/LLM Integration") || skills.contains("RAG") || skills.contains("Machine Learning") || skills.contains("AI Agents")) return "AI_ML";
        if (skills.contains("UI/UX Design") || skills.contains("Graphic Design")) return "DESIGN";
        if (skills.contains("Video Editing")) return "CREATIVE";
        if (skills.contains("SEO") || skills.contains("Digital Marketing")) return "MARKETING";
        if (skills.contains("Data Science")) return "DATA";
        return "DEVELOPMENT";
    }

    private Integer hintInt(Map<String, Object> h, String k) {
        Object v = h.get(k);
        return v instanceof Number n ? n.intValue() : null;
    }

    private Double hintDouble(Map<String, Object> h, String k) {
        Object v = h.get(k);
        return v instanceof Number n ? n.doubleValue() : null;
    }

    private String hintStr(Map<String, Object> h, String k) {
        Object v = h.get(k);
        return v instanceof String s ? s : null;
    }
}
