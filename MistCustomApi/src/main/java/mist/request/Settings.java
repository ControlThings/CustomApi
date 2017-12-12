package mist.request;

import mist.CommissionItem;

/**
 * Created by jeppe on 12/8/17.
 */

public class Settings {

    public static void commission(CommissionCb callback) {
        SettingsCommission.request(null, callback);
    }

    public static void commission(CommissionItem item, CommissionCb callback) {
        SettingsCommission.request(item, callback);
    }

    public static void commissionRefresh(CommissionRefreshCb callback) {
        SettingsCommissionRefresh.request(callback);
    }

    public static void commissionSetWifi(String ssid, String password, CommissionSetWifiCb callback) {
        SettingsCommissionSetWifi.request(ssid, password, callback);
    }

    public static void addPeer(AddPeerCb callback) {
        SettingsAddPeer.request(callback);
    }
    public abstract static class CommissionCb extends Callback {
        public void cb() {};
    }

    public abstract static class CommissionRefreshCb extends Callback {
        public void cb() {};
    }

    public abstract static class CommissionSetWifiCb extends Callback {
        public void cb() {};
    }

    public abstract static class AddPeerCb extends Callback {
        public void cb() {};
    }
}
