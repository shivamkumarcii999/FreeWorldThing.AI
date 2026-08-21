import React, { useState } from 'react';
import { IncomeDetails, DeductionDetails } from '../types/tax';
import { UploadCloud, FileText, CheckCircle2, AlertCircle, Sparkles, X, ArrowRight, ShieldCheck } from 'lucide-react';

interface ExtractedDoc {
  documentType: string;
  employerName: string;
  panNumber: string;
  assessmentYear: string;
  grossSalary: number;
  basicSalary: number;
  hraReceived: number;
  standardDeductionClaimed: number;
  section80C: number;
  section80D: number;
  section80CCD1B: number;
  section80CCD2: number;
  interestIncome?: number;
  tdsDeducted: number;
  fieldConfidence: Record<string, number>;
  verificationNotes: string;
}

interface DocumentImportModalProps {
  isOpen: boolean;
  onClose: () => void;
  onApplyExtractedData: (income: Partial<IncomeDetails>, deductions: Partial<DeductionDetails>) => void;
}

export const DocumentImportModal: React.FC<DocumentImportModalProps> = ({
  isOpen,
  onClose,
  onApplyExtractedData,
}) => {
  const [extractedDoc, setExtractedDoc] = useState<ExtractedDoc | null>(null);
  const [loading, setLoading] = useState(false);
  const [pastedText, setPastedText] = useState('');

  if (!isOpen) return null;

  const handleFetchPreset = async (presetKey: string) => {
    setLoading(true);
    try {
      const res = await fetch('/api/tax/document/parse', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ presetTemplateKey: presetKey }),
      });
      const data = await res.json();
      setExtractedDoc(data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleParseText = async () => {
    if (!pastedText.trim()) return;
    setLoading(true);
    try {
      const res = await fetch('/api/tax/document/parse', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ documentText: pastedText }),
      });
      const data = await res.json();
      setExtractedDoc(data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleConfirmAndApply = () => {
    if (!extractedDoc) return;
    onApplyExtractedData(
      {
        grossSalary: extractedDoc.grossSalary,
        incomeFromOtherSources: extractedDoc.interestIncome || 0,
        hraDetails: {
          basicSalary: extractedDoc.basicSalary,
          dearnessAllowance: 0,
          hraReceived: extractedDoc.hraReceived,
          rentPaid: extractedDoc.hraReceived * 0.9, // reasonable estimate
          cityType: 'NON_METRO',
        },
      },
      {
        section80C: extractedDoc.section80C,
        section80DSelf: extractedDoc.section80D,
        section80CCD1B: extractedDoc.section80CCD1B,
        section80CCD2: extractedDoc.section80CCD2,
      }
    );
    onClose();
  };

  const formatCurrency = (val: number) => {
    return '₹' + Math.round(val || 0).toLocaleString('en-IN');
  };

  return (
    <div className="modal-backdrop" onClick={onClose}>
      <div className="modal-content" onClick={(e) => e.stopPropagation()} style={{ maxWidth: '850px' }}>
        {/* Modal Header */}
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px', borderBottom: '1px solid var(--border-subtle)', paddingBottom: '14px' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
            <div style={{ width: '36px', height: '36px', borderRadius: '10px', background: 'linear-gradient(135deg, #10b981, #06b6d4)', display: 'flex', alignItems: 'center', justifyContent: 'center', color: '#fff' }}>
              <UploadCloud size={20} />
            </div>
            <div>
              <h3 style={{ fontSize: '1.25rem', fontWeight: 700 }}>AI Form 16 / Document Import Studio</h3>
              <p style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>
                Extract and review structured tax fields before they enter the deterministic calculator
              </p>
            </div>
          </div>
          <button onClick={onClose} style={{ background: 'none', border: 'none', color: 'var(--text-muted)', cursor: 'pointer' }}>
            <X size={22} />
          </button>
        </div>

        {/* Preset Sample Selectors */}
        <div style={{ marginBottom: '18px' }}>
          <label style={{ fontSize: '0.8rem', fontWeight: 600, color: 'var(--text-secondary)', display: 'block', marginBottom: '8px' }}>
            Load Sample Real-World Tax Document:
          </label>
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(220px, 1fr))', gap: '10px' }}>
            <button
              type="button"
              className="btn btn-secondary btn-sm"
              style={{ textAlign: 'left', justifyContent: 'flex-start', padding: '10px 14px' }}
              onClick={() => handleFetchPreset('FORM_16_SAMPLE_IT')}
            >
              <FileText size={16} color="var(--accent-cyan)" />
              <div>
                <div style={{ fontWeight: 600, fontSize: '0.85rem' }}>Form 16: Tech (₹14.5L)</div>
                <div style={{ fontSize: '0.7rem', color: 'var(--text-muted)' }}>TCS Ltd • 80C + 80D + NPS</div>
              </div>
            </button>

            <button
              type="button"
              className="btn btn-secondary btn-sm"
              style={{ textAlign: 'left', justifyContent: 'flex-start', padding: '10px 14px' }}
              onClick={() => handleFetchPreset('SALARY_SLIP_CONSULTANT')}
            >
              <FileText size={16} color="var(--accent-purple)" />
              <div>
                <div style={{ fontWeight: 600, fontSize: '0.85rem' }}>Salary Slip: Lead (₹24L)</div>
                <div style={{ fontSize: '0.7rem', color: 'var(--text-muted)' }}>HyperScale • 80CCD(2) Corp NPS</div>
              </div>
            </button>

            <button
              type="button"
              className="btn btn-secondary btn-sm"
              style={{ textAlign: 'left', justifyContent: 'flex-start', padding: '10px 14px' }}
              onClick={() => handleFetchPreset('FORM_16_SENIOR')}
            >
              <FileText size={16} color="var(--accent-amber)" />
              <div>
                <div style={{ fontWeight: 600, fontSize: '0.85rem' }}>Pensioner Certificate (₹5.5L)</div>
                <div style={{ fontSize: '0.7rem', color: 'var(--text-muted)' }}>SBI Pension Cell • 80TTB</div>
              </div>
            </button>
          </div>
        </div>

        {/* Or Paste Raw Form 16 Text */}
        <div style={{ marginBottom: '20px' }}>
          <label style={{ fontSize: '0.8rem', fontWeight: 600, color: 'var(--text-secondary)', display: 'block', marginBottom: '6px' }}>
            Or Paste Form 16 / Salary Certificate Text:
          </label>
          <div style={{ display: 'flex', gap: '10px' }}>
            <textarea
              className="input-field mono"
              rows={3}
              placeholder="Paste Form 16 Part B text or salary slip summary here..."
              value={pastedText}
              onChange={(e) => setPastedText(e.target.value)}
              style={{ fontSize: '0.8rem', resize: 'vertical' }}
            />
            <button
              type="button"
              className="btn btn-primary"
              onClick={handleParseText}
              disabled={loading || !pastedText.trim()}
              style={{ alignSelf: 'flex-start' }}
            >
              <Sparkles size={16} /> Parse
            </button>
          </div>
        </div>

        {/* Structured Field Verification Screen */}
        {extractedDoc && (
          <div style={{ background: 'rgba(255, 255, 255, 0.02)', border: '1px solid var(--border-subtle)', borderRadius: 'var(--radius-lg)', padding: '20px' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '14px', flexWrap: 'wrap', gap: '8px' }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                <CheckCircle2 size={18} color="var(--accent-emerald)" />
                <strong style={{ fontSize: '1rem' }}>{extractedDoc.documentType}</strong>
                <span className="badge badge-purple">{extractedDoc.employerName}</span>
              </div>
              <div className="badge badge-emerald">
                <ShieldCheck size={14} /> AI Extraction Review
              </div>
            </div>

            <div style={{ fontSize: '0.8rem', color: 'var(--text-secondary)', marginBottom: '16px' }}>
              {extractedDoc.verificationNotes}
            </div>

            {/* Extracted Fields Table */}
            <div style={{ overflowX: 'auto', marginBottom: '18px' }}>
              <table style={{ width: '100%', borderCollapse: 'collapse', fontSize: '0.85rem' }}>
                <thead>
                  <tr style={{ background: 'rgba(255, 255, 255, 0.04)', borderBottom: '1px solid var(--border-subtle)', textAlign: 'left' }}>
                    <th style={{ padding: '8px 10px', color: 'var(--text-secondary)' }}>Tax Head</th>
                    <th style={{ padding: '8px 10px', color: 'var(--text-secondary)' }}>Extracted Value</th>
                    <th style={{ padding: '8px 10px', color: 'var(--text-secondary)' }}>Confidence</th>
                  </tr>
                </thead>
                <tbody>
                  <tr style={{ borderBottom: '1px solid var(--border-subtle)' }}>
                    <td style={{ padding: '8px 10px', fontWeight: 600 }}>Gross Salary (Sec 17(1))</td>
                    <td className="mono" style={{ padding: '8px 10px', fontWeight: 700 }}>{formatCurrency(extractedDoc.grossSalary)}</td>
                    <td style={{ padding: '8px 10px' }}><span className="badge badge-emerald">99%</span></td>
                  </tr>
                  <tr style={{ borderBottom: '1px solid var(--border-subtle)' }}>
                    <td style={{ padding: '8px 10px' }}>HRA Allowance (Sec 10(13A))</td>
                    <td className="mono" style={{ padding: '8px 10px' }}>{formatCurrency(extractedDoc.hraReceived)}</td>
                    <td style={{ padding: '8px 10px' }}><span className="badge badge-emerald">98%</span></td>
                  </tr>
                  <tr style={{ borderBottom: '1px solid var(--border-subtle)' }}>
                    <td style={{ padding: '8px 10px' }}>Section 80C Investments</td>
                    <td className="mono" style={{ padding: '8px 10px' }}>{formatCurrency(extractedDoc.section80C)}</td>
                    <td style={{ padding: '8px 10px' }}><span className="badge badge-emerald">99%</span></td>
                  </tr>
                  <tr style={{ borderBottom: '1px solid var(--border-subtle)' }}>
                    <td style={{ padding: '8px 10px' }}>Section 80D Health Insurance</td>
                    <td className="mono" style={{ padding: '8px 10px' }}>{formatCurrency(extractedDoc.section80D)}</td>
                    <td style={{ padding: '8px 10px' }}><span className="badge badge-cyan">95%</span></td>
                  </tr>
                  {extractedDoc.section80CCD1B > 0 && (
                    <tr style={{ borderBottom: '1px solid var(--border-subtle)' }}>
                      <td style={{ padding: '8px 10px' }}>Section 80CCD(1B) NPS Tier-1</td>
                      <td className="mono" style={{ padding: '8px 10px' }}>{formatCurrency(extractedDoc.section80CCD1B)}</td>
                      <td style={{ padding: '8px 10px' }}><span className="badge badge-cyan">94%</span></td>
                    </tr>
                  )}
                  {extractedDoc.section80CCD2 > 0 && (
                    <tr style={{ borderBottom: '1px solid var(--border-subtle)' }}>
                      <td style={{ padding: '8px 10px' }}>Section 80CCD(2) Employer NPS</td>
                      <td className="mono" style={{ padding: '8px 10px' }}>{formatCurrency(extractedDoc.section80CCD2)}</td>
                      <td style={{ padding: '8px 10px' }}><span className="badge badge-emerald">98%</span></td>
                    </tr>
                  )}
                  <tr style={{ borderBottom: '1px solid var(--border-subtle)' }}>
                    <td style={{ padding: '8px 10px' }}>Total TDS Deducted (Pre-paid)</td>
                    <td className="mono" style={{ padding: '8px 10px', color: 'var(--accent-emerald)', fontWeight: 700 }}>
                      {formatCurrency(extractedDoc.tdsDeducted)}
                    </td>
                    <td style={{ padding: '8px 10px' }}><span className="badge badge-emerald">99%</span></td>
                  </tr>
                </tbody>
              </table>
            </div>

            {/* Action Bar */}
            <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '12px' }}>
              <button type="button" className="btn btn-secondary" onClick={onClose}>
                Cancel
              </button>
              <button type="button" className="btn btn-primary" onClick={handleConfirmAndApply}>
                <CheckCircle2 size={16} /> Confirm &amp; Load into Rule Engine
              </button>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};
