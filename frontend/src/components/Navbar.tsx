import React, { useState } from 'react';
import { Sparkles, Briefcase, Users, Layers, FolderGit2, LayoutDashboard, Plus, Wallet, ShieldCheck, ArrowRightLeft } from 'lucide-react';
import { User } from '../types';

interface NavbarProps {
  activeTab: string;
  setActiveTab: (tab: string) => void;
  currentUser: User | null;
  allUsers: User[];
  onSwitchUser: (user: User) => void;
  onOpenJobBuilder: () => void;
  onDepositWallet: () => void;
}

export const Navbar: React.FC<NavbarProps> = ({
  activeTab,
  setActiveTab,
  currentUser,
  allUsers,
  onSwitchUser,
  onOpenJobBuilder,
  onDepositWallet,
}) => {
  const [showUserDropdown, setShowUserDropdown] = useState(false);

  const navItems = [
    { id: 'home', label: 'Explore', icon: Sparkles },
    { id: 'jobs', label: 'Jobs & Matching', icon: Briefcase },
    { id: 'talent', label: 'Talent & Proofs', icon: Users },
    { id: 'services', label: 'Fixed Services', icon: Layers },
    { id: 'workspace', label: 'Workspace & Escrow', icon: FolderGit2 },
    { id: 'dashboard', label: 'Dashboard', icon: LayoutDashboard },
  ];

  return (
    <header className="sticky top-0 z-50 glass-panel border-b border-slate-800/80 bg-[#0b0f19]/90 backdrop-blur-xl">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex items-center justify-between h-16">
          {/* Brand Logo */}
          <div className="flex items-center space-x-3 cursor-pointer" onClick={() => setActiveTab('home')}>
            <div className="h-10 w-10 rounded-xl bg-gradient-to-tr from-cyan-500 via-indigo-500 to-purple-600 p-[2px] shadow-glow-cyan flex items-center justify-center">
              <div className="h-full w-full bg-slate-950 rounded-[10px] flex items-center justify-center">
                <Sparkles className="h-5 w-5 text-cyan-400 animate-pulse" />
              </div>
            </div>
            <div>
              <div className="flex items-center space-x-1.5">
                <span className="font-extrabold text-xl tracking-tight font-display bg-gradient-to-r from-white via-slate-100 to-slate-400 bg-clip-text text-transparent">
                  FreeWorldThing
                </span>
                <span className="px-1.5 py-0.5 text-[10px] font-bold bg-cyan-500/20 text-cyan-300 border border-cyan-500/30 rounded-md">
                  AI
                </span>
              </div>
              <p className="text-[10px] text-slate-400 font-medium tracking-wide">Find talent • Build faster • Earn smarter</p>
            </div>
          </div>

          {/* Nav Links */}
          <nav className="hidden md:flex items-center space-x-1">
            {navItems.map((item) => {
              const Icon = item.icon;
              const isActive = activeTab === item.id;
              return (
                <button
                  key={item.id}
                  onClick={() => setActiveTab(item.id)}
                  className={`flex items-center space-x-2 px-3.5 py-2 rounded-lg text-sm font-medium transition-all ${
                    isActive
                      ? 'bg-cyan-500/15 text-cyan-300 border border-cyan-500/30 shadow-sm'
                      : 'text-slate-300 hover:text-white hover:bg-slate-800/60'
                  }`}
                >
                  <Icon className={`h-4 w-4 ${isActive ? 'text-cyan-400' : 'text-slate-400'}`} />
                  <span>{item.label}</span>
                </button>
              );
            })}
          </nav>

          {/* Action Area & User Profile Switcher */}
          <div className="flex items-center space-x-3">
            <button
              onClick={onOpenJobBuilder}
              className="flex items-center space-x-2 px-3.5 py-2 rounded-lg text-sm font-semibold bg-gradient-to-r from-cyan-500 to-indigo-600 text-white shadow-glow-cyan hover:opacity-95 transition-all active:scale-95"
            >
              <Plus className="h-4 w-4" />
              <span className="hidden sm:inline">AI Job Builder</span>
            </button>

            {/* Persona Switcher Dropdown */}
            {currentUser && (
              <div className="relative">
                <button
                  onClick={() => setShowUserDropdown(!showUserDropdown)}
                  className="flex items-center space-x-2.5 p-1.5 pr-3 rounded-full glass-card hover:border-slate-700 transition-all text-left"
                >
                  <img
                    src={currentUser.avatarUrl || 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=150&q=80'}
                    alt={currentUser.fullName}
                    className="h-8 w-8 rounded-full object-cover ring-2 ring-cyan-500/40"
                  />
                  <div className="hidden lg:block">
                    <div className="text-xs font-semibold text-white flex items-center space-x-1">
                      <span>{currentUser.fullName}</span>
                      <ShieldCheck className="h-3.5 w-3.5 text-emerald-400" />
                    </div>
                    <div className="text-[10px] text-slate-400 font-medium">
                      {currentUser.role === 'CLIENT' ? 'Client' : 'Verified Specialist'}
                    </div>
                  </div>
                  <ArrowRightLeft className="h-3.5 w-3.5 text-slate-400 hidden sm:block" />
                </button>

                {showUserDropdown && (
                  <div className="absolute right-0 mt-2 w-72 rounded-2xl glass-panel shadow-2xl border border-slate-700/80 p-3 z-50">
                    <div className="px-3 py-2 border-b border-slate-800 mb-2">
                      <p className="text-xs font-bold text-slate-400 uppercase tracking-wider">Switch Persona / Test Role</p>
                      <div className="mt-2 flex items-center justify-between">
                        <span className="text-xs text-slate-300">Wallet Balance:</span>
                        <div className="flex items-center space-x-1 text-emerald-400 font-bold text-sm">
                          <Wallet className="h-3.5 w-3.5" />
                          <span>₹{(currentUser.walletBalance || 0).toLocaleString()}</span>
                        </div>
                      </div>
                      <button
                        onClick={() => {
                          onDepositWallet();
                          setShowUserDropdown(false);
                        }}
                        className="mt-2 w-full text-center py-1.5 text-xs font-semibold bg-emerald-500/20 text-emerald-300 hover:bg-emerald-500/30 rounded-lg border border-emerald-500/30 transition-all"
                      >
                        + Add ₹50,000 to Wallet
                      </button>
                    </div>

                    <div className="space-y-1">
                      {allUsers.map((u) => (
                        <button
                          key={u.id}
                          onClick={() => {
                            onSwitchUser(u);
                            setShowUserDropdown(false);
                          }}
                          className={`w-full flex items-center space-x-2.5 p-2 rounded-xl text-left transition-all ${
                            currentUser.id === u.id ? 'bg-cyan-500/20 text-cyan-300 font-semibold' : 'hover:bg-slate-800/80 text-slate-300'
                          }`}
                        >
                          <img
                            src={u.avatarUrl || 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=150&q=80'}
                            alt={u.fullName}
                            className="h-7 w-7 rounded-full object-cover"
                          />
                          <div className="flex-1 min-w-0">
                            <p className="text-xs font-medium truncate text-white">{u.fullName}</p>
                            <p className="text-[10px] text-slate-400 truncate">{u.role === 'CLIENT' ? '🏢 Client' : '⚡ Specialist'}</p>
                          </div>
                        </button>
                      ))}
                    </div>
                  </div>
                )}
              </div>
            )}
          </div>
        </div>
      </div>
    </header>
  );
};
