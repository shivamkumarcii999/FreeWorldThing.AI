import React from 'react';
import { AssessmentYear } from '../types/tax';
import { ShieldCheck, Sparkles, FileSpreadsheet, Briefcase, Calculator, TrendingUp } from 'lucide-react';

interface HeaderProps {
  activeTab: 'calculator' | 'presumptive' | 'optimizer';
  setActiveTab: (tab: 'calculator' | 'presumptive' | 'optimizer') => void;
  assessmentYear: AssessmentYear;
  setAssessmentYear: (ay: AssessmentYear) => void;
  onOpenExplain: () => void;
  onOpenImport: () => void;
}

export const Header: React.FC<HeaderProps> = ({
  activeTab,
  setActiveTab,
  assessmentYear,
  setAssessmentYear,
  onOpenExplain,
  onOpenImport,
}) => {
  return (
    <header className="app-header">
      <div className="brand-logo">
        <div className="brand-icon">
          <Calculator size={26} strokeWidth={2.2} />
        </div>
        <div>
          <div className="brand-title">ClearTaxer</div>
          <div className="brand-subtitle">AI-Powered Deterministic Tax Engine • India</div>
        </div>
      </div>

      <div style={{ display: 'flex', alignItems: 'center', gap: '14px', flexWrap: 'wrap' }}>
        {/* Navigation Tabs */}
        <div className="nav-tabs">
          <button
            className={`nav-tab ${activeTab === 'calculator' ? 'active' : ''}`}
            onClick={() => setActiveTab('calculator')}
          >
            <FileSpreadsheet size={16} />
            Regime Comparator
          </button>
          <button
            className={`nav-tab ${activeTab === 'presumptive' ? 'active' : ''}`}
            onClick={() => setActiveTab('presumptive')}
          >
            <Briefcase size={16} />
            Freelancer / 44ADA & 44AD
          </button>
          <button
            className={`nav-tab ${activeTab === 'optimizer' ? 'active' : ''}`}
            onClick={() => setActiveTab('optimizer')}
          >
            <TrendingUp size={16} />
            Deduction Optimizer
          </button>
        </div>

        {/* Assessment Year Selector */}
        <div style={{ display: 'flex', alignItems: 'center', gap: '6px', background: 'rgba(255,255,255,0.05)', padding: '4px 8px', borderRadius: '12px', border: '1px solid var(--border-subtle)' }}>
          <span style={{ fontSize: '0.75rem', fontWeight: 600, color: 'var(--text-muted)' }}>AY:</span>
          <button
            className={`btn btn-sm ${assessmentYear === '2024-25' ? 'btn-primary' : 'btn-secondary'}`}
            onClick={() => setAssessmentYear('2024-25')}
            title="FY 2023-24 (Current Filing)"
          >
            2024-25
          </button>
          <button
            className={`btn btn-sm ${assessmentYear === '2025-26' ? 'btn-primary' : 'btn-secondary'}`}
            onClick={() => setAssessmentYear('2025-26')}
            title="FY 2024-25 (Budget 2024 Revised)"
          >
            2025-26 <span className="badge badge-cyan" style={{ fontSize: '0.65rem', padding: '1px 4px' }}>Budget '24</span>
          </button>
        </div>

        {/* Form 16 Import */}
        <button className="btn btn-secondary btn-sm" onClick={onOpenImport} title="Import and extract fields from Form 16 / Salary Slip">
          <FileSpreadsheet size={16} color="var(--accent-cyan)" />
          Import Form 16
        </button>

        {/* AI Explain Trigger */}
        <button className="btn btn-ai btn-sm" onClick={onOpenExplain}>
          <Sparkles size={16} />
          AI Tax Explainer
        </button>

        {/* Engine Badge */}
        <div className="badge badge-emerald" title="Deterministic Java CBDT Rule Engine Active">
          <ShieldCheck size={14} />
          <span>CBDT Rule Engine v1.0</span>
        </div>
      </div>
    </header>
  );
};
