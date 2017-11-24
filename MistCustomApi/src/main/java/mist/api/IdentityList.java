package mist.api;

import android.os.RemoteException;
import android.util.Log;

import org.bson.BsonArray;
import org.bson.BsonBinaryWriter;
import org.bson.BsonDocument;
import org.bson.BsonValue;
import org.bson.BsonWriter;
import org.bson.RawBsonDocument;
import org.bson.io.BasicOutputBuffer;

import java.util.ArrayList;

import mist.MistIdentity;
import mist.RequestInterface;
import mist.sandbox.Callback;


class IdentityList {
    static int request(Identity.ListCb callback) {
        final String op = "wish.identity.list";

        BasicOutputBuffer buffer = new BasicOutputBuffer();
        BsonWriter writer = new BsonBinaryWriter(buffer);
        writer.writeStartDocument();
        writer.writeStartArray("args");
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

            private void response(byte[] dataBson) {
                BsonDocument bson = new RawBsonDocument(dataBson);
                ArrayList<MistIdentity> identityList = new ArrayList<MistIdentity>();
                BsonArray bsonIdentityList = new BsonArray(bson.getArray("data"));
                for (BsonValue listValue : bsonIdentityList) {
                    MistIdentity identity = new MistIdentity();
                    identity.setAlias(listValue.asDocument().get("alias").asString().getValue());
                    identity.setUid(listValue.asDocument().get("uid").asBinary().getData());
                    identity.setPrivkey(listValue.asDocument().get("privkey").asBoolean().getValue());
                    identityList.add(identity);
                }
                callback.cb(identityList);
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
