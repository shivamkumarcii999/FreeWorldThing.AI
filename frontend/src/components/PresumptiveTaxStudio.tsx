import React, { useState, useEffect } from 'react';
import {
  AssessmentYear,
  PresumptiveSection,
  PresumptiveTaxRequest,
  PresumptiveTaxResult,
  DeductionDetails
} from '../types/tax';
import { taxApi } from '../services/api';
import {
  Briefcase,
  CheckCircle2,
  AlertTriangle,
  Calendar,
  DollarSign,
  Building,
  Sparkles,
  Layers,
  ArrowRight
} from 'lucide-react';

interface PresumptiveTaxStudioProps {
  assessmentYear: AssessmentYear;
  deductions: DeductionDetails;
}

export const PresumptiveTaxStudio: React.FC<PresumptiveTaxStudioProps> = ({
  assessmentYear,
  deductions,
}) => {
  const [section, setSection] = useState<PresumptiveSection>('SECTION_44ADA');
  const [digitalReceipts, setDigitalReceipts] = useState<number>(3000000);
  const [cashReceipts, setCashReceipts] = useState<number>(0);
  const [customProfitPercentage, setCustomProfitPercentage] = useState<number | ''>('');
  const [otherIncome, setOtherIncome] = useState<number>(50000);
  const [result, setResult] = useState<PresumptiveTaxResult | null>(null);
  const [loading, setLoading] = useState<boolean>(false);

  const fetchPresumptive = async () => {
    setLoading(true);
    try {
      const req: PresumptiveTaxRequest = {
        assessmentYear,
        section,
        digitalReceipts: digitalReceipts || 0,
        cashReceipts: cashReceipts || 0,
        customProfitPercentage: customProfitPercentage === '' ? null : Number(customProfitPercentage),
        otherIncome: otherIncome || 0,
        deductions,
      };
      const res = await taxApi.calculatePresumptive(req);
      setResult(res);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchPresumptive();
  }, [assessmentYear, section, digitalReceipts, cashReceipts, customProfitPercentage, otherIncome]);

  const formatCurrency = (val: number) => {
    return '₹' + Math.round(val || 0).toLocaleString('en-IN');
  };

  return (
    <div className="glass-panel" style={{ padding: '28px' }}>
      <div style={{ marginBottom: '24px' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
          <Briefcase size={26} color="var(--accent-cyan)" />
          <h2 style={{ fontSize: '1.4rem' }}>
            Freelancer &amp; Small Business Presumptive Studio
          </h2>
        </div>
        <p style={{ fontSize: '0.85rem', color: 'var(--text-muted)', marginTop: '4px' }}>
          Section 44ADA (Professionals) &amp; Section 44AD (Small Business) • Advance Tax Calendar • GST Threshold
        </p>
      </div>

      {/* Scheme Selector */}
      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '14px', marginBottom: '24px' }}>
        <div
          onClick={() => {
            setSection('SECTION_44ADA');
            setDigitalReceipts(3000000);
          }}
          style={{
            cursor: 'pointer',
            padding: '16px',
            borderRadius: 'var(--radius-md)',
            border: section === 'SECTION_44ADA' ? '2px solid var(--accent-cyan)' : '1px solid var(--border-subtle)',
            background: section === 'SECTION_44ADA' ? 'rgba(6, 182, 212, 0.1)' : 'rgba(255, 255, 255, 0.02)',
            transition: 'var(--transition)',
          }}
        >
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <strong style={{ fontSize: '1rem', color: 'var(--text-primary)' }}>Section 44ADA (Professionals)</strong>
            <span className="badge badge-cyan">Min 50% Profit</span>
          </div>
          <p style={{ fontSize: '0.75rem', color: 'var(--text-muted)', marginTop: '6px' }}>
            Software Engineers, Consultants, Doctors, Lawyers, CA, Architects. Up to ₹75 Lakhs (95%+ digital).
          </p>
        </div>

        <div
          onClick={() => {
            setSection('SECTION_44AD');
            setDigitalReceipts(8000000);
          }}
          style={{
            cursor: 'pointer',
            padding: '16px',
            borderRadius: 'var(--radius-md)',
            border: section === 'SECTION_44AD' ? '2px solid var(--accent-emerald)' : '1px solid var(--border-subtle)',
            background: section === 'SECTION_44AD' ? 'rgba(16, 185, 129, 0.1)' : 'rgba(255, 255, 255, 0.02)',
            transition: 'var(--transition)',
          }}
        >
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <strong style={{ fontSize: '1rem', color: 'var(--text-primary)' }}>Section 44AD (Small Businesses)</strong>
            <span className="badge badge-emerald">6% Digital / 8% Cash</span>
          </div>
          <p style={{ fontSize: '0.75rem', color: 'var(--text-muted)', marginTop: '6px' }}>
            Traders, Retailers, E-commerce sellers, Wholesalers, Manufacturers. Up to ₹3 Crores turnover.
          </p>
        </div>
      </div>

      {/* Input Grid */}
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(220px, 1fr))', gap: '16px', marginBottom: '24px' }}>
        <div className="input-group">
          <label className="input-label">
            <span>Digital Receipts (Bank, UPI, Cards)</span>
            <span className="badge badge-emerald">95%+ Digital Bonus</span>
          </label>
          <div className="input-with-icon">
            <span className="prefix">₹</span>
            <input
              type="number"
              className="input-field mono"
              value={digitalReceipts}
              onChange={(e) => setDigitalReceipts(parseFloat(e.target.value) || 0)}
            />
          </div>
        </div>

        <div className="input-group">
          <label className="input-label">
            <span>Cash Receipts (if any)</span>
          </label>
          <div className="input-with-icon">
            <span className="prefix">₹</span>
            <input
              type="number"
              className="input-field mono"
              value={cashReceipts}
              onChange={(e) => setCashReceipts(parseFloat(e.target.value) || 0)}
            />
          </div>
        </div>

        <div className="input-group">
          <label className="input-label">
            <span>Other Income (Interest, etc.)</span>
          </label>
          <div className="input-with-icon">
            <span className="prefix">₹</span>
            <input
              type="number"
              className="input-field mono"
              value={otherIncome}
              onChange={(e) => setOtherIncome(parseFloat(e.target.value) || 0)}
            />
          </div>
        </div>

        <div className="input-group">
          <label className="input-label">
            <span>Custom Profit % (Optional)</span>
            <span className="badge badge-purple">{section === 'SECTION_44ADA' ? 'Min 50%' : 'Min 6%'}</span>
          </label>
          <input
            type="number"
            className="input-field mono"
            placeholder={section === 'SECTION_44ADA' ? '50' : '6'}
            value={customProfitPercentage}
            onChange={(e) => setCustomProfitPercentage(e.target.value === '' ? '' : parseFloat(e.target.value))}
          />
        </div>
      </div>

      {/* Results Display */}
      {result && (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
          {/* Eligibility & Summary Card */}
          <div
            style={{
              padding: '20px',
              borderRadius: 'var(--radius-lg)',
              background: result.isEligible ? 'rgba(16, 185, 129, 0.08)' : 'rgba(244, 63, 94, 0.08)',
              border: `1px solid ${result.isEligible ? 'rgba(16, 185, 129, 0.3)' : 'rgba(244, 63, 94, 0.3)'}`,
            }}
          >
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: '10px' }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
                {result.isEligible ? (
                  <CheckCircle2 size={24} color="var(--accent-emerald)" />
                ) : (
                  <AlertTriangle size={24} color="var(--accent-rose)" />
                )}
                <div>
                  <h3 style={{ fontSize: '1.1rem' }}>
                    {result.isEligible ? 'Presumptive Scheme Applicable' : 'Turnover Exceeds Presumptive Limit'}
                  </h3>
                  <div style={{ fontSize: '0.8rem', color: 'var(--text-secondary)' }}>
                    {result.eligibilityReason} (Digital share: {result.digitalPercentage.toFixed(1)}%)
                  </div>
                </div>
              </div>

              <div className="badge badge-cyan">
                Threshold: {formatCurrency(result.eligibilityThreshold)}
              </div>
            </div>

            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(180px, 1fr))', gap: '16px', marginTop: '18px', borderTop: '1px solid var(--border-subtle)', paddingTop: '16px' }}>
              <div>
                <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>Deemed Net Profit Rate</div>
                <div className="mono" style={{ fontSize: '1.25rem', fontWeight: 700, color: 'var(--accent-cyan)' }}>
                  {result.deemedProfitRate.toFixed(1)}%
                </div>
              </div>
              <div>
                <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>Deemed Net Profit Amount</div>
                <div className="mono" style={{ fontSize: '1.25rem', fontWeight: 700, color: 'var(--accent-emerald)' }}>
                  {formatCurrency(result.deemedProfitAmount)}
                </div>
              </div>
              <div>
                <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>Recommended Regime</div>
                <div style={{ fontSize: '1.25rem', fontWeight: 700, color: 'var(--text-primary)' }}>
                  {result.recommendedRegime === 'NEW' ? 'New (115BAC)' : 'Old Regime'}
                </div>
              </div>
              <div>
                <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>Total Tax Liability</div>
                <div className="mono" style={{ fontSize: '1.4rem', fontWeight: 800, color: 'var(--accent-emerald)' }}>
                  {formatCurrency(result.taxPayable)}
                </div>
              </div>
            </div>
          </div>

          {/* GST Threshold Warning Card */}
          <div
            style={{
              padding: '16px 20px',
              borderRadius: 'var(--radius-md)',
              background: result.gstRegistrationMandatory ? 'rgba(245, 158, 11, 0.1)' : 'rgba(255, 255, 255, 0.02)',
              border: `1px solid ${result.gstRegistrationMandatory ? 'rgba(245, 158, 11, 0.3)' : 'var(--border-subtle)'}`,
              display: 'flex',
              alignItems: 'center',
              gap: '14px',
            }}
          >
            <Building size={24} color={result.gstRegistrationMandatory ? 'var(--accent-amber)' : 'var(--text-muted)'} />
            <div>
              <div style={{ fontWeight: 600, fontSize: '0.9rem' }}>
                GST Applicability Evaluation ({formatCurrency(result.gstThreshold)} Threshold)
              </div>
              <div style={{ fontSize: '0.8rem', color: 'var(--text-secondary)' }}>
                {result.gstApplicabilityNote}
              </div>
            </div>
          </div>

          {/* Advance Tax Schedule Table */}
          <div>
            <h3 style={{ fontSize: '1.1rem', marginBottom: '12px', display: 'flex', alignItems: 'center', gap: '8px' }}>
              <Calendar size={18} color="var(--accent-cyan)" />
              Quarterly Advance Tax Schedule (Section 211(1)(b))
            </h3>
            <div style={{ overflowX: 'auto' }}>
              <table style={{ width: '100%', borderCollapse: 'collapse', fontSize: '0.85rem' }}>
                <thead>
                  <tr style={{ background: 'rgba(255, 255, 255, 0.04)', borderBottom: '1px solid var(--border-subtle)', textAlign: 'left' }}>
                    <th style={{ padding: '10px 12px', color: 'var(--text-secondary)' }}>Installment</th>
                    <th style={{ padding: '10px 12px', color: 'var(--text-secondary)' }}>Due Date</th>
                    <th style={{ padding: '10px 12px', color: 'var(--text-secondary)' }}>Cumulative Due %</th>
                    <th style={{ padding: '10px 12px', color: 'var(--text-secondary)' }}>Amount Due</th>
                    <th style={{ padding: '10px 12px', color: 'var(--text-secondary)' }}>Compliance Note</th>
                  </tr>
                </thead>
                <tbody>
                  {result.advanceTaxSchedule.map((inst) => (
                    <tr key={inst.installmentNumber} style={{ borderBottom: '1px solid var(--border-subtle)' }}>
                      <td style={{ padding: '10px 12px', fontWeight: 600 }}>Installment #{inst.installmentNumber}</td>
                      <td style={{ padding: '10px 12px' }}>
                        <span className="badge badge-purple">{inst.dueDate}</span>
                      </td>
                      <td style={{ padding: '10px 12px' }}>{inst.cumulativePercentage}%</td>
                      <td className="mono" style={{ padding: '10px 12px', fontWeight: 700, color: inst.installmentAmount > 0 ? 'var(--accent-emerald)' : 'var(--text-muted)' }}>
                        {formatCurrency(inst.installmentAmount)}
                      </td>
                      <td style={{ padding: '10px 12px', color: 'var(--text-secondary)', fontSize: '0.8rem' }}>
                        {inst.note}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
