// AppToMist.aidl
package mist.customapi;

// Declare any non-default types here with import statements

interface AppToMist {

  void sendAppToMist(in byte[] data);
  void registerProcessDeath(in IBinder clientDeathListener,in byte[] id);
}
