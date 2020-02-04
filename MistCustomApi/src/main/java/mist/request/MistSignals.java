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

import org.bson.BSONException;
import org.bson.BsonArray;
import org.bson.BsonBinaryWriter;
import org.bson.BsonDocument;
import org.bson.BsonDocumentReader;
import org.bson.BsonWriter;
import org.bson.RawBsonDocument;
import org.bson.io.BasicOutputBuffer;

import mist.Peer;
import mist.RequestInterface;
import mist.sandbox.Callback;

class MistSignals {
    static int request(Peer peer, Mist.SignalsCb callback) {
        String op = "signals";


        BasicOutputBuffer buffer = new BasicOutputBuffer();
        BsonWriter writer = new BsonBinaryWriter(buffer);
        writer.writeStartDocument();
        writer.writeStartArray("args");

        if (peer != null) {
            writer.pipe(new BsonDocumentReader(new RawBsonDocument(peer.toBson())));
            op = "wish."+op;
        }

        writer.writeEndArray();
        writer.writeEndDocument();
        writer.flush();

        int requestId = RequestInterface.getInstance().mistApiRequest(op, buffer.toByteArray(), new Callback.Stub() {
            private Mist.SignalsCb callback;
            private String op;

            @Override
            public void ack(byte[] dataBson) throws RemoteException {
                response(dataBson);
                callback.end();
            }

            @Override
            public void sig(byte[] dataBson) throws RemoteException {
                response(dataBson);
            }

            private void response(byte[] data) {

                String signal;
                BsonDocument document = null;
                try {
                    BsonDocument bsonDocument = new RawBsonDocument(data);
                    BsonArray bsonArray = bsonDocument.getArray("data");
                    if (bsonArray.size() == 0) {
                        return;
                    }
                    signal = bsonArray.get(0).asString().getValue();
                    if (bsonArray.size() > 1 && bsonArray.get(1).isDocument()) {
                        document = bsonArray.get(1).asDocument();
                    }

                } catch (BSONException e) {
                    callback.err(mist.request.Callback.BSON_ERROR_CODE, mist.request.Callback.BSON_ERROR_STRING);
                    return;
                }
                if (document != null) {
                    callback.cb(signal, document);
                } else {
                    callback.cb(signal);
                }
            }

            @Override
            public void err(int code, String msg) throws RemoteException {
                MistLog.err(op, code, msg);
                callback.err(code, msg);
            }

            private Callback init(Mist.SignalsCb callback,String op) {
                this.callback = callback;
                this.op = op;
                return this;
            }
        }.init(callback, op));

        if (requestId == 0) {
            callback.err(0, "request fail");
            MistLog.err(op, requestId, "request fail");
        }

        return requestId;
    }
}
