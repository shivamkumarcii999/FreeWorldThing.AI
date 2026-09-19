package com.freeworldthing.service;

import com.freeworldthing.dto.AiJobSpecResponse;
import com.freeworldthing.dto.SuggestedMilestone;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AiJobBuilderService {

    public AiJobSpecResponse parsePromptToJobSpec(String prompt, String preferredCurrency) {
        String currency = (preferredCurrency != null && !preferredCurrency.isBlank()) ? preferredCurrency : "INR";
        String lower = prompt != null ? prompt.toLowerCase() : "";

        String title;
        String category;
        List<String> requiredSkills = new ArrayList<>();
        double minBudget;
        double maxBudget;
        int durationWeeks;
        String complexity;
        String projectType = "Fixed Price";
        List<SuggestedMilestone> milestones = new ArrayList<>();

        if (lower.contains("rag") || lower.contains("llm") || lower.contains("langchain") || lower.contains("chatbot") || lower.contains("ai")) {
            category = "AI/ML";
            title = "Production RAG AI Agent & Knowledge Base Integration";
            requiredSkills = Arrays.asList("Python", "LangChain", "FastAPI", "Vector DB (Pinecone/Chroma)", "OpenAI/Gemini API", "React");
            minBudget = currency.equals("USD") ? 1200.0 : 85000.0;
            maxBudget = currency.equals("USD") ? 3500.0 : 250000.0;
            durationWeeks = 4;
            complexity = "Expert";

            milestones.add(SuggestedMilestone.builder()
                    .sequenceOrder(1)
                    .title("Architecture & Vector Pipeline Setup")
                    .description("Ingestion pipeline, document chunking, embeddings generation, and vector index setup.")
                    .suggestedAmount(minBudget * 0.25)
                    .estimatedDays(5)
                    .build());
            milestones.add(SuggestedMilestone.builder()
                    .sequenceOrder(2)
                    .title("Core RAG Engine & Tool Calling")
                    .description("LangChain/LlamaIndex retrieval pipeline, hybrid search, context reranking, and citation engine.")
                    .suggestedAmount(minBudget * 0.35)
                    .estimatedDays(8)
                    .build());
            milestones.add(SuggestedMilestone.builder()
                    .sequenceOrder(3)
                    .title("FastAPI Backend & Security Layer")
                    .description("Streaming responses, user rate limiting, JWT auth, and error fallbacks.")
                    .suggestedAmount(minBudget * 0.20)
                    .estimatedDays(5)
                    .build());
            milestones.add(SuggestedMilestone.builder()
                    .sequenceOrder(4)
                    .title("Frontend UI & Production Deployment")
                    .description("Interactive streaming chat UI with React, dark mode, citation badges, and Docker/Cloud deployment.")
                    .suggestedAmount(minBudget * 0.20)
                    .estimatedDays(6)
                    .build());
        } else if (lower.contains("mobile") || lower.contains("flutter") || lower.contains("react native") || lower.contains("ios") || lower.contains("android")) {
            category = "Mobile";
            title = "Cross-Platform Mobile App with Offline Sync & Push Notifications";
            requiredSkills = Arrays.asList("React Native", "TypeScript", "Redux Toolkit", "Node.js", "Firebase", "REST APIs");
            minBudget = currency.equals("USD") ? 1500.0 : 95000.0;
            maxBudget = currency.equals("USD") ? 4000.0 : 280000.0;
            durationWeeks = 6;
            complexity = "Intermediate";

            milestones.add(SuggestedMilestone.builder().sequenceOrder(1).title("UI/UX Screens & Navigation").description("Figma to React Native screens, theme setup, responsive layouts.").suggestedAmount(minBudget * 0.3).estimatedDays(7).build());
            milestones.add(SuggestedMilestone.builder().sequenceOrder(2).title("API Integration & Offline Storage").description("State management, offline SQLite/MMKV cache, and REST endpoints.").suggestedAmount(minBudget * 0.4).estimatedDays(10).build());
            milestones.add(SuggestedMilestone.builder().sequenceOrder(3).title("Testing, Push Notifications & App Store Prep").description("End-to-end testing, deep linking, build artifacts for iOS & Android.").suggestedAmount(minBudget * 0.3).estimatedDays(7).build());
        } else if (lower.contains("design") || lower.contains("ui") || lower.contains("ux") || lower.contains("figma")) {
            category = "UI/UX";
            title = "Complete SaaS Product Design System & Interactive Prototypes";
            requiredSkills = Arrays.asList("Figma", "UI/UX Design", "Design Systems", "Prototyping", "User Research");
            minBudget = currency.equals("USD") ? 800.0 : 50000.0;
            maxBudget = currency.equals("USD") ? 2200.0 : 150000.0;
            durationWeeks = 3;
            complexity = "Intermediate";

            milestones.add(SuggestedMilestone.builder().sequenceOrder(1).title("Wireframes & User Flow").description("Information architecture, user journey maps, wireframes.").suggestedAmount(minBudget * 0.3).estimatedDays(5).build());
            milestones.add(SuggestedMilestone.builder().sequenceOrder(2).title("Design System & High-Fidelity UI").description("Component library, typography, dark/light themes, 15+ core screens.").suggestedAmount(minBudget * 0.5).estimatedDays(8).build());
            milestones.add(SuggestedMilestone.builder().sequenceOrder(3).title("Interactive Prototype & Dev Handoff").description("Clickable prototype, micro-interactions, developer asset export.").suggestedAmount(minBudget * 0.2).estimatedDays(4).build());
        } else {
            category = "Full Stack";
            title = "Modern Full Stack Web Application (Spring Boot + React + Cloud)";
            requiredSkills = Arrays.asList("Java", "Spring Boot 3", "React", "TypeScript", "PostgreSQL", "Docker");
            minBudget = currency.equals("USD") ? 1000.0 : 70000.0;
            maxBudget = currency.equals("USD") ? 3000.0 : 200000.0;
            durationWeeks = 4;
            complexity = "Intermediate";

            milestones.add(SuggestedMilestone.builder().sequenceOrder(1).title("Database Schema & REST API Endpoints").description("JPA entities, flyway migrations, CRUD APIs, and auth token generation.").suggestedAmount(minBudget * 0.35).estimatedDays(6).build());
            milestones.add(SuggestedMilestone.builder().sequenceOrder(2).title("Frontend Dashboard & State Management").description("React components, responsive tables, forms with validation, API hooks.").suggestedAmount(minBudget * 0.40).estimatedDays(9).build());
            milestones.add(SuggestedMilestone.builder().sequenceOrder(3).title("Testing, Dockerization & CI/CD Pipeline").description("Unit/integration tests, Docker compose, production build and deployment guide.").suggestedAmount(minBudget * 0.25).estimatedDays(5).build());
        }

        String description = "### Project Overview\n" +
                "We are seeking an experienced specialist to build **" + title + "**.\n\n" +
                "### Detailed Requirements:\n" +
                "- Based on user specification: \"" + prompt + "\"\n" +
                "- High performance, robust architecture, and clean testable code.\n" +
                "- Thorough documentation and milestone-based deliverables verified on FreeWorldThing AI.\n\n" +
                "### Required Tech Stack:\n" +
                "- " + String.join(", ", requiredSkills) + "\n\n" +
                "### Deliverable Guidelines:\n" +
                "- Tested GitHub repository with clear commit history.\n" +
                "- Fully documented API / interactive walkthrough.\n" +
                "- Live demo URL or automated verification suite.";

        return AiJobSpecResponse.builder()
                .title(title)
                .description(description)
                .category(category)
                .requiredSkills(requiredSkills)
                .minBudget(minBudget)
                .maxBudget(maxBudget)
                .budgetCurrency(currency)
                .estimatedDurationWeeks(durationWeeks)
                .complexity(complexity)
                .projectType(projectType)
                .suggestedMilestones(milestones)
                .aiConfidenceScore(0.94)
                .build();
    }
}
