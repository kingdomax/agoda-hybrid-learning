import { useEffect, useState } from "react";
import "./App.css";
import { AgodaNativeInfo } from "./plugins/AgodaNativeInfo";
import type { SessionValueChangedEvent } from "./plugins/AgodaNativeInfo";

type WebLog = {
    id: number;
    message: string;
};

function App() {
    const [logs, setLogs] = useState<WebLog[]>([
        { id: 1, message: "Capacitor React app mounted." },
    ]);

    const [sessionKey, setSessionKey] = useState("demo_token");
    const [sessionValue, setSessionValue] = useState("abc-123");

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
        let isMounted = true;

        const registerListener = async () => {
            const handle = await AgodaNativeInfo.addListener(
                "sessionValueChanged",
                (event: SessionValueChangedEvent) => {
                    if (!isMounted) {
                        return;
                    }

                    addLog(
                        `Listener event: sessionValueChanged ${JSON.stringify(event)}`,
                    );
                },
            );

            addLog("Listener registered: sessionValueChanged");

            return handle;
        };

        const handlePromise = registerListener();

        return () => {
            isMounted = false;

            handlePromise
                .then((handle) => {
                    handle?.remove();
                    addLog("Listener removed: sessionValueChanged");
                })
                .catch((error) => {
                    console.error("Failed to remove listener", error);
                });
        };
    }, []);

    const callPlugin = async <T,>(label: string, action: () => Promise<T>) => {
        try {
            addLog(`Calling plugin: ${label}`);

            const result = await action();

            addLog(`${label} result: ${JSON.stringify(result)}`);
        } catch (error) {
            addLog(
                `${label} error: ${
                    error instanceof Error ? error.message : "Unknown error"
                }`,
            );
        }
    };

    return (
        <main className="app-shell">
            <section className="card">
                <p className="eyebrow">Agoda Hybrid Learning</p>

                <h1>Capacitor Native Plugin</h1>

                <p className="description">
                    This React TypeScript app runs inside Capacitor Android
                    using live URL mode. It calls Promise plugin methods and
                    also listens to native/web plugin events.
                </p>

                <div className="field-group">
                    <label htmlFor="session-key">Session key</label>
                    <input
                        id="session-key"
                        value={sessionKey}
                        onChange={(event) => setSessionKey(event.target.value)}
                    />
                </div>

                <div className="field-group">
                    <label htmlFor="session-value">Session value</label>
                    <input
                        id="session-value"
                        value={sessionValue}
                        onChange={(event) =>
                            setSessionValue(event.target.value)
                        }
                    />
                </div>

                <div className="button-row">
                    <button
                        onClick={() =>
                            callPlugin("echo", () =>
                                AgodaNativeInfo.echo({
                                    value: "hello from React",
                                }),
                            )
                        }
                    >
                        Echo Native
                    </button>

                    <button
                        onClick={() =>
                            callPlugin("getDeviceInfo", () =>
                                AgodaNativeInfo.getDeviceInfo(),
                            )
                        }
                    >
                        Get Device Info
                    </button>

                    <button
                        onClick={() =>
                            callPlugin("saveSessionValue", () =>
                                AgodaNativeInfo.saveSessionValue({
                                    key: sessionKey,
                                    value: sessionValue,
                                }),
                            )
                        }
                    >
                        Save Session Value
                    </button>

                    <button
                        onClick={() =>
                            callPlugin("getSessionValue", () =>
                                AgodaNativeInfo.getSessionValue({
                                    key: sessionKey,
                                }),
                            )
                        }
                    >
                        Get Session Value
                    </button>

                    <button
                        onClick={() =>
                            callPlugin("removeAllListeners", () =>
                                AgodaNativeInfo.removeAllListeners(),
                            )
                        }
                    >
                        Remove All Listeners
                    </button>
                </div>

                <section className="log-panel">
                    <h2>Plugin Logs</h2>

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
