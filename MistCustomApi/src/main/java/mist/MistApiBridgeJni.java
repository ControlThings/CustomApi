/**
 * Copyright (C) 2020, ControlThings Oy Ab
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * @license Apache-2.0
 */
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
        //connected(false);
        mistApiBridge.unBind();
    }
}
