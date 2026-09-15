import {
  TaxCalculationRequest,
  RegimeComparisonResult,
  TaxCalculationResult,
  PresumptiveTaxRequest,
  PresumptiveTaxResult,
  TaxExplanationRequest,
  TaxExplanationResponse
} from '../types/tax';

// Resolve API base URL:
// - Local dev: undefined -> same-origin, proxied by Vite to http://localhost:8080
// - Render static site: VITE_API_BASE_URL comes from the backend service 'host' property
//   (e.g. "cleartaxer-backend.onrender.com" or "https://cleartaxer-backend.onrender.com")
const rawBase = (import.meta.env.VITE_API_BASE_URL as string | undefined)?.trim();
let origin = '';
if (rawBase) {
  origin = rawBase.replace(/\/$/, '');
  if (!/^https?:\/\//i.test(origin)) {
    origin = 'https://' + origin;
  }
}
const API_BASE = origin + '/api/tax';

export const DOCUMENT_API_BASE = API_BASE;

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
