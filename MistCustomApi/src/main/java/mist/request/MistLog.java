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
