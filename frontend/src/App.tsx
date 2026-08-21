import React, { useState, useEffect, useCallback } from 'react';
import {
  AssessmentYear,
  AgeCategory,
  IncomeDetails,
  DeductionDetails,
  RegimeComparisonResult,
  TaxRegime,
  TaxCalculationRequest
} from './types/tax';
import { taxApi } from './services/api';
import { Header } from './components/Header';
import { IncomeDeductionForm } from './components/IncomeDeductionForm';
import { RegimeComparisonHero } from './components/RegimeComparisonHero';
import { AuditTraceInspector } from './components/AuditTraceInspector';
import { DeductionOptimizer } from './components/DeductionOptimizer';
import { PresumptiveTaxStudio } from './components/PresumptiveTaxStudio';
import { TaxExplanationDrawer } from './components/TaxExplanationDrawer';
import { ExportSummaryModal } from './components/ExportSummaryModal';
import { DocumentImportModal } from './components/DocumentImportModal';

export function App() {
  const [activeTab, setActiveTab] = useState<'calculator' | 'presumptive' | 'optimizer'>('calculator');
  const [assessmentYear, setAssessmentYear] = useState<AssessmentYear>('2024-25');
  const [ageCategory, setAgeCategory] = useState<AgeCategory>('BELOW_60');

  const [income, setIncome] = useState<IncomeDetails>({
    grossSalary: 1200000,
    incomeFromHouseProperty: 0,
    businessIncome: 0,
    shortTermCapitalGains: 0,
    longTermCapitalGains: 0,
    incomeFromOtherSources: 50000,
    hraDetails: {
      basicSalary: 600000,
      dearnessAllowance: 0,
      hraReceived: 240000,
      rentPaid: 216000,
      cityType: 'NON_METRO',
    }
  });

  const [deductions, setDeductions] = useState<DeductionDetails>({
    section80C: 150000,
    section80DSelf: 25000,
    section80DParents: 25000,
    parentsAreSeniorCitizens: false,
    section80CCD1B: 50000,
    section80CCD2: 0,
    section80E: 0,
    section80G: 0,
    section80TTA: 10000,
    section80TTB: 0,
    homeLoanInterestSelfOccupied: 0,
    calculatedHraExemption: 0,
    otherDeductions: 0,
  });

  const [comparison, setComparison] = useState<RegimeComparisonResult | null>(null);
  const [selectedRegimeForAudit, setSelectedRegimeForAudit] = useState<TaxRegime>('NEW');
  const [isExplainOpen, setIsExplainOpen] = useState(false);
  const [isExportOpen, setIsExportOpen] = useState(false);
  const [isImportOpen, setIsImportOpen] = useState(false);
  const [calculating, setCalculating] = useState(false);

  const runCalculation = useCallback(async () => {
    setCalculating(true);
    try {
      const request: TaxCalculationRequest = {
        assessmentYear,
        ageCategory,
        income,
        deductions,
      };
      const result = await taxApi.compareRegimes(request);
      setComparison(result);
      if (result) {
        setSelectedRegimeForAudit(result.recommendedRegime);
      }
    } catch (err) {
      console.error('Error running tax comparison:', err);
    } finally {
      setCalculating(false);
    }
  }, [assessmentYear, ageCategory, income, deductions]);

  useEffect(() => {
    runCalculation();
  }, [runCalculation]);

  const handleLoadPreset = (presetKey: string) => {
    if (presetKey === '7.5L_REBATE') {
      setIncome({
        grossSalary: 750000,
        incomeFromHouseProperty: 0,
        businessIncome: 0,
        shortTermCapitalGains: 0,
        longTermCapitalGains: 0,
        incomeFromOtherSources: 0,
      });
      setDeductions({
        section80C: 0,
        section80DSelf: 0,
        section80DParents: 0,
        parentsAreSeniorCitizens: false,
        section80CCD1B: 0,
        section80CCD2: 0,
        section80E: 0,
        section80G: 0,
        section80TTA: 0,
        section80TTB: 0,
        homeLoanInterestSelfOccupied: 0,
        calculatedHraExemption: 0,
        otherDeductions: 0,
      });
      setAgeCategory('BELOW_60');
    } else if (presetKey === '12L_STANDARD') {
      setIncome({
        grossSalary: 1200000,
        incomeFromHouseProperty: 0,
        businessIncome: 0,
        shortTermCapitalGains: 0,
        longTermCapitalGains: 0,
        incomeFromOtherSources: 50000,
      });
      setDeductions({
        section80C: 150000,
        section80DSelf: 25000,
        section80DParents: 25000,
        parentsAreSeniorCitizens: false,
        section80CCD1B: 50000,
        section80CCD2: 0,
        section80E: 0,
        section80G: 0,
        section80TTA: 10000,
        section80TTB: 0,
        homeLoanInterestSelfOccupied: 0,
        calculatedHraExemption: 0,
        otherDeductions: 0,
      });
      setAgeCategory('BELOW_60');
    } else if (presetKey === '60L_HNI') {
      setIncome({
        grossSalary: 6000000,
        incomeFromHouseProperty: 0,
        businessIncome: 0,
        shortTermCapitalGains: 0,
        longTermCapitalGains: 0,
        incomeFromOtherSources: 200000,
      });
      setDeductions({
        section80C: 150000,
        section80DSelf: 25000,
        section80DParents: 50000,
        parentsAreSeniorCitizens: true,
        section80CCD1B: 50000,
        section80CCD2: 100000,
        section80E: 0,
        section80G: 0,
        section80TTA: 10000,
        section80TTB: 0,
        homeLoanInterestSelfOccupied: 200000,
        calculatedHraExemption: 0,
        otherDeductions: 0,
      });
      setAgeCategory('BELOW_60');
    } else if (presetKey === 'SENIOR_6L') {
      setIncome({
        grossSalary: 450000,
        incomeFromHouseProperty: 0,
        businessIncome: 0,
        shortTermCapitalGains: 0,
        longTermCapitalGains: 0,
        incomeFromOtherSources: 150000,
      });
      setDeductions({
        section80C: 50000,
        section80DSelf: 50000,
        section80DParents: 0,
        parentsAreSeniorCitizens: false,
        section80CCD1B: 0,
        section80CCD2: 0,
        section80E: 0,
        section80G: 0,
        section80TTA: 0,
        section80TTB: 50000,
        homeLoanInterestSelfOccupied: 0,
        calculatedHraExemption: 0,
        otherDeductions: 0,
      });
      setAgeCategory('SENIOR_60_TO_80');
    }
  };

  const activeAuditCalculation =
    comparison && (selectedRegimeForAudit === 'NEW' ? comparison.newRegime : comparison.oldRegime);

  return (
    <div className="app-shell">
      <Header
        activeTab={activeTab}
        setActiveTab={setActiveTab}
        assessmentYear={assessmentYear}
        setAssessmentYear={setAssessmentYear}
        onOpenExplain={() => setIsExplainOpen(true)}
        onOpenImport={() => setIsImportOpen(true)}
      />

      {activeTab === 'calculator' && (
        <div className="main-grid">
          {/* Left Column: Form */}
          <div>
            <IncomeDeductionForm
              ageCategory={ageCategory}
              setAgeCategory={setAgeCategory}
              income={income}
              setIncome={setIncome}
              deductions={deductions}
              setDeductions={setDeductions}
              onLoadPreset={handleLoadPreset}
            />
          </div>

          {/* Right Column: Hero & Audit */}
          <div>
            <RegimeComparisonHero
              comparison={comparison}
              selectedRegimeForAudit={selectedRegimeForAudit}
              setSelectedRegimeForAudit={setSelectedRegimeForAudit}
              onOpenExplain={() => setIsExplainOpen(true)}
              onOpenExport={() => setIsExportOpen(true)}
            />

            {activeAuditCalculation && (
              <AuditTraceInspector calculation={activeAuditCalculation} />
            )}
          </div>
        </div>
      )}

      {activeTab === 'presumptive' && (
        <PresumptiveTaxStudio
          assessmentYear={assessmentYear}
          deductions={deductions}
        />
      )}

      {activeTab === 'optimizer' && (
        <DeductionOptimizer
          deductions={deductions}
          setDeductions={setDeductions}
          oldRegimeResult={comparison ? comparison.oldRegime : null}
        />
      )}

      {/* AI Explanation Drawer Modal */}
      <TaxExplanationDrawer
        isOpen={isExplainOpen}
        onClose={() => setIsExplainOpen(false)}
        comparison={comparison}
        activeCalculation={activeAuditCalculation}
      />

      {/* PDF / Print Summary Modal */}
      <ExportSummaryModal
        isOpen={isExportOpen}
        onClose={() => setIsExportOpen(false)}
        comparison={comparison}
      />

      {/* AI Form 16 / Document Import Modal */}
      <DocumentImportModal
        isOpen={isImportOpen}
        onClose={() => setIsImportOpen(false)}
        onApplyExtractedData={(extractedIncome, extractedDeductions) => {
          setIncome((prev) => ({ ...prev, ...extractedIncome }));
          setDeductions((prev) => ({ ...prev, ...extractedDeductions }));
        }}
      />
    </div>
  );
}
export default App;
