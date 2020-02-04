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
import org.bson.BsonValue;
import org.bson.BsonWriter;
import org.bson.RawBsonDocument;
import org.bson.io.BasicOutputBuffer;

import java.util.ArrayList;
import java.util.List;

import mist.Peer;
import mist.Request;
import mist.RequestInterface;
import mist.sandbox.Callback;

import static mist.request.Callback.BSON_ERROR_CODE;
import static mist.request.Callback.BSON_ERROR_STRING;


class IdentityFriendRequestList {
    static int request(Peer peer, Identity.FriendRequestListCb callback) {
        final String op = "wish.identity.friendRequestList";

        BasicOutputBuffer buffer = new BasicOutputBuffer();
        BsonWriter writer = new BsonBinaryWriter(buffer);
        writer.writeStartDocument();
        writer.writeStartArray("args");

        if (peer != null) {
            writer.pipe(new BsonDocumentReader(new RawBsonDocument(peer.toBson())));
        } else {
            writer.writeNull();
        }

        writer.writeEndArray();
        writer.writeEndDocument();
        writer.flush();

        int requestId = RequestInterface.getInstance().mistApiRequest(op, buffer.toByteArray(), new Callback.Stub() {
            private Identity.FriendRequestListCb callback;

            @Override
            public void ack(byte[] data) throws RemoteException {
                response(data);
                callback.end();
            }

            @Override
            public void sig(byte[] data) throws RemoteException {

            }

            private void response(byte[] data) {
                List<Request> requests;
                try {
                    BsonDocument bson = new RawBsonDocument(data);
                    requests = new ArrayList<>();
                    BsonArray bsonArray = new BsonArray(bson.getArray("data"));
                    for (BsonValue listValue : bsonArray) {
                        Request request = new Request();
                        BsonDocument document = listValue.asDocument();
                        request.setLuid(document.get("luid").asBinary().getData());
                        request.setRuid(document.get("ruid").asBinary().getData());
                        request.setAlias(document.get("alias").asString().getValue());
                        request.setPubkey(document.get("pubkey").asBinary().getData());

                        if (document.containsKey("meta")) {
                            request.setMeta(document.getDocument("meta").asDocument());
                        }

                        requests.add(request);
                    }
                } catch (BSONException e) {
                    callback.err(BSON_ERROR_CODE, BSON_ERROR_STRING);
                    return;
                }
                callback.cb(requests);
            }

            @Override
            public void err(int code, String msg) throws RemoteException {
                MistLog.err(op, code, msg);
                callback.err(code, msg);
            }

            private Callback init(Identity.FriendRequestListCb callback) {
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
