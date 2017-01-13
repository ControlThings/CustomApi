package mist.api;

import java.util.ArrayList;

import mist.Peer;
import mist.RequestInterface;

/**
 * Created by jeppe on 11/30/16.
 */

public class Mist {

    public static int signals(SignalsCb callback) {
        return MistSignals.request(callback);
    }

    public static void listPeers(ListPeersCb callback) {
        MistListPeers.request(callback);
    }

    public static void settings(SettingsCb callback) {
        MistSettings.request(callback);
    }

    public interface SignalsCb extends ErrorCallback {
        public void cb(String signal);
    }

    public interface ListPeersCb extends ErrorCallback {
        public void cb(ArrayList<Peer> peers);
    }

    public interface SettingsCb extends ErrorCallback {
        public void cb();
    }

    public static void cancel(int id) {
        RequestInterface.getInstance().mistApiRequestCancel(id);
    }
}

