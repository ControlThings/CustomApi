package mist.api;

import android.os.RemoteException;
import android.util.Log;

import org.bson.BsonBinaryWriter;
import org.bson.BsonDocument;
import org.bson.BsonWriter;
import org.bson.RawBsonDocument;
import org.bson.io.BasicOutputBuffer;

import mist.RequestInterface;
import mist.sandbox.Callback;

/**
 * Created by jeppe on 11/30/16.
 */

class MistSignals {
    static int request(Mist.SignalsCb callback) {
        final String op = "mist.signals";

        BasicOutputBuffer buffer = new BasicOutputBuffer();
        BsonWriter writer = new BsonBinaryWriter(buffer);
        writer.writeStartDocument();
        writer.writeStartArray("args");
        writer.writeEndArray();
        writer.writeEndDocument();
        writer.flush();

        final int id = RequestInterface.getInstance().mistApiRequest(op, buffer.toByteArray(), new Callback.Stub() {
            private Mist.SignalsCb callback;

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
                String signalData = bson.getString("data").getValue();
                callback.cb(signalData);
            }

            @Override
            public void err(int code, String msg) throws RemoteException {
                Log.d(op, "RPC error: " + msg + " code: " + code);
                callback.err(code, msg);
            }

            private Callback init(Mist.SignalsCb callback) {
                this.callback = callback;
                return this;
            }
        }.init(callback));

        return id;
    }
}
