package mist.api;

import org.bson.BsonArray;
import org.bson.BsonDocument;
import org.json.JSONObject;

import mist.Peer;
import mist.RequestInterface;

/**
 * Created by jeppe on 11/30/16.
 */

public class Control {

    public static int follow(Peer peer, FollowCb callback) {
        return ControlFollow.request(peer, callback);
    }

    public static void model(Peer peer, ModelCb callback){
        ControlModel.request(peer, callback);
    };

    public static void write(Peer peer, String epid, Boolean state, Control.WriteCb callback) {
        ControlWrite.request(peer, epid, state, callback);
    }
    public static void write(Peer peer, String epid, int state, Control.WriteCb callback) {
        ControlWrite.request(peer, epid, state, callback);
    }
    public static void write(Peer peer, String epid, float state, Control.WriteCb callback) {
        ControlWrite.request(peer, epid, state, callback);
    }
    public static void write(Peer peer, String epid, String state, Control.WriteCb callback) {
        ControlWrite.request(peer, epid, state, callback);
    }

    public abstract static class FollowCb extends Callback {
        public abstract void cbBool(String epid, boolean value);
        public abstract void cbInt(String epid, int value);
        public abstract void cbFloat(String epid, float value);
        public abstract void cbString(String epid, String value);
    }

    public abstract static class ModelCb extends Callback {
        public abstract void cb(JSONObject data);
    }

    public abstract static class WriteCb extends Callback {
        public abstract void cb();
    }

    public static void cancel(int id) {
        RequestInterface.getInstance().mistApiRequestCancel(id);
    }
}
