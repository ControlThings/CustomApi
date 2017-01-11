package fi.ct.mist.customapi;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONObject;

import java.util.ArrayList;

import mist.Peer;
import mist.api.Control;
import mist.api.Identity;
import mist.MistIdentity;
import mist.MistService;
import mist.api.Mist;

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

                        Mist.signals(new Mist.SignalsCb() {
                            @Override
                            public void cb(String signal) {
                                Log.d("Signals", signal);
                            }

                            @Override
                            public void err(int code, String msg) {

                            }

                            @Override
                            public void end() {

                            }
                        });

                        new android.os.Handler().postDelayed(
                                new Runnable() {
                                    public void run() {
                                        Mist.listPeers(new Mist.ListPeersCb() {
                                            @Override
                                            public void cb(ArrayList<Peer> peers) {
                                                for (Peer peer : peers) {
                                                    Control.model(peer, new Control.ModelCb() {
                                                        @Override
                                                        public void cb(JSONObject data) {
                                                            Log.d("Model:", data.toString());
                                                        }

                                                        @Override
                                                        public void err(int code, String msg) {

                                                        }

                                                        @Override
                                                        public void end() {

                                                        }
                                                    });
                                                }
                                            }

                                            @Override
                                            public void err(int code, String msg) {

                                            }

                                            @Override
                                            public void end() {

                                            }
                                        });
                                    }
                                },
                                2000);

                    }
                },
                1000);
    }

}
