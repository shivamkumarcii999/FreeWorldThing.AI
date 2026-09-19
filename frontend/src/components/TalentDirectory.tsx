import React, { useState } from 'react';
import { ShieldCheck, Star, Github, ExternalLink, GitCommit, Award, CheckCircle2, DollarSign, MapPin, Sparkles } from 'lucide-react';
import { ProofOfWork, User } from '../types';

interface TalentDirectoryProps {
  talentList: any[];
  currentUser: User | null;
  onHireTalent: (freelancerId: number, name: string) => void;
}

export const TalentDirectory: React.FC<TalentDirectoryProps> = ({
  talentList,
  currentUser,
  onHireTalent,
}) => {
  const [selectedSpecialist, setSelectedSpecialist] = useState<any | null>(null);

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 py-8">
      {/* Header */}
      <div className="mb-8 flex flex-col md:flex-row md:items-end justify-between gap-4">
        <div>
          <div className="inline-flex items-center space-x-1.5 px-3 py-1 rounded-full bg-emerald-500/10 text-emerald-400 border border-emerald-500/20 text-xs font-bold mb-2">
            <ShieldCheck className="h-3.5 w-3.5" />
            <span>100% Evidence-Grounded Profiles</span>
          </div>
          <h2 className="text-2xl sm:text-3xl font-extrabold font-display text-white">
            Verified Technical Talent & Proofs
          </h2>
          <p className="text-sm text-slate-400 max-w-2xl">
            Engineers with verified GitHub repositories, measured test coverage, and milestone track records.
          </p>
        </div>
      </div>

      {/* Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {talentList.map((item) => {
          const user: User = item.user;
          const profile = item.profile;
          const proofs: ProofOfWork[] = item.proofs || [];

          return (
            <div
              key={user.id}
              className="rounded-2xl glass-card border border-slate-800 p-6 flex flex-col justify-between space-y-5 group hover:border-cyan-500/50 transition-all"
            >
              <div>
                {/* Header Profile */}
                <div className="flex items-start justify-between gap-3 mb-4">
                  <div className="flex items-center space-x-3">
                    <img
                      src={user.avatarUrl || 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=150&q=80'}
                      alt={user.fullName}
                      className="h-14 w-14 rounded-2xl object-cover ring-2 ring-cyan-500/40"
                    />
                    <div>
                      <div className="flex items-center space-x-1.5">
                        <h3 className="font-bold text-base text-white group-hover:text-cyan-300 transition-colors">
                          {user.fullName}
                        </h3>
                        {profile?.verifiedBadge && <ShieldCheck className="h-4 w-4 text-emerald-400" />}
                      </div>
                      <p className="text-xs text-slate-400 line-clamp-1">{profile?.title || user.headline}</p>
                      <div className="flex items-center space-x-2 text-[11px] text-slate-400 mt-1">
                        <span className="flex items-center space-x-0.5 text-amber-400 font-bold">
                          <Star className="h-3 w-3 fill-amber-400" />
                          <span>{profile?.rating || user.ratingAvg || 5.0}</span>
                        </span>
                        <span>•</span>
                        <span className="flex items-center space-x-1">
                          <MapPin className="h-3 w-3" />
                          <span>{profile?.location || user.location || 'Remote'}</span>
                        </span>
                      </div>
                    </div>
                  </div>
                  <div className="text-right shrink-0">
                    <span className="text-sm font-extrabold text-emerald-400">
                      ₹{(profile?.hourlyRate || user.hourlyRate || 3000).toLocaleString()}/hr
                    </span>
                    <div className="text-[10px] text-slate-400">{user.completedProjects || 20}+ done</div>
                  </div>
                </div>

                {/* Proof of Work Score Badge */}
                <div className="p-3 rounded-xl bg-slate-900/90 border border-slate-800 mb-4 flex items-center justify-between">
                  <div className="flex items-center space-x-2">
                    <Award className="h-4 w-4 text-cyan-400" />
                    <div>
                      <div className="text-[10px] text-slate-400 font-bold uppercase tracking-wider">Proof-of-Work Score</div>
                      <div className="text-xs text-slate-300">Code & Commit Verified</div>
                    </div>
                  </div>
                  <span className="text-base font-extrabold gradient-text">
                    {profile?.proofOfWorkScore || 98.0}/100
                  </span>
                </div>

                {/* Skills */}
                <div className="flex flex-wrap gap-1.5 mb-4">
                  {(profile?.skills?.split(',') || ['Python', 'FastAPI', 'React']).slice(0, 4).map((s: string, idx: number) => (
                    <span key={idx} className="px-2 py-0.5 text-[11px] rounded-md bg-slate-800/80 border border-slate-700/60 text-slate-300">
                      {s.trim()}
                    </span>
                  ))}
                </div>

                {/* Top Proof Highlight */}
                {proofs.length > 0 && (
                  <div className="p-3 rounded-xl bg-slate-900/60 border border-slate-800/60 space-y-1.5">
                    <div className="flex items-center justify-between text-[11px]">
                      <span className="font-bold text-slate-200 truncate">{proofs[0].projectTitle}</span>
                      <span className="text-emerald-400 font-semibold">{proofs[0].testCoveragePercent}% Test Cov</span>
                    </div>
                    <p className="text-[11px] text-slate-400 line-clamp-2 leading-relaxed">{proofs[0].solution}</p>
                    <div className="flex items-center space-x-3 pt-1 text-[11px] text-slate-400">
                      <span className="flex items-center space-x-1">
                        <GitCommit className="h-3 w-3 text-cyan-400" />
                        <span>{proofs[0].commitCount || 100}+ commits</span>
                      </span>
                      {proofs[0].repoUrl && (
                        <a href={proofs[0].repoUrl} target="_blank" rel="noreferrer" className="flex items-center space-x-1 text-cyan-400 hover:underline">
                          <Github className="h-3 w-3" />
                          <span>Repo</span>
                        </a>
                      )}
                    </div>
                  </div>
                )}
              </div>

              {/* Action Buttons */}
              <div className="flex items-center space-x-2 pt-3 border-t border-slate-800/80">
                <button
                  onClick={() => setSelectedSpecialist(item)}
                  className="flex-1 py-2 text-xs font-semibold rounded-xl glass-panel hover:bg-slate-800 text-slate-300 hover:text-white transition-all text-center"
                >
                  View Proofs ({proofs.length})
                </button>
                <button
                  onClick={() => onHireTalent(user.id, user.fullName)}
                  className="px-4 py-2 text-xs font-bold rounded-xl bg-gradient-to-r from-emerald-500 to-cyan-500 hover:from-emerald-400 hover:to-cyan-400 text-slate-950 shadow-glow-emerald transition-all active:scale-95 shrink-0"
                >
                  Hire →
                </button>
              </div>
            </div>
          );
        })}
      </div>

      {/* Proof of Work Modal */}
      {selectedSpecialist && (
        <div className="fixed inset-0 z-50 overflow-y-auto bg-black/80 backdrop-blur-md flex items-center justify-center p-4">
          <div className="relative w-full max-w-3xl glass-panel rounded-2xl border border-slate-700 shadow-2xl p-6 sm:p-8 space-y-6 max-h-[85vh] overflow-y-auto">
            <div className="flex items-center justify-between pb-3 border-b border-slate-800">
              <div className="flex items-center space-x-3">
                <img
                  src={selectedSpecialist.user.avatarUrl}
                  alt={selectedSpecialist.user.fullName}
                  className="h-12 w-12 rounded-xl object-cover ring-2 ring-cyan-500/40"
                />
                <div>
                  <h3 className="text-lg font-bold text-white flex items-center space-x-1.5">
                    <span>{selectedSpecialist.user.fullName}</span>
                    <ShieldCheck className="h-4 w-4 text-emerald-400" />
                  </h3>
                  <p className="text-xs text-slate-400">{selectedSpecialist.profile?.title}</p>
                </div>
              </div>
              <button onClick={() => setSelectedSpecialist(null)} className="text-slate-400 hover:text-white">✕</button>
            </div>

            <div className="space-y-4">
              <h4 className="text-xs font-bold text-slate-400 uppercase tracking-wider">Verified Proof-of-Work Projects</h4>
              {selectedSpecialist.proofs?.map((p: ProofOfWork) => (
                <div key={p.id} className="p-4 rounded-xl bg-slate-900/90 border border-slate-800 space-y-3">
                  <div className="flex items-start justify-between">
                    <div>
                      <h5 className="font-bold text-sm text-white">{p.projectTitle}</h5>
                      <p className="text-xs text-cyan-300 font-medium">{p.technologies}</p>
                    </div>
                    <div className="flex items-center space-x-2">
                      <span className="px-2 py-0.5 text-xs font-bold rounded bg-emerald-500/20 text-emerald-300 border border-emerald-500/30">
                        {p.evidenceScore || 98}/100 Evidence
                      </span>
                    </div>
                  </div>

                  <div className="space-y-2 text-xs text-slate-300">
                    <p><strong className="text-slate-400">Problem:</strong> {p.problem}</p>
                    <p><strong className="text-slate-400">Engineering Solution:</strong> {p.solution}</p>
                  </div>

                  <div className="flex items-center justify-between pt-2 border-t border-slate-800 text-xs">
                    <div className="flex items-center space-x-4 text-slate-400">
                      <span>✓ {p.testCoveragePercent}% Test Coverage</span>
                      <span>✓ {p.commitCount} Git Commits</span>
                    </div>
                    {p.repoUrl && (
                      <a
                        href={p.repoUrl}
                        target="_blank"
                        rel="noreferrer"
                        className="flex items-center space-x-1 text-cyan-400 hover:underline font-semibold"
                      >
                        <Github className="h-3.5 w-3.5" />
                        <span>Inspect Repository</span>
                      </a>
                    )}
                  </div>
                </div>
              ))}
            </div>

            <div className="flex items-center justify-end space-x-3 pt-3 border-t border-slate-800">
              <button
                onClick={() => setSelectedSpecialist(null)}
                className="px-4 py-2 text-sm text-slate-400 hover:text-white"
              >
                Close
              </button>
              <button
                onClick={() => {
                  onHireTalent(selectedSpecialist.user.id, selectedSpecialist.user.fullName);
                  setSelectedSpecialist(null);
                }}
                className="px-6 py-2.5 rounded-xl bg-gradient-to-r from-emerald-500 to-cyan-500 text-slate-950 font-bold text-sm shadow-glow-emerald"
              >
                Hire Specialist →
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
