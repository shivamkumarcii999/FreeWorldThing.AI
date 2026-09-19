# FreeWorldThing.AI (FWT AI) 🚀

> **Autonomous AI-Powered Freelance & Services Marketplace Ecosystem**  
> Smarter Job Scoping, Verified Proof-of-Work Matchmaking, Proposal Copilot, and Real-Time AI Milestone Escrow.

---

## 🌟 Overview

**FreeWorldThing AI** revolutionizes modern remote talent collaboration by infusing autonomous AI agents across the entire project lifecycle:

- 🧠 **AI Job Spec Architect**: Converts natural language project concepts into structured milestone plans, tech stacks, and budget distributions.
- 🎯 **Proof-of-Work Matchmaker**: Matches clients with verified talent using git-level code proof, test coverage metrics, and semantic skill embeddings.
- ✍️ **AI Proposal Copilot & Reviewer**: Drafts personalized proposals citing real delivered projects and scores bid quality (0–100).
- 🛡️ **Autonomous Project Health Monitor**: Continuous scanning of milestone deliverables, escrow release conditions, and sprint blockers.
- 🔌 **Pluggable AI Gateway**: Multi-provider LLM abstraction (Mock Engine, OpenAI, Anthropic, Gemini) with zero vendor lock-in.

---

## 🛠️ Architecture & Tech Stack

### Frontend
- **Framework**: React 18 with TypeScript & Vite
- **Styling**: Tailwind CSS & Glassmorphism Design System
- **Icons & UI**: Lucide React, Dynamic Modals, Real-Time Kanban Workspace

### Backend
- **Framework**: Java 17 / Spring Boot 3
- **Security**: JWT Authentication & Spring Security
- **Data & Persistence**: Spring Data JPA with PostgreSQL / H2 Database
- **AI Orchestration**: `AiGateway`, `AiOrchestrator`, `MockAiEngine`, `AiJobBuilderService`

---

## 🚀 Getting Started

### Prerequisites
- **Node.js**: v18+
- **Java JDK**: 17+
- **Maven**: 3.8+

### 1. Run Backend Server
```bash
cd backend
mvn spring-boot:run
```
*Backend runs on `http://localhost:8080`*

### 2. Run Frontend Client
```bash
cd frontend
npm install
npm run dev
```
*Frontend runs on `http://localhost:5173`*

---

## 📡 Key API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/ai/job-spec` | Generates structured milestone job spec from text prompt |
| `POST` | `/api/ai/proposal-draft` | Creates proof-grounded proposal draft for freelancers |
| `GET` | `/api/ai/workspace-health/{id}` | Real-time AI health & blocker audit for project workspace |
| `POST` | `/api/ai/job-analyze` | NLP feature extraction and skill breakdown |
| `POST` | `/api/ai/review-proposal` | Instant quality feedback and score for proposal text |

---

## 📄 License
MIT © FreeWorldThing.AI
