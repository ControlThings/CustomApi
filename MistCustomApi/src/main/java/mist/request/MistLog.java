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
package mist.request;

import android.util.Log;

/**
 * Created by jeppe on 1/10/17.
 */

class MistLog {

    static void err(String op, int code, String msg) {
        Log.e("RPC error", msg + " code: " + code + " op: " + op);
    }
}
