package mist.api;

import android.content.Context;
import android.support.annotation.Keep;

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

    public static void settings(Settings.Hint hint, SettingsCb callback) {
        MistSettings.request(hint, callback);
    }

    public static void login(LoginCb callback) {
        RequestInterface.getInstance().registerLoginCB(callback);
    }

    public abstract static class SignalsCb extends Callback {
        public abstract void cb(String signal);
    }

    public abstract static class ListPeersCb extends Callback {
        public abstract void cb(ArrayList<Peer> peers);
    }

    public abstract static class SettingsCb extends Callback {
        public void cb() {};
    }

    public abstract static class LoginCb extends Callback {
        public abstract void cb(boolean connected);
    }

    public static void cancel(int id) {
        RequestInterface.getInstance().mistApiRequestCancel(id);
    }

    public static class Settings{
        public enum Hint {
            commission("commission"),
            addPeer("addPeer");

            private String type;

            private Hint(String type) {
                this.type = type;
            }

            public String getType() {
                return type;
            }
        }
    }


}

