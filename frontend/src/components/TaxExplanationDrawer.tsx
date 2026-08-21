import React, { useState, useEffect } from 'react';
import { RegimeComparisonResult, TaxCalculationResult, TaxExplanationResponse } from '../types/tax';
import { taxApi } from '../services/api';
import { Sparkles, X, Send, BookOpen, Lightbulb, HelpCircle, ShieldAlert, Bot } from 'lucide-react';

interface TaxExplanationDrawerProps {
  isOpen: boolean;
  onClose: () => void;
  comparison: RegimeComparisonResult | null;
  activeCalculation: TaxCalculationResult | null;
}

export const TaxExplanationDrawer: React.FC<TaxExplanationDrawerProps> = ({
  isOpen,
  onClose,
  comparison,
  activeCalculation,
}) => {
  const [explanation, setExplanation] = useState<TaxExplanationResponse | null>(null);
  const [loading, setLoading] = useState(false);
  const [question, setQuestion] = useState('');
  const [chatHistory, setChatHistory] = useState<Array<{ sender: 'user' | 'ai'; text: string }>>([]);

  const fetchExplanation = async (userQ?: string) => {
    setLoading(true);
    try {
      const res = await taxApi.explainTax({
        calculationResult: activeCalculation || undefined,
        comparisonResult: comparison || undefined,
        userQuestion: userQ,
      });
      setExplanation(res);
      if (userQ) {
        setChatHistory((prev) => [
          ...prev,
          { sender: 'user', text: userQ },
          { sender: 'ai', text: res.faqClarifications[1] || res.plainLanguageBreakdown },
        ]);
      }
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (isOpen) {
      fetchExplanation();
    }
  }, [isOpen, comparison, activeCalculation]);

  if (!isOpen) return null;

  const handleAskQuestion = (e: React.FormEvent) => {
    e.preventDefault();
    if (!question.trim()) return;
    fetchExplanation(question.trim());
    setQuestion('');
  };

  const handleQuickQuestion = (qText: string) => {
    fetchExplanation(qText);
  };

  return (
    <div className="modal-backdrop" onClick={onClose}>
      <div className="modal-content" onClick={(e) => e.stopPropagation()} style={{ maxWidth: '850px' }}>
        {/* Header */}
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px', borderBottom: '1px solid var(--border-subtle)', paddingBottom: '14px' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
            <div style={{ width: '36px', height: '36px', borderRadius: '10px', background: 'linear-gradient(135deg, #8b5cf6, #6366f1)', display: 'flex', alignItems: 'center', justifyContent: 'center', color: '#fff' }}>
              <Bot size={20} />
            </div>
            <div>
              <h3 style={{ fontSize: '1.2rem', fontWeight: 700 }}>AI Tax Explanation Assistant</h3>
              <p style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>
                Grounded in deterministic CBDT rule engine execution traces • Zero hallucination
              </p>
            </div>
          </div>
          <button
            onClick={onClose}
            style={{ background: 'none', border: 'none', color: 'var(--text-muted)', cursor: 'pointer' }}
          >
            <X size={22} />
          </button>
        </div>

        {loading && !explanation ? (
          <div style={{ textAlign: 'center', padding: '40px' }}>
            <Sparkles size={32} className="pulse" color="var(--accent-purple)" />
            <p style={{ marginTop: '12px', color: 'var(--text-muted)' }}>Synthesizing plain-language rule engine breakdown...</p>
          </div>
        ) : explanation ? (
          <div style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
            {/* Executive Summary */}
            <div style={{ padding: '16px', background: 'rgba(139, 92, 246, 0.1)', border: '1px solid rgba(139, 92, 246, 0.3)', borderRadius: 'var(--radius-md)' }}>
              <div style={{ fontSize: '0.75rem', textTransform: 'uppercase', letterSpacing: '0.05em', color: '#a78bfa', fontWeight: 700 }}>
                Executive Summary
              </div>
              <div style={{ fontSize: '1.05rem', fontWeight: 600, color: 'var(--text-primary)', marginTop: '4px' }}>
                {explanation.executiveSummary}
              </div>
            </div>

            {/* Plain Language Step-by-Step Breakdown */}
            <div>
              <h4 style={{ fontSize: '0.95rem', display: 'flex', alignItems: 'center', gap: '8px', color: 'var(--text-primary)', marginBottom: '10px' }}>
                <BookOpen size={16} color="var(--accent-cyan)" />
                Plain-Language Computation Narrative
              </h4>
              <div
                style={{
                  background: 'rgba(255, 255, 255, 0.02)',
                  border: '1px solid var(--border-subtle)',
                  borderRadius: 'var(--radius-md)',
                  padding: '16px',
                  fontSize: '0.85rem',
                  lineHeight: '1.7',
                  whiteSpace: 'pre-line',
                  color: 'var(--text-secondary)',
                }}
              >
                {explanation.plainLanguageBreakdown}
              </div>
            </div>

            {/* Strategic Tips & Observations */}
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '14px' }}>
              <div style={{ background: 'rgba(255,255,255,0.02)', border: '1px solid var(--border-subtle)', borderRadius: 'var(--radius-md)', padding: '14px' }}>
                <h5 style={{ fontSize: '0.85rem', color: '#34d399', display: 'flex', alignItems: 'center', gap: '6px', marginBottom: '8px' }}>
                  <Lightbulb size={14} /> Key Observations
                </h5>
                <ul style={{ paddingLeft: '18px', fontSize: '0.8rem', color: 'var(--text-secondary)', display: 'flex', flexDirection: 'column', gap: '6px' }}>
                  {explanation.keyObservations.map((obs, idx) => (
                    <li key={idx}>{obs}</li>
                  ))}
                </ul>
              </div>

              <div style={{ background: 'rgba(255,255,255,0.02)', border: '1px solid var(--border-subtle)', borderRadius: 'var(--radius-md)', padding: '14px' }}>
                <h5 style={{ fontSize: '0.85rem', color: '#fbbf24', display: 'flex', alignItems: 'center', gap: '6px', marginBottom: '8px' }}>
                  <HelpCircle size={14} /> Strategic Tax Planning
                </h5>
                <ul style={{ paddingLeft: '18px', fontSize: '0.8rem', color: 'var(--text-secondary)', display: 'flex', flexDirection: 'column', gap: '6px' }}>
                  {explanation.strategicTips.map((tip, idx) => (
                    <li key={idx}>{tip}</li>
                  ))}
                </ul>
              </div>
            </div>

            {/* Socratic Chat & Scenario Q&A */}
            <div>
              <h4 style={{ fontSize: '0.95rem', display: 'flex', alignItems: 'center', gap: '8px', marginBottom: '10px' }}>
                <HelpCircle size={16} color="var(--accent-purple)" />
                Scenario Q&amp;A Simulator
              </h4>

              {/* Quick Prompt Chips */}
              <div style={{ display: 'flex', gap: '8px', flexWrap: 'wrap', marginBottom: '12px' }}>
                <button
                  type="button"
                  className="btn btn-secondary btn-sm"
                  onClick={() => handleQuickQuestion('Why is the New Regime better for my income?')}
                >
                  💬 Why New Regime?
                </button>
                <button
                  type="button"
                  className="btn btn-secondary btn-sm"
                  onClick={() => handleQuickQuestion('How does Section 80CCD(1B) NPS save more tax?')}
                >
                  💬 NPS 80CCD(1B) Benefit?
                </button>
                <button
                  type="button"
                  className="btn btn-secondary btn-sm"
                  onClick={() => handleQuickQuestion('How is HRA exemption calculated under Rule 2A?')}
                >
                  💬 HRA Exemption Math?
                </button>
              </div>

              {/* Interactive Chat Log */}
              {chatHistory.length > 0 && (
                <div style={{ maxHeight: '200px', overflowY: 'auto', display: 'flex', flexDirection: 'column', gap: '10px', marginBottom: '14px' }}>
                  {chatHistory.map((msg, i) => (
                    <div
                      key={i}
                      style={{
                        alignSelf: msg.sender === 'user' ? 'flex-end' : 'flex-start',
                        maxWidth: '85%',
                        padding: '10px 14px',
                        borderRadius: 'var(--radius-md)',
                        fontSize: '0.85rem',
                        background: msg.sender === 'user' ? 'rgba(139, 92, 246, 0.25)' : 'rgba(255, 255, 255, 0.05)',
                        border: '1px solid var(--border-subtle)',
                        color: 'var(--text-primary)',
                      }}
                    >
                      <strong>{msg.sender === 'user' ? 'You: ' : 'AI Explainer: '}</strong>
                      {msg.text}
                    </div>
                  ))}
                </div>
              )}

              {/* Question Input Form */}
              <form onSubmit={handleAskQuestion} style={{ display: 'flex', gap: '10px' }}>
                <input
                  type="text"
                  className="input-field"
                  placeholder="Ask a question (e.g., 'What if I invest 50k more in ELSS?')..."
                  value={question}
                  onChange={(e) => setQuestion(e.target.value)}
                />
                <button type="submit" className="btn btn-ai" disabled={loading}>
                  <Send size={16} />
                </button>
              </form>
            </div>

            {/* Compliance Disclaimer */}
            <div style={{ display: 'flex', alignItems: 'flex-start', gap: '10px', padding: '12px', background: 'rgba(0,0,0,0.3)', borderRadius: 'var(--radius-sm)', fontSize: '0.75rem', color: 'var(--text-muted)' }}>
              <ShieldAlert size={16} style={{ flexShrink: 0, marginTop: '2px' }} />
              <div>{explanation.statutoryDisclaimer}</div>
            </div>
          </div>
        ) : null}
      </div>
    </div>
  );
};
