# Mist CustomApi aar


###Requirements
 
 You will need [Mist p2p service](https://mist.controlthings.fi/dist/Wish-v0.4.0-beta-3.apk) and [Mist Generic Tool](https://mist.controlthings.fi/dist/MistUi-v0.6.0-beta-3.apk) apps instaled on your phone to be able to use Mist CustomApi.
 
### Configuration in Android Studio

1. Click **File** > **New** > **New Module...** and select **Import .JAR/AAR Package**.

2. Click **File** > **Project Structure...** and select **Dependencies** tab, click **+** > **3 Module dependency** select **MistCustomApi** and click **OK OK**


### Usage


####Basic
***Create a service and name it:***
```java
Intent mistService;
mistService = new Intent(this, MistService.class);
mistService.putExtra("name", "your app name");
```

***Start and Stop :***
*To be able to use the api the MistService must be runing.*
```
startService(mistService);

stopService(mistService);
```

Login:
*After login is true you allowed to make any request.*
```
Mist.login(new Mist.LoginCb() {
    @Override
    public void cb(boolean connected) {
     //true if logged in
     }
});
```

####Mist
***ListPeers:***
```
Mist.listPeers(new Mist.ListPeersCb() {
    @Override
    public void cb(List<Peer> list) {
    }
});
```

***Signals:***
*Signals needs to be cancelled when they are not needed any more.*
```
int id = Mist.signals(new Mist.SignalsCb() {
    @Override
    public void cb(String signal) {
    }

    @Override
    public void cb(String signal, BsonDocument document) {
    }
});

Mist.cancel(id);
```

***Cancel:***
```
Mist.cancel(int);
```
        
####Commission
***List:***
```
Commission.list(new Commission.ListCb() {
    @Override
    public void cb(List<CommissionItem> list) {
    }
})
```

***Progress:***
*Progress is a signal so it needs to be canceld like all signals*
```
int id = Commission.progress(new Commission.ProgressCb() {
    @Override
    public void cb(String s) {
    }
});
       
Mist.cancel(id);
```

***Refresh:***
```
Commission.refresh(new Commission.RefreshCb() {
    @Override
    public void cb() {
    }
});
```

***Start:***
*CommissionItem: item from Commission.list*
```
Commission.start(CommissionItem, new Commission.StartCb() {
     @Override
     public void cb(List<WifiItem> list) {
     }

     @Override
     public void finished(List<Peer> list) {
     }
});
```

***SetWifi:***
*WifiItem: item from Commission.start*
*String: wifi password*
 
```
Commission.setWifi(WifiItem, String, new Commission.SetWifiCb() {
    @Override
    public void cb(List<Peer> list) {
    }
})
```

####Control
***Follow:***
*Peer: item from Mist.listPeers*
```
Control.follow(Peer, new Control.FollowCb() {
    @Override
    public void cbBool(String epid, boolean value) {}

    @Override
    public void cbInt(String epid, int value) {}

    @Override
    public void cbFloat(String epid, float value) {}

    @Override
    public void cbString(String epid, String value) {}
});
```

***Invoke:***
*Peer: item from Mist.listPeers*
```
Control.invoke(Peer, String/Boolean/Float/Int/BsonArray/Byte[]/BsonDocument, new Control.InvokeCb() {
    @Override
    public void cbBoolean(Boolean data) {}

    @Override
    public void cbInt(int data) {}

    @Override
    public void cbFloat(double data) {}

    @Override
    public void cbString(String data) {}

    @Override
    public void cbByte(byte[] data) {}

    @Override
    public void cbArray(BsonArray array) {}

    @Override
    public void cbDocument(BsonDocument document) {}
});
```

***Model:***
*Peer: item from Mist.listPeers*
```
Control.model(Peer, new Control.ModelCb() {
    @Override
    public void cb(JSONObject jsonObject) {}
});
```

***Read:***
*Peer: item from Mist.listPeers*
*String: endpiont name*
```
Control.read(Peer, String, new Control.ReadCb() {
    @Override
    public void cbBoolean(Boolean data) {}

    @Override
    public void cbInt(int data) {}

    @Override
    public void cbFloat(double data) {}

    @Override
    public void cbString(String data) {}
});
```

***Write:***
*Peer: item from Mist.listPeers*
*String: endpiont name*
```
Control.write(Peer, String, Boolean/String/int/float, new Control.WriteCb() {
    @Override
    public void cb() {}
});
```

####Identity
***Create:***
*Peer: remote device peer or null* 
```
Identity.list(null/Peer, new Identity.ListCb() {
    @Override
    public void cb(List<mist.Identity> list) {}
});
```

***FriendRequest:***
*Peer: remote device peer or null* 
*byte[]:  uid*
*BsonDocument: meta data as bson*
```
Identity.friendRequest(null/Peer, byte[], null/BsonDocument, new Identity.FriendRequestCb() {
    @Override
    public void cb(boolean b) {}
});
```

***FriendRequestAccept:***
*Peer: remote device peer or null* 
*byte[]: luid from frendrequestList*
*byte[]: ruid from frendrequestList*
```
Identity.friendRequestAccept(null/Peer, byte[] luid, byte[] ruid, new Identity.FriendRequestAcceptCb() {
    @Override
    public void cb(boolean b) {}
});
```

***FriendRequestDecline:***
*Peer: remote device peer or null* 
*byte[]: luid from frendrequestList*
*byte[]: ruid from frendrequestList*
```
Identity.friendRequestDecline(null/Peer, byte[] luid, byte[] ruid, new Identity.FriendRequestDeclineCb() {
    @Override
    public void cb(boolean b) {}
});
```

***FriendRequestList:***
*Peer: remote device peer or null* 
```
Identity.friendRequestList(null/Peer, new Identity.FriendRequestListCb() {
    @Override
    public void cb(List<Request> list) {}
});
```

***Get:***
*Peer: remote device peer or null* 
*byte[]: uid*
```
Identity.get(null/Peer, byte[] uid, new Identity.GetCb() {
    @Override
    public void cb(mist.Identity identity) {}
});
```

***List:***
*Peer: remote device peer or null* 
```
Identity.list(null/Peer, new Identity.ListCb() {
    @Override
    public void cb(List<mist.Identity> list) {
        
    }
});
```

***Update:***
*Peer: remote device peer or null*
*mist.Identity: item from Identity.list *
*String/Bsondocument: device alias as string or  anything as bsondocument*
```
Identity.update(null/Peer, mist.Identity, String/Bsondocument, new Identity.UpdateCb() {
    @Override
    public void cb(mist.Identity identity) {}
});
```

####Settings
***AddPeer:***
*Add Peer to ui using Mist Generic Tool*
```
Settings.addPeer(new Settings.AddPeerCb() {
    @Override
    public void cb() {}
});
```

***Commission:***
*Commission device using Mist Generic Tool*
```
Settings.commission(new Settings.CommissionCb() {
    @Override
    public void cb() {}
});
```
####Errors
*All request  handel error*
```
   @Override
   public void err(int code, String msg) {}
```
