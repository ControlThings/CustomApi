package mist.request;

import java.util.List;

import mist.LocalDiscovery;

/**
 * Created by jeppe on 11/30/16.
 */

public class Wld {

    /**
     *
     * @param callback Wld.ListCb
     * @return
     */
    public static int list(ListCb callback) {
       return WldList.request(callback);
    }

    /**
     *
     * @param luid Byte array of identity luid
     * @param ruid Byte array of identity ruid
     * @param rhid Byte array of identity rhid
     * @param callback Wld.FriendRequest
     * @return
     */
    public static int friendRequest(byte[] luid, byte[] ruid, byte[] rhid, Wld.FriendRequestCb callback) {
       return WldFriendRequest.request(luid, ruid, rhid, callback);
    }

    public abstract static class ListCb extends Callback {
        public abstract void cb(List<LocalDiscovery> localDiscoveries);
    }

    public abstract static class FriendRequestCb extends Callback {
        public abstract void cb(boolean value);
    }



}
