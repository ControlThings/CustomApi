package mist.request;

import org.bson.BsonDocument;

import java.util.ArrayList;

import mist.Peer;
import mist.RequestInterface;

/**
 * Created by jeppe on 11/30/16.
 */

public class Mist {

    public static int signals(SignalsCb callback) {
        return MistSignals.request(null, callback);
    }

    public static int signals(Peer peer, SignalsCb callback) {
        return MistSignals.request(peer, callback);
    }

    public static void listPeers(ListPeersCb callback) {
        MistListPeers.request(callback);
    }

    public static void login(LoginCb callback) {
        RequestInterface.getInstance().registerLoginCB(callback);
    }

    public abstract static class SignalsCb extends Callback {
        public void cb(String signal) {};
        public void cb(String signal, BsonDocument document) {};
    }

    public abstract static class ListPeersCb extends Callback {
        public abstract void cb(ArrayList<Peer> peers);
    }

    public abstract static class LoginCb extends Callback {
        public abstract void cb(boolean connected);
    }

    public static void cancel(int id) {
        RequestInterface.getInstance().mistApiRequestCancel(id);
    }


}

