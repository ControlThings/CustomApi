package mist.request;

import android.os.RemoteException;

import org.bson.BsonBinary;
import org.bson.BsonBinaryWriter;
import org.bson.BsonDocument;
import org.bson.BsonWriter;
import org.bson.RawBsonDocument;
import org.bson.io.BasicOutputBuffer;

import mist.Peer;
import mist.RequestInterface;
import mist.sandbox.Callback;

class ControlWrite {

    static int request(Peer peer, String epid, Boolean state, Control.WriteCb callback) {
       return send(peer, epid, state, null, null, null, callback);
    }
    static int request(Peer peer, String epid, int state, Control.WriteCb callback) {
        return send(peer, epid, null, state, null, null, callback);
    }
    static int request(Peer peer, String epid, float state, Control.WriteCb callback) {
        return send(peer, epid, null, null, state, null, callback);
    }
    static int request(Peer peer, String epid, String state, Control.WriteCb callback) {
        return send(peer, epid, null, null, null, state, callback);
    }

    private static int send(Peer peer, String epid, Boolean boolState, Integer intState, Float floatState, String stringState, Control.WriteCb callback) {
        final String op = "mist.control.write";

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

        if (boolState != null) {
            writer.writeBoolean(boolState);
        }
        else if (intState != null) {
            writer.writeInt32(intState);
        }
        else if (floatState != null) {
            writer.writeDouble(floatState);
        }
        else if (stringState != null) {
            writer.writeString(stringState);
        }

        writer.writeEndArray();
        writer.writeEndDocument();
        writer.flush();

       int requestId = RequestInterface.getInstance().mistApiRequest(op, buffer.toByteArray(), new Callback.Stub() {
            private Control.WriteCb callback;

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
                BsonDocument bson = new RawBsonDocument(dataBson);
                callback.cb();
            }

            @Override
            public void err(int code, String msg) throws RemoteException {
                MistLog.err(op, code, msg);
                callback.err(code, msg);
            }

            private Callback init(Control.WriteCb callback) {
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
