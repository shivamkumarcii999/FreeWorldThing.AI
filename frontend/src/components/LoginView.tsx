import React, { useState } from 'react';
import { Sparkles, ShieldCheck, LogIn, UserPlus, Zap } from 'lucide-react';
import { User } from '../types';
import { auth } from '../services/api';

interface LoginViewProps {
  onLoggedIn: (user: User) => void;
}

const DEMO_ACCOUNTS = [
  { email: 'priya@demo.com', name: 'Priya Nair', role: 'Client · D2C founder' },
  { email: 'shivam@demo.com', name: 'Shivam Gupta', role: 'Freelancer · Java + AI' },
  { email: 'amit@demo.com', name: 'Amit Sharma', role: 'Freelancer · AI/RAG engineer' },
  { email: 'sara@demo.com', name: 'Sara Khan', role: 'Freelancer · Designer' },
];

const DEMO_PASSWORD = 'password123';

export const LoginView: React.FC<LoginViewProps> = ({ onLoggedIn }) => {
  const [mode, setMode] = useState<'login' | 'register'>('login');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [fullName, setFullName] = useState('');
  const [role, setRole] = useState<'CLIENT' | 'FREELANCER'>('CLIENT');
  const [error, setError] = useState('');
  const [busy, setBusy] = useState(false);

  const submit = async (e?: React.FormEvent, override?: { email: string; password: string }) => {
    e?.preventDefault();
    setError('');
    setBusy(true);
    try {
      const res = override
        ? await auth.login(override.email, override.password)
        : mode === 'login'
          ? await auth.login(email, password)
          : await auth.register(email, password, fullName, role);
      onLoggedIn(res.user);
    } catch (err: any) {
      setError(err?.message || 'Something went wrong');
    } finally {
      setBusy(false);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center p-4">
      <div className="w-full max-w-4xl grid grid-cols-1 lg:grid-cols-2 gap-8 items-center">
        {/* Brand side */}
        <div className="space-y-6 text-center lg:text-left">
          <div className="flex items-center justify-center lg:justify-start space-x-3">
            <div className="h-12 w-12 rounded-xl bg-gradient-to-tr from-cyan-500 via-indigo-500 to-purple-600 p-[2px] shadow-glow-cyan flex items-center justify-center">
              <div className="h-full w-full bg-slate-950 rounded-[10px] flex items-center justify-center">
                <Sparkles className="h-6 w-6 text-cyan-400" />
              </div>
            </div>
            <span className="font-extrabold text-2xl tracking-tight font-display bg-gradient-to-r from-white via-slate-100 to-slate-400 bg-clip-text text-transparent">
              FreeWorldThing
            </span>
            <span className="px-1.5 py-0.5 text-[10px] font-bold bg-cyan-500/20 text-cyan-300 border border-cyan-500/30 rounded-md">AI</span>
          </div>

          <h1 className="text-3xl sm:text-4xl font-extrabold font-display text-white leading-tight">
            Describe what you need.<br />
            <span className="gradient-text">AI finds the right person.</span>
          </h1>
          <p className="text-slate-400 text-sm leading-relaxed max-w-md mx-auto lg:mx-0">
            💡 Idea → 🤖 AI Job Builder → 🎯 Smart Matching → 🤝 Hire → 🛠️ Project Workspace → ✅ Verify → 💰 Pay → ⭐ Reputation
          </p>

          <div className="flex items-center justify-center lg:justify-start space-x-2 text-xs text-slate-400">
            <ShieldCheck className="h-4 w-4 text-emerald-400" />
            <span>Milestone escrow · Proof-of-work verification · AI project management</span>
          </div>
        </div>

        {/* Form side */}
        <div className="glass-panel rounded-2xl border border-slate-700/80 p-8 space-y-5">
          <div className="flex items-center space-x-2 pb-4 border-b border-slate-800">
            {mode === 'login' ? <LogIn className="h-5 w-5 text-cyan-400" /> : <UserPlus className="h-5 w-5 text-cyan-400" />}
            <h2 className="text-lg font-bold text-white">{mode === 'login' ? 'Sign in to FWT AI' : 'Create your account'}</h2>
          </div>

          <form onSubmit={submit} className="space-y-4">
            {mode === 'register' && (
              <input
                type="text" value={fullName} onChange={(e) => setFullName(e.target.value)}
                placeholder="Full name" required
                className="w-full glass-input rounded-xl px-4 py-2.5 text-sm"
              />
            )}
            <input
              type="email" value={email} onChange={(e) => setEmail(e.target.value)}
              placeholder="Email address" required
              className="w-full glass-input rounded-xl px-4 py-2.5 text-sm"
            />
            <input
              type="password" value={password} onChange={(e) => setPassword(e.target.value)}
              placeholder="Password" required minLength={6}
              className="w-full glass-input rounded-xl px-4 py-2.5 text-sm"
            />
            {mode === 'register' && (
              <div className="grid grid-cols-2 gap-3">
                {(['CLIENT', 'FREELANCER'] as const).map((r) => (
                  <button
                    key={r} type="button" onClick={() => setRole(r)}
                    className={`py-2.5 rounded-xl text-xs font-bold border transition-all ${
                      role === r
                        ? 'bg-cyan-500/20 text-cyan-300 border-cyan-500/40'
                        : 'glass-card text-slate-300 border-slate-700'
                    }`}
                  >
                    {r === 'CLIENT' ? "I'm hiring" : "I'm seeking work"}
                  </button>
                ))}
              </div>
            )}

            {error && <p className="text-xs text-rose-400 bg-rose-500/10 border border-rose-500/30 rounded-lg px-3 py-2">{error}</p>}

            <button
              type="submit" disabled={busy}
              className="w-full py-3 rounded-xl bg-gradient-to-r from-cyan-500 to-indigo-600 hover:from-cyan-400 hover:to-indigo-500 text-white font-bold text-sm shadow-glow-cyan transition-all active:scale-95 disabled:opacity-50"
            >
              {busy ? 'Please wait…' : mode === 'login' ? 'Sign in' : 'Create account'}
            </button>
          </form>

          <p className="text-center text-xs text-slate-400">
            {mode === 'login' ? "New here? " : 'Already have an account? '}
            <button onClick={() => { setMode(mode === 'login' ? 'register' : 'login'); setError(''); }}
              className="text-cyan-400 hover:text-cyan-300 font-semibold">
              {mode === 'login' ? 'Create an account' : 'Sign in'}
            </button>
          </p>

          {/* Demo accounts */}
          <div className="pt-4 border-t border-slate-800 space-y-2">
            <p className="text-[10px] font-bold text-slate-500 uppercase tracking-wider flex items-center space-x-1.5">
              <Zap className="h-3 w-3 text-amber-400" />
              <span>One-click demo accounts (password: password123)</span>
            </p>
            <div className="grid grid-cols-2 gap-2">
              {DEMO_ACCOUNTS.map((d) => (
                <button
                  key={d.email}
                  onClick={() => submit(undefined, { email: d.email, password: DEMO_PASSWORD })}
                  disabled={busy}
                  className="p-2.5 rounded-xl glass-card border border-slate-700 text-left hover:border-cyan-500/40 transition-all disabled:opacity-50"
                >
                  <p className="text-xs font-bold text-white truncate">{d.name}</p>
                  <p className="text-[10px] text-slate-400 truncate">{d.role}</p>
                </button>
              ))}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};
