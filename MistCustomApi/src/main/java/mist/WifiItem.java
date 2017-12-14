package mist;

import org.bson.BSONException;
import org.bson.BsonDocument;

import java.io.Serializable;

/**
 * Created by jeppe on 12/14/17.
 */

public class WifiItem implements Serializable {

    private String ssid;
    private int level;

    public static WifiItem fromBson(BsonDocument bsonDocument) {
        WifiItem item = new WifiItem();

        try {
            item.ssid = bsonDocument.get("ssid").asString().getValue();
            item.level = bsonDocument.get("level").asInt32().getValue();
        } catch (BSONException e) {
            return null;
        }

        return item;
    }

    public String getSsid() {
        return ssid;
    }

    public int getLevel() {
        return level;
    }
}
