package mist.request;

import android.os.RemoteException;

import org.bson.BSONException;
import org.bson.BsonBinary;
import org.bson.BsonBinaryWriter;
import org.bson.BsonDocument;
import org.bson.BsonWriter;
import org.bson.RawBsonDocument;
import org.bson.io.BasicOutputBuffer;

import mist.Peer;
import mist.RequestInterface;
import mist.sandbox.Callback;

class ControlRead {

    static int request(Peer peer, String epid, Control.ReadCb callback) {
        final String op = "mist.control.read";

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

        writer.writeString(epid);

        writer.writeEndArray();
        writer.writeEndDocument();
        writer.flush();

       int requestId = RequestInterface.getInstance().mistApiRequest(op, buffer.toByteArray(), new Callback.Stub() {
            private Control.ReadCb callback;

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
                BsonDocument bson;
                try {
                    bson = new RawBsonDocument(dataBson);
                } catch (BSONException e) {
                    callback.err(mist.request.Callback.BSON_ERROR_CODE, mist.request.Callback.BSON_ERROR_STRING);
                    return;
                }
                if (bson.get("data").isBoolean()) {
                    callback.cbBoolean(bson.get("data").asBoolean().getValue());
                } else if (bson.get("data").isInt32()) {
                    callback.cbInt(bson.get("data").asInt32().getValue());
                } else if (bson.get("data").isDouble()) {
                    float value = (float) bson.get("data").asDouble().getValue();
                    callback.cbFloat(value);
                } else if (bson.get("data").isString()) {
                    callback.cbString(bson.get("data").asString().getValue());
                }
            }

            @Override
            public void err(int code, String msg) throws RemoteException {
                MistLog.err(op, code, msg);
                callback.err(code, msg);
            }

            private Callback init(Control.ReadCb callback) {
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
