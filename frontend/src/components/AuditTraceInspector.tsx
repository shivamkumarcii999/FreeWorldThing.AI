import React from 'react';
import { TaxCalculationResult } from '../types/tax';
import { CheckCircle, ShieldCheck, FileText, Layers } from 'lucide-react';

interface AuditTraceInspectorProps {
  calculation: TaxCalculationResult;
}

export const AuditTraceInspector: React.FC<AuditTraceInspectorProps> = ({ calculation }) => {
  const formatCurrency = (val: number) => {
    return '₹' + (val || 0).toLocaleString('en-IN', { maximumFractionDigits: 0 });
  };

  return (
    <div className="glass-panel" style={{ padding: '24px', marginTop: '20px' }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '18px', flexWrap: 'wrap', gap: '10px' }}>
        <div>
          <h3 style={{ fontSize: '1.15rem', display: 'flex', alignItems: 'center', gap: '8px' }}>
            <Layers size={20} color="var(--accent-cyan)" />
            Audit Trace &amp; Slab Breakdown ({calculation.regime} Regime • {calculation.assessmentYear})
          </h3>
          <p style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>
            Deterministic rule engine execution trace • {calculation.ruleEngineVersion}
          </p>
        </div>

        <div className="badge badge-emerald">
          <ShieldCheck size={14} />
          <span>CBDT Verified Trace</span>
        </div>
      </div>

      {/* Progressive Slabs Breakdown Table */}
      <div style={{ marginBottom: '22px' }}>
        <h4 style={{ fontSize: '0.9rem', color: 'var(--text-secondary)', marginBottom: '10px' }}>
          1. Progressive Income Tax Slabs
        </h4>
        <div style={{ overflowX: 'auto' }}>
          <table style={{ width: '100%', borderCollapse: 'collapse', fontSize: '0.85rem' }}>
            <thead>
              <tr style={{ background: 'rgba(255, 255, 255, 0.04)', borderBottom: '1px solid var(--border-subtle)', textAlign: 'left' }}>
                <th style={{ padding: '10px 12px', color: 'var(--text-secondary)' }}>Slab Range</th>
                <th style={{ padding: '10px 12px', color: 'var(--text-secondary)' }}>Taxable Slice</th>
                <th style={{ padding: '10px 12px', color: 'var(--text-secondary)' }}>Tax Rate</th>
                <th style={{ padding: '10px 12px', color: 'var(--text-secondary)', textAlign: 'right' }}>Tax for Slab</th>
              </tr>
            </thead>
            <tbody>
              {calculation.slabBreakdowns.map((slab, idx) => (
                <tr
                  key={idx}
                  style={{
                    borderBottom: '1px solid var(--border-subtle)',
                    background: slab.taxableAmountInSlab > 0 ? 'rgba(255, 255, 255, 0.015)' : 'transparent',
                  }}
                >
                  <td style={{ padding: '10px 12px', fontWeight: 600 }}>{slab.slabRange}</td>
                  <td className="mono" style={{ padding: '10px 12px' }}>{formatCurrency(slab.taxableAmountInSlab)}</td>
                  <td style={{ padding: '10px 12px' }}>
                    <span className="badge" style={{ background: slab.ratePercentage > 0 ? 'rgba(245, 158, 11, 0.15)' : 'rgba(255, 255, 255, 0.05)', color: slab.ratePercentage > 0 ? '#fbbf24' : 'var(--text-muted)' }}>
                      {slab.ratePercentage}%
                    </span>
                  </td>
                  <td className="mono" style={{ padding: '10px 12px', textAlign: 'right', fontWeight: 600 }}>
                    {formatCurrency(slab.taxForSlab)}
                  </td>
                </tr>
              ))}
              <tr style={{ background: 'rgba(0, 0, 0, 0.3)', fontWeight: 700 }}>
                <td colSpan={3} style={{ padding: '10px 12px' }}>Total Tax on Slabs (before rebate/cess)</td>
                <td className="mono" style={{ padding: '10px 12px', textAlign: 'right', color: 'var(--accent-emerald)' }}>
                  {formatCurrency(calculation.taxOnSlabs)}
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      {/* Step-by-Step Rule Engine Execution Trace */}
      <div>
        <h4 style={{ fontSize: '0.9rem', color: 'var(--text-secondary)', marginBottom: '10px' }}>
          2. Complete Audit Trace Steps
        </h4>
        <div style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
          {calculation.auditTrace.map((step) => (
            <div
              key={step.stepNumber}
              style={{
                display: 'flex',
                alignItems: 'flex-start',
                justifyContent: 'space-between',
                padding: '12px 16px',
                background: 'rgba(255, 255, 255, 0.02)',
                border: '1px solid var(--border-subtle)',
                borderRadius: 'var(--radius-md)',
                gap: '12px',
              }}
            >
              <div style={{ display: 'flex', alignItems: 'flex-start', gap: '12px' }}>
                <div
                  style={{
                    width: '24px',
                    height: '24px',
                    borderRadius: '50%',
                    background: 'rgba(16, 185, 129, 0.15)',
                    color: '#34d399',
                    fontSize: '0.75rem',
                    fontWeight: 700,
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center',
                    flexShrink: 0,
                    marginTop: '2px',
                  }}
                >
                  {step.stepNumber}
                </div>
                <div>
                  <div style={{ display: 'flex', alignItems: 'center', gap: '8px', flexWrap: 'wrap' }}>
                    <span style={{ fontWeight: 600, color: 'var(--text-primary)', fontSize: '0.9rem' }}>
                      {step.stepName}
                    </span>
                    <span className="badge badge-purple" style={{ fontSize: '0.7rem' }}>
                      {step.ruleReference}
                    </span>
                  </div>
                  <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)', marginTop: '2px' }}>
                    {step.description}
                  </div>
                </div>
              </div>

              <div className="mono" style={{ textAlign: 'right', fontWeight: 600, fontSize: '0.9rem', flexShrink: 0 }}>
                {step.formattedDetails}
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};
