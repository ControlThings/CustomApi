package mist.request;

import android.os.RemoteException;

import org.bson.BsonArray;
import org.bson.BsonBinaryWriter;
import org.bson.BsonDocument;
import org.bson.BsonDocumentReader;
import org.bson.BsonReader;
import org.bson.BsonValue;
import org.bson.BsonWriter;
import org.bson.RawBsonDocument;
import org.bson.io.BasicOutputBuffer;

import java.util.ArrayList;
import java.util.List;

import mist.CommissionItem;
import mist.Peer;
import mist.RequestInterface;
import mist.WifiItem;
import mist.sandbox.Callback;

/**
 * Created by jeppe on 11/29/16.
 */

class CommissionStart {

    static int request(final CommissionItem item, Commission.StartCb callback) {
        final String op = "settings";
        final String settingsType = "commission";

        BasicOutputBuffer buffer = new BasicOutputBuffer();
        BsonWriter writer = new BsonBinaryWriter(buffer);
        writer.writeStartDocument();

        writer.writeStartArray("args");
        writer.writeString(settingsType);
        BsonReader bsonReader = new BsonDocumentReader(new RawBsonDocument(item.toBson()));
        writer.pipe(bsonReader);
        writer.writeEndArray();

        writer.writeEndDocument();
        writer.flush();

        int requestId = RequestInterface.getInstance().mistApiRequest(op, buffer.toByteArray(), new Callback.Stub() {
            private Commission.StartCb callback;

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
                    private Commission.StartCb callback;

                    @Override
                    public void cb(String signal, BsonDocument document) {
                        super.cb(signal, document);

                        if (signal.equals("commission.claimed")) {

                            List<WifiItem> items = new ArrayList<>();
                            if (document.containsKey("args")) {
                                if (document.get("args").isArray()) {
                                    BsonArray argsArray = document.getArray("args");
                                    if (argsArray.size() > 0) {
                                        for (BsonValue value : argsArray) {
                                            if (value.isDocument()) {
                                                BsonDocument wifiDocument = value.asDocument();
                                                if (wifiDocument != null) {
                                                    items.add(WifiItem.fromBson(wifiDocument));
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
                            callback.err(COMMISSION_ERROR_CODE, "Non available wifi or bad bson structure");
                            Mist.cancel(signalId);
                        }

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
                                            callback.finished(items);
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

                    private Mist.SignalsCb init(Commission.StartCb callback) {
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

            private Callback init(Commission.StartCb callback) {
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
