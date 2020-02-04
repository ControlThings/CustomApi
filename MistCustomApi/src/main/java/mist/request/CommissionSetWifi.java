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

import android.os.RemoteException;

import org.bson.BsonArray;
import org.bson.BsonBinaryWriter;
import org.bson.BsonDocument;
import org.bson.BsonValue;
import org.bson.BsonWriter;
import org.bson.io.BasicOutputBuffer;

import java.util.ArrayList;
import java.util.List;

import mist.Peer;
import mist.RequestInterface;
import mist.WifiItem;
import mist.sandbox.Callback;

/**
 * Created by jeppe on 11/29/16.
 */

class CommissionSetWifi {

    static int request(WifiItem item, String password, Commission.SetWifiCb callback) {
        final String op = "settings";
        final String settingsType = "commission.setWifi";

        BasicOutputBuffer buffer = new BasicOutputBuffer();
        BsonWriter writer = new BsonBinaryWriter(buffer);
        writer.writeStartDocument();
        writer.writeStartArray("args");
        writer.writeString(settingsType);

        writer.writeStartDocument();
        writer.writeString("ssid", item.getSsid());
        writer.writeString("password", password);
        writer.writeEndDocument();

        writer.writeEndArray();
        writer.writeEndDocument();
        writer.flush();

        int requestId = RequestInterface.getInstance().mistApiRequest(op, buffer.toByteArray(), new Callback.Stub() {
            private Commission.SetWifiCb callback;

            @Override
            public void ack(byte[] dataBson) throws RemoteException {
                response(dataBson);
                callback.end();
            }

            @Override
            public void sig(byte[] dataBson) throws RemoteException {
                response(dataBson);
            }

            private int signalId;

            private void response(byte[] dataBson) {
                // todo move to real rpc implementation
                signalId = Mist.signals(new Mist.SignalsCb() {
                    private Commission.SetWifiCb callback;

                    @Override
                    public void cb(String signal, BsonDocument document) {
                        super.cb(signal, document);

                        if (signal.equals("commission.finished")) {

                            List<Peer> items = new ArrayList<>();
                            if (document.containsKey("args")) {
                                if (document.get("args").isArray()) {
                                    BsonArray argsArray = document.getArray("args");
                                    if (argsArray.size() > 0) {
                                        for (BsonValue value : argsArray) {
                                            if (value.isDocument()) {
                                                BsonDocument peerDocument = value.asDocument();
                                                if (peerDocument != null) {
                                                    items.add(Peer.fromBson(peerDocument));
                                                }
                                            }
                                        }
                                        if (items.size() > 0) {
                                            callback.cb(items);
                                            Mist.cancel(signalId);
                                            return;
                                        }
                                    }
                                }
                            }
                            callback.err(COMMISSION_ERROR_CODE, "Non available peers or bad bson structure");
                            Mist.cancel(signalId);
                        }

                        if (signal.equals("commission.err")) {
                            if (document.containsKey("hint") && document.get("hint").isString()) {
                                callback.err(COMMISSION_ERROR_CODE, document.getString("hint").getValue());
                            } else {
                                callback.err(COMMISSION_ERROR_CODE, document.toJson());
                            }
                            Mist.cancel(signalId);
                        }
                    }

                    private Mist.SignalsCb init(Commission.SetWifiCb callback) {
                        this.callback = callback;
                        return this;
                    }

                }.init(callback));
            }

            @Override
            public void err(int code, String msg) throws RemoteException {
                MistLog.err(op, code, msg);
                callback.err(code, msg);
            }

            private Callback init(Commission.SetWifiCb callback) {
                this.callback = callback;
                return this;
            }
        }.init(callback));

        if (requestId == 0) {
            callback.err(0, "request fail");
            MistLog.err(op, requestId, "request fail");
        }

        return requestId;
    }
}
