import React, { useState, useEffect } from 'react';
import { 
  Sparkles, ShieldCheck, Cpu, ArrowRight, Bot, Activity, CheckCircle2, 
  Layers, Users, Briefcase, DollarSign 
} from 'lucide-react';
import { User, Job, ServiceListing, Contract } from './types';
import { api, auth, getToken } from './services/api';
import { LoginView } from './components/LoginView';
import { Navbar } from './components/Navbar';
import { HeroSection } from './components/HeroSection';
import { AiJobBuilderModal } from './components/AiJobBuilderModal';
import { JobsFeed } from './components/JobsFeed';
import { TalentDirectory } from './components/TalentDirectory';
import { ServicesCatalog } from './components/ServicesCatalog';
import { ProjectWorkspaceView } from './components/ProjectWorkspaceView';
import { DashboardView } from './components/DashboardView';

export const App: React.FC = () => {
  const [activeTab, setActiveTab] = useState<string>('home');
  const [allUsers, setAllUsers] = useState<User[]>([]);
  const [currentUser, setCurrentUser] = useState<User | null>(null);
  const [jobs, setJobs] = useState<Job[]>([]);
  const [talentList, setTalentList] = useState<any[]>([]);
  const [services, setServices] = useState<ServiceListing[]>([]);
  const [contracts, setContracts] = useState<Contract[]>([]);
  const [activeContractId, setActiveContractId] = useState<number | null>(null);

  const [booting, setBooting] = useState(true);
  const [showJobBuilderModal, setShowJobBuilderModal] = useState(false);
  const [initialJobPrompt, setInitialJobPrompt] = useState<string>('');
  const [notification, setNotification] = useState<{ message: string; type: 'success' | 'info' | 'error' } | null>(null);

  const triggerNotification = (message: string, type: 'success' | 'info' | 'error' = 'success') => {
    setNotification({ message, type });
    setTimeout(() => setNotification(null), 4500);
  };

  // Load all initial data from backend
  const loadData = async () => {
    try {
      const [usersData, jobsData, talentData, servicesData, contractsData] = await Promise.all([
        api.getUsers().catch(() => []),
        api.getJobs().catch(() => []),
        api.getAllTalent().catch(() => []),
        api.getAllServices().catch(() => []),
        api.getContracts().catch(() => []),
      ]);

      if (usersData && usersData.length > 0) {
        setAllUsers(usersData);
        if (!currentUser) {
          // Default to first client (Shiva)
          const clientUser = usersData.find((u: User) => u.role === 'CLIENT') || usersData[0];
          setCurrentUser(clientUser);
        } else {
          // Update current user state with latest
          const updated = usersData.find((u: User) => u.id === currentUser.id);
          if (updated) setCurrentUser(updated);
        }
      }

      setJobs(Array.isArray(jobsData) ? jobsData : []);
      setTalentList(Array.isArray(talentData) ? talentData : []);
      setServices(Array.isArray(servicesData) ? servicesData : []);
      setContracts(Array.isArray(contractsData) ? contractsData : []);
      if (Array.isArray(contractsData) && contractsData.length > 0 && !activeContractId) {
        setActiveContractId(contractsData[0].id);
      }
    } catch (err) {
      console.error('Failed to load initial marketplace data', err);
    }
  };

  useEffect(() => {
    const bootstrap = async () => {
      if (getToken()) {
        try {
          const me = await api.getMe();
          setCurrentUser(me);
        } catch {
          auth.logout();
        }
      }
      setBooting(false);
    };
    bootstrap();
    loadData();
  }, []);

  const handleDepositWallet = async () => {
    if (!currentUser) return;
    const amount = 50000;
    try {
      const updatedUser = await api.depositWallet(currentUser.id, amount);
      setCurrentUser(updatedUser);
      triggerNotification(`Deposited ₹${amount.toLocaleString()} into Escrow Wallet!`, 'success');
      loadData();
    } catch (err) {
      triggerNotification('Deposit simulation completed locally', 'info');
      if (currentUser) {
        setCurrentUser({
          ...currentUser,
          walletBalance: (currentUser.walletBalance || 0) + amount,
        });
      }
    }
  };

  const handleAnalyzePrompt = (prompt: string) => {
    setInitialJobPrompt(prompt);
    setShowJobBuilderModal(true);
  };

  const handleJobCreated = (newJob: any) => {
    triggerNotification(`Job "${newJob.title}" created with AI milestones & published!`, 'success');
    setShowJobBuilderModal(false);
    loadData();
    setActiveTab('jobs');
  };

  const handleOpenContract = (contractId: number) => {
    setActiveContractId(contractId);
    setActiveTab('workspace');
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  const handleHireTalent = async (freelancerId: number, name: string) => {
    if (!currentUser) return;
    try {
      const contractData = {
        title: `Custom Autonomous AI Build with ${name}`,
        clientId: currentUser.id,
        freelancerId: freelancerId,
        totalValue: 75000,
        currency: 'INR',
        milestones: [
          {
            title: 'Phase 1: Architecture, Ingestion & Schemas',
            description: 'Setup verified repository with CI/CD and data pipeline.',
            amount: 35000,
            orderIndex: 0,
          },
          {
            title: 'Phase 2: Core Model & Full Stack Integration',
            description: 'Deploy fine-tuned LLM endpoint and production dashboard.',
            amount: 40000,
            orderIndex: 1,
          },
        ],
      };

      const newContract = await api.createContract(contractData);
      triggerNotification(`Smart Escrow Contract #${newContract.id} created with ${name}!`, 'success');
      await loadData();
      handleOpenContract(newContract.id);
    } catch (err) {
      console.error('Failed to create contract', err);
      triggerNotification('Failed to create contract. Ensure backend is running.', 'error');
    }
  };

  const handleBuyService = async (service: ServiceListing, selectedTier: any) => {
    if (!currentUser) return;
    try {
      const contractData = {
        title: `Package: ${service.title} (${selectedTier.name})`,
        clientId: currentUser.id,
        freelancerId: service.freelancerId,
        totalValue: selectedTier.price,
        currency: service.currency || 'INR',
        milestones: [
          {
            title: `${selectedTier.name} Deliverable Package`,
            description: `${service.description} (Delivery target: ${selectedTier.deliveryDays} days)`,
            amount: selectedTier.price,
            orderIndex: 0,
          },
        ],
      };

      const newContract = await api.createContract(contractData);
      triggerNotification(`Purchased ${service.title}! Escrow Workspace initialized.`, 'success');
      await loadData();
      handleOpenContract(newContract.id);
    } catch (err) {
      console.error('Failed to buy service', err);
      triggerNotification('Failed to purchase package. Check backend status.', 'error');
    }
  };

  if (booting) {
    return <div className="min-h-screen bg-[#0b0f19]" />;
  }

  if (!currentUser) {
    return (
      <div className="min-h-screen bg-[#0b0f19] text-slate-100 font-sans">
        <LoginView onLoggedIn={(user) => { setCurrentUser(user); loadData(); }} />
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-[#0b0f19] text-slate-100 flex flex-col font-sans selection:bg-purple-500 selection:text-white">
      {/* Toast Notification */}
      {notification && (
        <div className="fixed bottom-6 right-6 z-50 animate-bounce">
          <div className={`flex items-center gap-3 px-5 py-3.5 rounded-2xl shadow-2xl backdrop-blur-xl border ${
            notification.type === 'success' 
              ? 'bg-slate-900/95 border-emerald-500/50 text-emerald-300' 
              : notification.type === 'error'
              ? 'bg-slate-900/95 border-rose-500/50 text-rose-300'
              : 'bg-slate-900/95 border-purple-500/50 text-purple-300'
          }`}>
            <CheckCircle2 className="w-5 h-5 flex-shrink-0" />
            <span className="text-sm font-medium">{notification.message}</span>
          </div>
        </div>
      )}

      {/* Top Navbar */}
      <Navbar
        activeTab={activeTab}
        setActiveTab={setActiveTab}
        currentUser={currentUser}
        allUsers={allUsers}
        onSwitchUser={async (user: User) => {
          try {
            const res = await auth.login(user.email, 'password123');
            setCurrentUser(res.user);
            triggerNotification(`Signed in as ${user.fullName} (${user.role})`, 'info');
            loadData();
          } catch {
            setCurrentUser(user);
            triggerNotification(`Viewing as ${user.fullName} (demo session)`, 'info');
          }
        }}
        onOpenJobBuilder={() => setShowJobBuilderModal(true)}
        onDepositWallet={handleDepositWallet}
      />

      {/* Main Content Area */}
      <main className="flex-1 w-full">
        {activeTab === 'home' && (
          <div className="space-y-12">
            <HeroSection
              onAnalyzePrompt={handleAnalyzePrompt}
              onSelectCategory={(cat: string) => {
                setActiveTab('jobs');
              }}
            />

            {/* Feature Highlights Grid */}
            <div className="max-w-7xl mx-auto px-4 sm:px-6">
              <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
                <div className="glass-panel p-6 rounded-2xl border border-slate-800 hover:border-purple-500/50 transition group">
                  <div className="w-12 h-12 rounded-2xl bg-purple-500/10 border border-purple-500/30 flex items-center justify-center text-purple-400 mb-5 group-hover:scale-110 transition">
                    <Bot className="w-6 h-6" />
                  </div>
                  <h3 className="text-lg font-bold text-white mb-2">Natural Language Scoping</h3>
                  <p className="text-slate-400 text-sm leading-relaxed">
                    Type a 1-sentence prompt. Our AI decomposes it into structured scopes, verifiable milestones, and recommended budgets.
                  </p>
                </div>

                <div className="glass-panel p-6 rounded-2xl border border-slate-800 hover:border-cyan-500/50 transition group">
                  <div className="w-12 h-12 rounded-2xl bg-cyan-500/10 border border-cyan-500/30 flex items-center justify-center text-cyan-400 mb-5 group-hover:scale-110 transition">
                    <ShieldCheck className="w-6 h-6" />
                  </div>
                  <h3 className="text-lg font-bold text-white mb-2">Autonomous Smart Escrow</h3>
                  <p className="text-slate-400 text-sm leading-relaxed">
                    Funds stay securely locked per-milestone. Released automatically with cryptographic proof-of-work upon client sign-off.
                  </p>
                </div>

                <div className="glass-panel p-6 rounded-2xl border border-slate-800 hover:border-emerald-500/50 transition group">
                  <div className="w-12 h-12 rounded-2xl bg-emerald-500/10 border border-emerald-500/30 flex items-center justify-center text-emerald-400 mb-5 group-hover:scale-110 transition">
                    <Activity className="w-6 h-6" />
                  </div>
                  <h3 className="text-lg font-bold text-white mb-2">AI Project Management</h3>
                  <p className="text-slate-400 text-sm leading-relaxed">
                    Real-time health audits, milestone velocity tracking, and proactive blocker resolution embedded in every workspace.
                  </p>
                </div>
              </div>

              {/* Feed Preview */}
              <div className="mt-14 space-y-6">
                <div className="flex items-center justify-between">
                  <div>
                    <h2 className="text-2xl font-bold font-display text-white">Live AI Opportunities</h2>
                    <p className="text-slate-400 text-sm">Explore real-time scoped contracts with verified smart escrow</p>
                  </div>
                  <button
                    onClick={() => setActiveTab('jobs')}
                    className="px-4 py-2 rounded-xl bg-slate-800 hover:bg-slate-700 text-slate-200 border border-slate-700 text-sm font-semibold flex items-center gap-2 group transition"
                  >
                    View All Jobs
                    <ArrowRight className="w-4 h-4 group-hover:translate-x-1 transition" />
                  </button>
                </div>

                <JobsFeed
                  jobs={jobs.slice(0, 3)}
                  currentUser={currentUser}
                  onRefreshJobs={loadData}
                  onOpenContract={handleOpenContract}
                />
              </div>
            </div>
          </div>
        )}

        {activeTab === 'jobs' && (
          <JobsFeed
            jobs={jobs}
            currentUser={currentUser}
            onRefreshJobs={loadData}
            onOpenContract={handleOpenContract}
          />
        )}

        {activeTab === 'talent' && (
          <TalentDirectory
            talentList={talentList}
            currentUser={currentUser}
            onHireTalent={handleHireTalent}
          />
        )}

        {activeTab === 'services' && (
          <ServicesCatalog
            services={services}
            currentUser={currentUser}
            onBuyService={handleBuyService}
          />
        )}

        {activeTab === 'workspace' && (
          <ProjectWorkspaceView
            contracts={contracts}
            activeContractId={activeContractId}
            onSelectContract={(id: number) => setActiveContractId(id)}
            currentUser={currentUser}
          />
        )}

        {activeTab === 'dashboard' && (
          <DashboardView
            currentUser={currentUser}
            contracts={contracts}
            onSelectContract={handleOpenContract}
            onBrowseJobs={() => setActiveTab('jobs')}
            onRefresh={loadData}
            onDepositWallet={handleDepositWallet}
          />
        )}
      </main>

      {/* AI Job Builder Modal */}
      <AiJobBuilderModal
        isOpen={showJobBuilderModal}
        onClose={() => setShowJobBuilderModal(false)}
        initialPrompt={initialJobPrompt}
        currentUser={currentUser}
        onJobCreated={handleJobCreated}
      />

      {/* Global Footer */}
      <footer className="mt-16 border-t border-slate-800/80 bg-[#070a12]/80 backdrop-blur-xl">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-10">
          <div className="flex flex-col md:flex-row items-center justify-between gap-6">
            <div className="flex items-center gap-3">
              <div className="w-9 h-9 rounded-xl bg-gradient-to-tr from-purple-600 to-cyan-500 flex items-center justify-center text-white shadow-glow">
                <Sparkles className="w-5 h-5" />
              </div>
              <div>
                <span className="text-lg font-bold font-display tracking-tight text-white">
                  FreeWorldThing<span className="text-purple-400">.AI</span>
                </span>
                <p className="text-xs text-slate-400">Autonomous AI Freelance Marketplace & Work Protocol</p>
              </div>
            </div>

            <div className="flex flex-wrap items-center gap-8 text-sm text-slate-400">
              <button className="hover:text-white transition" onClick={() => setActiveTab('jobs')}>Explore Jobs</button>
              <button className="hover:text-white transition" onClick={() => setActiveTab('talent')}>Verified Talent</button>
              <button className="hover:text-white transition" onClick={() => setActiveTab('services')}>Pre-Packaged Services</button>
              <button className="hover:text-white transition" onClick={() => setActiveTab('dashboard')}>Escrow Dashboard</button>
            </div>

            <div className="text-xs text-slate-500 font-mono">
              v1.0.0-PROD • AI Autonomous Protocol
            </div>
          </div>
        </div>
      </footer>
    </div>
  );
};
