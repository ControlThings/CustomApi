package mist.request;

import java.util.List;

import mist.CommissionItem;
import mist.Peer;
import mist.RequestInterface;
import mist.WifiItem;

/**
 * Created by jeppe on 12/7/17.
 */

public class Commission {

    public static int refresh(RefreshCb callback) {
        return CommissionRefresh.request(callback);
    }

    public static int list(String type, ListCb callback) {
        return CommissionList.request(type, callback);
    }

    public static int list(ListCb callback) {
        return CommissionList.request(null, callback);
    }

    public static int start(CommissionItem item, StartCb callback) {
        return CommissionStart.request(item, callback);
    }

    public static int setWifi(WifiItem item, String password, SetWifiCb callback) {
        return CommissionSetWifi.request(item, password, callback);
    }

    public abstract static class RefreshCb extends Callback {
        public abstract void cb();
    }


    public abstract static class ListCb extends Callback {
        public abstract void cb(List<CommissionItem> items);
    }

    public abstract static class StartCb extends Callback {
        public abstract void cb(List<WifiItem> items);
    }

    public abstract static class SetWifiCb extends Callback {
        public abstract void cb(List<Peer> peers);
    }
}
