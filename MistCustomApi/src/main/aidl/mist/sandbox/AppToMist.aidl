// AppToMist.aidl
package mist.sandbox;

// Declare any non-default types here with import statements
import mist.sandbox.Callback;

interface AppToMist {

int wishApiRequest(in String op, in byte[] data, Callback listener);

int mistApiRequest(in String op, in byte[] data, Callback listener);

void mistApiCancel(in int id);

boolean login(in IBinder client, in byte[] id, in String name);

}
