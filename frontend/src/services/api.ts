import {
  Job,
  AiJobSpecResponse,
  Proposal,
  Contract,
  Milestone,
  TaskItem,
  ProjectMessage,
  AiProjectHealthReport,
  ServiceListing,
  User,
  ProofOfWork,
} from '../types';

const API_BASE = '/api';
const TOKEN_KEY = 'fwt_token';

// ---------- auth token handling ----------

export function getToken(): string | null {
  return localStorage.getItem(TOKEN_KEY);
}

export function setToken(token: string | null) {
  if (token) localStorage.setItem(TOKEN_KEY, token);
  else localStorage.removeItem(TOKEN_KEY);
}

function headers(json = true): HeadersInit {
  const h: Record<string, string> = {};
  if (json) h['Content-Type'] = 'application/json';
  const token = getToken();
  if (token) h['Authorization'] = `Bearer ${token}`;
  return h;
}

async function req<T>(url: string, options: RequestInit = {}): Promise<T> {
  const res = await fetch(`${API_BASE}${url}`, { ...options, headers: { ...headers(), ...(options.headers || {}) } });
  if (!res.ok) {
    let msg = `Request failed (${res.status})`;
    try {
      const body = await res.json();
      if (body?.message) msg = body.message;
    } catch {}
    throw new Error(msg);
  }
  return res.json();
}

// ---------- auth ----------

export const auth = {
  login: async (email: string, password: string): Promise<{ token: string; user: User }> => {
    const res = await fetch(`${API_BASE}/auth/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email, password }),
    });
    if (!res.ok) throw new Error('Invalid email or password');
    const data = await res.json();
    setToken(data.token);
    return { token: data.token, user: data.user };
  },

  register: async (email: string, password: string, fullName: string, role: 'CLIENT' | 'FREELANCER'): Promise<{ token: string; user: User }> => {
    const res = await fetch(`${API_BASE}/auth/register`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email, password, fullName, role }),
    });
    if (!res.ok) throw new Error('Registration failed — email may already be in use');
    const data = await res.json();
    setToken(data.token);
    return { token: data.token, user: data.user };
  },

  logout: () => setToken(null),

  me: async (): Promise<User> => {
    const res = await fetch(`${API_BASE}/users/me`, { headers: headers() });
    if (!res.ok) throw new Error('Session expired');
    return res.json();
  },
};

export const api = {
  // --- Session ---
  getMe: async (): Promise<User> => auth.me(),

  // --- AI Studio ---
  generateJobSpec: async (prompt: string, preferredCurrency = 'INR'): Promise<AiJobSpecResponse> => {
    const res = await fetch(`${API_BASE}/ai/job-spec`, {
      method: 'POST',
      headers: headers(),
      body: JSON.stringify({ prompt, preferredCurrency }),
    });
    if (!res.ok) throw new Error('Failed to generate AI job spec');
    return res.json();
  },

  generateProposalDraft: async (jobId: number, freelancerId: number, customNote = ''): Promise<any> => {
    const res = await fetch(`${API_BASE}/ai/proposal-draft`, {
      method: 'POST',
      headers: headers(),
      body: JSON.stringify({ jobId, freelancerId, customNote }),
    });
    if (!res.ok) throw new Error('Failed to generate AI proposal draft');
    return res.json();
  },

  getAiWorkspaceHealth: async (contractId: number): Promise<AiProjectHealthReport> => {
    return req(`/ai/workspace-health/${contractId}`);
  },

  // --- Jobs ---
  getJobs: async (category?: string): Promise<Job[]> => {
    const url = category && category !== 'ALL' ? `/jobs?category=${encodeURIComponent(category)}` : '/jobs';
    return req(url);
  },

  getJobById: async (id: number): Promise<Job> => req(`/jobs/${id}`),

  createJob: async (jobData: any): Promise<Job> =>
    req('/jobs', { method: 'POST', body: JSON.stringify(jobData) }),

  getJobMatches: async (jobId: number): Promise<any[]> => req(`/jobs/${jobId}/match`),

  getProposalsForJob: async (jobId: number): Promise<Proposal[]> => req(`/jobs/${jobId}/proposals`),

  submitProposal: async (proposalData: any): Promise<Proposal> =>
    req(`/jobs/${proposalData.jobId}/proposals`, {
      method: 'POST',
      body: JSON.stringify({
        coverLetter: proposalData.coverLetter,
        bidAmount: proposalData.proposedBudget ?? proposalData.bidAmount,
        proposedDurationDays: proposalData.proposedDurationDays,
      }),
    }),

  // --- Talent & Services ---
  getAllTalent: async (): Promise<any[]> => req('/talent'),

  getTalentById: async (userId: number): Promise<any> => req(`/talent/${userId}`),

  getProofsForUser: async (userId: number): Promise<ProofOfWork[]> => req(`/talent/${userId}/proofs`),

  getAllServices: async (): Promise<ServiceListing[]> => req('/services'),

  getServiceById: async (id: number): Promise<ServiceListing> => req(`/services/${id}`),

  // --- Contracts & Escrow ---
  getContracts: async (): Promise<Contract[]> => req('/contracts/mine'),

  getContractById: async (id: number): Promise<Contract> => req(`/contracts/${id}`),

  createContract: async (data: any): Promise<Contract> =>
    req('/contracts', { method: 'POST', body: JSON.stringify(data) }),

  fundMilestone: async (milestoneId: number): Promise<Milestone> =>
    req(`/contracts/milestones/${milestoneId}/fund`, { method: 'POST' }),

  submitMilestone: async (milestoneId: number, deliverableUrl: string, deliverableNotes: string): Promise<Milestone> =>
    req(`/contracts/milestones/${milestoneId}/submit`, {
      method: 'POST',
      body: JSON.stringify({ deliverableUrl, deliverableNotes }),
    }),

  approveMilestone: async (milestoneId: number): Promise<Milestone> =>
    req(`/contracts/milestones/${milestoneId}/approve`, { method: 'POST' }),

  // --- Project Workspace ---
  getWorkspace: async (contractId: number): Promise<{
    workspace: any;
    contract: Contract;
    milestones: Milestone[];
    tasks: TaskItem[];
    messages: ProjectMessage[];
  }> => req(`/workspace/${contractId}`),

  createTask: async (taskData: any): Promise<TaskItem> =>
    req('/workspace/tasks', { method: 'POST', body: JSON.stringify(taskData) }),

  updateTaskStatus: async (taskId: number, status: string): Promise<TaskItem> =>
    req(`/workspace/tasks/${taskId}/status?status=${status}`, { method: 'PATCH' }),

  sendMessage: async (msgData: any): Promise<ProjectMessage> =>
    req('/workspace/messages', { method: 'POST', body: JSON.stringify(msgData) }),

  // --- Users & Wallet ---
  getUsers: async (): Promise<User[]> => req('/users'),

  depositWallet: async (userId: number, amount: number): Promise<User> =>
    req(`/users/${userId}/wallet/deposit?amount=${amount}`, { method: 'POST' }),
};
