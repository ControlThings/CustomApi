package mist.request;

import mist.CommissionItem;

/**
 * Created by jeppe on 12/8/17.
 */

public class Settings {

    public static void commission(CommissionCb callback) {
        SettingsCommission.request(callback);
    }

    public static void addPeer(AddPeerCb callback) {
        SettingsAddPeer.request(callback);
    }

    public abstract static class CommissionCb extends Callback {
        public void cb() {};
    }

    public abstract static class AddPeerCb extends Callback {
        public void cb() {};
    }
}
