package mist;

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

import org.bson.BsonBinary;
import org.bson.BsonBinaryWriter;
import org.bson.BsonDocument;
import org.bson.BsonWriter;
import org.bson.RawBsonDocument;
import org.bson.io.BasicOutputBuffer;

import java.net.ContentHandler;
import java.util.LinkedList;
import java.util.Random;

import mist.MistApiBridgeJni;
import mist.api.Mist;
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
    private String appName;
    private boolean logined;

    private class DeferredCustomApiRequest {
        private String op;
        private byte[] data;
        Callback cb;

        DeferredCustomApiRequest(String op, byte[] data, Callback cb) {
            this.op = op;
            this.data = data;
            this.cb = cb;
        }
    }

    private LinkedList<DeferredCustomApiRequest> deferredWishRequests;
    private LinkedList<DeferredCustomApiRequest> deferredMistRequests;


    MistApiBridge(Context context, MistApiBridgeJni jni, String appName) {
        deferredWishRequests = new LinkedList<>();
        deferredMistRequests = new LinkedList<>();
        this.context = context;
        this.jni = jni;
        this.appName = appName;
        startSandboxService();
    }

    private void startSandboxService() {
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
            mBound = true;
            Log.d(TAG, "onServiceConnected");
            appToMist = AppToMist.Stub.asInterface(iBinder);
            login();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBound = false;
            logined = false;
        }
    };

    private void login() {

        BasicOutputBuffer buffer = new BasicOutputBuffer();
        BsonWriter writer = new BsonBinaryWriter(buffer);
        writer.writeStartDocument();
        writer.writeStartArray("args");
        writer.writeBinaryData(new BsonBinary(getId()));
        writer.writeString(appName);
        writer.writeEndArray();
        writer.writeEndDocument();
        writer.flush();
        try {
            int resId = appToMist.mistApiRequest("login", buffer.toByteArray(), new Callback.Stub() {
                @Override
                public void ack(byte[] data) throws RemoteException {
                    BsonDocument bsonDocument = new RawBsonDocument(data);
                    boolean state = bsonDocument.get("data").asBoolean().getValue();
                    if (state) {
                        logined = true;
                        jni.connected(true);
                        appToMist.register(new Binder());

                        for (DeferredCustomApiRequest req : deferredWishRequests) {
                            appToMist.wishApiRequest(req.op, req.data, req.cb);
                        }

                        for (DeferredCustomApiRequest req: deferredMistRequests) {
                            appToMist.mistApiRequest(req.op, req.data, req.cb);
                        }
                    } else {
                        context.unbindService(mConnection);
                    }
                }

                @Override
                public void sig(byte[] data) throws RemoteException {
                }

                @Override
                public void err(int code, String msg) throws RemoteException {
                    Log.d(TAG, "login error code: " + code);
                    context.unbindService(mConnection);
                }
            });
            if (resId == 0) {
                new android.os.Handler().postDelayed(
                        new Runnable() {
                            public void run() {
                                login();
                            }
                        },
                        1000);
            }
        } catch (RemoteException e) {
            Log.d(TAG, "remote exeption in register:");

        }
    }

    int wishApiRequest(String op, byte[] data, Callback listener) {
        int id = 0;
        if (!logined) {
            Log.v(TAG, "Error: not logined; adding to deferred wish req list ");
            deferredWishRequests.add(new DeferredCustomApiRequest(op, data, listener));
        } else {
            try {
                id = appToMist.wishApiRequest(op, data, listener);
            } catch (RemoteException e) {
                Log.e(TAG, "RemoteException occured while performing wishApiRequest: " + e);
                deferredWishRequests.add(new DeferredCustomApiRequest(op, data, listener));
                startSandboxService();
            }
        }
        return id;
    }

    int mistApiRequest(String op, byte[] data, Callback listener) {
        int id = 0;
        if (!logined) {
            Log.v(TAG, "Error: not logined; adding to deferred mist req list");
            deferredMistRequests.add(new DeferredCustomApiRequest(op, data, listener));
        } else {
            try {
                id = appToMist.mistApiRequest(op, data, listener);
            } catch (RemoteException e) {
                Log.e(TAG, "RemoteException occured while performing mistApiRequest " + e);
                deferredMistRequests.add(new DeferredCustomApiRequest(op, data, listener));
                startSandboxService();
            }
        }
        return id;
    }

    void mistApiCancel(int id) {
        if (!logined) {
            Log.v(TAG, "Error: not logined");
        } else {
            try {
                appToMist.mistApiCancel(id);
            } catch (RemoteException e) {
                Log.e(TAG, "RemoteException occured while performing mistApiCancel  " + e);
                startSandboxService();
            }
        }
    }


    void unBind() {
        if (mBound) {
            mBound = false;
            logined = false;
            Log.d(TAG, "UNBIND");
            context.unbindService(mConnection);
        } else {
            Log.d(TAG, "Not bound when unbinding");
        }
    }

}
