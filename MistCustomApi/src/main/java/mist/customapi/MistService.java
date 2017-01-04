package mist.customapi;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by jeppe on 1/4/17.
 */

public class MistService extends Service {

    private final String TAG = "Mist Service";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_NOT_STICKY;
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private void connect() {

    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "in onDestroy");
        super.onDestroy();

    }
}
