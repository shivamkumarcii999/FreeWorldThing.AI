export type AssessmentYear = '2024-25' | '2025-26';
export type TaxRegime = 'OLD' | 'NEW';
export type AgeCategory = 'BELOW_60' | 'SENIOR_60_TO_80' | 'SUPER_SENIOR_ABOVE_80';
export type MetroType = 'METRO' | 'NON_METRO';
export type PresumptiveSection = 'SECTION_44ADA' | 'SECTION_44AD';

export interface HraDetails {
  basicSalary: number;
  dearnessAllowance: number;
  hraReceived: number;
  rentPaid: number;
  cityType: MetroType;
}

export interface IncomeDetails {
  grossSalary: number;
  incomeFromHouseProperty: number;
  businessIncome: number;
  shortTermCapitalGains: number;
  longTermCapitalGains: number;
  incomeFromOtherSources: number;
  hraDetails?: HraDetails;
}

export interface DeductionDetails {
  section80C: number;
  section80DSelf: number;
  section80DParents: number;
  parentsAreSeniorCitizens: boolean;
  section80CCD1B: number;
  section80CCD2: number;
  section80E: number;
  section80G: number;
  section80TTA: number;
  section80TTB: number;
  homeLoanInterestSelfOccupied: number;
  calculatedHraExemption: number;
  otherDeductions: number;
}

export interface TaxCalculationRequest {
  assessmentYear: string;
  ageCategory: AgeCategory;
  preferredRegime?: TaxRegime | null;
  income: IncomeDetails;
  deductions: DeductionDetails;
}

export interface SlabBreakdown {
  slabRange: string;
  minIncome: number;
  maxIncome: number;
  taxableAmountInSlab: number;
  ratePercentage: number;
  taxForSlab: number;
}

export interface AuditTraceStep {
  stepNumber: number;
  stepName: string;
  ruleReference: string;
  description: string;
  amount: number;
  formattedDetails: string;
}

export interface TaxCalculationResult {
  regime: TaxRegime;
  assessmentYear: string;
  ageCategory: AgeCategory;
  ruleEngineVersion: string;
  grossTotalIncome: number;
  standardDeduction: number;
  totalDeductionsAllowed: number;
  netTaxableIncome: number;
  taxOnSlabs: number;
  slabBreakdowns: SlabBreakdown[];
  rebateSection87A: number;
  taxAfterRebate: number;
  surchargeRate: number;
  surchargeAmount: number;
  marginalReliefSurcharge: number;
  healthAndEducationCess: number;
  totalTaxPayable: number;
  effectiveTaxRate: number;
  auditTrace: AuditTraceStep[];
}

export interface RegimeComparisonResult {
  assessmentYear: string;
  oldRegime: TaxCalculationResult;
  newRegime: TaxCalculationResult;
  recommendedRegime: TaxRegime;
  taxSaved: number;
  recommendationSummary: string;
  optimizationTips: string[];
  comparisonMetrics: Record<string, any>;
}

export interface AdvanceTaxInstallment {
  installmentNumber: number;
  dueDate: string;
  cumulativePercentage: number;
  cumulativeAmountDue: number;
  installmentAmount: number;
  note: string;
}

export interface PresumptiveTaxRequest {
  assessmentYear: string;
  section: PresumptiveSection;
  digitalReceipts: number;
  cashReceipts: number;
  customProfitPercentage?: number | null;
  otherIncome: number;
  deductions: DeductionDetails;
}

export interface PresumptiveTaxResult {
  section: PresumptiveSection;
  assessmentYear: string;
  totalGrossReceipts: number;
  digitalReceipts: number;
  cashReceipts: number;
  digitalPercentage: number;
  eligibilityThreshold: number;
  isEligible: boolean;
  eligibilityReason: string;
  deemedProfitRate: number;
  deemedProfitAmount: number;
  totalGrossIncome: number;
  oldRegimeCalculation: TaxCalculationResult;
  newRegimeCalculation: TaxCalculationResult;
  recommendedRegime: TaxRegime;
  taxPayable: number;
  advanceTaxSchedule: AdvanceTaxInstallment[];
  gstThreshold: number;
  gstRegistrationMandatory: boolean;
  gstApplicabilityNote: string;
}

export interface TaxExplanationRequest {
  calculationResult?: TaxCalculationResult;
  comparisonResult?: RegimeComparisonResult;
  userQuestion?: string;
}

export interface TaxExplanationResponse {
  executiveSummary: string;
  plainLanguageBreakdown: string;
  keyObservations: string[];
  strategicTips: string[];
  faqClarifications: string[];
  statutoryDisclaimer: string;
}
