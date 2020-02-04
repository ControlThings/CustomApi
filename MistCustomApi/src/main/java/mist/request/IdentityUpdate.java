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
import org.bson.BsonBinary;
import org.bson.BsonBinaryWriter;
import org.bson.BsonDocument;
import org.bson.BsonDocumentReader;
import org.bson.BsonWriter;
import org.bson.RawBsonDocument;
import org.bson.io.BasicOutputBuffer;

import mist.Peer;
import mist.RequestInterface;
import mist.sandbox.Callback;

import static mist.request.Callback.BSON_ERROR_STRING;


class IdentityUpdate {
    static int request(Peer peer, mist.Identity identity, String alias, BsonDocument metaDocument, Identity.UpdateCb callback) {
        final String op = "wish.identity.update";

        BasicOutputBuffer buffer = new BasicOutputBuffer();
        BsonWriter writer = new BsonBinaryWriter(buffer);
        writer.writeStartDocument();
        writer.writeStartArray("args");

        if (peer != null) {
            writer.pipe(new BsonDocumentReader(new RawBsonDocument(peer.toBson())));
        } else {
            writer.writeNull();
        }

        writer.writeBinaryData(new BsonBinary(identity.getUid()));

        if (alias != null) {
          writer.writeStartDocument();
          writer.writeString("alias", alias);
          writer.writeEndDocument();
        } else {
            writer.pipe(new BsonDocumentReader(metaDocument));
        }

        writer.writeEndArray();
        writer.writeEndDocument();
        writer.flush();

        int requestId = RequestInterface.getInstance().mistApiRequest(op, buffer.toByteArray(), new Callback.Stub() {
            private Identity.UpdateCb callback;

            @Override
            public void ack(byte[] data) throws RemoteException {
                response(data);
                callback.end();
            }

            @Override
            public void sig(byte[] data) throws RemoteException {

            }

            private void response(byte[] data) {
                mist.Identity identity;
                try {
                    BsonDocument bson = new RawBsonDocument(data);
                    BsonDocument bsonDocument = bson.getDocument("data");
                    identity = mist.Identity.fromBson(bsonDocument);
                } catch (BSONException e) {
                    callback.err(mist.request.Callback.BSON_ERROR_CODE, BSON_ERROR_STRING);
                    return;
                }
                callback.cb(identity);
            }

            @Override
            public void err(int code, String msg) throws RemoteException {
                MistLog.err(op, code, msg);
                callback.err(code, msg);
            }

            private Callback init(Identity.UpdateCb callback) {
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
