package mist.request;

import android.os.RemoteException;
import android.util.Log;

import org.bson.BsonBinary;
import org.bson.BsonBinaryReader;
import org.bson.BsonBinaryWriter;
import org.bson.BsonDocumentReader;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.RawBsonDocument;
import org.bson.io.BasicOutputBuffer;

import mist.CommissionItem;
import mist.RequestInterface;
import mist.sandbox.Callback;

/**
 * Created by jeppe on 11/29/16.
 */

class SettingsCommission {

    static int request(Settings.CommissionCb callback) {
        final String op = "settings";
        final String settingsType = "commission";

        BasicOutputBuffer buffer = new BasicOutputBuffer();
        BsonWriter writer = new BsonBinaryWriter(buffer);
        writer.writeStartDocument();

        writer.writeStartArray("args");
        writer.writeString(settingsType);

        writer.writeEndArray();
        writer.writeEndDocument();
        writer.flush();

        int requestId = RequestInterface.getInstance().mistApiRequest(op, buffer.toByteArray(), new Callback.Stub() {
            private Settings.CommissionCb callback;

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
                callback.cb();
            }

            @Override
            public void err(int code, String msg) throws RemoteException {
                MistLog.err(op, code, msg);
                callback.err(code, msg);
            }

            private Callback init(Settings.CommissionCb callback) {
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
