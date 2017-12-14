package mist.request;

/**
 * Created by jeppe on 3/29/17.
 */

class Callback {

    public static final int COMMISSION_ERROR_CODE = 745;
    public static final int BSON_ERROR_CODE = 836;
    public static final String BSON_ERROR_STRING = "Bad BSON structure";

    public void err(int code, String msg){};
    public void end(){};
}
