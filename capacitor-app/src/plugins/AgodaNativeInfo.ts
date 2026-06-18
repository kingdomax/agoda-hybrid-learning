import { registerPlugin } from "@capacitor/core";
import type { PluginListenerHandle } from "@capacitor/core";

export type EchoOptions = {
    value: string;
};

export type EchoResult = {
    value: string;
    platform: string;
};

export type DeviceInfoResult = {
    platform: string;
    osVersion: string;
    model: string;
    manufacturer: string;
};

export type SaveSessionValueOptions = {
    key: string;
    value: string;
};

export type SaveSessionValueResult = {
    success: boolean;
};

export type GetSessionValueOptions = {
    key: string;
};

export type GetSessionValueResult = {
    value: string | null;
};

export type SessionValueChangedEvent = {
    key: string;
    value: string | null;
    source: "android" | "web";
    changedAtEpochMs: number;
};

export interface AgodaNativeInfoPlugin {
    echo(options: EchoOptions): Promise<EchoResult>;

    getDeviceInfo(): Promise<DeviceInfoResult>;

    saveSessionValue(
        options: SaveSessionValueOptions,
    ): Promise<SaveSessionValueResult>;

    getSessionValue(
        options: GetSessionValueOptions,
    ): Promise<GetSessionValueResult>;

    addListener(
        eventName: "sessionValueChanged",
        listenerFunc: (event: SessionValueChangedEvent) => void,
    ): Promise<PluginListenerHandle>;

    removeAllListeners(): Promise<void>;
}

export const AgodaNativeInfo = registerPlugin<AgodaNativeInfoPlugin>(
    "AgodaNativeInfo",
    {
        // web fallback implementation file only load when there is no native detected !
        web: () =>
            import("./AgodaNativeInfo.web.ts").then(
                (module) => new module.AgodaNativeInfoWeb(),
            ),
    },
);
