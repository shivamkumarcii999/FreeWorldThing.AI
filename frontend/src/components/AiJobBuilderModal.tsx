import React, { useState, useEffect } from 'react';
import { X, Sparkles, Plus, Trash2, ArrowRight, CheckCircle, Clock, DollarSign, Cpu } from 'lucide-react';
import { api } from '../services/api';
import { AiJobSpecResponse, SuggestedMilestone, User } from '../types';

interface AiJobBuilderModalProps {
  isOpen: boolean;
  onClose: () => void;
  initialPrompt?: string;
  currentUser: User | null;
  onJobCreated: (newJob: any) => void;
}

export const AiJobBuilderModal: React.FC<AiJobBuilderModalProps> = ({
  isOpen,
  onClose,
  initialPrompt = '',
  currentUser,
  onJobCreated,
}) => {
  const [prompt, setPrompt] = useState(initialPrompt);
  const [currency, setCurrency] = useState('INR');
  const [isAnalyzing, setIsAnalyzing] = useState(false);
  const [isPublishing, setIsPublishing] = useState(false);
  const [jobSpec, setJobSpec] = useState<AiJobSpecResponse | null>(null);

  useEffect(() => {
    if (initialPrompt) {
      setPrompt(initialPrompt);
      handleAnalyze(initialPrompt);
    }
  }, [initialPrompt]);

  if (!isOpen) return null;

  const handleAnalyze = async (p = prompt) => {
    if (!p.trim()) return;
    setIsAnalyzing(true);
    try {
      const spec = await api.generateJobSpec(p.trim(), currency);
      setJobSpec(spec);
    } catch (err) {
      console.error(err);
      alert('Failed to analyze prompt with AI.');
    } finally {
      setIsAnalyzing(false);
    }
  };

  const handleAddMilestone = () => {
    if (!jobSpec) return;
    const newM: SuggestedMilestone = {
      sequenceOrder: jobSpec.suggestedMilestones.length + 1,
      title: 'New Milestone Sprint',
      description: 'Scope and deliver sprint items.',
      suggestedAmount: 20000,
      estimatedDays: 7,
    };
    setJobSpec({
      ...jobSpec,
      suggestedMilestones: [...jobSpec.suggestedMilestones, newM],
    });
  };

  const handleRemoveMilestone = (idx: number) => {
    if (!jobSpec) return;
    const updated = jobSpec.suggestedMilestones.filter((_, i) => i !== idx);
    setJobSpec({
      ...jobSpec,
      suggestedMilestones: updated.map((m, i) => ({ ...m, sequenceOrder: i + 1 })),
    });
  };

  const handleMilestoneChange = (idx: number, field: keyof SuggestedMilestone, val: any) => {
    if (!jobSpec) return;
    const updated = [...jobSpec.suggestedMilestones];
    updated[idx] = { ...updated[idx], [field]: val };
    setJobSpec({ ...jobSpec, suggestedMilestones: updated });
  };

  const handlePublish = async () => {
    if (!jobSpec) return;
    setIsPublishing(true);
    try {
      const payload = {
        clientId: currentUser?.id || 1,
        clientName: currentUser?.fullName || 'Alex Morgan',
        title: jobSpec.title,
        description: jobSpec.description,
        category: jobSpec.category,
        requiredSkills: jobSpec.requiredSkills,
        minBudget: jobSpec.minBudget,
        maxBudget: jobSpec.maxBudget,
        budgetCurrency: jobSpec.budgetCurrency,
        estimatedDurationWeeks: jobSpec.estimatedDurationWeeks,
        complexity: jobSpec.complexity,
        projectType: jobSpec.projectType,
        suggestedMilestonesJson: JSON.stringify(jobSpec.suggestedMilestones),
      };

      const created = await api.createJob(payload);
      alert('🎉 Job published successfully with AI Milestones!');
      onJobCreated(created);
      onClose();
    } catch (err) {
      console.error(err);
      alert('Error publishing job.');
    } finally {
      setIsPublishing(false);
    }
  };

  return (
    <div className="fixed inset-0 z-50 overflow-y-auto bg-black/80 backdrop-blur-md flex items-center justify-center p-4">
      <div className="relative w-full max-w-4xl glass-panel rounded-2xl border border-slate-700 shadow-2xl p-6 sm:p-8 overflow-hidden max-h-[90vh] flex flex-col">
        {/* Header */}
        <div className="flex items-center justify-between pb-4 border-b border-slate-800">
          <div className="flex items-center space-x-2">
            <div className="p-2 rounded-lg bg-cyan-500/20 text-cyan-400 border border-cyan-500/30">
              <Sparkles className="h-5 w-5" />
            </div>
            <div>
              <h3 className="text-lg sm:text-xl font-bold font-display text-white">AI Job Builder & Spec Studio</h3>
              <p className="text-xs text-slate-400">Transform natural language prompts into production project roadmaps</p>
            </div>
          </div>
          <button onClick={onClose} className="p-2 text-slate-400 hover:text-white rounded-lg hover:bg-slate-800 transition-colors">
            <X className="h-5 w-5" />
          </button>
        </div>

        {/* Scrollable Body */}
        <div className="flex-1 overflow-y-auto py-6 space-y-6 pr-1">
          {/* Prompt Box */}
          <div>
            <label className="block text-xs font-semibold text-slate-300 uppercase tracking-wider mb-2">
              Describe your project requirement
            </label>
            <div className="flex gap-2">
              <textarea
                rows={2}
                value={prompt}
                onChange={(e) => setPrompt(e.target.value)}
                placeholder="e.g. Build an AI customer support chatbot with Python, RAG, FastAPI and React..."
                className="flex-1 glass-input rounded-xl p-3 text-sm focus:ring-1 focus:ring-cyan-400 resize-none"
              />
              <button
                onClick={() => handleAnalyze()}
                disabled={isAnalyzing || !prompt.trim()}
                className="px-5 rounded-xl bg-gradient-to-r from-cyan-500 to-indigo-600 hover:from-cyan-400 hover:to-indigo-500 text-white font-bold text-sm shadow-glow-cyan flex items-center justify-center space-x-2 disabled:opacity-50 shrink-0"
              >
                {isAnalyzing ? (
                  <span className="inline-block animate-spin">🌀</span>
                ) : (
                  <>
                    <Sparkles className="h-4 w-4" />
                    <span className="hidden sm:inline">Decompose</span>
                  </>
                )}
              </button>
            </div>
          </div>

          {/* AI Result Spec */}
          {jobSpec && (
            <div className="space-y-6 animate-fade-in">
              <div className="p-4 rounded-xl bg-slate-900/80 border border-slate-800 space-y-4">
                <div className="flex flex-wrap items-center justify-between gap-2 pb-3 border-b border-slate-800">
                  <div>
                    <span className="text-[10px] font-bold px-2 py-0.5 rounded bg-cyan-500/20 text-cyan-300 border border-cyan-500/30 uppercase">
                      Category: {jobSpec.category}
                    </span>
                    <h4 className="text-lg font-bold text-white mt-1">{jobSpec.title}</h4>
                  </div>
                  <div className="flex items-center space-x-2 text-xs">
                    <span className="text-slate-400">AI Confidence:</span>
                    <span className="font-bold text-emerald-400">{(jobSpec.aiConfidenceScore * 100).toFixed(0)}%</span>
                  </div>
                </div>

                {/* Grid Info */}
                <div className="grid grid-cols-2 sm:grid-cols-4 gap-3">
                  <div className="p-3 rounded-lg bg-slate-800/60 border border-slate-700/50">
                    <div className="flex items-center space-x-1.5 text-slate-400 text-xs mb-1">
                      <DollarSign className="h-3.5 w-3.5 text-emerald-400" />
                      <span>Budget Range</span>
                    </div>
                    <p className="text-sm font-bold text-white">
                      ₹{jobSpec.minBudget?.toLocaleString()} - ₹{jobSpec.maxBudget?.toLocaleString()}
                    </p>
                  </div>
                  <div className="p-3 rounded-lg bg-slate-800/60 border border-slate-700/50">
                    <div className="flex items-center space-x-1.5 text-slate-400 text-xs mb-1">
                      <Clock className="h-3.5 w-3.5 text-cyan-400" />
                      <span>Estimated Duration</span>
                    </div>
                    <p className="text-sm font-bold text-white">{jobSpec.estimatedDurationWeeks} Weeks</p>
                  </div>
                  <div className="p-3 rounded-lg bg-slate-800/60 border border-slate-700/50">
                    <div className="flex items-center space-x-1.5 text-slate-400 text-xs mb-1">
                      <Cpu className="h-3.5 w-3.5 text-purple-400" />
                      <span>Complexity</span>
                    </div>
                    <p className="text-sm font-bold text-white">{jobSpec.complexity}</p>
                  </div>
                  <div className="p-3 rounded-lg bg-slate-800/60 border border-slate-700/50">
                    <div className="flex items-center space-x-1.5 text-slate-400 text-xs mb-1">
                      <CheckCircle className="h-3.5 w-3.5 text-indigo-400" />
                      <span>Project Type</span>
                    </div>
                    <p className="text-sm font-bold text-white">{jobSpec.projectType}</p>
                  </div>
                </div>

                {/* Skills */}
                <div>
                  <label className="block text-xs font-semibold text-slate-400 mb-1.5">Required Tech Stack & Skills:</label>
                  <div className="flex flex-wrap gap-1.5">
                    {jobSpec.requiredSkills?.map((skill, i) => (
                      <span key={i} className="px-2.5 py-1 text-xs rounded-lg bg-slate-800 border border-slate-700 text-slate-200">
                        {skill}
                      </span>
                    ))}
                  </div>
                </div>
              </div>

              {/* Milestone Roadmap */}
              <div>
                <div className="flex items-center justify-between mb-3">
                  <div className="flex items-center space-x-2">
                    <h4 className="text-sm font-bold text-white uppercase tracking-wider">
                      🎯 AI-Suggested Milestone Roadmap ({jobSpec.suggestedMilestones?.length || 0})
                    </h4>
                    <span className="text-xs text-slate-400">• Fully customizable</span>
                  </div>
                  <button
                    onClick={handleAddMilestone}
                    className="flex items-center space-x-1 px-2.5 py-1 text-xs font-semibold rounded-lg bg-slate-800 hover:bg-slate-700 text-cyan-300 border border-slate-700 transition-all"
                  >
                    <Plus className="h-3.5 w-3.5" />
                    <span>Add Milestone</span>
                  </button>
                </div>

                <div className="space-y-3">
                  {jobSpec.suggestedMilestones?.map((m, idx) => (
                    <div key={idx} className="p-3.5 rounded-xl glass-card border border-slate-700/80 space-y-2 relative">
                      <div className="flex items-center justify-between gap-2">
                        <div className="flex items-center space-x-2 flex-1">
                          <span className="h-6 w-6 rounded-full bg-cyan-500/20 text-cyan-300 border border-cyan-500/30 flex items-center justify-center text-xs font-bold shrink-0">
                            {idx + 1}
                          </span>
                          <input
                            type="text"
                            value={m.title}
                            onChange={(e) => handleMilestoneChange(idx, 'title', e.target.value)}
                            className="font-semibold text-sm text-white bg-transparent border-b border-transparent hover:border-slate-600 focus:border-cyan-400 focus:outline-none w-full"
                          />
                        </div>
                        <div className="flex items-center space-x-2">
                          <div className="flex items-center space-x-1 text-emerald-400 font-bold text-xs bg-emerald-500/10 px-2 py-1 rounded border border-emerald-500/20">
                            <span>₹</span>
                            <input
                              type="number"
                              value={m.suggestedAmount}
                              onChange={(e) => handleMilestoneChange(idx, 'suggestedAmount', Number(e.target.value))}
                              className="w-16 bg-transparent text-emerald-300 text-right focus:outline-none"
                            />
                          </div>
                          <button
                            onClick={() => handleRemoveMilestone(idx)}
                            className="p-1 text-slate-500 hover:text-rose-400 rounded transition-colors"
                          >
                            <Trash2 className="h-4 w-4" />
                          </button>
                        </div>
                      </div>
                      <textarea
                        rows={2}
                        value={m.description}
                        onChange={(e) => handleMilestoneChange(idx, 'description', e.target.value)}
                        className="w-full text-xs text-slate-300 bg-slate-900/60 p-2 rounded-lg border border-slate-800 focus:outline-none focus:border-slate-600 resize-none"
                      />
                    </div>
                  ))}
                </div>
              </div>
            </div>
          )}
        </div>

        {/* Footer Actions */}
        <div className="pt-4 border-t border-slate-800 flex items-center justify-between">
          <button onClick={onClose} className="px-4 py-2 text-sm text-slate-400 hover:text-white transition-colors">
            Cancel
          </button>
          {jobSpec && (
            <button
              onClick={handlePublish}
              disabled={isPublishing}
              className="flex items-center space-x-2 px-6 py-2.5 rounded-xl bg-gradient-to-r from-emerald-500 to-cyan-500 hover:from-emerald-400 hover:to-cyan-400 text-slate-950 font-extrabold text-sm shadow-glow-emerald transition-all active:scale-95 disabled:opacity-50"
            >
              <span>{isPublishing ? 'Publishing...' : 'Publish Job & Match Talent'}</span>
              <ArrowRight className="h-4 w-4" />
            </button>
          )}
        </div>
      </div>
    </div>
  );
};
