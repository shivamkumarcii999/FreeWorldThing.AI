import React, { useState } from 'react';
import { 
  Wallet, TrendingUp, ShieldCheck, CheckCircle2, Clock, 
  ArrowUpRight, Sparkles, Award, Star,
  Briefcase, FileText, Activity, AlertCircle, RefreshCw
} from 'lucide-react';
import { User, Contract } from '../types';

interface DashboardViewProps {
  currentUser: User | null;
  contracts: Contract[];
  onSelectContract: (contractId: number) => void;
  onBrowseJobs: () => void;
  onRefresh: () => void;
  onDepositWallet: () => void;
}

export const DashboardView: React.FC<DashboardViewProps> = ({
  currentUser,
  contracts,
  onSelectContract,
  onBrowseJobs,
  onRefresh,
  onDepositWallet,
}) => {
  const [activeTab, setActiveTab] = useState<'contracts' | 'activity' | 'ai-insights'>('contracts');

  if (!currentUser) return null;

  const isClient = currentUser.role === 'CLIENT';
  const activeContracts = contracts.filter(c => c.status === 'ACTIVE');
  const completedContracts = contracts.filter(c => c.status === 'COMPLETED');
  const totalVolume = contracts.reduce((sum, c) => sum + (c.totalValue || c.totalAmount || 0), 0);

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 py-8 space-y-8 animate-fade-in">
      {/* Header Banner */}
      <div className="relative overflow-hidden rounded-3xl bg-gradient-to-r from-purple-950/60 via-slate-900 to-cyan-950/40 border border-slate-800/80 p-8 shadow-2xl">
        <div className="absolute top-0 right-0 w-96 h-96 bg-purple-500/10 rounded-full blur-3xl pointer-events-none -mr-20 -mt-20"></div>
        <div className="relative z-10 flex flex-col md:flex-row md:items-center justify-between gap-6">
          <div className="flex items-center gap-5">
            <img 
              src={currentUser.avatarUrl || `https://api.dicebear.com/7.x/bottts/svg?seed=${currentUser.email}`} 
              alt={currentUser.fullName}
              className="w-16 h-16 rounded-2xl border-2 border-purple-500/50 shadow-glow p-1 bg-slate-900"
            />
            <div>
              <div className="flex items-center gap-3 mb-1">
                <h1 className="text-2xl md:text-3xl font-bold font-display text-white">
                  Welcome back, {currentUser.fullName}
                </h1>
                <span className={`px-2.5 py-0.5 rounded-full text-xs font-semibold uppercase tracking-wider ${
                  isClient 
                    ? 'bg-purple-500/20 text-purple-300 border border-purple-500/30' 
                    : 'bg-cyan-500/20 text-cyan-300 border border-cyan-500/30'
                }`}>
                  {currentUser.role}
                </span>
              </div>
              <p className="text-slate-400 text-sm flex items-center gap-2">
                <ShieldCheck className="w-4 h-4 text-emerald-400" />
                Verified Autonomous Reputation ID: <span className="font-mono text-slate-300">FWT-REP-{currentUser.id.toString().padStart(4, '0')}</span>
              </p>
            </div>
          </div>

          <div className="flex items-center gap-3">
            <button 
              onClick={onDepositWallet}
              className="px-4 py-2.5 rounded-xl bg-purple-600/30 hover:bg-purple-600/50 text-purple-200 border border-purple-500/40 text-sm font-semibold transition flex items-center gap-2 shadow-glow"
            >
              <Wallet className="w-4 h-4" />
              Deposit Funds
            </button>
            <button 
              onClick={onBrowseJobs}
              className="px-4 py-2.5 rounded-xl bg-slate-800 hover:bg-slate-700 text-slate-200 border border-slate-700 text-sm font-semibold transition flex items-center gap-2"
            >
              <Briefcase className="w-4 h-4" />
              {isClient ? 'Find Talent' : 'Explore Jobs'}
            </button>
            <button 
              onClick={onRefresh}
              className="p-2.5 rounded-xl bg-slate-800/80 hover:bg-slate-700 text-slate-300 hover:text-white border border-slate-700 transition"
              title="Refresh Data"
            >
              <RefreshCw className="w-4 h-4" />
            </button>
          </div>
        </div>
      </div>

      {/* Metrics Row */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-5">
        {/* Wallet Balance */}
        <div className="glass-panel p-6 rounded-2xl border border-slate-800/80 relative overflow-hidden group hover:border-purple-500/40 transition">
          <div className="flex items-center justify-between mb-4">
            <span className="text-sm font-medium text-slate-400">Escrow Balance</span>
            <div className="w-10 h-10 rounded-xl bg-purple-500/10 border border-purple-500/30 flex items-center justify-center text-purple-400 group-hover:scale-110 transition">
              <Wallet className="w-5 h-5" />
            </div>
          </div>
          <div className="flex items-baseline gap-2">
            <span className="text-3xl font-bold font-display text-white">
              ₹{(currentUser.walletBalance || 0).toLocaleString()}
            </span>
            <span className="text-xs text-emerald-400 font-medium flex items-center">
              <ArrowUpRight className="w-3.5 h-3.5" /> Instant Liquid
            </span>
          </div>
          <p className="text-xs text-slate-500 mt-2">Available for smart-contract escrow releases</p>
        </div>

        {/* Active Projects */}
        <div className="glass-panel p-6 rounded-2xl border border-slate-800/80 relative overflow-hidden group hover:border-cyan-500/40 transition">
          <div className="flex items-center justify-between mb-4">
            <span className="text-sm font-medium text-slate-400">Active Workspaces</span>
            <div className="w-10 h-10 rounded-xl bg-cyan-500/10 border border-cyan-500/30 flex items-center justify-center text-cyan-400 group-hover:scale-110 transition">
              <Activity className="w-5 h-5" />
            </div>
          </div>
          <div className="flex items-baseline gap-2">
            <span className="text-3xl font-bold font-display text-white">
              {activeContracts.length}
            </span>
            <span className="text-xs text-cyan-400 font-medium">In Flight</span>
          </div>
          <p className="text-xs text-slate-500 mt-2">Under AI Project Manager surveillance</p>
        </div>

        {/* Total Volume */}
        <div className="glass-panel p-6 rounded-2xl border border-slate-800/80 relative overflow-hidden group hover:border-emerald-500/40 transition">
          <div className="flex items-center justify-between mb-4">
            <span className="text-sm font-medium text-slate-400">{isClient ? 'Total Invested' : 'Lifetime Earnings'}</span>
            <div className="w-10 h-10 rounded-xl bg-emerald-500/10 border border-emerald-500/30 flex items-center justify-center text-emerald-400 group-hover:scale-110 transition">
              <TrendingUp className="w-5 h-5" />
            </div>
          </div>
          <div className="flex items-baseline gap-2">
            <span className="text-3xl font-bold font-display text-white">
              ₹{totalVolume.toLocaleString()}
            </span>
            <span className="text-xs text-emerald-400 font-medium flex items-center">
              <CheckCircle2 className="w-3.5 h-3.5 mr-1" /> {completedContracts.length} Done
            </span>
          </div>
          <p className="text-xs text-slate-500 mt-2">100% verified on-chain & in escrow</p>
        </div>

        {/* Trust Score */}
        <div className="glass-panel p-6 rounded-2xl border border-slate-800/80 relative overflow-hidden group hover:border-amber-500/40 transition">
          <div className="flex items-center justify-between mb-4">
            <span className="text-sm font-medium text-slate-400">Reputation Score</span>
            <div className="w-10 h-10 rounded-xl bg-amber-500/10 border border-amber-500/30 flex items-center justify-center text-amber-400 group-hover:scale-110 transition">
              <Award className="w-5 h-5" />
            </div>
          </div>
          <div className="flex items-baseline gap-2">
            <span className="text-3xl font-bold font-display text-white">
              {currentUser.ratingAvg ? (currentUser.ratingAvg * 20).toFixed(1) : '98.5'}%
            </span>
            <span className="text-xs text-amber-400 font-medium flex items-center">
              <Star className="w-3.5 h-3.5 fill-amber-400 mr-1" /> Tier 1 Elite
            </span>
          </div>
          <p className="text-xs text-slate-500 mt-2">{currentUser.completedProjects || 0} completed projects</p>
        </div>
      </div>

      {/* Main Tabs Navigation */}
      <div className="flex items-center gap-3 border-b border-slate-800 pb-3">
        <button
          onClick={() => setActiveTab('contracts')}
          className={`px-4 py-2 rounded-xl text-sm font-semibold transition flex items-center gap-2 ${
            activeTab === 'contracts'
              ? 'bg-purple-500/20 text-purple-300 border border-purple-500/40'
              : 'text-slate-400 hover:text-white hover:bg-slate-800'
          }`}
        >
          <Briefcase className="w-4 h-4" />
          Active & Past Contracts ({contracts.length})
        </button>

        <button
          onClick={() => setActiveTab('ai-insights')}
          className={`px-4 py-2 rounded-xl text-sm font-semibold transition flex items-center gap-2 ${
            activeTab === 'ai-insights'
              ? 'bg-cyan-500/20 text-cyan-300 border border-cyan-500/40'
              : 'text-slate-400 hover:text-white hover:bg-slate-800'
          }`}
        >
          <Sparkles className="w-4 h-4" />
          Autonomous AI Work Advisor
        </button>
      </div>

      {/* Tab 1: Contracts List */}
      {activeTab === 'contracts' && (
        <div className="space-y-4">
          {contracts.length === 0 ? (
            <div className="glass-panel p-12 text-center rounded-2xl border border-slate-800">
              <FileText className="w-12 h-12 text-slate-600 mx-auto mb-4" />
              <h3 className="text-lg font-bold text-white mb-2">No Contracts Found</h3>
              <p className="text-slate-400 text-sm max-w-md mx-auto mb-6">
                {isClient 
                  ? 'Post a job with the AI Job Builder or hire a specialist directly to spawn a workspace.' 
                  : 'Submit proposals on open jobs to kickstart funded project workspaces.'}
              </p>
              <button onClick={onBrowseJobs} className="px-5 py-2.5 rounded-xl bg-purple-600 hover:bg-purple-500 text-white font-semibold text-sm shadow-glow">
                Explore Marketplace
              </button>
            </div>
          ) : (
            contracts.map(contract => {
              const otherPartyName = isClient 
                ? (contract.freelancer?.fullName || contract.freelancerName || 'Specialist') 
                : (contract.client?.fullName || contract.clientName || 'Client');
              
              const milestones = contract.milestones || [];
              const submittedCount = milestones.filter(m => m.status === 'SUBMITTED' || m.status === 'DELIVERED').length;
              const paidCount = milestones.filter(m => m.status === 'PAID' || m.status === 'APPROVED' || m.status === 'RELEASED').length;
              const totalMilestones = milestones.length || 1;
              const progressPct = Math.round((paidCount / totalMilestones) * 100);
              const amount = contract.totalValue || contract.totalAmount || 0;

              return (
                <div 
                  key={contract.id}
                  className="glass-panel p-6 rounded-2xl border border-slate-800 hover:border-slate-700 transition flex flex-col lg:flex-row lg:items-center justify-between gap-6"
                >
                  <div className="space-y-3 flex-1">
                    <div className="flex flex-wrap items-center gap-3">
                      <span className={`px-2.5 py-0.5 rounded-full text-xs font-semibold uppercase ${
                        contract.status === 'ACTIVE' 
                          ? 'bg-emerald-500/20 text-emerald-400 border border-emerald-500/30' 
                          : 'bg-slate-800 text-slate-300'
                      }`}>
                        {contract.status}
                      </span>
                      <h3 
                        className="text-lg font-bold text-white hover:text-purple-400 transition cursor-pointer"
                        onClick={() => onSelectContract(contract.id)}
                      >
                        {contract.title}
                      </h3>
                    </div>

                    <div className="flex flex-wrap items-center gap-6 text-sm text-slate-300">
                      <div className="flex items-center gap-2">
                        <span className="text-slate-500">{isClient ? 'Specialist:' : 'Client:'}</span>
                        <span className="text-white font-medium">{otherPartyName}</span>
                      </div>

                      <div className="flex items-center gap-1.5">
                        <Clock className="w-4 h-4 text-slate-500" />
                        <span>Contract #{contract.id}</span>
                      </div>

                      <div className="flex items-center gap-1.5 font-mono text-emerald-400 font-semibold">
                        <Wallet className="w-4 h-4" />
                        <span>₹{amount.toLocaleString()} Total</span>
                      </div>
                    </div>

                    {/* Milestone progress bar */}
                    <div className="space-y-1 max-w-md pt-2">
                      <div className="flex justify-between text-xs text-slate-400">
                        <span>Milestone Progress ({paidCount}/{totalMilestones} Completed)</span>
                        <span>{progressPct}%</span>
                      </div>
                      <div className="w-full h-2 rounded-full bg-slate-800 overflow-hidden border border-slate-700">
                        <div 
                          className="h-full bg-gradient-to-r from-purple-500 to-cyan-500 transition-all duration-500"
                          style={{ width: `${progressPct}%` }}
                        ></div>
                      </div>
                      {submittedCount > 0 && (
                        <p className="text-xs text-amber-400 flex items-center gap-1 mt-1">
                          <AlertCircle className="w-3.5 h-3.5" /> {submittedCount} deliverable(s) waiting for client review
                        </p>
                      )}
                    </div>
                  </div>

                  <div className="flex items-center gap-3">
                    <button
                      onClick={() => onSelectContract(contract.id)}
                      className="px-5 py-2.5 rounded-xl bg-gradient-to-r from-purple-600 to-indigo-600 hover:from-purple-500 hover:to-indigo-500 text-white font-semibold text-sm flex items-center gap-2 whitespace-nowrap shadow-glow"
                    >
                      <Activity className="w-4 h-4" />
                      Open Workspace & Escrow
                    </button>
                  </div>
                </div>
              );
            })
          )}
        </div>
      )}

      {/* Tab 2: AI Work Insights */}
      {activeTab === 'ai-insights' && (
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          <div className="glass-panel p-6 rounded-2xl border border-cyan-500/30 space-y-4">
            <div className="flex items-center gap-3">
              <div className="p-2.5 rounded-xl bg-cyan-500/10 border border-cyan-500/30 text-cyan-400">
                <Sparkles className="w-5 h-5" />
              </div>
              <div>
                <h3 className="font-bold text-white">AI Project Acceleration Signals</h3>
                <p className="text-xs text-slate-400">Proactive autonomous recommendations</p>
              </div>
            </div>

            <div className="space-y-3 pt-2">
              <div className="p-3.5 rounded-xl bg-slate-800/80 border border-slate-700 space-y-1">
                <div className="flex items-center justify-between">
                  <span className="text-xs font-semibold text-emerald-400 uppercase">Velocity Boost</span>
                  <span className="text-[10px] text-slate-400">Real-time</span>
                </div>
                <p className="text-sm text-slate-300">
                  Deliverable submission speeds are currently <strong>32% faster</strong> than marketplace baseline on PyTorch and LangChain contracts.
                </p>
              </div>

              <div className="p-3.5 rounded-xl bg-slate-800/80 border border-slate-700 space-y-1">
                <div className="flex items-center justify-between">
                  <span className="text-xs font-semibold text-purple-400 uppercase">Smart Escrow Guard</span>
                  <span className="text-[10px] text-slate-400">Active</span>
                </div>
                <p className="text-sm text-slate-300">
                  Pre-funding milestones automatically attaches verified CI/CD pipeline test verification to every deliverable pull request.
                </p>
              </div>
            </div>
          </div>

          <div className="glass-panel p-6 rounded-2xl border border-purple-500/30 space-y-4">
            <div className="flex items-center gap-3">
              <div className="p-2.5 rounded-xl bg-purple-500/10 border border-purple-500/30 text-purple-400">
                <Award className="w-5 h-5" />
              </div>
              <div>
                <h3 className="font-bold text-white">Reputation & Proof-of-Work Matrix</h3>
                <p className="text-xs text-slate-400">Immutable skill verification vector</p>
              </div>
            </div>

            <div className="space-y-3 pt-2">
              <div className="flex items-center justify-between p-3 rounded-xl bg-slate-800/60 border border-slate-700 text-sm">
                <span className="text-slate-300">Code Verifiability Index</span>
                <span className="font-mono text-emerald-400 font-bold">99.4 / 100</span>
              </div>
              <div className="flex items-center justify-between p-3 rounded-xl bg-slate-800/60 border border-slate-700 text-sm">
                <span className="text-slate-300">Smart Escrow Completion Rate</span>
                <span className="font-mono text-purple-400 font-bold">100.0%</span>
              </div>
              <div className="flex items-center justify-between p-3 rounded-xl bg-slate-800/60 border border-slate-700 text-sm">
                <span className="text-slate-300">AI Prompt Decomposition Quality</span>
                <span className="font-mono text-cyan-400 font-bold">Grade A+ (Autonomous)</span>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
