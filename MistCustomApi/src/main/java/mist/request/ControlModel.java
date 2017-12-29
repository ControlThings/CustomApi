package mist.request;

import android.os.RemoteException;
import android.util.Log;

import org.bson.BsonBinary;
import org.bson.BsonBinaryWriter;
import org.bson.BsonDocument;
import org.bson.BsonWriter;
import org.bson.RawBsonDocument;
import org.bson.io.BasicOutputBuffer;
import org.json.JSONException;
import org.json.JSONObject;

import mist.Peer;
import mist.RequestInterface;
import mist.sandbox.Callback;

/**
 * Created by jeppe on 25/07/16.
 */
class ControlModel {
    static int request(Peer peer, Control.ModelCb callback) {
        final String op = "mist.control.model";

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

        int requestId = RequestInterface.getInstance().mistApiRequest(op, buffer.toByteArray(), new Callback.Stub() {
            private Control.ModelCb callback;

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
                JSONObject jsonObject;
                try {

                    BsonDocument bson = new RawBsonDocument(dataBson);
                    BsonDocument bsonDocument = bson.get("data").asDocument();
                    jsonObject = new JSONObject(bsonDocument.toJson());
                } catch (JSONException e) {
                    callback.err(mist.request.Callback.BSON_ERROR_CODE, mist.request.Callback.BSON_ERROR_STRING);
                    return;
                }
                callback.cb(jsonObject);
            }

            @Override
            public void err(int code, String msg) throws RemoteException {
                MistLog.err(op, code, msg);
                callback.err(code, msg);
            }

            private Callback init(Control.ModelCb callback) {
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
