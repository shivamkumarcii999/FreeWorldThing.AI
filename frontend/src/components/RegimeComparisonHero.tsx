import React from 'react';
import { RegimeComparisonResult, TaxRegime } from '../types/tax';
import { CheckCircle2, AlertCircle, ArrowRight, Sparkles, Printer, Info } from 'lucide-react';

interface RegimeComparisonHeroProps {
  comparison: RegimeComparisonResult | null;
  selectedRegimeForAudit: TaxRegime;
  setSelectedRegimeForAudit: (regime: TaxRegime) => void;
  onOpenExplain: () => void;
  onOpenExport: () => void;
}

export const RegimeComparisonHero: React.FC<RegimeComparisonHeroProps> = ({
  comparison,
  selectedRegimeForAudit,
  setSelectedRegimeForAudit,
  onOpenExplain,
  onOpenExport,
}) => {
  if (!comparison) {
    return (
      <div className="glass-panel" style={{ padding: '32px', textAlign: 'center' }}>
        <p style={{ color: 'var(--text-muted)' }}>Enter income details to compute regime comparison...</p>
      </div>
    );
  }

  const { oldRegime, newRegime, recommendedRegime, taxSaved, recommendationSummary } = comparison;

  const formatCurrency = (val: number) => {
    return '₹' + (val || 0).toLocaleString('en-IN', { maximumFractionDigits: 0 });
  };

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
      {/* Recommendation Banner */}
      <div
        className="glass-panel pulse"
        style={{
          padding: '20px 24px',
          background:
            recommendedRegime === 'NEW'
              ? 'linear-gradient(135deg, rgba(16, 185, 129, 0.15) 0%, rgba(6, 182, 212, 0.1) 100%)'
              : 'linear-gradient(135deg, rgba(139, 92, 246, 0.15) 0%, rgba(59, 130, 246, 0.1) 100%)',
          borderColor: recommendedRegime === 'NEW' ? 'rgba(16, 185, 129, 0.4)' : 'rgba(139, 92, 246, 0.4)',
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center',
          flexWrap: 'wrap',
          gap: '16px',
        }}
      >
        <div style={{ display: 'flex', alignItems: 'center', gap: '14px' }}>
          <div
            style={{
              width: '44px',
              height: '44px',
              borderRadius: '50%',
              background: recommendedRegime === 'NEW' ? 'var(--accent-emerald)' : 'var(--accent-purple)',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              color: '#ffffff',
            }}
          >
            <CheckCircle2 size={26} />
          </div>
          <div>
            <div style={{ fontSize: '0.8rem', textTransform: 'uppercase', letterSpacing: '0.05em', color: 'var(--text-secondary)' }}>
              Deterministic Recommendation
            </div>
            <div style={{ fontSize: '1.25rem', fontWeight: 800, color: 'var(--text-primary)' }}>
              {recommendedRegime === 'NEW' ? 'New Tax Regime (Sec 115BAC)' : 'Old Tax Regime'}{' '}
              {taxSaved > 0 && (
                <span className="badge badge-emerald" style={{ fontSize: '0.85rem', marginLeft: '6px' }}>
                  Saves {formatCurrency(taxSaved)}
                </span>
              )}
            </div>
            <div style={{ fontSize: '0.85rem', color: 'var(--text-secondary)', marginTop: '2px' }}>
              {recommendationSummary}
            </div>
          </div>
        </div>

        <div style={{ display: 'flex', gap: '10px' }}>
          <button className="btn btn-ai btn-sm" onClick={onOpenExplain}>
            <Sparkles size={16} />
            Ask AI Why
          </button>
          <button className="btn btn-secondary btn-sm no-print" onClick={onOpenExport}>
            <Printer size={16} />
            Export / Print
          </button>
        </div>
      </div>

      {/* Side-by-side Regime Comparison Grid */}
      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '20px' }}>
        {/* NEW REGIME CARD */}
        <div
          className="glass-panel"
          style={{
            padding: '24px',
            position: 'relative',
            border: recommendedRegime === 'NEW' ? '2px solid var(--accent-emerald)' : '1px solid var(--border-subtle)',
            boxShadow: recommendedRegime === 'NEW' ? 'var(--shadow-glow)' : 'var(--shadow-sm)',
          }}
        >
          {recommendedRegime === 'NEW' && (
            <div
              style={{
                position: 'absolute',
                top: '-12px',
                right: '20px',
                background: 'var(--accent-emerald)',
                color: '#ffffff',
                fontSize: '0.75rem',
                fontWeight: 700,
                padding: '2px 10px',
                borderRadius: '9999px',
                boxShadow: '0 2px 8px rgba(16, 185, 129, 0.4)',
              }}
            >
              RECOMMENDED
            </div>
          )}

          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '14px' }}>
            <div>
              <h3 style={{ fontSize: '1.2rem', color: 'var(--text-primary)' }}>New Tax Regime</h3>
              <p style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>Section 115BAC • Concessional Slabs</p>
            </div>
            <span className="badge badge-cyan">Default Regime</span>
          </div>

          {/* Tax Payable Hero Value */}
          <div style={{ margin: '16px 0', padding: '16px', background: 'rgba(0,0,0,0.25)', borderRadius: 'var(--radius-md)' }}>
            <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>Total Tax Payable (incl. Cess)</div>
            <div className="mono" style={{ fontSize: '2rem', fontWeight: 800, color: 'var(--accent-emerald)', marginTop: '4px' }}>
              {formatCurrency(newRegime.totalTaxPayable)}
            </div>
            <div style={{ fontSize: '0.75rem', color: 'var(--text-secondary)', marginTop: '4px' }}>
              Effective Tax Rate: <strong style={{ color: 'var(--text-primary)' }}>{newRegime.effectiveTaxRate.toFixed(2)}%</strong>
            </div>
          </div>

          {/* Breakdown Items */}
          <div style={{ display: 'flex', flexDirection: 'column', gap: '8px', fontSize: '0.85rem' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between' }}>
              <span style={{ color: 'var(--text-secondary)' }}>Gross Total Income:</span>
              <span className="mono" style={{ fontWeight: 600 }}>{formatCurrency(newRegime.grossTotalIncome)}</span>
            </div>
            <div style={{ display: 'flex', justifyContent: 'space-between' }}>
              <span style={{ color: 'var(--text-secondary)' }}>Standard Deduction (16(ia)):</span>
              <span className="mono" style={{ color: '#34d399' }}>-{formatCurrency(newRegime.standardDeduction)}</span>
            </div>
            <div style={{ display: 'flex', justifyContent: 'space-between' }}>
              <span style={{ color: 'var(--text-secondary)' }}>Permissible Deductions (80CCD(2)):</span>
              <span className="mono" style={{ color: '#34d399' }}>-{formatCurrency(newRegime.totalDeductionsAllowed)}</span>
            </div>
            <div style={{ display: 'flex', justifyContent: 'space-between', borderTop: '1px solid var(--border-subtle)', paddingTop: '6px' }}>
              <span style={{ fontWeight: 600, color: 'var(--text-primary)' }}>Net Taxable Income:</span>
              <span className="mono" style={{ fontWeight: 700 }}>{formatCurrency(newRegime.netTaxableIncome)}</span>
            </div>
            <div style={{ display: 'flex', justifyContent: 'space-between' }}>
              <span style={{ color: 'var(--text-secondary)' }}>Tax on Slabs:</span>
              <span className="mono">{formatCurrency(newRegime.taxOnSlabs)}</span>
            </div>
            {newRegime.rebateSection87A > 0 && (
              <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                <span style={{ color: '#34d399' }}>Rebate Sec 87A:</span>
                <span className="mono" style={{ color: '#34d399' }}>-{formatCurrency(newRegime.rebateSection87A)}</span>
              </div>
            )}
            {newRegime.surchargeAmount > 0 && (
              <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                <span style={{ color: 'var(--accent-amber)' }}>Surcharge ({newRegime.surchargeRate * 100}%):</span>
                <span className="mono" style={{ color: 'var(--accent-amber)' }}>+{formatCurrency(newRegime.surchargeAmount)}</span>
              </div>
            )}
            <div style={{ display: 'flex', justifyContent: 'space-between' }}>
              <span style={{ color: 'var(--text-secondary)' }}>Health &amp; Education Cess (4%):</span>
              <span className="mono">+{formatCurrency(newRegime.healthAndEducationCess)}</span>
            </div>
          </div>

          <button
            type="button"
            className={`btn btn-sm ${selectedRegimeForAudit === 'NEW' ? 'btn-primary' : 'btn-secondary'}`}
            style={{ width: '100%', marginTop: '18px' }}
            onClick={() => setSelectedRegimeForAudit('NEW')}
          >
            Inspect New Regime Math Trace
          </button>
        </div>

        {/* OLD REGIME CARD */}
        <div
          className="glass-panel"
          style={{
            padding: '24px',
            position: 'relative',
            border: recommendedRegime === 'OLD' ? '2px solid var(--accent-purple)' : '1px solid var(--border-subtle)',
            boxShadow: recommendedRegime === 'OLD' ? 'var(--shadow-cyan-glow)' : 'var(--shadow-sm)',
          }}
        >
          {recommendedRegime === 'OLD' && (
            <div
              style={{
                position: 'absolute',
                top: '-12px',
                right: '20px',
                background: 'var(--accent-purple)',
                color: '#ffffff',
                fontSize: '0.75rem',
                fontWeight: 700,
                padding: '2px 10px',
                borderRadius: '9999px',
                boxShadow: '0 2px 8px rgba(139, 92, 246, 0.4)',
              }}
            >
              RECOMMENDED
            </div>
          )}

          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '14px' }}>
            <div>
              <h3 style={{ fontSize: '1.2rem', color: 'var(--text-primary)' }}>Old Tax Regime</h3>
              <p style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>Chapter VI-A &amp; HRA Deductions</p>
            </div>
            <span className="badge badge-purple">Traditional</span>
          </div>

          {/* Tax Payable Hero Value */}
          <div style={{ margin: '16px 0', padding: '16px', background: 'rgba(0,0,0,0.25)', borderRadius: 'var(--radius-md)' }}>
            <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>Total Tax Payable (incl. Cess)</div>
            <div className="mono" style={{ fontSize: '2rem', fontWeight: 800, color: 'var(--accent-purple)', marginTop: '4px' }}>
              {formatCurrency(oldRegime.totalTaxPayable)}
            </div>
            <div style={{ fontSize: '0.75rem', color: 'var(--text-secondary)', marginTop: '4px' }}>
              Effective Tax Rate: <strong style={{ color: 'var(--text-primary)' }}>{oldRegime.effectiveTaxRate.toFixed(2)}%</strong>
            </div>
          </div>

          {/* Breakdown Items */}
          <div style={{ display: 'flex', flexDirection: 'column', gap: '8px', fontSize: '0.85rem' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between' }}>
              <span style={{ color: 'var(--text-secondary)' }}>Gross Total Income:</span>
              <span className="mono" style={{ fontWeight: 600 }}>{formatCurrency(oldRegime.grossTotalIncome)}</span>
            </div>
            <div style={{ display: 'flex', justifyContent: 'space-between' }}>
              <span style={{ color: 'var(--text-secondary)' }}>Standard Deduction (16(ia)):</span>
              <span className="mono" style={{ color: '#34d399' }}>-{formatCurrency(oldRegime.standardDeduction)}</span>
            </div>
            <div style={{ display: 'flex', justifyContent: 'space-between' }}>
              <span style={{ color: 'var(--text-secondary)' }}>Chapter VI-A Deductions:</span>
              <span className="mono" style={{ color: '#34d399' }}>-{formatCurrency(oldRegime.totalDeductionsAllowed)}</span>
            </div>
            <div style={{ display: 'flex', justifyContent: 'space-between', borderTop: '1px solid var(--border-subtle)', paddingTop: '6px' }}>
              <span style={{ fontWeight: 600, color: 'var(--text-primary)' }}>Net Taxable Income:</span>
              <span className="mono" style={{ fontWeight: 700 }}>{formatCurrency(oldRegime.netTaxableIncome)}</span>
            </div>
            <div style={{ display: 'flex', justifyContent: 'space-between' }}>
              <span style={{ color: 'var(--text-secondary)' }}>Tax on Slabs:</span>
              <span className="mono">{formatCurrency(oldRegime.taxOnSlabs)}</span>
            </div>
            {oldRegime.rebateSection87A > 0 && (
              <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                <span style={{ color: '#34d399' }}>Rebate Sec 87A:</span>
                <span className="mono" style={{ color: '#34d399' }}>-{formatCurrency(oldRegime.rebateSection87A)}</span>
              </div>
            )}
            {oldRegime.surchargeAmount > 0 && (
              <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                <span style={{ color: 'var(--accent-amber)' }}>Surcharge ({oldRegime.surchargeRate * 100}%):</span>
                <span className="mono" style={{ color: 'var(--accent-amber)' }}>+{formatCurrency(oldRegime.surchargeAmount)}</span>
              </div>
            )}
            <div style={{ display: 'flex', justifyContent: 'space-between' }}>
              <span style={{ color: 'var(--text-secondary)' }}>Health &amp; Education Cess (4%):</span>
              <span className="mono">+{formatCurrency(oldRegime.healthAndEducationCess)}</span>
            </div>
          </div>

          <button
            type="button"
            className={`btn btn-sm ${selectedRegimeForAudit === 'OLD' ? 'btn-primary' : 'btn-secondary'}`}
            style={{ width: '100%', marginTop: '18px' }}
            onClick={() => setSelectedRegimeForAudit('OLD')}
          >
            Inspect Old Regime Math Trace
          </button>
        </div>
      </div>
    </div>
  );
};
