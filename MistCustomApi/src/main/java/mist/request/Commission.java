package mist.request;

import mist.RequestInterface;

/**
 * Created by jeppe on 12/7/17.
 */

public class Commission {

    public static int list(String type, ListCb callback) {
        return CommissionList.request(type, callback);
    }

    public abstract static class ListCb extends Callback {
        public abstract void cb(byte[] bson);
    }

}
