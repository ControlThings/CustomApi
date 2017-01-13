package mist;


import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.util.Log;

import org.bson.BsonDocument;
import org.bson.BsonValue;
import org.bson.RawBsonDocument;

import java.util.ArrayList;
import mist.api.Mist;
import mist.sandbox.Callback;

/**
 * Created by jan on 1/9/17.
 */
public class RequestInterface {

    static {
        System.loadLibrary("mistcustomapi");
    }

    private static RequestInterface ourInstance = new RequestInterface();

    public static RequestInterface getInstance() {
        return ourInstance;
    }

    private ArrayList<Mist.LoginCb> loginCallbackList;
    private RequestInterface() {
        /* Register the this class instance down to JNI code so that we can later call signalConnected */
        registerInstance();
        loginCallbackList = new ArrayList<>();
    }

    /**
     * Make a Wish Api request, such as "identity.list"
     *
     * @param op The name of the Wish RPC request
     * @param argsBson the arguments in BSON format of the request
     * @param cb the callback to be invoked when a reply arrives
     * @return the RPC id of the request, or 0 for fail
     */
    public synchronized int wishApiRequest(String op, byte[] argsBson, final Callback cb) {
        Callback intercept = new Callback.Stub() {
            @Override
            public void ack(final byte[] data) throws RemoteException {

                Runnable task = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            cb.ack(data);
                        } catch (RemoteException e) {
                            Log.d("wishApiRequest", e.toString());
                        }
                    }
                };

                new Handler(Looper.getMainLooper()).post(task);
            }

            @Override
            public void sig(final byte[] data) throws RemoteException {

                Runnable task = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            cb.sig(data);
                        } catch (RemoteException e) {
                            Log.d("wishApiRequest", e.toString());
                        }
                    }
                };

                new Handler(Looper.getMainLooper()).post(task);
            }

            @Override
            public void err(final int code, final String msg) throws RemoteException {

                Runnable task = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            cb.err(code, msg);
                        } catch (RemoteException e) {
                            Log.d("wishApiRequest", e.toString());
                        }
                    }
                };

                new Handler(Looper.getMainLooper()).post(task);
            }
        };
        return jniWishApiRequest(op, argsBson, intercept);

    }

    /**
     * Make a Mist Api request, such as "control.model"
     * @param op The name of the Wish RPC request
     * @param argsBson the arguments in BSON format of the request, that is an array named args, for example for "control.model": { args: [0: {luid, ruid, rsid, rhid} ] }"
     * @param cb the callback to be invoked when a reply arrives
     * @return the RPC id of the request, or 0 for fail
     */
    public synchronized int mistApiRequest(String op, byte[] argsBson, final Callback cb) {
        Callback intercept = new Callback.Stub() {
            @Override
            public void ack(final byte[] data) throws RemoteException {

                Runnable task = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            cb.ack(data);
                        } catch (RemoteException e) {
                            Log.d("wishApiRequest", e.toString());
                        }
                    }
                };

                new Handler(Looper.getMainLooper()).post(task);
            }

            @Override
            public void sig(final byte[] data) throws RemoteException {

                Runnable task = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            cb.sig(data);
                        } catch (RemoteException e) {
                            Log.d("wishApiRequest", e.toString());
                        }
                    }
                };

                new Handler(Looper.getMainLooper()).post(task);
            }

            @Override
            public void err(final int code, final String msg) throws RemoteException {

                Runnable task = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            cb.err(code, msg);
                        } catch (RemoteException e) {
                            Log.d("wishApiRequest", e.toString());
                        }
                    }
                };

                new Handler(Looper.getMainLooper()).post(task);
            }
        };
        return jniMistApiRequest(op, argsBson, intercept);
    }

    public synchronized void mistApiRequestCancel(int id) {
        jniMistApiRequestCancel(id);
    }



    public synchronized void registerLoginCB(final Mist.LoginCb callback) {

        /* First, get connected status */
        loginCallbackList.add(callback);
        if (isConnected()) {
            /* if connected == true, invoke the callback immediately */
            Runnable task = new Runnable() {
                @Override
                public void run() {
                        callback.cb(true);
                }
            };
            new Handler(Looper.getMainLooper()).post(task);
        }
    }

    /**
     * JNI call to wishApiRequest
     * @param op
     * @param argsBson
     * @param cb
     * @return the RPC id of the request, or 0 for fail
     */
    native int jniWishApiRequest(String op, byte[] argsBson, Callback cb);

    /**
     * JNI call to mistApiRequest
     * @param op
     * @param argsBson
     * @param cb
     * @return the RPC id of the request, or 0 for fail
     */
    native int jniMistApiRequest(String op, byte[] argsBson, Callback cb);

    native void jniMistApiRequestCancel(int id);

    native boolean isConnected();

    native void registerInstance();

    /* This method will be called by JNI when MistApiBridgeJni.connected is called */
    synchronized void signalConnected(boolean connected) {
        for (Mist.LoginCb loginCb : loginCallbackList) {
            loginCb.cb(connected);
        }
    }

}
