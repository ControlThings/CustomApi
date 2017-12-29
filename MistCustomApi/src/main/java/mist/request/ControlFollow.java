package mist.request;

import android.os.RemoteException;
import android.util.Log;

import org.bson.BSONException;
import org.bson.BsonBinary;
import org.bson.BsonBinaryWriter;
import org.bson.BsonDocument;
import org.bson.BsonValue;
import org.bson.BsonWriter;
import org.bson.RawBsonDocument;
import org.bson.io.BasicOutputBuffer;

import mist.CommissionItem;
import mist.Peer;
import mist.RequestInterface;
import mist.sandbox.Callback;

class ControlFollow {

    static int request(Peer peer, Control.FollowCb callback) {
        final String op = "mist.control.follow";

        BasicOutputBuffer buffer = new BasicOutputBuffer();
        BsonWriter writer = new BsonBinaryWriter(buffer);
        writer.writeStartDocument();
        writer.writeStartArray("args");

        writer.writeStartDocument();
        writer.writeBinaryData("luid", new BsonBinary(peer.getLuid()));
        writer.writeBinaryData("ruid", new BsonBinary(peer.getRuid()));
        writer.writeBinaryData("rhid", new BsonBinary(peer.getRhid()));
        writer.writeBinaryData("rsid", new BsonBinary(peer.getRsid()));
        ;
        writer.writeString("protocol", peer.getProtocol());
        writer.writeBoolean("online", peer.isOnline());
        writer.writeEndDocument();

        writer.writeEndArray();
        writer.writeEndDocument();
        writer.flush();

        int requestId = RequestInterface.getInstance().mistApiRequest(op, buffer.toByteArray(), new Callback.Stub() {
            private Control.FollowCb callback;

            @Override
            public void ack(byte[] dataBson) throws RemoteException {
                response(dataBson);
                callback.end();
            }

            @Override
            public void sig(byte[] dataBson) throws RemoteException {
                response(dataBson);
            }

            private void response(final byte[] dataBson) {
                BsonValue followValue;
                String followEpid;
                try {
                    BsonDocument bson = new RawBsonDocument(dataBson);

                    Log.d("FOLLOW", "Res: " + bson.toJson());

                    BsonDocument followData = bson.get("data").asDocument();
                    followValue = followData.get("data");
                    followEpid = followData.getString("id").getValue();

                } catch (BSONException e) {
                    callback.err(mist.request.Callback.BSON_ERROR_CODE, mist.request.Callback.BSON_ERROR_STRING);
                    return;
                }
                if (followValue.isBoolean()) {
                    callback.cbBool(followEpid, followValue.asBoolean().getValue());
                }
                if (followValue.isInt32()) {
                    callback.cbInt(followEpid, followValue.asInt32().getValue());
                }
                if (followValue.isDouble()) {
                    float value = (float) followValue.asDouble().getValue();
                    callback.cbFloat(followEpid, value);
                }
                if (followValue.isString()) {
                    callback.cbString(followEpid, followValue.asString().getValue());
                }

            }

            @Override
            public void err(int code, String msg) throws RemoteException {
                MistLog.err(op, code, msg);
                callback.err(code, msg);
            }

            private Callback init(Control.FollowCb callback) {
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










