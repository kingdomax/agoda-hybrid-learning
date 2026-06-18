import { WebPlugin } from "@capacitor/core";
import type {
    AgodaNativeInfoPlugin,
    DeviceInfoResult,
    EchoOptions,
    EchoResult,
    GetSessionValueOptions,
    GetSessionValueResult,
    SaveSessionValueOptions,
    SaveSessionValueResult,
    SessionValueChangedEvent,
} from "./AgodaNativeInfo";

// Implement fallback web implementation for AgodaNativeInfoWeb
export class AgodaNativeInfoWeb
    extends WebPlugin
    implements AgodaNativeInfoPlugin
{
    private readonly sessionValues = new Map<string, string>();

    async echo(options: EchoOptions): Promise<EchoResult> {
        if (!options.value?.trim()) {
            throw this.unavailable("value is required");
        }

        return {
            value: options.value,
            platform: "web",
        };
    }

    async getDeviceInfo(): Promise<DeviceInfoResult> {
        return {
            platform: "web",
            osVersion: window.navigator.userAgent,
            model: "browser",
            manufacturer: "unknown",
        };
    }

    async saveSessionValue(
        options: SaveSessionValueOptions,
    ): Promise<SaveSessionValueResult> {
        if (!options.key?.trim()) {
            throw this.unavailable("key is required");
        }

        this.sessionValues.set(options.key, options.value);

        const event: SessionValueChangedEvent = {
            key: options.key,
            value: options.value,
            source: "web",
            changedAtEpochMs: Date.now(),
        };

        this.notifyListeners("sessionValueChanged", event);

        return {
            success: true,
        };
    }

    async getSessionValue(
        options: GetSessionValueOptions,
    ): Promise<GetSessionValueResult> {
        if (!options.key?.trim()) {
            throw this.unavailable("key is required");
        }

        return {
            value: this.sessionValues.get(options.key) ?? null,
        };
    }
}
