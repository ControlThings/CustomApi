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

    Binder binder = new Binder();

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

    MistApiBridge(Context context, MistApiBridgeJni jni, String appName) {
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
            int resId = appToMist.mistApiRequest(binder, "login", buffer.toByteArray(), new Callback.Stub() {
                @Override
                public void ack(byte[] data) throws RemoteException {
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
     *
     * @param op
     * @param data
     * @param listener
     * @return the RPC id of the request, or 0 for error (request is not buffered in case of error)
     */
    int wishApiRequest(String op, byte[] data, Callback listener) {
        int id = 0;
        if (!logined) {
            Log.v(TAG, "Error: not logined.. (w)");
        } else {
            try {
                id = appToMist.wishApiRequest(binder, op, data, listener);
            } catch (RemoteException e) {
                Log.e(TAG, "RemoteException occured while performing wishApiRequest, restarting service: " + e);
                startSandboxService();
            } catch (Exception e) {
                Log.d(TAG, "appToMist.wishApiRequest failed in an unhandled way!");
            }
        }
        return id;
    }

    /**
     *
     * @param op
     * @param data
     * @param listener
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
