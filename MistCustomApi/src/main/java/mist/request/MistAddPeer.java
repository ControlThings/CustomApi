package mist.request;

import android.os.RemoteException;

import org.bson.BSONException;
import org.bson.BsonArray;
import org.bson.BsonBinary;
import org.bson.BsonBinaryWriter;
import org.bson.BsonDocument;
import org.bson.BsonValue;
import org.bson.BsonWriter;
import org.bson.RawBsonDocument;
import org.bson.io.BasicOutputBuffer;


import java.util.ArrayList;
import java.util.List;

import mist.*;
import mist.sandbox.*;

/**
 * Created by jeppe on 8/23/16.
 */
class MistAddPeer {
    static int request(Peer peer, Mist.AddPeerCb callback) {
        final String op = "sandbox.addPeer";

        BasicOutputBuffer buffer = new BasicOutputBuffer();
        BsonWriter writer = new BsonBinaryWriter(buffer);
        writer.writeStartDocument();
        writer.writeStartArray("args");
        writer.writeStartDocument();
        writer.writeBinaryData("luid", new BsonBinary(peer.getLuid()));
        writer.writeBinaryData("ruid", new BsonBinary(peer.getRuid()));
        writer.writeBinaryData("rhid", new BsonBinary(peer.getRhid()));
        writer.writeBinaryData("rsid", new BsonBinary(peer.getRsid()));
        writer.writeString("protocol", peer.getProtocol());
        writer.writeBoolean("online", peer.isOnline());
        writer.writeEndDocument();
        writer.writeEndArray();
        writer.writeEndDocument();
        writer.flush();

        int requestId = RequestInterface.getInstance().mistApiRequest(op, buffer.toByteArray(), new mist.sandbox.Callback.Stub() {
            private Mist.AddPeerCb callback;

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
                try {
                    BsonDocument bson = new RawBsonDocument(dataBson);
                    boolean value = bson.getBoolean("data").getValue();
                    callback.cb(value);
                } catch (BSONException e) {
                    callback.err(mist.request.Callback.BSON_ERROR_CODE, mist.request.Callback.BSON_ERROR_STRING);
                }
            }

            @Override
            public void err(int code, String msg) throws RemoteException {
                MistLog.err(op, code, msg);
                callback.err(code, msg);
            }

            private mist.sandbox.Callback init(Mist.AddPeerCb callback) {
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
