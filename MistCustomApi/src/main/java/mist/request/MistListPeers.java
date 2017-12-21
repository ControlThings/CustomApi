package mist.request;

import android.os.RemoteException;

import org.bson.BsonArray;
import org.bson.BsonBinaryWriter;
import org.bson.BsonDocument;
import org.bson.BsonValue;
import org.bson.BsonWriter;
import org.bson.RawBsonDocument;
import org.bson.io.BasicOutputBuffer;

import java.util.ArrayList;
import java.util.List;

import mist.Peer;
import mist.RequestInterface;
import mist.sandbox.Callback;

/**
 * Created by jeppe on 11/29/16.
 */

class MistListPeers {

    static int request(Mist.ListPeersCb callback) {
        final String op = "listPeers";

        BasicOutputBuffer buffer = new BasicOutputBuffer();
        BsonWriter writer = new BsonBinaryWriter(buffer);
        writer.writeStartDocument();
        writer.writeStartArray("args");
        writer.writeEndArray();
        writer.writeEndDocument();
        writer.flush();

        int requestId = RequestInterface.getInstance().mistApiRequest(op, buffer.toByteArray(), new Callback.Stub() {
            private Mist.ListPeersCb callback;

            @Override
            public void ack(byte[] dataBson) throws RemoteException {
                response(dataBson);
                callback.end();
            }

            @Override
            public void sig(byte[] dataBson) throws RemoteException {
                response(dataBson);
            }

            private void response(byte[] dataBson) {
                List<Peer> peers = new ArrayList<Peer>();
                BsonDocument bson = new RawBsonDocument(dataBson);

                BsonArray bsonListServices = bson.getArray("data");
                for (BsonValue bsonValue: bsonListServices) {
                    BsonDocument peerDocument = bsonValue.asDocument();
                    Peer peer = Peer.fromBson(peerDocument);
                    peers.add(peer);
                }
                callback.cb(peers);
            }

            @Override
            public void err(int code, String msg) throws RemoteException {
                MistLog.err(op, code, msg);
                callback.err(code, msg);
            }

            private Callback init(Mist.ListPeersCb callback) {
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
