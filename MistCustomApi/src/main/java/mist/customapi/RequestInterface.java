package mist.customapi;

import mist.sandbox.Callback;

/**
 * Created by jan on 1/9/17.
 */
public class RequestInterface {

  /*  static {
        System.loadLibrary("mistcustomapi");
    }
*/
    private static RequestInterface ourInstance = new RequestInterface();

    public static RequestInterface getInstance() {
        return ourInstance;
    }

    private RequestInterface() {
    }

    /**
     * Make a Wish Api request, such as "identity.list"
     *
     * @param op The name of the Wish RPC request
     * @param argsBson the arguments in BSON format of the request
     * @param cb the callback to be invoked when a reply arrives
     * @return the RPC id of the request, or 0 for fail
     */
 /*   public int wishApiRequest(String op, byte[] argsBson, Callback cb) {
        return jniWishApiRequest(op, argsBson, cb);
    }
*/
    /**
     * Make a Mist Api request, such as "control.model"
     * @param op The name of the Wish RPC request
     * @param argsBson the arguments in BSON format of the request, that is an array named args, for example for "control.model": { args: [0: {luid, ruid, rsid, rhid} ] }"
     * @param cb the callback to be invoked when a reply arrives
     * @return the RPC id of the request, or 0 for fail
     */
  /*  public int mistApiRequest(String op, byte[] argsBson, Callback cb) {
        return jniMistApiRequest(op, argsBson, cb);
    }

    public void mistApiRequestCancel(int id) {
        jniMistApiRequestCancel(id);
    }
/*
    native int jniWishApiRequest(String op, byte[] argsBson, Callback cb);

    native int jniMistApiRequest(String op, byte[] argsBson, Callback cb);

    native void jniMistApiRequestCancel(int id);
*/
}
