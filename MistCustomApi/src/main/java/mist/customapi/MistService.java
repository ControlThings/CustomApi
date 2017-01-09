package mist.customapi;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.UUID;

import mist.sandbox.AppToMist;
import mist.sandbox.Callback;

/**
 * Created by jeppe on 1/4/17.
 */

public class MistService extends Service {

    private final String TAG = "Mist Service";



    private Intent mistSandbox;


    private MistApiBridgeJni mistApiBridgeJni;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");

        mistApiBridgeJni = new MistApiBridgeJni(this.getBaseContext());

        return Service.START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        mistApiBridgeJni.disconnect();
    }
}
