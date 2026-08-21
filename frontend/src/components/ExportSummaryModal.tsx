import React from 'react';
import { RegimeComparisonResult } from '../types/tax';
import { Printer, X, Download, ShieldCheck, CheckCircle2 } from 'lucide-react';

interface ExportSummaryModalProps {
  isOpen: boolean;
  onClose: () => void;
  comparison: RegimeComparisonResult | null;
}

export const ExportSummaryModal: React.FC<ExportSummaryModalProps> = ({
  isOpen,
  onClose,
  comparison,
}) => {
  if (!isOpen || !comparison) return null;

  const { oldRegime, newRegime, recommendedRegime, taxSaved, assessmentYear } = comparison;

  const handlePrint = () => {
    window.print();
  };

  const formatCurrency = (val: number) => {
    return '₹' + Math.round(val || 0).toLocaleString('en-IN');
  };

  return (
    <div className="modal-backdrop" onClick={onClose}>
      <div className="modal-content" onClick={(e) => e.stopPropagation()} style={{ maxWidth: '800px', background: '#ffffff', color: '#111827' }}>
        {/* Modal Controls (Hidden in print) */}
        <div className="no-print" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px', borderBottom: '1px solid #e5e7eb', paddingBottom: '12px' }}>
          <div style={{ fontWeight: 700, fontSize: '1.1rem', color: '#111827' }}>
            Tax Computation Summary Statement
          </div>
          <div style={{ display: 'flex', gap: '10px' }}>
            <button className="btn btn-primary btn-sm" onClick={handlePrint}>
              <Printer size={16} /> Print / Save as PDF
            </button>
            <button
              onClick={onClose}
              style={{ background: 'none', border: 'none', color: '#6b7280', cursor: 'pointer' }}
            >
              <X size={22} />
            </button>
          </div>
        </div>

        {/* Printable Document Content */}
        <div style={{ padding: '10px 0', fontFamily: 'var(--font-sans)', color: '#1f2937' }}>
          {/* Header */}
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', borderBottom: '2px solid #10b981', paddingBottom: '14px', marginBottom: '20px' }}>
            <div>
              <h1 style={{ fontSize: '1.5rem', fontWeight: 800, color: '#0f172a' }}>ClearTaxer</h1>
              <div style={{ fontSize: '0.85rem', color: '#64748b' }}>Income Tax Computation &amp; Regime Analysis Sheet</div>
            </div>
            <div style={{ textAlign: 'right' }}>
              <div style={{ fontWeight: 700, fontSize: '0.9rem', color: '#0f172a' }}>Assessment Year: {assessmentYear}</div>
              <div style={{ fontSize: '0.75rem', color: '#64748b' }}>Generated on: {new Date().toLocaleDateString('en-IN', { dateStyle: 'long' })}</div>
            </div>
          </div>

          {/* Recommendation Highlights */}
          <div style={{ background: '#f0fdf4', border: '1px solid #bbf7d0', borderRadius: '8px', padding: '14px 18px', marginBottom: '20px', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <div>
              <div style={{ fontSize: '0.75rem', fontWeight: 700, color: '#15803d', textTransform: 'uppercase' }}>
                Recommended Tax Strategy
              </div>
              <div style={{ fontSize: '1.2rem', fontWeight: 800, color: '#166534', marginTop: '2px' }}>
                {recommendedRegime === 'NEW' ? 'New Tax Regime (Section 115BAC)' : 'Old Tax Regime'}
              </div>
            </div>
            {taxSaved > 0 && (
              <div style={{ textAlign: 'right' }}>
                <div style={{ fontSize: '0.75rem', color: '#15803d' }}>Tax Savings</div>
                <div style={{ fontSize: '1.3rem', fontWeight: 800, color: '#15803d' }}>{formatCurrency(taxSaved)}</div>
              </div>
            )}
          </div>

          {/* Side by Side Detailed Table */}
          <table style={{ width: '100%', borderCollapse: 'collapse', fontSize: '0.85rem', marginBottom: '24px' }}>
            <thead>
              <tr style={{ background: '#f8fafc', borderBottom: '2px solid #cbd5e1', textAlign: 'left' }}>
                <th style={{ padding: '10px', color: '#475569' }}>Particulars</th>
                <th style={{ padding: '10px', textAlign: 'right', color: '#475569' }}>Old Tax Regime (₹)</th>
                <th style={{ padding: '10px', textAlign: 'right', color: '#475569' }}>New Tax Regime (₹)</th>
              </tr>
            </thead>
            <tbody>
              <tr style={{ borderBottom: '1px solid #e2e8f0' }}>
                <td style={{ padding: '8px 10px', fontWeight: 600 }}>Gross Total Income (Salary + Other)</td>
                <td style={{ padding: '8px 10px', textAlign: 'right' }}>{formatCurrency(oldRegime.grossTotalIncome)}</td>
                <td style={{ padding: '8px 10px', textAlign: 'right' }}>{formatCurrency(newRegime.grossTotalIncome)}</td>
              </tr>
              <tr style={{ borderBottom: '1px solid #e2e8f0' }}>
                <td style={{ padding: '8px 10px' }}>Less: Standard Deduction (Section 16(ia))</td>
                <td style={{ padding: '8px 10px', textAlign: 'right', color: '#15803d' }}>-{formatCurrency(oldRegime.standardDeduction)}</td>
                <td style={{ padding: '8px 10px', textAlign: 'right', color: '#15803d' }}>-{formatCurrency(newRegime.standardDeduction)}</td>
              </tr>
              <tr style={{ borderBottom: '1px solid #e2e8f0' }}>
                <td style={{ padding: '8px 10px' }}>Less: Chapter VI-A Deductions (80C, 80D, NPS)</td>
                <td style={{ padding: '8px 10px', textAlign: 'right', color: '#15803d' }}>-{formatCurrency(oldRegime.totalDeductionsAllowed)}</td>
                <td style={{ padding: '8px 10px', textAlign: 'right', color: '#15803d' }}>-{formatCurrency(newRegime.totalDeductionsAllowed)}</td>
              </tr>
              <tr style={{ borderBottom: '2px solid #cbd5e1', background: '#f8fafc', fontWeight: 700 }}>
                <td style={{ padding: '10px' }}>Net Taxable Income (Sec 288A)</td>
                <td style={{ padding: '10px', textAlign: 'right' }}>{formatCurrency(oldRegime.netTaxableIncome)}</td>
                <td style={{ padding: '10px', textAlign: 'right' }}>{formatCurrency(newRegime.netTaxableIncome)}</td>
              </tr>
              <tr style={{ borderBottom: '1px solid #e2e8f0' }}>
                <td style={{ padding: '8px 10px' }}>Income Tax Computed on Slabs</td>
                <td style={{ padding: '8px 10px', textAlign: 'right' }}>{formatCurrency(oldRegime.taxOnSlabs)}</td>
                <td style={{ padding: '8px 10px', textAlign: 'right' }}>{formatCurrency(newRegime.taxOnSlabs)}</td>
              </tr>
              {oldRegime.rebateSection87A > 0 || newRegime.rebateSection87A > 0 ? (
                <tr style={{ borderBottom: '1px solid #e2e8f0' }}>
                  <td style={{ padding: '8px 10px' }}>Less: Tax Rebate under Section 87A</td>
                  <td style={{ padding: '8px 10px', textAlign: 'right', color: '#15803d' }}>-{formatCurrency(oldRegime.rebateSection87A)}</td>
                  <td style={{ padding: '8px 10px', textAlign: 'right', color: '#15803d' }}>-{formatCurrency(newRegime.rebateSection87A)}</td>
                </tr>
              ) : null}
              {oldRegime.surchargeAmount > 0 || newRegime.surchargeAmount > 0 ? (
                <tr style={{ borderBottom: '1px solid #e2e8f0' }}>
                  <td style={{ padding: '8px 10px' }}>Add: High Income Surcharge</td>
                  <td style={{ padding: '8px 10px', textAlign: 'right' }}>+{formatCurrency(oldRegime.surchargeAmount)}</td>
                  <td style={{ padding: '8px 10px', textAlign: 'right' }}>+{formatCurrency(newRegime.surchargeAmount)}</td>
                </tr>
              ) : null}
              <tr style={{ borderBottom: '1px solid #e2e8f0' }}>
                <td style={{ padding: '8px 10px' }}>Add: Health &amp; Education Cess (4%)</td>
                <td style={{ padding: '8px 10px', textAlign: 'right' }}>+{formatCurrency(oldRegime.healthAndEducationCess)}</td>
                <td style={{ padding: '8px 10px', textAlign: 'right' }}>+{formatCurrency(newRegime.healthAndEducationCess)}</td>
              </tr>
              <tr style={{ borderTop: '2px solid #0f172a', background: '#f1f5f9', fontWeight: 800, fontSize: '0.95rem' }}>
                <td style={{ padding: '12px 10px', color: '#0f172a' }}>Total Tax Liability (Rounded Sec 288B)</td>
                <td style={{ padding: '12px 10px', textAlign: 'right', color: recommendedRegime === 'OLD' ? '#15803d' : '#0f172a' }}>
                  {formatCurrency(oldRegime.totalTaxPayable)}
                </td>
                <td style={{ padding: '12px 10px', textAlign: 'right', color: recommendedRegime === 'NEW' ? '#15803d' : '#0f172a' }}>
                  {formatCurrency(newRegime.totalTaxPayable)}
                </td>
              </tr>
              <tr>
                <td style={{ padding: '6px 10px', color: '#64748b', fontSize: '0.75rem' }}>Effective Tax Rate on Gross Income</td>
                <td style={{ padding: '6px 10px', textAlign: 'right', color: '#64748b', fontSize: '0.75rem' }}>{oldRegime.effectiveTaxRate.toFixed(2)}%</td>
                <td style={{ padding: '6px 10px', textAlign: 'right', color: '#64748b', fontSize: '0.75rem' }}>{newRegime.effectiveTaxRate.toFixed(2)}%</td>
              </tr>
            </tbody>
          </table>

          {/* Statutory Footer / Disclaimer */}
          <div style={{ borderTop: '1px solid #e2e8f0', paddingTop: '12px', fontSize: '0.7rem', color: '#64748b', lineHeight: '1.4' }}>
            <strong>Statutory Notice:</strong> This statement has been mathematically calculated by the ClearTaxer deterministic Java rule engine based on official CBDT slabs and provisions. ClearTaxer provides computational assistance and does not submit filings to the Income Tax Department. Taxpayers are advised to verify details with a qualified Chartered Accountant prior to filing.
          </div>
        </div>
      </div>
    </div>
  );
};
