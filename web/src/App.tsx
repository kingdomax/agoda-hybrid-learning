import { useState } from "react";
import "./App.css";

type WebLog = {
    id: number;
    message: string;
};

function App() {
    const [logs, setLogs] = useState<WebLog[]>([
        { id: 1, message: "React app mounted inside browser/WebView." },
    ]);

    const addLog = (message: string) => {
        setLogs((current) => [{ id: Date.now(), message }, ...current]);
    };

    const handleWebButtonClick = () => {
        addLog("Button clicked from React web app.");
    };

    const handleCheckUserAgent = () => {
        addLog(window.navigator.userAgent);
    };

    return (
        <main className="app-shell">
            <section className="card">
                <p className="eyebrow">Agoda Hybrid Learning</p>

                <h1>Mini Booking Web</h1>

                <p className="description">
                    This React TypeScript app is running from Vite. Today we
                    will load it inside a native Android WebView.
                </p>

                <div className="button-row">
                    <button onClick={handleWebButtonClick}>
                        Test React Button
                    </button>

                    <button onClick={handleCheckUserAgent}>
                        Log User Agent
                    </button>
                </div>

                <section className="log-panel">
                    <h2>Web Logs</h2>

                    {logs.map((log) => (
                        <p key={log.id} className="log-line">
                            {log.message}
                        </p>
                    ))}
                </section>
            </section>
        </main>
    );
}

export default App;
