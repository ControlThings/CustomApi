package mist;

import android.content.Context;

import mist.MistApiBridge;

/**
 * Created by jan on 1/9/17.
 */

class MistApiBridgeJni {
    static {
        System.loadLibrary("mistcustomapi");
    }

    MistApiBridge mistApiBridge;
    MistApiBridgeJni(Context context, String name) {
        mistApiBridge = new MistApiBridge(context, this, name);
        register(mistApiBridge);
    }

    native void register(MistApiBridge bridge);
    native void connected(boolean connected);

    void disconnect() {
        connected(false);
        mistApiBridge.unBind();
    }
}
