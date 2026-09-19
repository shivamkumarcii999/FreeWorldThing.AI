import React, { useState, useEffect } from 'react';
import {
  FolderGit2,
  ShieldCheck,
  CheckCircle2,
  Clock,
  DollarSign,
  AlertTriangle,
  Sparkles,
  Send,
  Plus,
  ArrowRight,
  Github,
  ExternalLink,
  MessageSquare,
  Kanban,
  Check,
  RefreshCw,
} from 'lucide-react';
import { Contract, Milestone, TaskItem, ProjectMessage, AiProjectHealthReport, User } from '../types';
import { api } from '../services/api';

interface ProjectWorkspaceViewProps {
  contracts: Contract[];
  activeContractId: number | null;
  onSelectContract: (id: number) => void;
  currentUser: User | null;
}

export const ProjectWorkspaceView: React.FC<ProjectWorkspaceViewProps> = ({
  contracts,
  activeContractId,
  onSelectContract,
  currentUser,
}) => {
  const [activeTab, setActiveTab] = useState<'milestones' | 'tasks' | 'chat' | 'ai-health'>('milestones');
  const [workspaceData, setWorkspaceData] = useState<{
    workspace: any;
    contract: Contract;
    milestones: Milestone[];
    tasks: TaskItem[];
    messages: ProjectMessage[];
  } | null>(null);

  const [aiHealth, setAiHealth] = useState<AiProjectHealthReport | null>(null);
  const [isScanningHealth, setIsScanningHealth] = useState(false);
  const [chatInput, setChatInput] = useState('');
  const [showSubmitModal, setShowSubmitModal] = useState<Milestone | null>(null);
  const [deliverableUrl, setDeliverableUrl] = useState('');
  const [deliverableNotes, setDeliverableNotes] = useState('');
  const [showNewTaskModal, setShowNewTaskModal] = useState(false);
  const [newTaskTitle, setNewTaskTitle] = useState('');
  const [newTaskPriority, setNewTaskPriority] = useState('HIGH');

  const selectedContract = contracts.find((c) => c.id === activeContractId) || contracts[0];

  useEffect(() => {
    if (selectedContract) {
      loadWorkspace(selectedContract.id);
    }
  }, [selectedContract?.id]);

  const loadWorkspace = async (id: number) => {
    try {
      const data = await api.getWorkspace(id);
      setWorkspaceData(data);
    } catch (err) {
      console.error(err);
    }
  };

  const handleFundMilestone = async (milestoneId: number) => {
    if (!selectedContract) return;
    try {
      await api.fundMilestone(milestoneId);
      alert('🛡️ Milestone funded! Held securely in Escrow.');
      loadWorkspace(selectedContract.id);
    } catch (err) {
      console.error(err);
      alert('Failed to fund milestone.');
    }
  };

  const handleSubmitDeliverable = async () => {
    if (!showSubmitModal || !selectedContract) return;
    try {
      await api.submitMilestone(showSubmitModal.id, deliverableUrl, deliverableNotes);
      alert('📦 Deliverable submitted for client review!');
      setShowSubmitModal(null);
      setDeliverableUrl('');
      setDeliverableNotes('');
      loadWorkspace(selectedContract.id);
    } catch (err) {
      console.error(err);
      alert('Failed to submit deliverable.');
    }
  };

  const handleApproveMilestone = async (milestoneId: number) => {
    if (!selectedContract) return;
    try {
      await api.approveMilestone(milestoneId);
      alert('🎉 Milestone approved! Escrow payment released to specialist wallet.');
      loadWorkspace(selectedContract.id);
    } catch (err) {
      console.error(err);
      alert('Failed to approve milestone.');
    }
  };

  const handleSendMessage = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!chatInput.trim() || !workspaceData?.workspace?.id) return;
    try {
      await api.sendMessage({
        workspaceId: workspaceData.workspace.id,
        senderId: currentUser?.id || 1,
        senderName: currentUser?.fullName || 'User',
        senderRole: currentUser?.role || 'CLIENT',
        content: chatInput.trim(),
        messageType: 'TEXT',
      });
      setChatInput('');
      loadWorkspace(selectedContract.id);
    } catch (err) {
      console.error(err);
    }
  };

  const handleUpdateTask = async (taskId: number, status: string) => {
    if (!selectedContract) return;
    try {
      await api.updateTaskStatus(taskId, status);
      loadWorkspace(selectedContract.id);
    } catch (err) {
      console.error(err);
    }
  };

  const handleCreateTask = async () => {
    if (!newTaskTitle.trim() || !workspaceData?.workspace?.id || !selectedContract) return;
    try {
      await api.createTask({
        workspaceId: workspaceData.workspace.id,
        title: newTaskTitle.trim(),
        priority: newTaskPriority,
        assignedToName: selectedContract.freelancerName || 'Specialist',
      });
      setShowNewTaskModal(false);
      setNewTaskTitle('');
      loadWorkspace(selectedContract.id);
    } catch (err) {
      console.error(err);
    }
  };

  const handleRunAiHealthScan = async () => {
    if (!selectedContract) return;
    setIsScanningHealth(true);
    try {
      const report = await api.getAiWorkspaceHealth(selectedContract.id);
      setAiHealth(report);
      setActiveTab('ai-health');
    } catch (err) {
      console.error(err);
      alert('Failed to run AI health scan.');
    } finally {
      setIsScanningHealth(false);
    }
  };

  if (!selectedContract) {
    return (
      <div className="max-w-7xl mx-auto px-4 py-16 text-center text-slate-400">
        <FolderGit2 className="h-16 w-16 mx-auto text-slate-600 mb-4" />
        <h3 className="text-xl font-bold text-white mb-2">No Active Workspaces Found</h3>
        <p className="text-sm">Hire a specialist or create a job with milestones to launch an interactive escrow workspace.</p>
      </div>
    );
  }

  const milestones = workspaceData?.milestones || selectedContract.milestones || [];
  const tasks = workspaceData?.tasks || [];
  const messages = workspaceData?.messages || [];

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 py-8">
      {/* Workspace Header */}
      <div className="mb-6 p-6 rounded-2xl glass-panel border border-slate-800 space-y-4">
        <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
          <div>
            <div className="flex items-center space-x-2 mb-1">
              <span className="px-2.5 py-0.5 text-xs font-bold rounded-lg bg-cyan-500/20 text-cyan-300 border border-cyan-500/30 uppercase">
                Active Escrow Workspace
              </span>
              <span className="text-xs text-slate-400">Contract #{selectedContract.id}</span>
            </div>
            <h2 className="text-2xl font-extrabold font-display text-white">{selectedContract.title}</h2>
            <div className="flex flex-wrap items-center gap-4 text-xs text-slate-300 mt-2">
              <span>🏢 Client: <strong className="text-white">{selectedContract.clientName || 'Alex Morgan'}</strong></span>
              <span>⚡ Specialist: <strong className="text-white">{selectedContract.freelancerName || 'Aravind Sharma'}</strong></span>
              <span className="text-emerald-400 font-bold">Total: ₹{(selectedContract.totalValue || selectedContract.totalAmount || 120000).toLocaleString()}</span>
            </div>
          </div>

          {/* AI Health Quick Action */}
          <div className="flex items-center space-x-3">
            <button
              onClick={handleRunAiHealthScan}
              disabled={isScanningHealth}
              className="flex items-center space-x-2 px-4 py-2.5 rounded-xl bg-gradient-to-r from-purple-600 to-indigo-600 hover:from-purple-500 hover:to-indigo-500 text-white text-xs font-bold shadow-glow-purple transition-all"
            >
              <Sparkles className="h-4 w-4 text-cyan-300" />
              <span>{isScanningHealth ? 'Scanning...' : '🤖 AI PM Health Scan'}</span>
            </button>
          </div>
        </div>

        {/* Tab Switcher */}
        <div className="flex items-center space-x-2 pt-2 border-t border-slate-800 overflow-x-auto">
          {[
            { id: 'milestones', label: `Milestones & Escrow (${milestones.length})`, icon: ShieldCheck },
            { id: 'tasks', label: `Kanban Tasks (${tasks.length})`, icon: Kanban },
            { id: 'chat', label: `Project Chat (${messages.length})`, icon: MessageSquare },
            { id: 'ai-health', label: 'AI Health Report', icon: Sparkles },
          ].map((tab) => {
            const Icon = tab.icon;
            const isActive = activeTab === tab.id;
            return (
              <button
                key={tab.id}
                onClick={() => setActiveTab(tab.id as any)}
                className={`flex items-center space-x-2 px-4 py-2 rounded-xl text-xs font-bold transition-all shrink-0 ${
                  isActive
                    ? 'bg-cyan-500/20 text-cyan-300 border border-cyan-500/40 shadow-sm'
                    : 'text-slate-400 hover:text-white hover:bg-slate-800/60'
                }`}
              >
                <Icon className="h-4 w-4" />
                <span>{tab.label}</span>
              </button>
            );
          })}
        </div>
      </div>

      {/* Tab Content 1: Milestones & Escrow */}
      {activeTab === 'milestones' && (
        <div className="space-y-6">
          <div className="flex items-center justify-between">
            <h3 className="text-sm font-bold text-slate-300 uppercase tracking-wider">
              Escrow Milestone Roadmap
            </h3>
            <span className="text-xs text-slate-400">
              🔒 Funds held safely by FWT Escrow until deliverable approval
            </span>
          </div>

          <div className="space-y-4">
            {milestones.map((m, idx) => {
              const status = m.status;
              const isPaid = status === 'PAID' || status === 'APPROVED' || status === 'RELEASED';
              const isFunded = status === 'FUNDED' || status === 'IN_PROGRESS';
              const isSubmitted = status === 'SUBMITTED' || status === 'DELIVERED';
              const isPending = status === 'PENDING' || status === 'UNFUNDED';

              return (
                <div
                  key={m.id || idx}
                  className={`p-6 rounded-2xl glass-card border transition-all ${
                    isPaid
                      ? 'border-emerald-500/40 bg-emerald-950/10'
                      : isSubmitted
                      ? 'border-amber-500/40 bg-amber-950/10 shadow-glow-cyan'
                      : isFunded
                      ? 'border-cyan-500/40 bg-cyan-950/10'
                      : 'border-slate-800'
                  }`}
                >
                  <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
                    <div className="space-y-2 flex-1">
                      <div className="flex items-center space-x-2">
                        <span className="h-6 w-6 rounded-full bg-slate-800 text-cyan-300 border border-slate-700 flex items-center justify-center text-xs font-bold">
                          {m.sequence || m.sequenceOrder || idx + 1}
                        </span>
                        <h4 className="text-base font-bold text-white">{m.title}</h4>
                        <span
                          className={`px-2.5 py-0.5 text-[11px] font-bold rounded-md uppercase tracking-wider ${
                            isPaid
                              ? 'bg-emerald-500/20 text-emerald-300 border border-emerald-500/30'
                              : isSubmitted
                              ? 'bg-amber-500/20 text-amber-300 border border-amber-500/30'
                              : isFunded
                              ? 'bg-cyan-500/20 text-cyan-300 border border-cyan-500/30'
                              : 'bg-slate-800 text-slate-400 border border-slate-700'
                          }`}
                        >
                          {status}
                        </span>
                      </div>

                      <p className="text-xs text-slate-300 leading-relaxed max-w-3xl">
                        {m.description}
                      </p>

                      {/* Deliverable info if submitted */}
                      {m.deliverableNotes && (
                        <div className="p-3 rounded-xl bg-slate-900/90 border border-slate-800 text-xs space-y-1">
                          <span className="font-bold text-cyan-300">📦 Submitted Deliverable:</span>
                          <p className="text-slate-300">{m.deliverableNotes}</p>
                        </div>
                      )}
                    </div>

                    {/* Amount and Action */}
                    <div className="flex md:flex-col items-center md:items-end justify-between gap-3 shrink-0">
                      <div className="text-right">
                        <div className="text-xs text-slate-400">Milestone Amount</div>
                        <div className="text-lg font-extrabold text-white">
                          ₹{(m.amount || 40000).toLocaleString()}
                        </div>
                      </div>

                      <div className="flex items-center space-x-2">
                        {isPending && (
                          <button
                            onClick={() => handleFundMilestone(m.id)}
                            className="px-4 py-2 rounded-xl bg-gradient-to-r from-cyan-500 to-indigo-600 hover:from-cyan-400 hover:to-indigo-500 text-white font-bold text-xs shadow-glow-cyan transition-all"
                          >
                            🛡️ Fund Milestone
                          </button>
                        )}

                        {isFunded && (
                          <button
                            onClick={() => setShowSubmitModal(m)}
                            className="px-4 py-2 rounded-xl bg-gradient-to-r from-amber-500 to-orange-500 hover:from-amber-400 hover:to-orange-400 text-slate-950 font-bold text-xs shadow-lg transition-all"
                          >
                            📦 Submit Deliverable
                          </button>
                        )}

                        {isSubmitted && (
                          <button
                            onClick={() => handleApproveMilestone(m.id)}
                            className="px-4 py-2 rounded-xl bg-gradient-to-r from-emerald-500 to-cyan-500 hover:from-emerald-400 hover:to-cyan-400 text-slate-950 font-extrabold text-xs shadow-glow-emerald transition-all"
                          >
                            ✓ Approve & Release Payment
                          </button>
                        )}

                        {isPaid && (
                          <span className="flex items-center space-x-1 text-xs text-emerald-400 font-bold bg-emerald-500/10 px-3 py-1.5 rounded-lg border border-emerald-500/20">
                            <CheckCircle2 className="h-4 w-4" />
                            <span>Payment Released</span>
                          </span>
                        )}
                      </div>
                    </div>
                  </div>
                </div>
              );
            })}
          </div>
        </div>
      )}

      {/* Tab Content 2: Kanban Tasks */}
      {activeTab === 'tasks' && (
        <div className="space-y-4">
          <div className="flex items-center justify-between">
            <h3 className="text-sm font-bold text-slate-300 uppercase tracking-wider">
              Sprint Task Board
            </h3>
            <button
              onClick={() => setShowNewTaskModal(true)}
              className="flex items-center space-x-1 px-3 py-1.5 rounded-xl bg-cyan-500/20 text-cyan-300 hover:bg-cyan-500/30 border border-cyan-500/30 text-xs font-bold transition-all"
            >
              <Plus className="h-3.5 w-3.5" />
              <span>Create Task</span>
            </button>
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-4 gap-4">
            {(['TODO', 'IN_PROGRESS', 'REVIEW', 'DONE'] as const).map((col) => {
              const colTasks = tasks.filter((t) => t.status === col);
              return (
                <div key={col} className="p-4 rounded-2xl glass-panel border border-slate-800 space-y-3">
                  <div className="flex items-center justify-between pb-2 border-b border-slate-800">
                    <span className="text-xs font-bold text-slate-300 uppercase tracking-wider">
                      {col.replace('_', ' ')}
                    </span>
                    <span className="h-5 w-5 rounded-full bg-slate-800 text-slate-400 flex items-center justify-center text-[10px] font-bold">
                      {colTasks.length}
                    </span>
                  </div>

                  <div className="space-y-2 min-h-[250px]">
                    {colTasks.map((t) => (
                      <div key={t.id} className="p-3.5 rounded-xl glass-card border border-slate-700/80 space-y-2">
                        <div className="flex items-start justify-between gap-1">
                          <h5 className="text-xs font-bold text-white">{t.title}</h5>
                          <span className={`text-[9px] px-1.5 py-0.5 rounded font-bold ${
                            t.priority === 'HIGH' || t.priority === 'URGENT' ? 'bg-rose-500/20 text-rose-300' : 'bg-slate-800 text-slate-400'
                          }`}>
                            {t.priority}
                          </span>
                        </div>
                        {t.description && <p className="text-[11px] text-slate-400">{t.description}</p>}
                        
                        {/* Status advance button */}
                        <div className="pt-2 flex items-center justify-between border-t border-slate-800 text-[10px]">
                          <span className="text-slate-500">{t.assignedToName || 'Specialist'}</span>
                          {col !== 'DONE' && (
                            <button
                              onClick={() => {
                                const next = col === 'TODO' ? 'IN_PROGRESS' : col === 'IN_PROGRESS' ? 'REVIEW' : 'DONE';
                                handleUpdateTask(t.id, next);
                              }}
                              className="text-cyan-400 hover:text-cyan-300 font-bold"
                            >
                              Move Next →
                            </button>
                          )}
                        </div>
                      </div>
                    ))}
                  </div>
                </div>
              );
            })}
          </div>
        </div>
      )}

      {/* Tab Content 3: Chat */}
      {activeTab === 'chat' && (
        <div className="glass-panel rounded-2xl border border-slate-800 p-6 flex flex-col h-[550px]">
          <div className="flex-1 overflow-y-auto space-y-3 pr-2 mb-4">
            {messages.map((m) => {
              const isMe = m.senderId === currentUser?.id;
              return (
                <div key={m.id} className={`flex flex-col ${isMe ? 'items-end' : 'items-start'}`}>
                  <div className="flex items-center space-x-1.5 mb-1 text-[11px] text-slate-400">
                    <span className="font-bold text-slate-300">{m.senderName}</span>
                    <span className="text-[9px] px-1 rounded bg-slate-800 text-slate-400">{m.senderRole}</span>
                  </div>
                  <div
                    className={`p-3 rounded-2xl max-w-md text-xs leading-relaxed ${
                      isMe
                        ? 'bg-gradient-to-r from-cyan-600 to-indigo-600 text-white'
                        : 'bg-slate-800/90 text-slate-200 border border-slate-700/80'
                    }`}
                  >
                    {m.content}
                  </div>
                </div>
              );
            })}
          </div>

          <form onSubmit={handleSendMessage} className="flex gap-2 pt-3 border-t border-slate-800">
            <input
              type="text"
              value={chatInput}
              onChange={(e) => setChatInput(e.target.value)}
              placeholder="Send message or daily sync update to workspace..."
              className="flex-1 glass-input rounded-xl px-4 py-2.5 text-xs focus:border-cyan-400 focus:outline-none"
            />
            <button
              type="submit"
              className="px-5 rounded-xl bg-gradient-to-r from-cyan-500 to-indigo-600 text-white font-bold text-xs flex items-center space-x-1"
            >
              <Send className="h-3.5 w-3.5" />
              <span>Send</span>
            </button>
          </form>
        </div>
      )}

      {/* Tab Content 4: AI Health Report */}
      {activeTab === 'ai-health' && (
        <div className="glass-panel rounded-2xl border border-slate-800 p-6 space-y-6">
          <div className="flex items-center justify-between pb-4 border-b border-slate-800">
            <div className="flex items-center space-x-2">
              <Sparkles className="h-5 w-5 text-purple-400" />
              <h3 className="text-lg font-bold text-white">AI Project Manager Diagnostic</h3>
            </div>
            <button
              onClick={handleRunAiHealthScan}
              className="flex items-center space-x-1 px-3 py-1.5 rounded-xl bg-slate-800 hover:bg-slate-700 text-cyan-300 text-xs font-bold"
            >
              <RefreshCw className="h-3.5 w-3.5" />
              <span>Re-Scan Project</span>
            </button>
          </div>

          {aiHealth ? (
            <div className="space-y-6 animate-fade-in">
              {/* Health pill & Progress */}
              <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
                <div className="p-4 rounded-xl bg-slate-900/80 border border-slate-800">
                  <div className="text-xs text-slate-400 mb-1">Health Status</div>
                  <div className="text-lg font-extrabold text-emerald-400">{aiHealth.healthStatus}</div>
                </div>
                <div className="p-4 rounded-xl bg-slate-900/80 border border-slate-800">
                  <div className="text-xs text-slate-400 mb-1">Completion Progress</div>
                  <div className="text-lg font-extrabold text-cyan-400">{aiHealth.completionPercent}%</div>
                </div>
                <div className="p-4 rounded-xl bg-slate-900/80 border border-slate-800">
                  <div className="text-xs text-slate-400 mb-1">AI Confidence</div>
                  <div className="text-lg font-extrabold text-purple-400">{(aiHealth.confidenceScore * 100).toFixed(0)}%</div>
                </div>
              </div>

              {/* Executive summary */}
              <div className="p-4 rounded-xl bg-slate-900/60 border border-slate-800 space-y-1">
                <span className="text-xs font-bold text-cyan-300 uppercase tracking-wider">Executive Summary</span>
                <p className="text-sm text-slate-200 leading-relaxed">{aiHealth.executiveSummary}</p>
              </div>

              {/* Blockers & Next Steps */}
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div className="p-4 rounded-xl bg-rose-950/20 border border-rose-900/40 space-y-2">
                  <div className="flex items-center space-x-1.5 text-rose-400 text-xs font-bold uppercase tracking-wider">
                    <AlertTriangle className="h-4 w-4" />
                    <span>Active Blockers ({aiHealth.blockers?.length || 0})</span>
                  </div>
                  <ul className="space-y-1.5 text-xs text-slate-300">
                    {aiHealth.blockers?.length ? (
                      aiHealth.blockers.map((b, i) => <li key={i}>• {b}</li>)
                    ) : (
                      <li className="text-emerald-400 font-medium">✓ No critical blockers identified.</li>
                    )}
                  </ul>
                </div>

                <div className="p-4 rounded-xl bg-emerald-950/20 border border-emerald-900/40 space-y-2">
                  <div className="flex items-center space-x-1.5 text-emerald-400 text-xs font-bold uppercase tracking-wider">
                    <CheckCircle2 className="h-4 w-4" />
                    <span>Recommended Next Steps</span>
                  </div>
                  <ul className="space-y-1.5 text-xs text-slate-300">
                    {aiHealth.nextSteps?.map((s, i) => <li key={i}>• {s}</li>)}
                  </ul>
                </div>
              </div>
            </div>
          ) : (
            <div className="text-center py-12 text-slate-400">
              <button
                onClick={handleRunAiHealthScan}
                className="px-6 py-3 rounded-xl bg-gradient-to-r from-purple-600 to-indigo-600 text-white font-bold text-sm shadow-glow-purple"
              >
                Run First AI Project Health Scan →
              </button>
            </div>
          )}
        </div>
      )}

      {/* Deliverable Submission Modal */}
      {showSubmitModal && (
        <div className="fixed inset-0 z-50 overflow-y-auto bg-black/80 backdrop-blur-md flex items-center justify-center p-4">
          <div className="relative w-full max-w-lg glass-panel rounded-2xl border border-slate-700 shadow-2xl p-6 space-y-4">
            <h3 className="text-lg font-bold text-white">Submit Deliverable: {showSubmitModal.title}</h3>
            <div>
              <label className="block text-xs font-bold text-slate-300 mb-1">GitHub Repo / Preview URL</label>
              <input
                type="text"
                value={deliverableUrl}
                onChange={(e) => setDeliverableUrl(e.target.value)}
                placeholder="https://github.com/username/project-repo"
                className="w-full glass-input rounded-xl p-2.5 text-xs text-white focus:outline-none"
              />
            </div>
            <div>
              <label className="block text-xs font-bold text-slate-300 mb-1">Deliverable Notes & Verification Evidence</label>
              <textarea
                rows={4}
                value={deliverableNotes}
                onChange={(e) => setDeliverableNotes(e.target.value)}
                placeholder="Summary of completed milestone sprint, test coverage results, and staging credentials..."
                className="w-full glass-input rounded-xl p-2.5 text-xs text-white focus:outline-none resize-none"
              />
            </div>
            <div className="flex items-center justify-end space-x-2 pt-2 border-t border-slate-800">
              <button onClick={() => setShowSubmitModal(null)} className="px-4 py-2 text-xs text-slate-400 hover:text-white">
                Cancel
              </button>
              <button
                onClick={handleSubmitDeliverable}
                className="px-5 py-2 rounded-xl bg-gradient-to-r from-emerald-500 to-cyan-500 text-slate-950 font-bold text-xs"
              >
                Submit for Approval →
              </button>
            </div>
          </div>
        </div>
      )}

      {/* New Task Modal */}
      {showNewTaskModal && (
        <div className="fixed inset-0 z-50 overflow-y-auto bg-black/80 backdrop-blur-md flex items-center justify-center p-4">
          <div className="relative w-full max-w-md glass-panel rounded-2xl border border-slate-700 shadow-2xl p-6 space-y-4">
            <h3 className="text-lg font-bold text-white">Create Sprint Task</h3>
            <div>
              <label className="block text-xs font-bold text-slate-300 mb-1">Task Title</label>
              <input
                type="text"
                value={newTaskTitle}
                onChange={(e) => setNewTaskTitle(e.target.value)}
                placeholder="e.g. Implement FastAPI SSE endpoint"
                className="w-full glass-input rounded-xl p-2.5 text-xs text-white focus:outline-none"
              />
            </div>
            <div>
              <label className="block text-xs font-bold text-slate-300 mb-1">Priority</label>
              <select
                value={newTaskPriority}
                onChange={(e) => setNewTaskPriority(e.target.value)}
                className="w-full glass-input rounded-xl p-2.5 text-xs text-white bg-slate-900 focus:outline-none"
              >
                <option value="LOW">LOW</option>
                <option value="MEDIUM">MEDIUM</option>
                <option value="HIGH">HIGH</option>
                <option value="URGENT">URGENT</option>
              </select>
            </div>
            <div className="flex items-center justify-end space-x-2 pt-2 border-t border-slate-800">
              <button onClick={() => setShowNewTaskModal(false)} className="px-4 py-2 text-xs text-slate-400 hover:text-white">
                Cancel
              </button>
              <button
                onClick={handleCreateTask}
                className="px-5 py-2 rounded-xl bg-cyan-500 text-slate-950 font-bold text-xs"
              >
                Add Task
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
