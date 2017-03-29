package mist.api;

import java.util.ArrayList;

import mist.MistIdentity;

/**
 * Created by jeppe on 11/30/16.
 */

public class Identity {

    public static void list(ListCb callback) {
        IdentityList.request(callback);
    }

    public abstract static class ListCb extends Callback {
        public abstract void cb(ArrayList<MistIdentity> identityList);
    }


}
