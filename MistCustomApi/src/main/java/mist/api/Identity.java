package mist.api;

import org.bson.BsonDocument;

import java.util.ArrayList;

import mist.MistIdentity;

/**
 * Created by jeppe on 11/30/16.
 */

public class Identity {

    public static void list(ListCb callback) {
        IdentityList.request(callback);
    }

    public static void friendRequest(byte[] uid, BsonDocument contact, FriendRequestCb callback) {
        IdentityFriendRequest.request(uid, contact, callback);
    }

    public abstract static class ListCb extends Callback {
        public abstract void cb(ArrayList<MistIdentity> identityList);
    }

    public abstract static class FriendRequestCb extends Callback {
        public abstract void cb(boolean state);
    }

}
