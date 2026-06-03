import { useEffect, useState } from "react";
import "./App.css";
import {
    createRequestId,
    isRunningInsideAndroidBridge,
    type NativeResponse,
    postToNative,
} from "./bridge/nativeBridge";

type WebLog = {
    id: number;
    message: string;
};

const App = () => {
    const [logs, setLogs] = useState<WebLog[]>([
        { id: 1, message: "React app mounted." },
    ]);
    const [tokenInput, setTokenInput] = useState("demo-token-123");
    const [bridgeAvailable, setBridgeAvailable] = useState(false);

    const addLog = (message: string) => {
        setLogs((current) => [
            {
                id: Date.now() + Math.random(),
                message,
            },
            ...current,
        ]);
    };

    useEffect(() => {
        // eslint-disable-next-line react-hooks/set-state-in-effect
        setBridgeAvailable(isRunningInsideAndroidBridge());

        // binding JS callback with setState in useEffect
        window.AgodaNativeBridge = {
            onNativeMessage: (message: NativeResponse) => {
                addLog(`Native → JS: ${JSON.stringify(message)}`);
            },
        };

        addLog("window.AgodaNativeBridge.onNativeMessage registered.");

        return () => {
            delete window.AgodaNativeBridge;
        };
    }, []);

    const handlePing = () => {
        sendToNative({
            id: createRequestId(),
            type: "PING",
        });
    };

    const handleGetDeviceInfo = () => {
        sendToNative({
            id: createRequestId(),
            type: "GET_DEVICE_INFO",
        });
    };

    const handleSaveToken = () => {
        sendToNative({
            id: createRequestId(),
            type: "SAVE_TOKEN",
            payload: {
                token: tokenInput,
            },
        });
    };

    const handleGetToken = () => {
        sendToNative({
            id: createRequestId(),
            type: "GET_TOKEN",
        });
    };

    const handleCheckUserAgent = () => {
        addLog(window.navigator.userAgent);
    };

    const sendToNative = (request: Parameters<typeof postToNative>[0]) => {
        try {
            addLog(`JS → Native: ${JSON.stringify(request)}`);
            postToNative(request);
        } catch (error) {
            addLog(
                `Bridge error: ${
                    error instanceof Error ? error.message : "Unknown error"
                }`,
            );
        }
    };

    return (
        <main className="app-shell">
            <section className="card">
                <p className="eyebrow">Agoda Hybrid Learning</p>

                <h1>Mini Booking Web</h1>

                <p className="description">
                    This React app is running inside a native Android WebView.
                    Today it communicates with Kotlin through a raw JS/native
                    bridge.
                </p>

                <p className={bridgeAvailable ? "status-ok" : "status-warn"}>
                    Bridge status:{" "}
                    {bridgeAvailable ? "Available" : "Not available"}
                </p>

                <div className="field-group">
                    <label htmlFor="token">Demo token</label>
                    <input
                        id="token"
                        value={tokenInput}
                        onChange={(event) => setTokenInput(event.target.value)}
                    />
                </div>

                <div className="button-row">
                    <button onClick={handlePing}>Ping Native</button>
                    <button onClick={handleGetDeviceInfo}>
                        Get Device Info
                    </button>
                    <button onClick={handleSaveToken}>Save Token Native</button>
                    <button onClick={handleGetToken}>Get Token Native</button>
                    <button onClick={handleCheckUserAgent}>
                        Log User Agent
                    </button>
                </div>

                <section className="log-panel">
                    <h2>Bridge Logs</h2>

                    {logs.map((log) => (
                        <p key={log.id} className="log-line">
                            {log.message}
                        </p>
                    ))}
                </section>
            </section>
        </main>
    );
};

export default App;
