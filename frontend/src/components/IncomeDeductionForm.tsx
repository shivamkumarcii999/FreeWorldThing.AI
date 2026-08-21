import React, { useState } from 'react';
import {
  AgeCategory,
  IncomeDetails,
  DeductionDetails,
  MetroType,
  HraDetails
} from '../types/tax';
import {
  Coins,
  Home,
  Shield,
  Building,
  Sparkles,
  ChevronDown,
  ChevronUp,
  HelpCircle
} from 'lucide-react';

interface IncomeDeductionFormProps {
  ageCategory: AgeCategory;
  setAgeCategory: (age: AgeCategory) => void;
  income: IncomeDetails;
  setIncome: React.Dispatch<React.SetStateAction<IncomeDetails>>;
  deductions: DeductionDetails;
  setDeductions: React.Dispatch<React.SetStateAction<DeductionDetails>>;
  onLoadPreset: (presetKey: string) => void;
}

export const IncomeDeductionForm: React.FC<IncomeDeductionFormProps> = ({
  ageCategory,
  setAgeCategory,
  income,
  setIncome,
  deductions,
  setDeductions,
  onLoadPreset,
}) => {
  const [showHraCalculator, setShowHraCalculator] = useState(false);
  const [showAdvancedDeductions, setShowAdvancedDeductions] = useState(false);

  const handleIncomeChange = (field: keyof IncomeDetails, value: number) => {
    setIncome((prev) => ({ ...prev, [field]: Math.max(0, value || 0) }));
  };

  const handleDeductionChange = (field: keyof DeductionDetails, value: any) => {
    setDeductions((prev) => ({ ...prev, [field]: value }));
  };

  const handleHraChange = (field: keyof HraDetails, value: any) => {
    const currentHra = income.hraDetails || {
      basicSalary: income.grossSalary * 0.5,
      dearnessAllowance: 0,
      hraReceived: 0,
      rentPaid: 0,
      cityType: 'NON_METRO',
    };
    const updated = { ...currentHra, [field]: value };
    setIncome((prev) => ({ ...prev, hraDetails: updated }));
  };

  return (
    <div className="glass-panel" style={{ padding: '24px' }}>
      {/* Header & Sample Presets */}
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '18px', flexWrap: 'wrap', gap: '10px' }}>
        <h2 style={{ fontSize: '1.25rem', display: 'flex', alignItems: 'center', gap: '8px' }}>
          <Coins size={20} color="var(--accent-emerald)" />
          Income &amp; Deductions
        </h2>

        {/* Quick Presets */}
        <div style={{ display: 'flex', gap: '6px', flexWrap: 'wrap' }}>
          <button
            type="button"
            className="btn btn-secondary btn-sm"
            onClick={() => onLoadPreset('7.5L_REBATE')}
            title="Salaried ₹7.5L: Zero tax via 87A rebate"
          >
            ⚡ ₹7.5L Zero-Tax
          </button>
          <button
            type="button"
            className="btn btn-secondary btn-sm"
            onClick={() => onLoadPreset('12L_STANDARD')}
            title="Salaried ₹12L: 80C + 80D + NPS"
          >
            ⚡ ₹12L Deductions
          </button>
          <button
            type="button"
            className="btn btn-secondary btn-sm"
            onClick={() => onLoadPreset('60L_HNI')}
            title="₹60L Income: Surcharge & Marginal Relief"
          >
            ⚡ ₹60L High Net Worth
          </button>
          <button
            type="button"
            className="btn btn-secondary btn-sm"
            onClick={() => onLoadPreset('SENIOR_6L')}
            title="Senior Citizen ₹6L + 80TTB"
          >
            ⚡ Senior Citizen
          </button>
        </div>
      </div>

      {/* Age Category Selector */}
      <div className="input-group">
        <label className="input-label">
          <span>Taxpayer Category / Age</span>
        </label>
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: '8px' }}>
          <button
            type="button"
            className={`btn btn-sm ${ageCategory === 'BELOW_60' ? 'btn-primary' : 'btn-secondary'}`}
            onClick={() => setAgeCategory('BELOW_60')}
          >
            &lt; 60 Years
          </button>
          <button
            type="button"
            className={`btn btn-sm ${ageCategory === 'SENIOR_60_TO_80' ? 'btn-primary' : 'btn-secondary'}`}
            onClick={() => setAgeCategory('SENIOR_60_TO_80')}
          >
            60 - 80 (Senior)
          </button>
          <button
            type="button"
            className={`btn btn-sm ${ageCategory === 'SUPER_SENIOR_ABOVE_80' ? 'btn-primary' : 'btn-secondary'}`}
            onClick={() => setAgeCategory('SUPER_SENIOR_ABOVE_80')}
          >
            &gt; 80 (Super Senior)
          </button>
        </div>
      </div>

      <hr style={{ borderColor: 'var(--border-subtle)', margin: '18px 0' }} />

      {/* Primary Income Heads */}
      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '14px' }}>
        <div className="input-group">
          <label className="input-label">
            <span>Gross Salary Income</span>
            <span className="badge badge-emerald">Sec 15-17</span>
          </label>
          <div className="input-with-icon">
            <span className="prefix">₹</span>
            <input
              type="number"
              className="input-field mono"
              placeholder="e.g. 1200000"
              value={income.grossSalary || ''}
              onChange={(e) => handleIncomeChange('grossSalary', parseFloat(e.target.value))}
            />
          </div>
        </div>

        <div className="input-group">
          <label className="input-label">
            <span>Income from Other Sources</span>
            <span className="badge badge-cyan">Interest / Div</span>
          </label>
          <div className="input-with-icon">
            <span className="prefix">₹</span>
            <input
              type="number"
              className="input-field mono"
              placeholder="e.g. 50000"
              value={income.incomeFromOtherSources || ''}
              onChange={(e) => handleIncomeChange('incomeFromOtherSources', parseFloat(e.target.value))}
            />
          </div>
        </div>
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '14px' }}>
        <div className="input-group">
          <label className="input-label">
            <span>Capital Gains (STCG / LTCG)</span>
          </label>
          <div className="input-with-icon">
            <span className="prefix">₹</span>
            <input
              type="number"
              className="input-field mono"
              placeholder="0"
              value={income.longTermCapitalGains || ''}
              onChange={(e) => handleIncomeChange('longTermCapitalGains', parseFloat(e.target.value))}
            />
          </div>
        </div>

        <div className="input-group">
          <label className="input-label">
            <span>House Property Income / Loss</span>
            <span className="badge badge-amber">Rental / Loan</span>
          </label>
          <div className="input-with-icon">
            <span className="prefix">₹</span>
            <input
              type="number"
              className="input-field mono"
              placeholder="0 or negative"
              value={income.incomeFromHouseProperty || ''}
              onChange={(e) => setIncome((prev) => ({ ...prev, incomeFromHouseProperty: parseFloat(e.target.value) || 0 }))}
            />
          </div>
        </div>
      </div>

      {/* HRA Exemption Calculator Expansion */}
      <div style={{ marginBottom: '16px', background: 'rgba(255, 255, 255, 0.02)', border: '1px solid var(--border-subtle)', borderRadius: 'var(--radius-md)', padding: '12px' }}>
        <button
          type="button"
          onClick={() => setShowHraCalculator(!showHraCalculator)}
          style={{ width: '100%', background: 'none', border: 'none', color: 'var(--text-primary)', display: 'flex', justifyContent: 'space-between', alignItems: 'center', cursor: 'pointer' }}
        >
          <span style={{ fontSize: '0.9rem', fontWeight: 600, display: 'flex', alignItems: 'center', gap: '8px' }}>
            <Home size={16} color="var(--accent-cyan)" />
            HRA Exemption Calculator (Section 10(13A))
          </span>
          {showHraCalculator ? <ChevronUp size={16} /> : <ChevronDown size={16} />}
        </button>

        {showHraCalculator && (
          <div style={{ marginTop: '14px', display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '12px' }}>
            <div className="input-group">
              <label className="input-label">Basic Salary + DA (Annual)</label>
              <div className="input-with-icon">
                <span className="prefix">₹</span>
                <input
                  type="number"
                  className="input-field mono"
                  placeholder="e.g. 600000"
                  value={income.hraDetails?.basicSalary || ''}
                  onChange={(e) => handleHraChange('basicSalary', parseFloat(e.target.value) || 0)}
                />
              </div>
            </div>
            <div className="input-group">
              <label className="input-label">HRA Received (Annual)</label>
              <div className="input-with-icon">
                <span className="prefix">₹</span>
                <input
                  type="number"
                  className="input-field mono"
                  placeholder="e.g. 240000"
                  value={income.hraDetails?.hraReceived || ''}
                  onChange={(e) => handleHraChange('hraReceived', parseFloat(e.target.value) || 0)}
                />
              </div>
            </div>
            <div className="input-group">
              <label className="input-label">Total Rent Paid (Annual)</label>
              <div className="input-with-icon">
                <span className="prefix">₹</span>
                <input
                  type="number"
                  className="input-field mono"
                  placeholder="e.g. 216000"
                  value={income.hraDetails?.rentPaid || ''}
                  onChange={(e) => handleHraChange('rentPaid', parseFloat(e.target.value) || 0)}
                />
              </div>
            </div>
            <div className="input-group">
              <label className="input-label">City Type</label>
              <select
                className="input-field"
                value={income.hraDetails?.cityType || 'NON_METRO'}
                onChange={(e) => handleHraChange('cityType', e.target.value as MetroType)}
              >
                <option value="METRO">Metro (Delhi, Mumbai, Kolkata, Chennai - 50%)</option>
                <option value="NON_METRO">Non-Metro (40% of Basic)</option>
              </select>
            </div>
          </div>
        )}
      </div>

      <hr style={{ borderColor: 'var(--border-subtle)', margin: '18px 0' }} />

      {/* Chapter VI-A Deductions Section */}
      <h3 style={{ fontSize: '1.05rem', marginBottom: '14px', display: 'flex', alignItems: 'center', gap: '8px' }}>
        <Shield size={18} color="var(--accent-purple)" />
        Deductions &amp; Investments (Old Regime Focus)
      </h3>

      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '14px' }}>
        <div className="input-group">
          <label className="input-label">
            <span>Section 80C</span>
            <span className="badge badge-purple">Max ₹1.5L</span>
          </label>
          <div className="input-with-icon">
            <span className="prefix">₹</span>
            <input
              type="number"
              className="input-field mono"
              placeholder="EPF, PPF, ELSS, Life Ins"
              value={deductions.section80C || ''}
              onChange={(e) => handleDeductionChange('section80C', parseFloat(e.target.value) || 0)}
            />
          </div>
        </div>

        <div className="input-group">
          <label className="input-label">
            <span>Section 80CCD(1B) NPS</span>
            <span className="badge badge-purple">Max ₹50,000</span>
          </label>
          <div className="input-with-icon">
            <span className="prefix">₹</span>
            <input
              type="number"
              className="input-field mono"
              placeholder="Voluntary NPS Tier-1"
              value={deductions.section80CCD1B || ''}
              onChange={(e) => handleDeductionChange('section80CCD1B', parseFloat(e.target.value) || 0)}
            />
          </div>
        </div>
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '14px' }}>
        <div className="input-group">
          <label className="input-label">
            <span>80D Medical (Self &amp; Family)</span>
            <span className="badge badge-purple">Max ₹25k / ₹50k</span>
          </label>
          <div className="input-with-icon">
            <span className="prefix">₹</span>
            <input
              type="number"
              className="input-field mono"
              placeholder="Self Health Insurance"
              value={deductions.section80DSelf || ''}
              onChange={(e) => handleDeductionChange('section80DSelf', parseFloat(e.target.value) || 0)}
            />
          </div>
        </div>

        <div className="input-group">
          <label className="input-label">
            <span>80D Medical (Parents)</span>
            <span className="badge badge-purple">Max ₹25k / ₹50k</span>
          </label>
          <div className="input-with-icon">
            <span className="prefix">₹</span>
            <input
              type="number"
              className="input-field mono"
              placeholder="Parents Health Insurance"
              value={deductions.section80DParents || ''}
              onChange={(e) => handleDeductionChange('section80DParents', parseFloat(e.target.value) || 0)}
            />
          </div>
          <label style={{ display: 'flex', alignItems: 'center', gap: '6px', fontSize: '0.75rem', color: 'var(--text-secondary)', marginTop: '4px', cursor: 'pointer' }}>
            <input
              type="checkbox"
              checked={deductions.parentsAreSeniorCitizens}
              onChange={(e) => handleDeductionChange('parentsAreSeniorCitizens', e.target.checked)}
            />
            Parents are Senior Citizens (&gt;= 60 yrs - ₹50k limit)
          </label>
        </div>
      </div>

      {/* Advanced Deductions Toggle */}
      <div style={{ marginTop: '10px' }}>
        <button
          type="button"
          onClick={() => setShowAdvancedDeductions(!showAdvancedDeductions)}
          className="btn btn-secondary btn-sm"
          style={{ width: '100%' }}
        >
          {showAdvancedDeductions ? '▲ Hide More Deductions (80CCD(2), Home Loan, 80E, 80TTA)' : '▼ Show More Deductions (80CCD(2), Home Loan, 80E, 80TTA)'}
        </button>

        {showAdvancedDeductions && (
          <div style={{ marginTop: '14px', display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '14px' }}>
            <div className="input-group">
              <label className="input-label">
                <span>80CCD(2) Employer NPS</span>
                <span className="badge badge-emerald">Allowed in New &amp; Old</span>
              </label>
              <div className="input-with-icon">
                <span className="prefix">₹</span>
                <input
                  type="number"
                  className="input-field mono"
                  placeholder="Employer NPS contribution"
                  value={deductions.section80CCD2 || ''}
                  onChange={(e) => handleDeductionChange('section80CCD2', parseFloat(e.target.value) || 0)}
                />
              </div>
            </div>

            <div className="input-group">
              <label className="input-label">
                <span>Sec 24(b) Home Loan Interest</span>
                <span className="badge badge-purple">Max ₹2,00,000</span>
              </label>
              <div className="input-with-icon">
                <span className="prefix">₹</span>
                <input
                  type="number"
                  className="input-field mono"
                  placeholder="Self-occupied loan interest"
                  value={deductions.homeLoanInterestSelfOccupied || ''}
                  onChange={(e) => handleDeductionChange('homeLoanInterestSelfOccupied', parseFloat(e.target.value) || 0)}
                />
              </div>
            </div>

            <div className="input-group">
              <label className="input-label">
                <span>Section 80E (Education Loan)</span>
                <span className="badge badge-purple">No Limit</span>
              </label>
              <div className="input-with-icon">
                <span className="prefix">₹</span>
                <input
                  type="number"
                  className="input-field mono"
                  placeholder="Education loan interest"
                  value={deductions.section80E || ''}
                  onChange={(e) => handleDeductionChange('section80E', parseFloat(e.target.value) || 0)}
                />
              </div>
            </div>

            <div className="input-group">
              <label className="input-label">
                <span>80TTA / 80TTB (Interest)</span>
                <span className="badge badge-purple">Max ₹10k / ₹50k</span>
              </label>
              <div className="input-with-icon">
                <span className="prefix">₹</span>
                <input
                  type="number"
                  className="input-field mono"
                  placeholder="Savings bank interest"
                  value={deductions.section80TTA || ''}
                  onChange={(e) => handleDeductionChange('section80TTA', parseFloat(e.target.value) || 0)}
                />
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};
