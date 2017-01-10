package mist;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import mist.MistApiBridgeJni;

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
