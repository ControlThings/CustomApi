package mist.customapi;

import java.util.ArrayList;

import mist.customapi.ErrorCallback;

/**
 * Created by jeppe on 11/30/16.
 */

public class Identity {

    public static void list(ListCb callback) {
      //  IdentityList.request(callback);
    }

    public interface ListCb extends ErrorCallback {
        public void cb(ArrayList<MistIdentity> identityList);
    }


}
