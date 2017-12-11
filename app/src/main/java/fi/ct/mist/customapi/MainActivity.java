package fi.ct.mist.customapi;

import android.content.ClipData;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Switch;
import android.widget.TextView;

import org.bson.BsonDocument;
import org.bson.RawBsonDocument;

import java.util.ArrayList;
import java.util.List;

import mist.CommissionItem;
import mist.Peer;
import mist.MistService;
import mist.request.Commission;
import mist.request.Mist;
import mist.request.Settings;
import mist.sandbox.Callback;

public class MainActivity extends AppCompatActivity {

    private boolean mBound = false;
    private MistService mistService;
    private TextView peerOnlineState;
    private Switch enabled;
    private TextView counter;
    private TextView lon;
    private TextView lat;
    private TextView accuracy;

    // Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

/*
        SharedPreferences preferences = getBaseContext().getSharedPreferences("test", Context.MODE_PRIVATE);
        String idString = preferences.getString("id", "empty");

        Log.d("TEST", idString);


        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("id", "mainActivity");
        editor.commit();
*/

        Intent intent = new Intent(this, MistService.class);
        intent.putExtra("name", "TestApp");
        startService(intent);

        Mist.login(new Mist.LoginCb() {
            @Override
            public void cb(boolean connected) {
                Log.d("MainActivity", "Mist.loginCb");
                ready();
            }

            @Override
            public void err(int code, String msg) {

            }

            @Override
            public void end() {

            }
        });
    }

    private void ready() {
        Mist.signals(new Mist.SignalsCb() {
            @Override
            public void cb(String signal) {
                if (signal.equals("commission.list")) {
                    Log.d("TEST", "signals commission.list");
                }
            }

            @Override
            public void cb(String signal, BsonDocument document) {
                Log.d("TEST", "emit signal " + signal);
                Log.d("TEST", "emit document " + document.getString("ep"));
            }
        });

        Settings.commissionRefresh(new Settings.CommissionRefreshCb() {
            @Override
            public void cb() {
                super.cb();
                Log.d("TEST", "settings cb");
            }
        });

        runOnUiThread(new Runnable() {
            public void run() {
                new android.os.Handler().postDelayed(
                        new Runnable() {
                            public void run() {
                                Log.i("tag", "This'll run 300 milliseconds later");
                                Commission.list( new Commission.ListCb() {
                                    @Override
                                    public void cb(List<CommissionItem> items) {

                                        for (CommissionItem item : items) {

                                            if (item.getType().equals(CommissionItem.type_wifi)) {
                                                Settings.commission(item, new Settings.CommissionCb() {
                                                    @Override
                                                    public void cb() {
                                                        super.cb();
                                                        Log.d("TEST", "settings commission cb");
                                                    }
                                                });
                                            }
                                            Log.d("TEST", "type: " + item.getType() + " name: " + item.getName());



                                        }

                                       ;

                                    }
                                });
                            }
                        },
                        5000);
            }
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("Mainactivity", "onDestroy");

        //cancel();
    }
}


