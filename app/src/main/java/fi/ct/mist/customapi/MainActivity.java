package fi.ct.mist.customapi;

import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import org.bson.BsonBinaryWriter;
import org.bson.BsonWriter;
import org.bson.io.BasicOutputBuffer;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import mist.Peer;
import mist.RequestInterface;
import mist.api.Control;
import mist.api.Identity;
import mist.MistIdentity;
import mist.MistService;
import mist.api.Mist;
import mist.sandbox.Callback;

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
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        ready();
                    }
                },
                300);

    }

    int id = 0;

    private void follow(Peer peer) {
        id = Control.follow(peer, new Control.FollowCb() {
            @Override
            public void cbBool(String epid, boolean value) {

            }

            @Override
            public void cbInt(String epid, int value) {
                Log.d("Follow", epid + " : " + value);
            }

            @Override
            public void cbFloat(String epid, float value) {

            }

            @Override
            public void cbString(String epid, String value) {

            }

            @Override
            public void err(int code, String msg) {

            }

            @Override
            public void end() {

            }
        });


    }

    private void cancel() {
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        if (id != 0) {
                            Control.cancel(id);
                        }
                    }
                },
                10000);
    }


    private void ready() {



        Mist.signals(new Mist.SignalsCb() {
            @Override
            public void cb(String signal) {
                Log.d("Signals", signal);
                if (signal.equals("peers")) {
                    Mist.listPeers(new Mist.ListPeersCb() {
                        @Override
                        public void cb(ArrayList<Peer> peers) {
                            for (final Peer peer : peers) {
                                Control.model(peer, new Control.ModelCb() {
                                    @Override
                                    public void cb(JSONObject data) {
                                        Log.d("Model:", data.toString());
                                        follow(peer);

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

            }

            @Override
            public void err(int code, String msg) {

            }

            @Override
            public void end() {

            }

        });


        Mist.listPeers(new Mist.ListPeersCb() {
            @Override
            public void cb(ArrayList<Peer> peers) {
                if (peers.size() == 0) {
                    BasicOutputBuffer buffer = new BasicOutputBuffer();
                    BsonWriter writer = new BsonBinaryWriter(buffer);
                    writer.writeStartDocument();
                    writer.writeStartArray("args");
                    writer.writeEndArray();
                    writer.writeEndDocument();
                    writer.flush();

                    RequestInterface.getInstance().mistApiRequest("settings", buffer.toByteArray(), new Callback.Stub() {
                        @Override
                        public void ack(byte[] data) throws RemoteException {

                        }

                        @Override
                        public void sig(byte[] data) throws RemoteException {

                        }

                        @Override
                        public void err(int code, String msg) throws RemoteException {

                        }


                    });
                }

                for (final Peer peer : peers) {
                    Control.model(peer, new Control.ModelCb() {
                        @Override
                        public void cb(JSONObject data) {
                            Log.d("Model:", data.toString());
                            follow(peer);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cancel();
    }
}


