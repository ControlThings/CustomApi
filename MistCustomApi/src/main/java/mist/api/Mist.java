package mist.api;

import android.content.Context;

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

    public static void settings(String hint, SettingsCb callback) {
        MistSettings.request(hint, callback);
    }

    public static void login(LoginCb callback) {

        //MistLogin.request(callback);
        RequestInterface.getInstance().registerLoginCB(callback);
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

    public interface LoginCb extends ErrorCallback {
        public void cb(boolean connected);
    }

    public static void cancel(int id) {
        RequestInterface.getInstance().mistApiRequestCancel(id);
    }
}

