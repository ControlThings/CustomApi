package mist;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
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
        String name;
        if (intent.hasExtra("name")) {
            name = intent.getStringExtra("name");
        } else {
            final PackageManager pm = getApplicationContext().getPackageManager();
            ApplicationInfo ai;
            try {
                ai = pm.getApplicationInfo( this.getPackageName(), 0);
            } catch (final PackageManager.NameNotFoundException e) {
                ai = null;
            }
            name = (String) (ai != null ? pm.getApplicationLabel(ai) : "(unknown)");
        }

        mistApiBridgeJni = new MistApiBridgeJni(this.getBaseContext(), name);

        return Service.START_NOT_STICKY;
    }



    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        super.onCreate();

    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.d(TAG, "onTaskRemoved");
        stopSelf();
        super.onTaskRemoved(rootIntent);

    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind");
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        Log.d(TAG, "onRebind");
        super.onRebind(intent);

    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        mistApiBridgeJni.disconnect();
    }
}
