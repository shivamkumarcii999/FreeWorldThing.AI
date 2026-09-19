import React, { useState } from 'react';
import { Layers, Star, Clock, Check, ShieldCheck, ArrowRight } from 'lucide-react';
import { ServiceListing, User } from '../types';

interface ServicesCatalogProps {
  services: ServiceListing[];
  currentUser: User | null;
  onBuyService: (service: ServiceListing, selectedTier: any) => void;
}

export const ServicesCatalog: React.FC<ServicesCatalogProps> = ({
  services,
  currentUser,
  onBuyService,
}) => {
  const [selectedService, setSelectedService] = useState<ServiceListing | null>(null);

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 py-8">
      <div className="mb-8">
        <div className="inline-flex items-center space-x-1.5 px-3 py-1 rounded-full bg-cyan-500/10 text-cyan-300 border border-cyan-500/20 text-xs font-bold mb-2">
          <Layers className="h-3.5 w-3.5" />
          <span>Fixed-Price Catalog</span>
        </div>
        <h2 className="text-2xl sm:text-3xl font-extrabold font-display text-white">
          Production-Ready Specialized Services
        </h2>
        <p className="text-sm text-slate-400 max-w-2xl">
          Purchase pre-packaged AI and engineering deliverables with guaranteed timelines and milestone escrow.
        </p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {services.map((svc) => {
          let packages: any[] = [];
          try {
            if (svc.packagesJson) packages = JSON.parse(svc.packagesJson);
          } catch (e) {}

          return (
            <div
              key={svc.id}
              className="rounded-2xl glass-card border border-slate-800 p-6 flex flex-col justify-between space-y-4 hover:border-cyan-500/50 transition-all"
            >
              <div className="space-y-3">
                <div className="flex items-center space-x-3 pb-3 border-b border-slate-800">
                  <img
                    src={svc.freelancerAvatar || 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=150&q=80'}
                    alt={svc.freelancerName}
                    className="h-10 w-10 rounded-full object-cover ring-2 ring-cyan-500/30"
                  />
                  <div>
                    <span className="text-xs font-bold text-white">{svc.freelancerName}</span>
                    <div className="flex items-center space-x-1 text-[11px] text-amber-400 font-bold">
                      <Star className="h-3 w-3 fill-amber-400" />
                      <span>{svc.rating || 4.9}</span>
                      <span className="text-slate-400">({svc.reviewCount || 20})</span>
                    </div>
                  </div>
                </div>

                <h3 className="font-bold text-base text-white leading-snug">{svc.title}</h3>
                <p className="text-xs text-slate-300 line-clamp-3 leading-relaxed">{svc.description}</p>

                <div className="flex items-center justify-between pt-2 text-xs text-slate-400">
                  <span className="flex items-center space-x-1">
                    <Clock className="h-3.5 w-3.5 text-cyan-400" />
                    <span>~{svc.deliveryDays || 7} Days Delivery</span>
                  </span>
                  <span className="text-sm font-extrabold text-emerald-400">
                    From ₹{svc.startingPrice?.toLocaleString()}
                  </span>
                </div>
              </div>

              <div className="pt-3 border-t border-slate-800 flex items-center justify-between">
                <button
                  onClick={() => setSelectedService(svc)}
                  className="w-full py-2.5 rounded-xl bg-gradient-to-r from-cyan-500/20 to-indigo-500/20 hover:from-cyan-500/30 hover:to-indigo-500/30 text-cyan-300 border border-cyan-500/30 text-xs font-bold transition-all text-center flex items-center justify-center space-x-2"
                >
                  <span>View Package Tiers ({packages.length || 3})</span>
                  <ArrowRight className="h-3.5 w-3.5" />
                </button>
              </div>
            </div>
          );
        })}
      </div>

      {/* Package Tier Modal */}
      {selectedService && (
        <div className="fixed inset-0 z-50 overflow-y-auto bg-black/80 backdrop-blur-md flex items-center justify-center p-4">
          <div className="relative w-full max-w-4xl glass-panel rounded-2xl border border-slate-700 shadow-2xl p-6 sm:p-8 space-y-6">
            <div className="flex items-center justify-between pb-3 border-b border-slate-800">
              <div>
                <h3 className="text-lg font-bold text-white">{selectedService.title}</h3>
                <p className="text-xs text-slate-400">Offered by {selectedService.freelancerName}</p>
              </div>
              <button onClick={() => setSelectedService(null)} className="text-slate-400 hover:text-white">✕</button>
            </div>

            {/* Package Grid */}
            <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
              {JSON.parse(selectedService.packagesJson || '[]').map((pkg: any, idx: number) => (
                <div
                  key={idx}
                  className={`p-5 rounded-2xl glass-card border flex flex-col justify-between space-y-4 ${
                    idx === 1 ? 'border-cyan-500 bg-slate-900 shadow-glow-cyan' : 'border-slate-800'
                  }`}
                >
                  <div>
                    <div className="flex items-center justify-between mb-2">
                      <span className="text-xs font-bold uppercase tracking-wider text-cyan-300">{pkg.tier}</span>
                      {idx === 1 && <span className="text-[10px] px-2 py-0.5 rounded bg-cyan-500/20 text-cyan-300 font-extrabold">POPULAR</span>}
                    </div>
                    <div className="text-2xl font-extrabold text-white mb-2">
                      ₹{pkg.price?.toLocaleString()}
                    </div>
                    <p className="text-xs text-slate-300 leading-relaxed mb-4">{pkg.desc}</p>
                    <div className="flex items-center space-x-1.5 text-xs text-slate-400">
                      <Clock className="h-3.5 w-3.5 text-cyan-400" />
                      <span>{pkg.duration} Days Delivery</span>
                    </div>
                  </div>

                  <button
                    onClick={() => {
                      onBuyService(selectedService, pkg);
                      setSelectedService(null);
                    }}
                    className="w-full py-2.5 rounded-xl bg-gradient-to-r from-emerald-500 to-cyan-500 text-slate-950 font-bold text-xs shadow-glow-emerald active:scale-95 transition-all"
                  >
                    Start Milestone Escrow →
                  </button>
                </div>
              ))}
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
