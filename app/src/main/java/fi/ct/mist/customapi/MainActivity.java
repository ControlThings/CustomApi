package fi.ct.mist.customapi;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.WindowDecorActionBar;
import android.util.Log;

import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

import mist.customapi.Identity;
import mist.customapi.MistIdentity;
import mist.customapi.MistService;

public class MainActivity extends AppCompatActivity {

    private boolean mBound = false;
    private MistService mistService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(this, MistService.class);
        startService(intent);
        /* Explicit call to ready which will make a request over the custom api binder after a while - FIXME this must be replaced by a proper "ready" signal */
        ready();
    }

    private void ready() {
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {

                        Identity.list(new Identity.ListCb() {
                            @Override
                            public void cb(ArrayList<MistIdentity> identityList) {
                                for (MistIdentity identity : identityList) {
                                    Log.d("alias", identity.getAlias());
                                }
                            }

                            @Override
                            public void err(int code, String msg) {
                                Log.d("Error", "code: " + code + " msg: " + msg);
                            }

                            @Override
                            public void end() {
                            }
                        });
                    }
                },
                1000);
    }

}
