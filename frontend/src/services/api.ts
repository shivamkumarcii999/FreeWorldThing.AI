import {
  TaxCalculationRequest,
  RegimeComparisonResult,
  TaxCalculationResult,
  PresumptiveTaxRequest,
  PresumptiveTaxResult,
  TaxExplanationRequest,
  TaxExplanationResponse
} from '../types/tax';

const API_BASE = '/api/tax';

export const taxApi = {
  async compareRegimes(request: TaxCalculationRequest): Promise<RegimeComparisonResult> {
    const res = await fetch(`${API_BASE}/compare`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(request),
    });
    if (!res.ok) throw new Error('Failed to calculate tax comparison');
    return res.json();
  },

  async calculateSingleRegime(request: TaxCalculationRequest, regime: 'OLD' | 'NEW'): Promise<TaxCalculationResult> {
    const res = await fetch(`${API_BASE}/calculate?regime=${regime}`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(request),
    });
    if (!res.ok) throw new Error('Failed to calculate tax for regime');
    return res.json();
  },

  async calculatePresumptive(request: PresumptiveTaxRequest): Promise<PresumptiveTaxResult> {
    const res = await fetch(`${API_BASE}/presumptive`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(request),
    });
    if (!res.ok) throw new Error('Failed to calculate presumptive tax');
    return res.json();
  },

  async explainTax(request: TaxExplanationRequest): Promise<TaxExplanationResponse> {
    const res = await fetch(`${API_BASE}/explain`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(request),
    });
    if (!res.ok) throw new Error('Failed to fetch tax explanation');
    return res.json();
  },

  async getMetadata(): Promise<any> {
    const res = await fetch(`${API_BASE}/metadata`);
    if (!res.ok) throw new Error('Failed to fetch system metadata');
    return res.json();
  }
};
