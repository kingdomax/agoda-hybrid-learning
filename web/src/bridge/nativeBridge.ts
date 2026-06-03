declare global {
    interface Window {
        // JS can call Android via this but Native need to inject object first (JS → Native)
        AgodaNative?: {
            postMessage: (message: string) => void;
        };

        // Android can call (Native → JS)
        AgodaNativeBridge?: {
            onNativeMessage: (message: NativeResponse) => void;
        };
    }
}

export type NativeRequest =
    | {
          id: string; // Each message has an id so request/response can be correlated.
          type: "PING";
          payload?: undefined;
      }
    | {
          id: string;
          type: "GET_DEVICE_INFO";
          payload?: undefined;
      }
    | {
          id: string;
          type: "SAVE_TOKEN";
          payload: {
              token: string;
          };
      }
    | {
          id: string;
          type: "GET_TOKEN";
          payload?: undefined;
      };

export type NativeResponse =
    | {
          id: string;
          type: "PONG";
          payload: {
              message: string;
              receivedAt: string;
          };
      }
    | {
          id: string;
          type: "DEVICE_INFO";
          payload: {
              platform: string;
              osVersion: string;
              model: string;
          };
      }
    | {
          id: string;
          type: "TOKEN_SAVED";
          payload: {
              success: boolean;
          };
      }
    | {
          id: string;
          type: "TOKEN_VALUE";
          payload: {
              token: string | null;
          };
      }
    | {
          id: string;
          type: "ERROR";
          payload: {
              message: string;
          };
      };

export const createRequestId = () => {
    return `web-${Date.now()}-${Math.random().toString(16).slice(2)}`;
};

export const isRunningInsideAndroidBridge = () => {
    return Boolean(window.AgodaNative?.postMessage);
};

export const postToNative = (request: NativeRequest) => {
    if (!window.AgodaNative?.postMessage) {
        throw new Error("AgodaNative bridge is not available.");
    }

    window.AgodaNative.postMessage(JSON.stringify(request));
};
