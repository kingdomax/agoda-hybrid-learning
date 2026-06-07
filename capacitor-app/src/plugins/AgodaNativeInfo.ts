import { registerPlugin } from "@capacitor/core";

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

export interface AgodaNativeInfoPlugin {
    echo(options: EchoOptions): Promise<EchoResult>;

    getDeviceInfo(): Promise<DeviceInfoResult>;

    saveSessionValue(
        options: SaveSessionValueOptions,
    ): Promise<SaveSessionValueResult>;

    getSessionValue(
        options: GetSessionValueOptions,
    ): Promise<GetSessionValueResult>;
}

export const AgodaNativeInfo =
    registerPlugin<AgodaNativeInfoPlugin>("AgodaNativeInfo"); // important must match the Android annotation later
