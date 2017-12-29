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

import java.util.Random;

import mist.sandbox.AppToMist;
import mist.sandbox.Callback;

/**
 * Created by jan on 1/9/17.
 */

class MistApiBridge {
    private final String TAG = "MistApiBridge";

    private final static String pref = "mist_pref";

    private Intent mistSandbox = new Intent();


    private Context context;
    private boolean mBound = false;
    private MistApiBridgeJni jni;
    private AppToMist appToMist;
    private String appName;
    private boolean logined = false;
    private boolean mistSandboxStarting = false;

    private Binder binder;

    MistApiBridge(Context context, MistApiBridgeJni jni, String appName) {
        this.context = context;
        this.jni = jni;
        this.appName = appName;
        startSandboxService();
        binder = new Binder();
    }

    private void startSandboxService() {
        mistSandbox.setComponent(new ComponentName("fi.ct.mist", "fi.ct.mist.sandbox.Sandbox"));
        if (!mistSandboxStarting) {
            context.startService(mistSandbox);
            mBound = context.bindService(mistSandbox, mConnection, 0);
        }
        mistSandboxStarting = true;
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
            Log.d(TAG, "onServiceConnected");
            appToMist = AppToMist.Stub.asInterface(iBinder);
            login();
            mistSandboxStarting = false;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            // We never get here for some reason
            Log.d(TAG, "onServiceDisconnected");
            logined = false;
            jni.connected(false);
            mistSandboxStarting = false;
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

        Log.d(TAG, "login a");

        try {
            int resId = appToMist.mistApiRequest(binder, "login", buffer.toByteArray(), new Callback.Stub() {
                @Override
                public void ack(byte[] data) throws RemoteException {
                    Log.d(TAG, "mistApiRequest: login acked");
                    BsonDocument bsonDocument = new RawBsonDocument(data);
                    boolean state = bsonDocument.get("data").asBoolean().getValue();

                    if (state) {
                        logined = true;
                        jni.connected(true);
                        //appToMist.register(binder);
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
                startSandboxService();
            }
        } catch (RemoteException e) {
            Log.d(TAG, "remote exeption in register.");
        } catch (NullPointerException e) {
            Log.d(TAG, "appToMist.mistApiRequest possibly not compatible!");
        } catch (Exception e) {
            Log.d(TAG, "appToMist.mistApiRequest failed to login in an unknown way!");
        }
    }

    /**
     * @param op       RPC operator string
     * @param data     RPC argument BSON
     * @param listener Callback function for response
     * @return the RPC id of the request, which can be used for mistApiCancel(), or 0 for error (request is not buffered in case of error)
     */
    int mistApiRequest(String op, byte[] data, Callback listener) {
        int id = 0;
        if (!logined) {
            Log.v(TAG, "Error: not logined..");
        } else {
            try {
                id = appToMist.mistApiRequest(binder, op, data, listener);
            } catch (RemoteException e) {
                Log.e(TAG, "RemoteException occured while performing mistApiRequest, restarting service: " + e);
                startSandboxService();
            } catch (Exception e) {
                Log.d(TAG, "appToMist.mistApiRequest failed in an unhandled way!");
            }
        }
        return id;
    }

    void mistApiCancel(int id) {
        if (!logined) {
            Log.v(TAG, "Error: not logined");
        } else {
            try {
                appToMist.mistApiCancel(binder, id);
            } catch (RemoteException e) {
                Log.e(TAG, "RemoteException occured while performing mistApiCancel  " + e);
                startSandboxService();
            } catch (Exception e) {
                Log.d(TAG, "appToMist.mistApiCancel failed in an unhandled way!");
            }
        }
    }


    void unBind() {
        Log.d(TAG, "UNBIND: " + mBound);
        jni.connected(false);

        if (logined) {
            try {
                appToMist.kill(binder);
            } catch (RemoteException e) {
                Log.e(TAG, "RemoteException occured while performing kill  " + e);
                startSandboxService();
            } catch (Exception e) {
                Log.d(TAG, "appToMist.kill failed in an unhandled way!");
            }

        }

        if (mBound) {
            mBound = false;
            mistSandboxStarting = false;
            context.unbindService(mConnection);
        } else {
            Log.d(TAG, "Not bound when unbinding");
        }

    }
}
