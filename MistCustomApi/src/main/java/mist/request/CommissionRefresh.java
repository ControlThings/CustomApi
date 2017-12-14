package mist.request;

import android.os.RemoteException;

import org.bson.BSONException;
import org.bson.BsonBinaryWriter;
import org.bson.BsonDocument;
import org.bson.BsonWriter;
import org.bson.io.BasicOutputBuffer;

import mist.RequestInterface;
import mist.sandbox.Callback;

/**
 * Created by jeppe on 11/29/16.
 */

class CommissionRefresh {

    static int request(Commission.RefreshCb callback) {
        final String op = "settings";
        final String settingsType = "commission.refresh";

        BasicOutputBuffer buffer = new BasicOutputBuffer();
        BsonWriter writer = new BsonBinaryWriter(buffer);
        writer.writeStartDocument();
        writer.writeStartArray("args");
        writer.writeString(settingsType);
        writer.writeEndArray();
        writer.writeEndDocument();
        writer.flush();

        int requestId = RequestInterface.getInstance().mistApiRequest(op, buffer.toByteArray(), new Callback.Stub() {
            private Commission.RefreshCb callback;

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

            private void response(final byte[] dataBson) {
                // todo move to real rpc implementation
                signalId = Mist.signals(new Mist.SignalsCb() {
                    private Commission.RefreshCb callback;

                    @Override
                    public void cb(String signal) {
                        super.cb(signal);
                        if (signal.equals("commission.list")) {
                            callback.cb();
                            Mist.cancel(signalId);
                        }
                    }

                    @Override
                    public void cb(String signal, BsonDocument document) {
                        super.cb(signal, document);
                        if (signal.equals("commission.err")) {
                          if (document.containsKey("hint") && document.get("hint").isString()) {
                              callback.err(COMMISSION_ERROR_CODE, document.getString("hint").getValue());
                          } else {
                              callback.err(COMMISSION_ERROR_CODE, document.toJson());
                          }
                            Mist.cancel(signalId);
                        }
                    }

                    private Mist.SignalsCb init(Commission.RefreshCb callback) {
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

            private Callback init(Commission.RefreshCb callback) {
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
