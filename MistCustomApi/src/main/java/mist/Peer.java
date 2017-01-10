package mist;

import java.io.Serializable;

/**
 * Created by jeppe on 11/16/16.
 */

public class Peer implements Serializable {

    private byte[] localId;
    private byte[] remoteId;
    private byte[] remoteHostId;
    private byte[] remoteServiceId;
    private boolean online;
    private String protocol;

    public byte[] getLocalId() {
        return localId;
    }

    public void setLocalId(byte[] localId) {
        this.localId = localId;
    }

    public byte[] getRemoteId() {
        return remoteId;
    }

    public void setRemoteId(byte[] remoteId) {
        this.remoteId = remoteId;
    }

    public byte[] getRemoteHostId() {
        return remoteHostId;
    }

    public void setRemoteHostId(byte[] remoteHostId) {
        this.remoteHostId = remoteHostId;
    }

    public byte[] getRemoteServiceId() {
        return remoteServiceId;
    }

    public void setRemoteServiceId(byte[] remoteServiceId) {
        this.remoteServiceId = remoteServiceId;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    private String byteArrayAsString(byte[] array) {
        String ret = new String();
        if (array == null) {
            ret = "null";
        } else {
            for (int i = 0; i < array.length; i++) {
                ret += "0x" + Integer.toHexString(array[i]) + ", ";
            }
        }
        return ret;
    }

    public String toString() {
        String s = new String();
        s = "luid: " + byteArrayAsString(localId);
        s += " ruid: " + byteArrayAsString(remoteId);
        s += " rhid: " + byteArrayAsString(remoteHostId);
        s += " rsid: " + byteArrayAsString(remoteServiceId);
        s += " protocol: " + protocol;
        s += online ? " online" : " offline";
        return s;
    }
}
