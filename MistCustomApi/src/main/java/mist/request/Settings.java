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
