/**
 * Copyright (C) 2020, ControlThings Oy Ab
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * @license Apache-2.0
 */
package mist.request;

import org.bson.BsonDocument;

import java.util.ArrayList;
import java.util.List;

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
        if (peer == null) {
            return 0;
        }
        return MistSignals.request(peer, callback);
    }

    public static int listPeers(ListPeersCb callback) {
        return MistListPeers.request(callback);
    }

    public static void login(LoginCb callback) {
        RequestInterface.getInstance().registerLoginCB(callback);
    }

    public abstract static class SignalsCb extends Callback {
        public void cb(String signal) {
        }

        public void cb(String signal, BsonDocument document) {
        }
    }

    public abstract static class ListPeersCb extends Callback {
        public abstract void cb(List<Peer> peers);
    }

    public abstract static class LoginCb extends Callback {
        public abstract void cb(boolean connected);
    }

    public static void cancel(int id) {
        RequestInterface.getInstance().mistApiRequestCancel(id);
    }


    public static class Signals {
        public static String Peers = "peers";
        public static String FriendRequest = "friendRequest";
        public static String Ready = "ready";
        public static String CommissionProgress = "commission.progress";
        public static String CommissionRefresh = "commission.refresh";
        public static String CommissionSetWifi = "commission.setWifi";
        public static String Commission = "commission";
        private static String CommissionList = "commission.list";
        private static String SandboxedSettings = "sandboxed.settings";
        private static String SandboxedLogin = "sandboxed.login";
    }
}

