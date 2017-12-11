package mist;

import android.util.Log;

import org.bson.BSONException;
import org.bson.BsonBinary;
import org.bson.BsonBinaryWriter;
import org.bson.BsonDocument;
import org.bson.BsonValue;
import org.bson.BsonWriter;
import org.bson.RawBsonDocument;
import org.bson.io.BasicOutputBuffer;

import java.io.Serializable;
import java.security.acl.LastOwnerException;
import java.util.ArrayList;

import mist.request.Commission;

public class CommissionItem implements Serializable {

    public static final String type_wifi = "wifi";
    public static final String type_local = "local";

    private String type;
    private String name;
    private Object item;

    private static int WISH_UID_LEN = 32;

    public static CommissionItem fromBson(byte[] data) {
        try {
            return fromBson(new RawBsonDocument(data));
        } catch (BSONException e) {
            return null;
        }

    }

    public static CommissionItem fromBson(BsonDocument bsonDocument) {
        CommissionItem commissionItem = new CommissionItem();
        try {

            String type = bsonDocument.getString("type").getValue();

            if (type.equals(type_wifi)) {
                WifiItem wifiItem = new WifiItem();
                commissionItem.type = type_wifi;

                wifiItem.ssid = bsonDocument.getString("ssid").getValue();
                commissionItem.name = wifiItem.ssid;

                if (bsonDocument.containsKey("level")) {
                    wifiItem.level = bsonDocument.getInt32("level").getValue();
                }

                commissionItem.item = wifiItem;
            } else if (type.equals(type_local)) {
                LocalItem localItem = new LocalItem();
                commissionItem.type = type_local;

                localItem.alias = bsonDocument.getString("alias").getValue();
                commissionItem.name = localItem.alias;

                localItem.ruid = bsonDocument.getBinary("ruid").getData();
                localItem.rhid = bsonDocument.getBinary("rhid").getData();

                if (localItem.ruid.length != WISH_UID_LEN) {
                    return null;
                }
                if (localItem.rhid.length != WISH_UID_LEN) {
                    return null;
                }

                if (bsonDocument.containsKey("pubkey")) {
                    localItem.pubkey = bsonDocument.getBinary("pubkey").getData();
                    if (localItem.pubkey.length != WISH_UID_LEN) {
                        return null;
                    }
                }

                if (bsonDocument.containsKey("class")) {
                    localItem.classType = bsonDocument.getString("class").getValue();
                }

                commissionItem.item = localItem;
            } else {
                return null;
            }
        } catch (BSONException e) {
            Log.d("TEST", "err " + e.getMessage());
            return null;
        }


        return commissionItem;
    }

    public byte[] toBson() {
        BasicOutputBuffer buffer = new BasicOutputBuffer();
        BsonWriter writer = new BsonBinaryWriter(buffer);
        writer.writeStartDocument();

        writer.writeString("type", getType());

        if (getType().equals(type_wifi)) {
            WifiItem item = (WifiItem) getItem();
            writer.writeString("ssid", item.getSsid());

            if (item.getLevel() != 0) {
                writer.writeInt32(item.getLevel());
            }

        } else if (getType().equals(type_local)) {
            LocalItem item = (LocalItem) getItem();

            writer.writeString("alias", item.getAlias());
            writer.writeBinaryData("ruid", new BsonBinary(item.getRuid()));
            writer.writeBinaryData("rhid", new BsonBinary(item.getRhid()));

            if (item.getPubkey() != null) {
                writer.writeBinaryData("pubkey", new BsonBinary(item.getPubkey()));
            }

            if (item.getClassType() != null) {
                writer.writeString("class", item.getClassType());
            }
        }

        writer.writeEndDocument();
        writer.flush();

        return buffer.toByteArray();
    }

    public static class LocalItem extends CommissionItem {
        private String alias;
        private byte[] ruid;
        private byte[] rhid;
        private byte[] pubkey;
        private String classType;

        public String getAlias() {
            return alias;
        }

        public byte[] getRuid() {
            return ruid;
        }

        public byte[] getRhid() {
            return rhid;
        }

        public byte[] getPubkey() {
            return pubkey;
        }

        public String getClassType() {
            return classType;
        }
    }

    private static class WifiItem extends CommissionItem {
        private String ssid;
        private int level;

        public String getSsid() {
            return ssid;
        }

        public int getLevel() {
            return level;
        }
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public Object getItem() {
        return item;
    }
}
