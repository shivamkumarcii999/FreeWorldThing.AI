# ClearTaxer — AI-Powered Tax Computation Platform (India) 🇮🇳

> **Core Design Principle:** *AI explains, the rule engine calculates.*  
> Deterministic, auditable, and mathematically accurate Indian Income Tax computation engine with zero calculation hallucinations, integrated with an AI explanation and Form 16 document parsing layer.

---

## 🌟 Key Features

### 1. ⚖️ Deterministic Multi-Regime Tax Engine (Java 17/21)
- **Assessment Year Versioning:** Complete support for **AY 2024-25** (FY 2023-24) and **AY 2025-26** (FY 2024-25 Budget 2024 updates).
- **Old vs New Regime (Section 115BAC):** Side-by-side comparison with automated optimal recommendation and tax savings calculation.
- **Section 87A Rebate & Marginal Relief:** 100% tax rebate for taxable income up to ₹7,00,000 (New Regime) / ₹5,00,000 (Old Regime) + marginal relief for income slightly exceeding ₹7 Lakhs.
- **Surcharge & Cess:** High-income surcharge tiers with marginal relief and 4% Health & Education Cess.
- **Chapter VI-A Deductions:** Section 10(13A) HRA, 80C, 80D (Self & Parents with Senior Citizen tiers), 80CCD(1B) NPS, 80CCD(2) Employer NPS, 80E, 80TTA/80TTB, Section 24(b) Home Loan Interest.

### 2. 💼 Freelancer & Small Business Presumptive Studio (44ADA & 44AD)
- **Section 44ADA (Professionals):** 50% deemed profit calculation on gross receipts up to ₹75 Lakhs (95%+ digital).
- **Section 44AD (Small Business):** 6% (digital) / 8% (cash) deemed profit calculation on turnover up to ₹3 Crores.
- **Quarterly Advance Tax Schedule:** 4-quarter installment breakdown (15% June, 45% Sept, 75% Dec, 100% March) tailored for presumptive filers under Section 211(1)(b).
- **GST Applicability Check:** Threshold evaluation (₹20L services / ₹40L goods).

### 3. 🔍 CBDT Verifiable Audit Trace Inspector
- Step-by-step statutory execution trace referencing Income Tax Act sections (Sec 14, 16(ia), 87A, 115BAC, 288A, 288B).
- Progressive tax slab slicing table with slice tax computations.

### 4. 📄 AI Form 16 & Document Import Studio
- Structured field extraction for Form 16 Part B, salary slips, and 26AS certificates.
- Interactive user confirmation screen with field confidence scores before entering the calculation pipeline.

### 5. 🤖 Socratic AI Tax Assistant & Headroom Optimizer
- Plain-language narrative grounded strictly in calculation traces.
- Interactive scenario Q&A simulator ("Why New Regime?", "What if I invest ₹50k in NPS?").
- Visual deduction headroom meters for 80C, 80D, and NPS.

### 6. 🖨️ Export & Print Summary
- Formatted, print-ready tax computation sheet for personal record keeping and CA consultation.

---

## 🏗️ Architecture

```
[React 18 + TypeScript Frontend] (Port 5173)
      │
      ▼ REST API
[Spring Boot 3.3 Application Layer] (Port 8080)
      ├──▶ [Deterministic Java Rule Engine] (AY 2024-25 & AY 2025-26, CBDT test-backed)
      ├──▶ [AI Document Parser & Explainer Service] (Pre-calculation confirmation gate)
      └──▶ [Statutory Audit Trace Generator] (Sections 14, 16, 80C-80U, 87A, 115BAC, 288A/B)
```

---

## 🚀 Quick Start Guide

### Prerequisites
- **Java**: OpenJDK 17 or higher
- **Maven**: 3.9+
- **Node.js**: v18+ & npm

### 1. Run Backend (Spring Boot)
```bash
cd backend
mvn clean test
mvn spring-boot:run
```
*Backend API starts at `http://localhost:8080`*

### 2. Run Frontend (React + Vite)
```bash
cd frontend
npm install
npm run dev
```
*Frontend UI starts at `http://localhost:5173`*

---

## 🧪 Testing

Run CBDT unit tests matching official worked examples:
```bash
cd backend
mvn test
```

---

## ⚖️ Disclaimer

*ClearTaxer is an educational, deterministic tax computation platform. It calculates and explains tax liabilities based on official CBDT rules, but does not submit returns to the Income Tax Department. Taxpayers should consult a qualified Chartered Accountant for formal tax filings.*
