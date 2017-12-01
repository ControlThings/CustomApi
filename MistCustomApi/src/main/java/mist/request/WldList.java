package mist.request;

import android.os.RemoteException;

import org.bson.BSONException;
import org.bson.BsonArray;
import org.bson.BsonBinaryWriter;
import org.bson.BsonDocument;
import org.bson.BsonValue;
import org.bson.BsonWriter;
import org.bson.RawBsonDocument;
import org.bson.io.BasicOutputBuffer;

import java.util.ArrayList;
import java.util.List;

import mist.RequestInterface;
import mist.sandbox.Callback;

import mist.LocalDiscovery;;

class WldList {
    static int request(Wld.ListCb callback) {
        final String op = "wish.wld.list";

        BasicOutputBuffer buffer = new BasicOutputBuffer();
        BsonWriter writer = new BsonBinaryWriter(buffer);
        writer.writeStartDocument();
        writer.writeStartArray("args");
        writer.writeEndArray();
        writer.writeEndDocument();
        writer.flush();

        int requestId = RequestInterface.getInstance().mistApiRequest(op, buffer.toByteArray(), new Callback.Stub() {
            private Wld.ListCb callback;

            @Override
            public void ack(byte[] data) throws RemoteException {
                response(data);
                callback.end();
            }

            @Override
            public void sig(byte[] data) throws RemoteException {

            }

            private void response(byte[] data) {
                try {
                    BsonDocument bson = new RawBsonDocument(data);
                    BsonArray bsonArray = bson.get("data").asArray();
                    List<LocalDiscovery> localDiscoveries = new ArrayList<LocalDiscovery>();

                    for (BsonValue listValue : bsonArray) {
                        LocalDiscovery localDiscovery = new LocalDiscovery();
                        localDiscovery.setType(listValue.asDocument().get("type").asString().getValue());
                        localDiscovery.setAlias(listValue.asDocument().get("alias").asString().getValue());
                        if (listValue.asDocument().containsKey("luid")) {
                            localDiscovery.setLuid(listValue.asDocument().get("luid").asBinary().getData());
                        }
                        localDiscovery.setRuid(listValue.asDocument().get("ruid").asBinary().getData());
                        localDiscovery.setRhid(listValue.asDocument().get("rhid").asBinary().getData());
                        localDiscovery.setPubkey(listValue.asDocument().get("pubkey").asBinary().getData());
                        if (listValue.asDocument().containsKey("claim")) {
                            localDiscovery.setClaim(listValue.asDocument().get("claim").asBoolean().getValue());
                        } else {
                            localDiscovery.setClaim(false);
                        }

                        localDiscoveries.add(localDiscovery);
                    }
                    callback.cb(localDiscoveries);
                } catch (BSONException e) {
                    callback.err(mist.request.Callback.BSON_ERROR_CODE, mist.request.Callback.BSON_ERROR_STRING);

                }
            }

            @Override
            public void err(int code, String msg) throws RemoteException {
                MistLog.err(op, code, msg);
                callback.err(code, msg);
            }

            private Callback init(Wld.ListCb callback) {
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
