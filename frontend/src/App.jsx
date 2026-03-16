import React, { useState } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import {
  Terminal,
  User,
  ArrowRight,
  RefreshCw,
  Trash2,
  HardDrive,
  Truck,
  Wrench,
  FileSpreadsheet
} from 'lucide-react';

const App = () => {
  const [showDashboard, setShowDashboard] = useState(false);
  const [activeReport, setActiveReport] = useState('maquinas');
  const [logs, setLogs] = useState([
    { id: 1, time: '16:35:01', msg: 'Dashboard Inicializado.', type: 'info' },
    { id: 2, time: '16:35:05', msg: 'Conectado ao Motor Java Engine.', type: 'success' }
  ]);

  // Terminal State for Excel Plus
  const [terminalInput, setTerminalInput] = useState('');
  const [terminalHistory, setTerminalHistory] = useState([
    { id: 1, text: 'AXIS EXCEL PLUS v1.0.0 - AMBIENTE DE EXECUÇÃO MCP', type: 'header' },
    { id: 2, text: 'Aguardando comandos...', type: 'info' }
  ]);

  const handleTerminalSubmit = async (e) => {
    e.preventDefault();
    if (!terminalInput.trim()) return;

    const cmdText = terminalInput;
    const newCmd = { id: Date.now(), text: `> ${cmdText}`, type: 'command' };
    setTerminalHistory(prev => [...prev, newCmd]);
    setTerminalInput('');
    
    // Call Python Bridge
    const processingId = Date.now() + 1;
    setTerminalHistory(prev => [...prev, { id: processingId, text: 'Processando comando via Excel Plus (Python)...', type: 'info' }]);

    try {
      const response = await fetch('http://localhost:5001/api/excel/execute', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ command: cmdText })
      });
      const data = await response.json();
      
      setTerminalHistory(prev => [
        ...prev.filter(i => i.id !== processingId),
        { id: Date.now() + 2, text: data.success ? `✓ ${data.msg}` : `✗ Erro: ${data.msg}`, type: data.success ? 'success' : 'error' }
      ]);
    } catch (err) {
      setTerminalHistory(prev => [
        ...prev.filter(i => i.id !== processingId),
        { id: Date.now() + 3, text: `✗ Servidor Python Offline: ${err.message}`, type: 'error' }
      ]);
    }
  };

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

  const maquinasCards = [
    { title: 'Renomear Arquivos', subtitle: 'File Organizer', icon: <RefreshCw />, desc: 'Movimenta e renomeia os arquivos de maquinário.', endpoint: 'maquinas/renomear' },
    { title: 'Importar BD', subtitle: 'Database Ingestion', icon: <HardDrive />, desc: 'Importa o relatório para o Banco de Dados.', endpoint: 'maquinas/importar' }
  ];

  const ferramentariaCards = [
    { title: 'Extrair Dados do Portal', subtitle: 'Portal Extraction', icon: <RefreshCw />, desc: 'Extrai os dados diretamente do Portal Ferramentaria.', endpoint: 'ferramentaria/extrair' },
    { title: 'Processo a Definir', subtitle: 'Pending Task', icon: <HardDrive />, desc: 'Este processo será definido em breve.', endpoint: 'ferramentaria/processo' }
  ];

  const themes = {
    maquinas: {
      name: 'MAQUINÁRIO',
      color: '#eab308',
      glow: 'rgba(234, 179, 8, 0.15)',
      text: 'text-yellow-500',
      border: 'border-yellow-500/30',
      bg: 'bg-yellow-600/20',
      cards: maquinasCards
    },
    ferramentaria: {
      name: 'FERRAMENTARIA',
      color: '#ef4444',
      glow: 'rgba(239, 68, 68, 0.15)',
      text: 'text-red-500',
      border: 'border-red-500/30',
      bg: 'bg-red-600/20',
      cards: ferramentariaCards
    },
    excelPlus: {
      name: 'EXCEL PLUS',
      color: '#22c55e',
      glow: 'rgba(34, 197, 94, 0.15)',
      text: 'text-green-500',
      border: 'border-green-500/30',
      bg: 'bg-green-600/20',
      cards: [
        { title: 'Criar Planilha', subtitle: 'New Spreadsheet', icon: <RefreshCw />, desc: 'Cria uma nova planilha Excel com cabeçalhos padrão.', endpoint: 'excel/criar' },
        { title: 'Gerar Relatório', subtitle: 'Data Analysis', icon: <HardDrive />, desc: 'Processa dados e gera um relatório detalhado em Excel.', endpoint: 'excel/relatorio' }
      ]
    }
  };

  const currentTheme = themes[activeReport];

  return (
    <div className="min-h-screen bg-[#05070a] text-white overflow-hidden">
      <AnimatePresence mode="wait">
        {!showDashboard ? (
          <motion.div
            key="landing"
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0, y: -20 }}
            className="flex flex-col items-center justify-center h-screen relative"
          >
            {/* Background Glow */}
            <div className="absolute w-[500px] h-[500px] bg-white/5 rounded-full blur-[120px] -z-10 animate-pulse"></div>

            <motion.div
              initial={{ scale: 0.9, opacity: 0 }}
              animate={{ scale: 1, opacity: 1 }}
              transition={{ duration: 0.8, ease: "easeOut" }}
              className="text-center"
            >
              <h1 className="text-[140px] font-['Barrio'] tracking-tight mb- text-white drop-shadow-[0_0_40px_rgba(255,255,255,0.2)]">
                AXIS
              </h1>
              <motion.p
                initial={{ y: 20, opacity: 0 }}
                animate={{ y: 0, opacity: 1 }}
                transition={{ delay: 0.5, duration: 0.8 }}
                className="text-gray-400 text-[9px] font-light tracking-[0.2em] uppercase mb-4"
              >
                O eixo da inteligência
              </motion.p>

              <motion.button
                whileHover={{ scale: 1.05, boxShadow: "0 0 20px rgba(255,255,255,0.1)" }}
                whileTap={{ scale: 0.95 }}
                onClick={() => setShowDashboard(true)}
                className="mt-4 px-8 py-3 bg-white text-black font-bold text-[10px] uppercase tracking-[0.2em] rounded-full transition-all hover:bg-gray-200"
              >
                Iniciar
              </motion.button>
            </motion.div>

            <div className="absolute bottom-10 text-[5px] text-gray-700 tracking-widest uppercase">
              Powered by AeroCode v4.0
            </div>
          </motion.div>
        ) : (
          <motion.div
            key="dashboard"
            initial={{ opacity: 0, x: 20 }}
            animate={{ opacity: 1, x: 0 }}
            className="flex h-screen"
            style={{
              '--theme-color': currentTheme.color,
              '--theme-glow': currentTheme.glow
            }}
          >
            {/* --- SIDEBAR --- */}
            <nav className="w-24 border-r border-white/5 flex flex-col items-center py-10 gap-10 bg-black/40 backdrop-blur-xl">
              <div className="w-12 h-12 bg-white/5 border border-white/10 rounded-2xl flex items-center justify-center">
                <span className="font-black text-xs">AX</span>
              </div>

              <div className="flex flex-col gap-8 flex-grow text-center">
                <button
                  onClick={() => setActiveReport('maquinas')}
                  className={`p-4 rounded-xl transition-all ${activeReport === 'maquinas' ? `${currentTheme.bg} ${currentTheme.text} border ${currentTheme.border}` : 'text-gray-600 hover:text-gray-400'}`}
                  title="Relatório Maquinário"
                >
                  <Truck size={24} />
                </button>
                <button
                  onClick={() => setActiveReport('ferramentaria')}
                  className={`p-4 rounded-xl transition-all ${activeReport === 'ferramentaria' ? `${currentTheme.bg} ${currentTheme.text} border ${currentTheme.border}` : 'text-gray-600 hover:text-gray-400'}`}
                  title="Portal Ferramentaria"
                >
                  <Wrench size={24} />
                </button>
                <button
                  onClick={() => setActiveReport('excelPlus')}
                  className={`p-4 rounded-xl transition-all ${activeReport === 'excelPlus' ? `${currentTheme.bg} ${currentTheme.text} border ${currentTheme.border}` : 'text-gray-600 hover:text-gray-400'}`}
                  title="Excel Plus"
                >
                  <FileSpreadsheet size={24} />
                </button>
              </div>

              <div className="w-10 h-10 rounded-full bg-gray-900 border border-white/10 flex items-center justify-center">
                <User size={18} className="text-gray-500" />
              </div>
            </nav >

            {/* --- CONTENT AREA --- */}
            < main className="flex-grow p-12 lg:p-20 overflow-y-auto" >
              <header className="flex justify-between items-end mb-16">
                <motion.div
                  key={activeReport + '-header'}
                  initial={{ x: -20, opacity: 0 }}
                  animate={{ x: 0, opacity: 1 }}
                >
                  <p className={`${currentTheme.text} text-xs font-bold tracking-[0.3em] uppercase mb-2`}>AXIS CONTROL</p>
                  <h1 className="text-4xl font-bold tracking-tight">
                    {activeReport === 'maquinas' ? 'Relatório' : 'Portal'} <span className={currentTheme.text}>{currentTheme.name}</span>
                  </h1>
                </motion.div>

                <div className="flex items-center gap-2 bg-white/5 px-4 py-2 rounded-full border border-white/5">
                  <span className="w-2 h-2 bg-green-500 rounded-full animate-pulse"></span>
                  <span className="text-[10px] font-bold text-gray-400 uppercase tracking-widest">Sistema Operacional</span>
                </div>
              </header>

              {activeReport === 'excelPlus' ? (
                <motion.div
                  initial={{ opacity: 0, scale: 0.95 }}
                  animate={{ opacity: 1, scale: 1 }}
                  className="bg-black/80 border border-green-500/30 rounded-2xl h-[600px] flex flex-col overflow-hidden backdrop-blur-2xl shadow-[0_0_50px_rgba(34,197,94,0.1)]"
                >
                  {/* Terminal Header */}
                  <div className="bg-green-500/10 px-6 py-3 border-b border-green-500/20 flex items-center justify-between">
                    <div className="flex items-center gap-3">
                      <div className="flex gap-1.5">
                        <div className="w-3 h-3 rounded-full bg-red-500/50"></div>
                        <div className="w-3 h-3 rounded-full bg-yellow-500/50"></div>
                        <div className="w-3 h-3 rounded-full bg-green-500/50"></div>
                      </div>
                      <span className="text-green-500/70 text-[10px] font-bold tracking-[0.2em] uppercase ml-4">mcp-server@axis: ~/excel-plus</span>
                    </div>
                    <Terminal size={14} className="text-green-500/50" />
                  </div>

                  {/* Terminal Output */}
                  <div className="flex-grow p-8 font-mono text-sm overflow-y-auto custom-scroll space-y-2 selection:bg-green-500/30">
                    {terminalHistory.map((item) => (
                      <div
                        key={item.id}
                        className={`
                          ${item.type === 'header' ? 'text-green-400 font-bold border-b border-green-500/20 pb-2 mb-4' : ''}
                          ${item.type === 'command' ? 'text-white' : ''}
                          ${item.type === 'success' ? 'text-green-400' : ''}
                          ${item.type === 'info' ? 'text-green-500/60' : ''}
                          ${item.type === 'error' ? 'text-red-400' : ''}
                        `}
                      >
                        {item.text}
                      </div>
                    ))}
                    <div className="h-4"></div>
                  </div>

                  {/* Terminal Input */}
                  <form onSubmit={handleTerminalSubmit} className="p-6 bg-black/40 border-t border-green-500/20">
                    <div className="flex items-center gap-3 text-green-500">
                      <span className="font-bold">➜</span>
                      <span className="text-green-500/50 font-bold">~</span>
                      <input
                        type="text"
                        value={terminalInput}
                        onChange={(e) => setTerminalInput(e.target.value)}
                        placeholder="Digite um comando para o MCP (ex: criar planilha vendas.xlsx)..."
                        className="flex-grow bg-transparent border-none outline-none text-green-400 placeholder:text-green-900/50 font-mono"
                        autoFocus
                      />
                    </div>
                  </form>
                </motion.div>
              ) : (
                <>
                  {/* CARDS GRID */}
                  <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8 mb-12">
                    {currentTheme.cards.map((card, i) => (
                      <motion.div
                        key={card.title}
                        initial={{ y: 20, opacity: 0 }}
                        animate={{ y: 0, opacity: 1 }}
                        transition={{ delay: i * 0.1 }}
                        className="glass-card p-10 group relative h-full flex flex-col"
                      >
                        <div className={`bg-white/5 w-14 h-14 rounded-2xl flex items-center justify-center mb-8 border border-white/5 transition-all group-hover:${currentTheme.border}`}>
                          {React.cloneElement(card.icon, { size: 28, className: currentTheme.text })}
                        </div>
                        <p className={`${currentTheme.text} text-[10px] font-bold tracking-[0.2em] uppercase mb-2`}>{card.subtitle}</p>
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
                        <Terminal size={16} className={currentTheme.text} />
                        <h4 className="text-[10px] font-bold text-gray-400 tracking-[0.2em] uppercase">Console Log</h4>
                      </div>
                      <button onClick={() => setLogs([])} className="p-2 hover:bg-white/5 rounded-lg transition-colors">
                        <Trash2 size={14} className="text-gray-500" />
                      </button>
                    </div>
                    <div className="h-60 overflow-y-auto p-8 font-mono text-xs space-y-3 bg-black/20 custom-scroll">
                      {logs.map(log => (
                        <div key={log.id} className="flex gap-4">
                          <span className="text-gray-600">[{log.time}]</span>
                          <span className={log.type === 'success' ? 'text-green-400' : log.type === 'error' ? 'text-red-400' : currentTheme.text}>
                            {log.type === 'info' ? '→' : '✓'} {log.msg}
                          </span>
                        </div>
                      ))}
                    </div>
                  </motion.div>
                </>
              )}
            </main >
          </motion.div>
        )}
      </AnimatePresence>
    </div>
  );
};

export default App;

