import React, { useState } from 'react';
import { Briefcase, Sparkles, Clock, DollarSign, Send, CheckCircle2, ChevronRight, UserCheck, ShieldCheck } from 'lucide-react';
import { Job, Proposal, TalentMatchResult, User } from '../types';
import { api } from '../services/api';

interface JobsFeedProps {
  jobs: Job[];
  currentUser: User | null;
  onRefreshJobs: () => void;
  onOpenContract: (contractId: number) => void;
}

export const JobsFeed: React.FC<JobsFeedProps> = ({
  jobs,
  currentUser,
  onRefreshJobs,
  onOpenContract,
}) => {
  const [selectedCategory, setSelectedCategory] = useState('ALL');
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedJob, setSelectedJob] = useState<Job | null>(null);
  const [matches, setMatches] = useState<TalentMatchResult[]>([]);
  const [proposals, setProposals] = useState<Proposal[]>([]);
  const [showApplyModal, setShowApplyModal] = useState(false);
  const [coverLetter, setCoverLetter] = useState('');
  const [bidAmount, setBidAmount] = useState<number>(50000);
  const [isGeneratingProposal, setIsGeneratingProposal] = useState(false);
  const [isSubmittingProposal, setIsSubmittingProposal] = useState(false);
  const [proposalScore, setProposalScore] = useState<number | null>(null);

  const categories = ['ALL', 'AI/ML', 'Full Stack', 'Mobile', 'UI/UX', 'Cloud'];

  const filteredJobs = jobs.filter((j) => {
    const matchesCat = selectedCategory === 'ALL' || (j.category && j.category.toUpperCase().includes(selectedCategory.toUpperCase().replace('/', '_')));
    const matchesSearch =
      j.title.toLowerCase().includes(searchTerm.toLowerCase()) ||
      j.description.toLowerCase().includes(searchTerm.toLowerCase()) ||
      (j.requiredSkills && j.requiredSkills.some((s) => s.toLowerCase().includes(searchTerm.toLowerCase())));
    return matchesCat && matchesSearch;
  });

  const handleSelectJob = async (job: Job) => {
    setSelectedJob(job);
    setBidAmount(job.budgetMin || 50000);
    try {
      const [matchRes, propRes] = await Promise.all([
        api.getJobMatches(job.id),
        api.getProposalsForJob(job.id),
      ]);
      setMatches(matchRes);
      setProposals(propRes);
    } catch (err) {
      console.error(err);
    }
  };

  const handleGenerateAiProposal = async () => {
    if (!selectedJob) return;
    setIsGeneratingProposal(true);
    try {
      const res = await api.generateProposalDraft(selectedJob.id, currentUser?.id || 3, 'Specialized in production delivery.');
      setCoverLetter(res.coverLetter);
      setBidAmount(res.suggestedBudget || selectedJob.budgetMin || 50000);
      setProposalScore(res.qualityScore);
    } catch (err) {
      console.error(err);
      alert('Failed to generate AI proposal draft.');
    } finally {
      setIsGeneratingProposal(false);
    }
  };

  const handleSubmitProposal = async () => {
    if (!selectedJob || !coverLetter.trim()) return;
    setIsSubmittingProposal(true);
    try {
      await api.submitProposal({
        jobId: selectedJob.id,
        freelancerId: currentUser?.id || 3,
        freelancerName: currentUser?.fullName || 'Aravind Sharma',
        coverLetter: coverLetter.trim(),
        proposedBudget: Number(bidAmount),
        proposedDurationDays: (selectedJob.durationWeeks || 4) * 7,
      });
      alert('🎉 Proposal submitted successfully with AI Quality verification!');
      setShowApplyModal(false);
      handleSelectJob(selectedJob);
      onRefreshJobs();
    } catch (err) {
      console.error(err);
      alert('Failed to submit proposal.');
    } finally {
      setIsSubmittingProposal(false);
    }
  };

  const handleHireDirectly = async (freelancerId: number, freelancerName: string, amount: number) => {
    if (!selectedJob) return;
    try {
      const contract = await api.createContract({
        jobId: selectedJob.id,
        title: selectedJob.title,
        clientId: currentUser?.id || 1,
        clientName: currentUser?.fullName || 'Alex Morgan',
        freelancerId,
        freelancerName,
        totalAmount: amount,
        currency: selectedJob.currency || 'INR',
        milestones: [
          { sequenceOrder: 1, title: 'Phase 1 Core Implementation', description: 'Core architecture and deliverables.', amount: amount * 0.4, daysFromStart: 7 },
          { sequenceOrder: 2, title: 'Phase 2 Testing & Handover', description: 'Testing, polish and deployment.', amount: amount * 0.6, daysFromStart: 14 },
        ],
      });
      alert('🤝 Contract created! Opening Escrow Workspace...');
      onOpenContract(contract.id);
    } catch (err) {
      console.error(err);
      alert('Failed to create contract.');
    }
  };

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 py-8">
      {/* Category Pills & Search */}
      <div className="flex flex-col md:flex-row items-center justify-between gap-4 mb-8">
        <div className="flex items-center space-x-2 overflow-x-auto w-full md:w-auto pb-2 md:pb-0">
          {categories.map((c) => (
            <button
              key={c}
              onClick={() => setSelectedCategory(c)}
              className={`px-4 py-2 rounded-xl text-xs font-bold transition-all shrink-0 ${
                selectedCategory === c
                  ? 'bg-cyan-500 text-slate-950 shadow-glow-cyan'
                  : 'glass-card text-slate-300 hover:text-white'
              }`}
            >
              {c}
            </button>
          ))}
        </div>

        <div className="w-full md:w-72">
          <input
            type="text"
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            placeholder="Filter jobs or skills..."
            className="w-full glass-input rounded-xl px-4 py-2 text-sm focus:border-cyan-400 focus:outline-none"
          />
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-12 gap-6">
        {/* Jobs List */}
        <div className="lg:col-span-6 space-y-4">
          <div className="flex items-center justify-between px-1">
            <span className="text-xs font-bold text-slate-400 uppercase tracking-wider">
              Available Jobs ({filteredJobs.length})
            </span>
            <span className="text-xs text-cyan-400 font-medium">Auto-matched with verified proof-of-work</span>
          </div>

          {filteredJobs.map((j) => {
            const isSelected = selectedJob?.id === j.id;
            return (
              <div
                key={j.id}
                onClick={() => handleSelectJob(j)}
                className={`p-5 rounded-2xl glass-card cursor-pointer border transition-all ${
                  isSelected
                    ? 'border-cyan-500 bg-slate-900/90 shadow-glow-cyan'
                    : 'border-slate-800 hover:border-slate-700'
                }`}
              >
                <div className="flex items-start justify-between gap-3 mb-2">
                  <div>
                    <span className="text-[10px] font-bold px-2 py-0.5 rounded bg-purple-500/20 text-purple-300 border border-purple-500/30 uppercase">
                      {j.category || 'DEVELOPMENT'}
                    </span>
                    <h3 className="text-base font-bold text-white mt-1.5 leading-snug">{j.title}</h3>
                  </div>
                  <div className="text-right shrink-0">
                    <span className="text-sm font-extrabold text-emerald-400">
                      ₹{j.budgetMin ? j.budgetMin.toLocaleString() : '50,000'} - ₹{j.budgetMax ? j.budgetMax.toLocaleString() : '150,000'}
                    </span>
                    <div className="text-[10px] text-slate-400">{j.projectType || 'Fixed Price'}</div>
                  </div>
                </div>

                <p className="text-xs text-slate-300 line-clamp-2 mb-3 leading-relaxed">{j.description}</p>

                <div className="flex flex-wrap gap-1.5 mb-4">
                  {j.requiredSkills?.slice(0, 4).map((s, i) => (
                    <span key={i} className="px-2 py-0.5 text-[11px] rounded-md bg-slate-800/80 border border-slate-700/60 text-slate-300">
                      {s}
                    </span>
                  ))}
                  {j.requiredSkills && j.requiredSkills.length > 4 && (
                    <span className="px-2 py-0.5 text-[11px] rounded-md bg-slate-800/40 text-slate-500">
                      +{j.requiredSkills.length - 4} more
                    </span>
                  )}
                </div>

                <div className="flex items-center justify-between pt-3 border-t border-slate-800/80 text-xs text-slate-400">
                  <div className="flex items-center space-x-3">
                    <span className="flex items-center space-x-1">
                      <Clock className="h-3.5 w-3.5 text-cyan-400" />
                      <span>{j.durationWeeks || 4} wks</span>
                    </span>
                    <span className="flex items-center space-x-1">
                      <UserCheck className="h-3.5 w-3.5 text-indigo-400" />
                      <span>{j.proposalCount || 0} proposals</span>
                    </span>
                  </div>
                  <span className="flex items-center space-x-1 text-cyan-300 font-semibold group-hover:translate-x-1 transition-transform">
                    <span>View Matches</span>
                    <ChevronRight className="h-4 w-4" />
                  </span>
                </div>
              </div>
            );
          })}
        </div>

        {/* Selected Job & Match Panel */}
        <div className="lg:col-span-6">
          {selectedJob ? (
            <div className="glass-panel rounded-2xl border border-slate-800 p-6 sticky top-24 space-y-6 max-h-[85vh] overflow-y-auto">
              {/* Top info */}
              <div className="space-y-2 pb-4 border-b border-slate-800">
                <div className="flex items-center justify-between">
                  <span className="px-2.5 py-0.5 text-xs font-bold rounded-lg bg-cyan-500/20 text-cyan-300 border border-cyan-500/30 uppercase">
                    {selectedJob.category}
                  </span>
                  <span className="text-xs text-slate-400">Status: <span className="text-emerald-400 font-bold">{selectedJob.status}</span></span>
                </div>
                <h2 className="text-xl font-bold font-display text-white">{selectedJob.title}</h2>
                <div className="flex flex-wrap items-center gap-4 text-xs text-slate-300 pt-1">
                  <span className="flex items-center space-x-1 text-emerald-400 font-bold">
                    <DollarSign className="h-4 w-4" />
                    <span>Budget: ₹{selectedJob.budgetMin?.toLocaleString()} - ₹{selectedJob.budgetMax?.toLocaleString()}</span>
                  </span>
                  <span className="flex items-center space-x-1">
                    <Clock className="h-4 w-4 text-cyan-400" />
                    <span>Est: {selectedJob.durationWeeks || 4} Weeks</span>
                  </span>
                </div>
              </div>

              {/* Action Buttons */}
              <div className="flex items-center gap-3">
                <button
                  onClick={() => {
                    setShowApplyModal(true);
                    handleGenerateAiProposal();
                  }}
                  className="flex-1 flex items-center justify-center space-x-2 py-3 rounded-xl bg-gradient-to-r from-cyan-500 to-indigo-600 hover:from-cyan-400 hover:to-indigo-500 text-white font-bold text-sm shadow-glow-cyan transition-all active:scale-95"
                >
                  <Sparkles className="h-4 w-4" />
                  <span>Apply with AI Proposal</span>
                </button>
              </div>

              {/* Description */}
              <div>
                <h4 className="text-xs font-bold text-slate-400 uppercase tracking-wider mb-2">Project Brief</h4>
                <div className="p-4 rounded-xl bg-slate-900/60 border border-slate-800/80 text-xs sm:text-sm text-slate-300 whitespace-pre-line leading-relaxed">
                  {selectedJob.description}
                </div>
              </div>

              {/* AI Matched Candidates */}
              <div>
                <div className="flex items-center justify-between mb-3">
                  <h4 className="text-xs font-bold text-white uppercase tracking-wider flex items-center space-x-1.5">
                    <Sparkles className="h-3.5 w-3.5 text-cyan-400" />
                    <span>Smart AI Matched Talent ({matches.length})</span>
                  </h4>
                  <span className="text-[10px] text-cyan-400 font-medium">Ranked by Proof-of-Work</span>
                </div>

                <div className="space-y-3">
                  {matches.map((m) => (
                    <div key={m.userId} className="p-4 rounded-xl glass-card border border-slate-700/80 space-y-3">
                      <div className="flex items-start justify-between gap-2">
                        <div className="flex items-center space-x-3">
                          <img
                            src={m.avatarUrl || 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=150&q=80'}
                            alt={m.fullName}
                            className="h-10 w-10 rounded-full object-cover ring-2 ring-cyan-500/30"
                          />
                          <div>
                            <div className="flex items-center space-x-1.5">
                              <span className="text-sm font-bold text-white">{m.fullName}</span>
                              {m.verifiedBadge && <ShieldCheck className="h-4 w-4 text-emerald-400" />}
                            </div>
                            <p className="text-xs text-slate-400">{m.title}</p>
                          </div>
                        </div>
                        <div className="text-right">
                          <span className="px-2 py-0.5 text-xs font-extrabold rounded-md bg-emerald-500/20 text-emerald-300 border border-emerald-500/30">
                            {m.matchScore}% Match
                          </span>
                          <div className="text-[10px] text-slate-400 mt-1">₹{m.hourlyRate?.toLocaleString()}/hr</div>
                        </div>
                      </div>

                      {/* Match reasoning */}
                      <p className="text-xs text-slate-300 bg-slate-900/80 p-2 rounded-lg border border-slate-800">
                        🤖 <span className="font-semibold text-cyan-300">AI Match Note:</span> {m.matchReasoning}
                      </p>

                      <div className="flex items-center justify-between pt-2 border-t border-slate-800/80 text-xs">
                        <span className="text-slate-400">Proof: <span className="text-slate-200">{m.topVerifiedProject}</span></span>
                        <button
                          onClick={() => handleHireDirectly(m.userId, m.fullName, selectedJob.budgetMin || 50000)}
                          className="px-3 py-1 rounded-lg bg-emerald-500/20 text-emerald-300 hover:bg-emerald-500/30 border border-emerald-500/30 font-bold transition-all"
                        >
                          Hire with Escrow →
                        </button>
                      </div>
                    </div>
                  ))}
                </div>
              </div>
            </div>
          ) : (
            <div className="glass-panel rounded-2xl border border-slate-800 p-12 text-center text-slate-400">
              <Briefcase className="h-12 w-12 mx-auto text-slate-600 mb-3" />
              <h3 className="text-base font-bold text-white mb-1">Select a Job from the Feed</h3>
              <p className="text-xs max-w-sm mx-auto">
                Click any posted opportunity to view AI candidate matching, requirement breakdowns, and submit proposals.
              </p>
            </div>
          )}
        </div>
      </div>

      {/* AI Proposal Drawer / Modal */}
      {showApplyModal && selectedJob && (
        <div className="fixed inset-0 z-50 overflow-y-auto bg-black/80 backdrop-blur-md flex items-center justify-center p-4">
          <div className="relative w-full max-w-2xl glass-panel rounded-2xl border border-slate-700 shadow-2xl p-6 sm:p-8 space-y-6">
            <div className="flex items-center justify-between pb-3 border-b border-slate-800">
              <div className="flex items-center space-x-2">
                <Sparkles className="h-5 w-5 text-cyan-400" />
                <h3 className="text-lg font-bold text-white">AI Proposal Assistant</h3>
              </div>
              <button onClick={() => setShowApplyModal(false)} className="text-slate-400 hover:text-white">✕</button>
            </div>

            <div className="space-y-4">
              <div>
                <label className="block text-xs font-bold text-slate-300 mb-1">Job Title</label>
                <p className="text-sm font-semibold text-white">{selectedJob.title}</p>
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-xs font-bold text-slate-300 mb-1">Proposed Budget (₹)</label>
                  <input
                    type="number"
                    value={bidAmount}
                    onChange={(e) => setBidAmount(Number(e.target.value))}
                    className="w-full glass-input rounded-xl p-2.5 text-sm focus:border-cyan-400 focus:outline-none text-emerald-400 font-bold"
                  />
                </div>
                <div>
                  <label className="block text-xs font-bold text-slate-300 mb-1">AI Quality Score</label>
                  <div className="flex items-center space-x-2 p-2.5 rounded-xl bg-slate-900 border border-slate-800">
                    <span className="text-sm font-extrabold text-emerald-400">{proposalScore || 96}%</span>
                    <span className="text-[10px] text-slate-400">• Evidence Grounded</span>
                  </div>
                </div>
              </div>

              <div>
                <div className="flex items-center justify-between mb-1">
                  <label className="block text-xs font-bold text-slate-300">Cover Letter & Technical Approach</label>
                  <button
                    onClick={handleGenerateAiProposal}
                    disabled={isGeneratingProposal}
                    className="text-xs text-cyan-400 hover:text-cyan-300 font-semibold flex items-center space-x-1"
                  >
                    <Sparkles className="h-3 w-3" />
                    <span>{isGeneratingProposal ? 'Generating...' : 'Regenerate with AI'}</span>
                  </button>
                </div>
                <textarea
                  rows={8}
                  value={coverLetter}
                  onChange={(e) => setCoverLetter(e.target.value)}
                  className="w-full glass-input rounded-xl p-3 text-xs sm:text-sm text-slate-200 focus:border-cyan-400 focus:outline-none resize-none font-mono"
                />
              </div>
            </div>

            <div className="flex items-center justify-end space-x-3 pt-3 border-t border-slate-800">
              <button onClick={() => setShowApplyModal(false)} className="px-4 py-2 text-sm text-slate-400 hover:text-white">
                Cancel
              </button>
              <button
                onClick={handleSubmitProposal}
                disabled={isSubmittingProposal || !coverLetter.trim()}
                className="flex items-center space-x-2 px-6 py-2.5 rounded-xl bg-gradient-to-r from-cyan-500 to-indigo-600 text-white font-bold text-sm shadow-glow-cyan active:scale-95 disabled:opacity-50"
              >
                <Send className="h-4 w-4" />
                <span>{isSubmittingProposal ? 'Submitting...' : 'Submit Verified Proposal'}</span>
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
