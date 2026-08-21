import React from 'react';
import { DeductionDetails, TaxCalculationResult } from '../types/tax';
import { TrendingUp, CheckCircle, AlertTriangle, ArrowRight, ShieldCheck } from 'lucide-react';

interface DeductionOptimizerProps {
  deductions: DeductionDetails;
  setDeductions: React.Dispatch<React.SetStateAction<DeductionDetails>>;
  oldRegimeResult: TaxCalculationResult | null;
}

export const DeductionOptimizer: React.FC<DeductionOptimizerProps> = ({
  deductions,
  setDeductions,
  oldRegimeResult,
}) => {
  const MAX_80C = 150000;
  const MAX_NPS = 50000;
  const MAX_80D_SELF = 25000;
  const MAX_80D_PARENTS = deductions.parentsAreSeniorCitizens ? 50000 : 25000;

  const current80C = Math.min(deductions.section80C || 0, MAX_80C);
  const headroom80C = Math.max(0, MAX_80C - current80C);

  const currentNps = Math.min(deductions.section80CCD1B || 0, MAX_NPS);
  const headroomNps = Math.max(0, MAX_NPS - currentNps);

  const current80DSelf = Math.min(deductions.section80DSelf || 0, MAX_80D_SELF);
  const headroom80DSelf = Math.max(0, MAX_80D_SELF - current80DSelf);

  const current80DParents = Math.min(deductions.section80DParents || 0, MAX_80D_PARENTS);
  const headroom80DParents = Math.max(0, MAX_80D_PARENTS - current80DParents);

  const totalUnclaimedHeadroom = headroom80C + headroomNps + headroom80DSelf + headroom80DParents;

  // Potential tax saving assuming 20% or 30% slab
  const estimatedTaxRate = oldRegimeResult && oldRegimeResult.netTaxableIncome > 1000000 ? 0.312 : 0.208; // including 4% cess
  const potentialTaxSaved = totalUnclaimedHeadroom * estimatedTaxRate;

  const formatCurrency = (val: number) => {
    return '₹' + Math.round(val || 0).toLocaleString('en-IN');
  };

  const handleFillAll = () => {
    setDeductions((prev) => ({
      ...prev,
      section80C: MAX_80C,
      section80CCD1B: MAX_NPS,
      section80DSelf: MAX_80D_SELF,
      section80DParents: MAX_80D_PARENTS,
    }));
  };

  return (
    <div className="glass-panel" style={{ padding: '28px' }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px', flexWrap: 'wrap', gap: '12px' }}>
        <div>
          <h2 style={{ fontSize: '1.35rem', display: 'flex', alignItems: 'center', gap: '10px' }}>
            <TrendingUp size={24} color="var(--accent-emerald)" />
            Deduction Optimizer &amp; Headroom Simulator
          </h2>
          <p style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>
            Identify unclaimed Chapter VI-A investment headroom and quantify additional tax savings.
          </p>
        </div>

        <button className="btn btn-primary btn-sm" onClick={handleFillAll}>
          ⚡ Maximize All Headroom
        </button>
      </div>

      {/* Potential Savings Banner */}
      <div
        style={{
          background: 'linear-gradient(135deg, rgba(16, 185, 129, 0.15) 0%, rgba(6, 182, 212, 0.1) 100%)',
          border: '1px solid rgba(16, 185, 129, 0.3)',
          borderRadius: 'var(--radius-lg)',
          padding: '20px',
          marginBottom: '24px',
          display: 'grid',
          gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))',
          gap: '16px',
        }}
      >
        <div>
          <div style={{ fontSize: '0.75rem', color: 'var(--text-secondary)' }}>Total Unclaimed Headroom</div>
          <div className="mono" style={{ fontSize: '1.75rem', fontWeight: 800, color: 'var(--accent-cyan)' }}>
            {formatCurrency(totalUnclaimedHeadroom)}
          </div>
        </div>
        <div>
          <div style={{ fontSize: '0.75rem', color: 'var(--text-secondary)' }}>Estimated Extra Tax Savings (Old Regime)</div>
          <div className="mono" style={{ fontSize: '1.75rem', fontWeight: 800, color: 'var(--accent-emerald)' }}>
            {formatCurrency(potentialTaxSaved)}
          </div>
        </div>
        <div>
          <div style={{ fontSize: '0.75rem', color: 'var(--text-secondary)' }}>Status</div>
          <div style={{ marginTop: '4px' }}>
            {totalUnclaimedHeadroom === 0 ? (
              <span className="badge badge-emerald">
                <CheckCircle size={14} /> 100% Deductions Maximized
              </span>
            ) : (
              <span className="badge badge-amber">
                <AlertTriangle size={14} /> Optimization Opportunities Available
              </span>
            )}
          </div>
        </div>
      </div>

      {/* Headroom Meters */}
      <div style={{ display: 'flex', flexDirection: 'column', gap: '18px' }}>
        {/* Section 80C */}
        <div style={{ background: 'rgba(255,255,255,0.02)', padding: '16px', borderRadius: 'var(--radius-md)', border: '1px solid var(--border-subtle)' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '8px' }}>
            <div>
              <strong style={{ fontSize: '0.95rem' }}>Section 80C (PPF, ELSS, EPF, Life Insurance, Tuition)</strong>
              <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>Capped at ₹1,50,000</div>
            </div>
            <div className="mono" style={{ fontSize: '0.9rem', fontWeight: 700 }}>
              {formatCurrency(current80C)} / {formatCurrency(MAX_80C)}
            </div>
          </div>
          <div style={{ width: '100%', height: '8px', background: 'rgba(255,255,255,0.1)', borderRadius: '4px', overflow: 'hidden' }}>
            <div
              style={{
                width: `${(current80C / MAX_80C) * 100}%`,
                height: '100%',
                background: 'linear-gradient(90deg, #10b981, #06b6d4)',
                transition: 'width 0.3s ease',
              }}
            />
          </div>
          {headroom80C > 0 && (
            <div style={{ fontSize: '0.75rem', color: 'var(--accent-amber)', marginTop: '6px' }}>
              💡 ₹{headroom80C.toLocaleString('en-IN')} remaining. Investing in ELSS or PPF can save up to {formatCurrency(headroom80C * 0.312)} in taxes.
            </div>
          )}
        </div>

        {/* Section 80CCD(1B) NPS */}
        <div style={{ background: 'rgba(255,255,255,0.02)', padding: '16px', borderRadius: 'var(--radius-md)', border: '1px solid var(--border-subtle)' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '8px' }}>
            <div>
              <strong style={{ fontSize: '0.95rem' }}>Section 80CCD(1B) (Exclusive NPS Tier-1 Deduction)</strong>
              <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>Exclusive additional ₹50,000 deduction over and above 80C</div>
            </div>
            <div className="mono" style={{ fontSize: '0.9rem', fontWeight: 700 }}>
              {formatCurrency(currentNps)} / {formatCurrency(MAX_NPS)}
            </div>
          </div>
          <div style={{ width: '100%', height: '8px', background: 'rgba(255,255,255,0.1)', borderRadius: '4px', overflow: 'hidden' }}>
            <div
              style={{
                width: `${(currentNps / MAX_NPS) * 100}%`,
                height: '100%',
                background: 'linear-gradient(90deg, #8b5cf6, #3b82f6)',
                transition: 'width 0.3s ease',
              }}
            />
          </div>
          {headroomNps > 0 && (
            <div style={{ fontSize: '0.75rem', color: 'var(--accent-cyan)', marginTop: '6px' }}>
              💡 ₹{headroomNps.toLocaleString('en-IN')} NPS headroom available.
            </div>
          )}
        </div>

        {/* Section 80D Health */}
        <div style={{ background: 'rgba(255,255,255,0.02)', padding: '16px', borderRadius: 'var(--radius-md)', border: '1px solid var(--border-subtle)' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '8px' }}>
            <div>
              <strong style={{ fontSize: '0.95rem' }}>Section 80D Health Insurance (Self + Parents)</strong>
              <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>
                Self/Family: ₹25k | Parents: {deductions.parentsAreSeniorCitizens ? '₹50k (Senior)' : '₹25k'}
              </div>
            </div>
            <div className="mono" style={{ fontSize: '0.9rem', fontWeight: 700 }}>
              {formatCurrency(current80DSelf + current80DParents)} / {formatCurrency(MAX_80D_SELF + MAX_80D_PARENTS)}
            </div>
          </div>
          <div style={{ width: '100%', height: '8px', background: 'rgba(255,255,255,0.1)', borderRadius: '4px', overflow: 'hidden' }}>
            <div
              style={{
                width: `${((current80DSelf + current80DParents) / (MAX_80D_SELF + MAX_80D_PARENTS)) * 100}%`,
                height: '100%',
                background: 'linear-gradient(90deg, #ec4899, #f43f5e)',
                transition: 'width 0.3s ease',
              }}
            />
          </div>
        </div>
      </div>
    </div>
  );
};
