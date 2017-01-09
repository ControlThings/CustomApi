package mist.customapi;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Base64;
import android.util.Log;

import java.net.ContentHandler;
import java.util.Random;

import mist.sandbox.AppToMist;
import mist.sandbox.Callback;

/**
 * Created by jan on 1/9/17.
 */

class MistApiBridge {
    private final String TAG = "Mist Api Bridge";

    private final static String pref = "mist_pref";

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

    private byte[] getId() {
        SharedPreferences preferences = context.getSharedPreferences(pref, Context.MODE_PRIVATE);
        String idString = preferences.getString("id", null);
        if (idString == null) {
            byte[] id = new byte[32];
            new Random().nextBytes(id);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("id", Base64.encodeToString(id, Base64.DEFAULT));
            editor.commit();
            return id;
        } else {
            return Base64.decode(idString, Base64.DEFAULT);
        }
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.d(TAG, "onServiceConnected");
            appToMist = AppToMist.Stub.asInterface(iBinder);
           try {
               if (appToMist.login(new Binder(), getId(), "name")) {
                   //jni.connected(true);
                   mBound = true;
               } else {
                   context.unbindService(mConnection);
               }
           } catch (RemoteException e) {
               Log.d(TAG, "remote exeption in register:");
           }


            //register/login
            //
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
                Log.e(TAG, "RemoteException occured while performing wishApiRequest: " + e);
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
                Log.e(TAG, "RemoteException occured while performing mistApiRequest " + e);
            }
        }
        return id;
    }

    void mistApiCancel(int id) {
        if (!mBound) {
            Log.v(TAG, "Error: not bound");
        } else {
            try {
                appToMist.mistApiCancel(id);
            } catch (RemoteException e) {
                Log.e(TAG, "RemoteException occured while performing mistApiCancel  " + e);
            }
        }
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
