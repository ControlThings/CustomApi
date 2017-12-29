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
import mist.RequestInterface;
import mist.sandbox.Callback;


class IdentityCreate {
    static int request(Peer peer, String alias, Identity.CreateCb callback) {
        final String op = "wish.identity.create";

        BasicOutputBuffer buffer = new BasicOutputBuffer();
        BsonWriter writer = new BsonBinaryWriter(buffer);
        writer.writeStartDocument();
        writer.writeStartArray("args");

        if (peer != null) {
            writer.pipe(new BsonDocumentReader(new RawBsonDocument(peer.toBson())));
        } else {
            writer.writeNull();
        }

        writer.writeString(alias);
        writer.writeEndArray();
        writer.writeEndDocument();
        writer.flush();

        int requestId = RequestInterface.getInstance().mistApiRequest(op, buffer.toByteArray(), new Callback.Stub() {
            private Identity.CreateCb callback;

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
                    callback.err(mist.request.Callback.BSON_ERROR_CODE, mist.request.Callback.BSON_ERROR_STRING);
                    return;
                }
                callback.cb(identity);
            }

            @Override
            public void err(int code, String msg) throws RemoteException {
                MistLog.err(op, code, msg);
                callback.err(code, msg);
            }

            private Callback init(Identity.CreateCb callback) {
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
