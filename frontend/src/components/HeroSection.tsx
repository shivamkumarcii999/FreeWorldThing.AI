import React, { useState } from 'react';
import { Sparkles, ArrowRight, ShieldCheck, Cpu, Code2, Smartphone, Palette, Cloud, CheckCircle2 } from 'lucide-react';

interface HeroSectionProps {
  onAnalyzePrompt: (prompt: string) => void;
  onSelectCategory: (category: string) => void;
}

export const HeroSection: React.FC<HeroSectionProps> = ({ onAnalyzePrompt, onSelectCategory }) => {
  const [prompt, setPrompt] = useState('');

  const samplePrompts = [
    "Build a production RAG AI assistant with FastAPI, LangChain & Next.js",
    "Full-stack React 19 SaaS dashboard with Spring Boot 3 & PostgreSQL",
    "Cross-platform Flutter mobile app with offline SQLite sync & biometrics",
    "High-converting SaaS UI/UX design system & interactive prototype in Figma",
  ];

  const categories = [
    { name: 'AI/ML & Agents', icon: Cpu, color: 'from-purple-500/20 to-indigo-500/20 text-purple-300' },
    { name: 'Full Stack & APIs', icon: Code2, color: 'from-cyan-500/20 to-blue-500/20 text-cyan-300' },
    { name: 'Mobile Apps', icon: Smartphone, color: 'from-emerald-500/20 to-teal-500/20 text-emerald-300' },
    { name: 'UI/UX Design', icon: Palette, color: 'from-pink-500/20 to-rose-500/20 text-pink-300' },
    { name: 'Cloud & DevOps', icon: Cloud, color: 'from-amber-500/20 to-orange-500/20 text-amber-300' },
  ];

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (prompt.trim()) {
      onAnalyzePrompt(prompt.trim());
    }
  };

  return (
    <div className="relative overflow-hidden pt-12 pb-16 md:pt-18 md:pb-24 border-b border-slate-800/60">
      {/* Background ambient lighting */}
      <div className="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-[600px] h-[350px] bg-gradient-to-tr from-cyan-500/10 via-purple-500/10 to-transparent blur-3xl pointer-events-none rounded-full" />

      <div className="max-w-5xl mx-auto px-4 sm:px-6 text-center relative z-10">
        {/* Core Loop Pill */}
        <div className="inline-flex items-center space-x-2 px-3.5 py-1.5 rounded-full glass-panel border border-cyan-500/30 text-xs font-semibold text-cyan-300 mb-6 shadow-glow-cyan animate-fade-in">
          <Sparkles className="h-3.5 w-3.5 text-cyan-400" />
          <span>Idea → 🤖 AI Job Builder → 🎯 Smart Matching → 🛠️ Escrow Workspace</span>
        </div>

        {/* Hero Title */}
        <h1 className="text-4xl sm:text-6xl lg:text-7xl font-extrabold font-display tracking-tight text-white leading-[1.1] mb-6">
          Turn your idea into software.{' '}
          <span className="gradient-text">Verified engineers on demand.</span>
        </h1>

        <p className="text-lg sm:text-xl text-slate-300 max-w-2xl mx-auto mb-10 leading-relaxed font-normal">
          Describe what you want in natural language. Our AI builder scopes milestones, matches proven technical proof-of-work, and protects every rupee via Milestone Escrow.
        </p>

        {/* AI Prompt Input Bar */}
        <form onSubmit={handleSubmit} className="max-w-3xl mx-auto mb-6">
          <div className="relative p-1.5 rounded-2xl glass-panel border border-cyan-500/40 shadow-2xl shadow-cyan-950/40 focus-within:border-cyan-400 focus-within:shadow-glow-cyan transition-all">
            <div className="flex items-center">
              <div className="pl-3 pr-2 text-cyan-400">
                <Sparkles className="h-6 w-6" />
              </div>
              <input
                type="text"
                value={prompt}
                onChange={(e) => setPrompt(e.target.value)}
                placeholder="What do you want to build? (e.g., Build an AI customer support chatbot with Python and RAG...)"
                className="w-full bg-transparent border-0 text-white placeholder-slate-400 text-sm sm:text-base focus:ring-0 focus:outline-none py-3"
              />
              <button
                type="submit"
                className="flex items-center space-x-2 px-5 py-3 rounded-xl bg-gradient-to-r from-cyan-500 to-indigo-600 hover:from-cyan-400 hover:to-indigo-500 text-white font-bold text-sm tracking-wide transition-all shadow-lg active:scale-95 shrink-0"
              >
                <span>Scope with AI</span>
                <ArrowRight className="h-4 w-4" />
              </button>
            </div>
          </div>
        </form>

        {/* Sample Prompts */}
        <div className="flex flex-wrap items-center justify-center gap-2 max-w-3xl mx-auto mb-12">
          <span className="text-xs text-slate-400 font-medium">Try prompt:</span>
          {samplePrompts.map((p, idx) => (
            <button
              key={idx}
              onClick={() => {
                setPrompt(p);
                onAnalyzePrompt(p);
              }}
              className="text-xs px-3 py-1 rounded-full glass-card hover:border-cyan-500/40 text-slate-300 hover:text-white transition-all truncate max-w-[280px]"
            >
              "{p}"
            </button>
          ))}
        </div>

        {/* Category Pills */}
        <div className="grid grid-cols-2 sm:grid-cols-5 gap-3 max-w-4xl mx-auto mb-12">
          {categories.map((c, idx) => {
            const Icon = c.icon;
            return (
              <button
                key={idx}
                onClick={() => onSelectCategory(c.name)}
                className="flex flex-col items-center p-3.5 rounded-xl glass-card hover:border-slate-600 transition-all text-center group"
              >
                <div className={`p-2.5 rounded-lg bg-gradient-to-br ${c.color} mb-2 group-hover:scale-110 transition-transform`}>
                  <Icon className="h-5 w-5" />
                </div>
                <span className="text-xs font-semibold text-slate-200 group-hover:text-cyan-300 transition-colors">
                  {c.name}
                </span>
              </button>
            );
          })}
        </div>

        {/* Trust Badges */}
        <div className="pt-8 border-t border-slate-800/60 grid grid-cols-1 sm:grid-cols-3 gap-6 text-left">
          <div className="flex items-start space-x-3 p-3 rounded-xl glass-card">
            <ShieldCheck className="h-6 w-6 text-emerald-400 shrink-0 mt-0.5" />
            <div>
              <h4 className="text-sm font-bold text-white">Milestone Escrow Protection</h4>
              <p className="text-xs text-slate-400">Funds released only after client review & approval of deliverables.</p>
            </div>
          </div>
          <div className="flex items-start space-x-3 p-3 rounded-xl glass-card">
            <CheckCircle2 className="h-6 w-6 text-cyan-400 shrink-0 mt-0.5" />
            <div>
              <h4 className="text-sm font-bold text-white">Proof-of-Work Verification</h4>
              <p className="text-xs text-slate-400">Grounded in GitHub repositories, commit depth, and test coverage metrics.</p>
            </div>
          </div>
          <div className="flex items-start space-x-3 p-3 rounded-xl glass-card">
            <Sparkles className="h-6 w-6 text-purple-400 shrink-0 mt-0.5" />
            <div>
              <h4 className="text-sm font-bold text-white">AI Project Manager Built-in</h4>
              <p className="text-xs text-slate-400">Automated health checks, blocker detection, and sprint summaries.</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};
