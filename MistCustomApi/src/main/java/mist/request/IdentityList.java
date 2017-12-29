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


class IdentityList {
    static int request(Peer peer, Identity.ListCb callback) {
        final String op = "wish.identity.list";

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
            private Identity.ListCb callback;

            @Override
            public void ack(byte[] data) throws RemoteException {
                response(data);
                callback.end();
            }

            @Override
            public void sig(byte[] data) throws RemoteException {

            }

            private void response(byte[] data) {
                List<mist.Identity> list;
                try {
                    BsonDocument bson = new RawBsonDocument(data);
                    BsonArray bsonList = bson.getArray("data");
                    list = new ArrayList<mist.Identity>();
                    for (BsonValue bsonIdentity : bsonList) {
                        list.add(mist.Identity.fromBson(bsonIdentity.asDocument()));
                    }
                } catch (BSONException e) {
                    callback.err(mist.request.Callback.BSON_ERROR_CODE, mist.request.Callback.BSON_ERROR_STRING);
                    return;
                }
                callback.cb(list);
            }

            @Override
            public void err(int code, String msg) throws RemoteException {
                MistLog.err(op, code, msg);
                callback.err(code, msg);
            }

            private Callback init(Identity.ListCb callback) {
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
