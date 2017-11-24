package mist.request;

import org.bson.BsonDocument;

import java.util.List;


/**
 * Created by jeppe on 11/30/16.
 */

public class Identity {

    /**
     *
     * @param alias String alias
     * @param callback Identity.CreateCb
     * @return
     */
    public static int create(String alias, CreateCb callback) {
        return IdentityCreate.request(alias, callback);
    }

    /**
     *
     * @param callback Identity.ListCb
     * @return
     */
    public static int list(ListCb callback) {
        return IdentityList.request(callback);
    }

    public static void friendRequest(byte[] uid, BsonDocument contact, FriendRequestCb callback) {
        IdentityFriendRequest.request(uid, contact, callback);
    }

    public abstract static class CreateCb extends Callback {
        public abstract void cb (mist.Identity identity);
    }

    public abstract static class ListCb extends Callback {
        public abstract void cb(List<mist.Identity> identityList);
    }

    public abstract static class FriendRequestCb extends Callback {
        public abstract void cb(boolean state);
    }

}
