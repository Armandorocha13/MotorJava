import React, { useState } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import {
  Mail,
  Package,
  Terminal,
  User,
  ArrowRight,
  TrendingUp,
  RefreshCw,
  Trash2,
  Database,
  LayoutDashboard,
  FileSpreadsheet,
  Zap
} from 'lucide-react';

const App = () => {
  const [activeTab, setActiveTab] = useState('ihs'); // 'ihs' ou 'vivo'
  const [logs, setLogs] = useState([
    { id: 1, time: '16:35:01', msg: 'Dashboard Unificado v4.5 Ativado.', type: 'info' },
    { id: 2, time: '16:35:05', msg: 'Conectado ao Motor Java Engine.', type: 'success' }
  ]);

  const addLog = (msg, type = 'info') => {
    const newLog = {
      id: Date.now(),
      time: new Date().toLocaleTimeString(),
      msg,
      type
    };
    setLogs(prev => [newLog, ...prev].slice(0, 50));
  };

  const triggerProcess = async (endpoint, title) => {
    addLog(`Acionando ${title}...`, 'info');
    try {
      const response = await fetch(`http://localhost:8080/api/${endpoint}`);
      const data = await response.json();
      if (data.success) {
        addLog(data.msg, 'success');
      } else {
        addLog(`Erro no motor: ${data.msg}`, 'error');
      }
    } catch (err) {
      addLog(`Motor fora de alcance: ${err.message}`, 'error');
    }
  };

  const ihsCards = [
    { title: 'Relatório Outlook', subtitle: 'Outlook Automation', icon: <Mail className="text-blue-400" />, desc: 'Processa modems (NEL, RJ, SP) dos downloads.', endpoint: 'outlook' },
    { title: 'Relatório Aniel', subtitle: 'Sinapse Manual', icon: <Database className="text-cyan-400" />, desc: 'Importa movimentações e saldos manuais.', endpoint: 'aniel' },
    { title: 'Relatório WMS', subtitle: 'Logística IHS', icon: <Package className="text-emerald-400" />, desc: 'Consolida estoque e devoluções técnicos.', endpoint: 'wms' }
  ];

  const vivoCards = [
    { title: 'Atualizar Planilha', subtitle: 'VBS Automation', icon: <FileSpreadsheet className="text-purple-400" />, desc: 'Executa script de atualização via terminal.', endpoint: 'vivo/atualizar' },
    { title: 'Importar Carga', subtitle: 'SQL Ingestion', icon: <Zap className="text-yellow-400" />, desc: 'Persiste os dados serializados no Banco de Dados.', endpoint: 'vivo/importar' }
  ];

  const currentCards = activeTab === 'ihs' ? ihsCards : vivoCards;

  return (
    <div className="min-h-screen bg-[#05070a] text-white flex">
      {/* --- SIDEBAR --- */}
      <nav className="w-24 border-r border-white/5 flex flex-col items-center py-10 gap-10 bg-black/40 backdrop-blur-xl">
        <div className="w-12 h-12 bg-blue-600 rounded-2xl flex items-center justify-center shadow-lg shadow-blue-600/20">
          <LayoutDashboard size={24} />
        </div>

        <div className="flex flex-col gap-8 flex-grow">
          <button
            onClick={() => setActiveTab('ihs')}
            className={`p-4 rounded-xl transition-all ${activeTab === 'ihs' ? 'bg-blue-600/20 text-blue-400 border border-blue-500/30' : 'text-gray-600 hover:text-white'}`}
            title="Relatório IHS"
          >
            <Database size={24} />
          </button>
          <button
            onClick={() => setActiveTab('vivo')}
            className={`p-4 rounded-xl transition-all ${activeTab === 'vivo' ? 'bg-purple-600/20 text-purple-400 border border-purple-500/30' : 'text-gray-600 hover:text-white'}`}
            title="Relatório Vivo"
          >
            <Zap size={24} />
          </button>
        </div>

        <div className="w-10 h-10 rounded-full bg-gray-900 border border-white/10 flex items-center justify-center">
          <User size={18} className="text-gray-500" />
        </div>
      </nav>

      {/* --- CONTENT AREA --- */}
      <main className="flex-grow p-12 lg:p-20 overflow-y-auto">
        <header className="flex justify-between items-end mb-16">
          <motion.div initial={{ x: -20, opacity: 0 }} animate={{ x: 0, opacity: 1 }}>
            <p className="text-blue-500 text-xs font-bold tracking-[0.3em] uppercase mb-2">Painel de Controle Unificado</p>
            <h1 className="text-4xl font-bold tracking-tight">
              Relatório {activeTab === 'ihs' ? <span className="text-blue-500">IHS</span> : <span className="text-purple-500">VIVO</span>}
            </h1>
          </motion.div>

          <div className="flex items-center gap-2 bg-white/5 px-4 py-2 rounded-full border border-white/5">
            <span className="w-2 h-2 bg-green-500 rounded-full animate-pulse"></span>
            <span className="text-[10px] font-bold text-gray-400 uppercase tracking-widest">Motor Java Online</span>
          </div>
        </header>

        {/* CARDS GRID */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8 mb-12">
          {currentCards.map((card, i) => (
            <motion.div
              key={card.title}
              initial={{ y: 20, opacity: 0 }}
              animate={{ y: 0, opacity: 1 }}
              transition={{ delay: i * 0.1 }}
              className="glass-card p-10 group relative h-full flex flex-col"
            >
              <div className="bg-white/5 w-14 h-14 rounded-2xl flex items-center justify-center mb-8 border border-white/5 transition-all group-hover:border-blue-500/30">
                {React.cloneElement(card.icon, { size: 28 })}
              </div>
              <p className="text-blue-500 text-[10px] font-bold tracking-[0.2em] uppercase mb-2">{card.subtitle}</p>
              <h3 className="text-xl font-bold mb-4">{card.title}</h3>
              <p className="text-gray-500 text-sm leading-relaxed italic flex-grow mb-8">{card.desc}</p>
              <button
                onClick={() => triggerProcess(card.endpoint, card.title)}
                className="btn-primary w-full group"
              >
                <span>Executar Processo</span>
                <ArrowRight size={18} className="group-hover:translate-x-1 transition-transform" />
              </button>
            </motion.div>
          ))}
        </div>

        {/* LOG CONSOLE */}
        <motion.div initial={{ y: 50, opacity: 0 }} animate={{ y: 0, opacity: 1 }} className="glass-card overflow-hidden">
          <div className="px-8 py-4 border-b border-white/5 bg-white/5 flex justify-between items-center">
            <div className="flex items-center gap-3">
              <Terminal size={16} className="text-blue-400" />
              <h4 className="text-[10px] font-bold text-gray-400 tracking-[0.2em] uppercase">Status do Sistema</h4>
            </div>
            <button onClick={() => setLogs([])} className="p-2 hover:bg-white/5 rounded-lg transition-colors">
              <Trash2 size={14} className="text-gray-500" />
            </button>
          </div>
          <div className="h-60 overflow-y-auto p-8 font-mono text-xs space-y-3 bg-black/20 custom-scroll">
            {logs.map(log => (
              <div key={log.id} className="flex gap-4">
                <span className="text-gray-600">[{log.time}]</span>
                <span className={log.type === 'success' ? 'text-green-400' : log.type === 'error' ? 'text-red-400' : 'text-blue-300'}>
                  {log.type === 'info' ? '→' : '✓'} {log.msg}
                </span>
              </div>
            ))}
          </div>
        </motion.div>
      </main>
    </div>
  );
};

export default App;
