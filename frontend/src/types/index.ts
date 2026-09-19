export interface User {
  id: number;
  email: string;
  fullName: string;
  avatarUrl?: string;
  role: 'CLIENT' | 'FREELANCER' | 'ADMIN';
  headline?: string;
  bio?: string;
  location?: string;
  hourlyRate?: number;
  walletBalance?: number;
  ratingAvg?: number;
  ratingCount?: number;
  completedProjects?: number;
  identityVerified?: boolean;
  paymentVerified?: boolean;
  businessVerified?: boolean;
  specialties?: string[];
}

export interface Profile {
  id: number;
  userId: number;
  title: string;
  bio: string;
  hourlyRate: number;
  currency: string;
  rating: number;
  reviewCount: number;
  completedProjects: number;
  location: string;
  githubUrl?: string;
  skills: string;
  proofOfWorkScore?: number;
  verifiedBadge?: boolean;
  availableNow?: boolean;
}

export interface ProofOfWork {
  id: number;
  userId: number;
  projectTitle: string;
  problem: string;
  solution: string;
  technologies: string;
  repoUrl?: string;
  liveDemoUrl?: string;
  evidenceScore?: number;
  testCoveragePercent?: number;
  commitCount?: number;
  verified?: boolean;
}

export interface Job {
  id: number;
  client?: User;
  clientName?: string;
  title: string;
  description: string;
  category: string;
  requiredSkills: string[];
  budgetMin: number;
  budgetMax: number;
  currency: string;
  durationWeeks?: number;
  complexity?: string;
  projectType?: string;
  status: 'OPEN' | 'IN_PROGRESS' | 'COMPLETED' | 'CLOSED';
  aiSpec?: string;
  proposalCount?: number;
  createdAt?: string;
}

export interface SuggestedMilestone {
  sequenceOrder: number;
  title: string;
  description: string;
  suggestedAmount: number;
  estimatedDays: number;
}

export interface AiJobSpecResponse {
  title: string;
  description: string;
  category: string;
  requiredSkills: string[];
  minBudget: number;
  maxBudget: number;
  budgetCurrency: string;
  estimatedDurationWeeks: number;
  complexity: string;
  projectType: string;
  suggestedMilestones: SuggestedMilestone[];
  aiConfidenceScore: number;
}

export interface TalentMatchResult {
  userId: number;
  fullName: string;
  avatarUrl?: string;
  title: string;
  location: string;
  hourlyRate: number;
  currency: string;
  rating: number;
  reviewCount: number;
  completedProjects: number;
  proofOfWorkScore: number;
  verifiedBadge: boolean;
  availableNow: boolean;
  skills: string[];
  matchScore: number;
  matchingSkills: string[];
  matchReasoning: string;
  topVerifiedProject: string;
}

export interface Proposal {
  id: number;
  jobId: number;
  freelancerId: number;
  freelancerName?: string;
  freelancer?: User;
  coverLetter: string;
  proposedBudget?: number;
  bidAmount?: number;
  proposedDurationDays?: number;
  durationWeeks?: number;
  aiQualityScore?: number;
  aiQualityFeedback?: string;
  status: 'PENDING' | 'SHORTLISTED' | 'ACCEPTED' | 'REJECTED' | 'SUBMITTED';
  createdAt?: string;
}

export interface Milestone {
  id: number;
  contractId?: number;
  sequence?: number;
  sequenceOrder?: number;
  title: string;
  description?: string;
  amount: number;
  status: 'PENDING' | 'UNFUNDED' | 'FUNDED' | 'IN_PROGRESS' | 'SUBMITTED' | 'DELIVERED' | 'APPROVED' | 'PAID' | 'RELEASED';
  dueDate?: string;
  submittedAt?: string;
  deliveredAt?: string;
  approvedAt?: string;
  paidAt?: string;
  deliverableNotes?: string;
  deliverableUrl?: string;
}

export interface Contract {
  id: number;
  jobId?: number;
  title: string;
  client?: User;
  clientId?: number;
  clientName?: string;
  freelancer?: User;
  freelancerId?: number;
  freelancerName?: string;
  totalValue?: number;
  totalAmount?: number;
  currency: string;
  status: 'ACTIVE' | 'COMPLETED' | 'CANCELLED' | 'DISPUTED';
  fundedAmount?: number;
  releasedAmount?: number;
  milestones?: Milestone[];
  startDate?: string;
}

export interface TaskItem {
  id: number;
  workspaceId: number;
  milestoneId?: number;
  title: string;
  description?: string;
  status: 'TODO' | 'IN_PROGRESS' | 'REVIEW' | 'DONE';
  priority?: 'LOW' | 'MEDIUM' | 'HIGH' | 'URGENT';
  assignedToName?: string;
}

export interface ProjectMessage {
  id: number;
  workspaceId: number;
  senderId: number;
  senderName: string;
  senderRole: string;
  content: string;
  messageType: string;
  createdAt?: string;
}

export interface AiProjectHealthReport {
  healthStatus: 'ON_TRACK' | 'AT_RISK' | 'BLOCKED' | 'COMPLETED';
  executiveSummary: string;
  completionPercent: number;
  blockers: string[];
  nextSteps: string[];
  confidenceScore: number;
}

export interface ServiceListing {
  id: number;
  freelancerId: number;
  freelancerName: string;
  freelancerAvatar?: string;
  title: string;
  description: string;
  category: string;
  startingPrice: number;
  currency: string;
  rating: number;
  reviewCount: number;
  deliveryDays: number;
  packagesJson?: string;
}
