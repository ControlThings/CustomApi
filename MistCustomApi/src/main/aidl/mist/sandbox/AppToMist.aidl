// AppToMist.aidl
package mist.sandbox;

// Declare any non-default types here with import statements
import mist.sandbox.Callback;

interface AppToMist {

void wishApiRequest(in String op, in byte[] data, Callback listener);

void mistApiRequest(in String op, in byte[] data, Callback listener);

}
