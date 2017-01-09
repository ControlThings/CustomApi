package mist.customapi;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import java.net.ContentHandler;

import mist.sandbox.AppToMist;
import mist.sandbox.Callback;

/**
 * Created by jan on 1/9/17.
 */

class MistApiBridge {
    private final String TAG = "Mist Api Bridge";

    private Context context;
    private boolean mBound = false;
    private MistApiBridgeJni jni;
    private AppToMist appToMist;

    MistApiBridge(Context context, MistApiBridgeJni jni) {
        this.context = context;
        this.jni = jni;
        Intent mistSandbox = new Intent();
        mistSandbox.setComponent(new ComponentName("fi.ct.mist", "fi.ct.mist.sandbox.Sandbox"));
        context.startService(mistSandbox);
        context.bindService(mistSandbox, mConnection, 0);
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.d(TAG, "onServiceConnected");
            appToMist = AppToMist.Stub.asInterface(iBinder);
            mBound = true;
            //register/login
            jni.connected(true);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBound = false;
        }
    };



    int wishApiRequest(String op, byte[] data, Callback listener) {
        int id = 0;
        if (!mBound) {
            Log.v(TAG, "Error: not bound");
        } else {
            try {
                id = appToMist.wishApiRequest(op, data, listener);
            } catch (RemoteException e) {
                Log.e(TAG, "RemoteException occured in wishApi: " + e);
            }
        }
        return id;
    }

    int mistApiRequest(String op, byte[] data, Callback listener) {
        int id = 0;
        if (!mBound) {
            Log.v(TAG, "Error: not bound");
        } else {
            try {
                id = appToMist.mistApiRequest(op, data, listener);
            } catch (RemoteException e) {
                Log.e(TAG, "RemoteException occured in wishApi: " + e);
            }
        }
        return id;
    }

    void unBind() {
        if (mBound) {
            mBound = false;
            context.unbindService(mConnection);
        } else {
            Log.d(TAG, "Not bound when unbinding");
        }
    }

}
