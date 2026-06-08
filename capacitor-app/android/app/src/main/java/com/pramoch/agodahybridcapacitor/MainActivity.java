package com.pramoch.agodahybridcapacitor;

import android.os.Bundle;
import android.webkit.WebView;
import com.getcapacitor.BridgeActivity;

public class MainActivity extends BridgeActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        WebView.setWebContentsDebuggingEnabled(true);
        registerPlugin(AgodaNativeInfoPlugin.class);
        super.onCreate(savedInstanceState);
    }
}
