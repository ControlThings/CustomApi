package mist.customapi;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import java.util.UUID;

import mist.sandbox.AppToMist;
import mist.sandbox.Callback;

/**
 * Created by jeppe on 1/4/17.
 */

public class MistService extends Service {

    private final String TAG = "Mist Service";

    private final IBinder mBinder = new MistServiceBinder();

    private boolean mBound = false;

    private Intent mistSandbox;
    private AppToMist appToMist;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        return Service.START_NOT_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
        connect();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class MistServiceBinder extends Binder {
        public MistService getService() {
            return MistService.this;
        }
    }

    public void connect() {
        Intent mistSandbox = new Intent();
        mistSandbox.setComponent(new ComponentName("fi.ct.mist", "fi.ct.mist.sandbox.Sandbox"));
        startService(mistSandbox);
        bindService(mistSandbox, mConnection, 0);
    }


    public void wishApiRequest(String op, byte[] data, Callback listener) {
        if (!mBound) {
            Log.v(TAG, "Error: not bound, attempting rebound");
            connect();
            return;
        } else {
            try {
                appToMist.wishApiRequest(op, data, listener);
            } catch (RemoteException e) {
                Log.e(TAG, "RemoteException occured in wishApi: " + e);
            }
        }
    }

    public void mistApiRequest(String op, byte[] data, Callback listener) {
        if (!mBound) {
            Log.v(TAG, "Error: not bound, attempting rebound");
            connect();
            return;
        } else {
            try {
                appToMist.mistApiRequest(op, data, listener);
            } catch (RemoteException e) {
                Log.e(TAG, "RemoteException occured in wishApi: " + e);
            }
        }
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.d(TAG, "onServiceConnected");
            appToMist = AppToMist.Stub.asInterface(iBinder);
            mBound = true;
            //todo register/login
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBound = false;
        }
    };

    @Override
    public void onDestroy() {
        Log.d(TAG, "in onDestroy");
        super.onDestroy();
        unbindService(mConnection);
        mBound = false;
    }
}
