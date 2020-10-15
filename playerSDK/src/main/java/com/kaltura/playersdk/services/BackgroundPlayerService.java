package com.kaltura.playersdk.services;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.webkit.WebView;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.kaltura.playersdk.KPPlayerConfig;
import com.kaltura.playersdk.PlayerViewController;
import com.kaltura.playersdk.events.KPErrorEventListener;
import com.kaltura.playersdk.events.KPFullScreenToggledEventListener;
import com.kaltura.playersdk.events.KPPlayheadUpdateEventListener;
import com.kaltura.playersdk.events.KPStateChangedEventListener;
import com.kaltura.playersdk.events.KPlayerState;
import com.kaltura.playersdk.types.KPError;
import com.kaltura.playersdk.types.MediaBundle;
import com.kaltura.playersdk.utils.LogUtils;

public class BackgroundPlayerService extends Service implements KPErrorEventListener, KPPlayheadUpdateEventListener, KPStateChangedEventListener, KPFullScreenToggledEventListener {

    PlayerViewController mPlayerView = null;


    public String SERVICE_URL = "";
    public String PARTNER_ID = "";
    public String UI_CONF_ID = "";
    public String ENTRY_ID = "";
    public String KS = "";
    public String izsession = "";
    public String adURL = "";

    int counter = 0;

    boolean shouldResume = false;
    double playback = 0.0f;

    KPlayerState currentState = KPlayerState.UNKNOWN;


    private IBinder mBinder = new MyBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("WRD","onStartCommand executed with startId: $startId");
        if (intent != null) {
            String action = intent.getAction();
            Log.d("WRD","using an intent with action " + action);
//            switch (action) {
//                Actions.START.name -> startService()
//                Actions.STOP.name -> stopService()
//                else -> log("This should never happen. No action in the received intent")
//            }
  //          startService();
        } else {
            Log.d("WRD",
                    "with a null intent. It has been probably restarted by the system."
            );
        }
        return Service.START_STICKY;

    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        if (mPlayerView != null){
            mPlayerView.removePlayer();
        }
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }



    @Override
    public void onCreate() {
        super.onCreate();
//        SERVICE_URL = "http://mp.streamamg.com";
//        PARTNER_ID = "3001133";
//        UI_CONF_ID = "30027349";
//        ENTRY_ID = "0_9fcwzpij";
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O)
            startMyOwnForeground();
        else
            startForeground(1, new Notification());
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private void startMyOwnForeground()
    {
        String NOTIFICATION_CHANNEL_ID = "example.permanence";
        String channelName = "Background Service";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setContentTitle("App is running in background")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(1, notification);
    }

    public void setupPlayer(Activity activity, PlayerViewController player){

        Log.d("WRD", "service setUpPlayer: " + counter);
        counter++;
        getPlayer(activity, player);
    }

    private PlayerViewController getPlayer(Activity activity, PlayerViewController player) {

        if (mPlayerView != null) {
            playback = mPlayerView.getCurrentPlaybackTime();
            shouldResume = true;
        }

            mPlayerView = player;

            if (mPlayerView != null) {
                mPlayerView.loadPlayerIntoActivity(activity);
//
//                if (!SERVICE_URL.startsWith("http")) {
//                    SERVICE_URL = "http://" + SERVICE_URL;
//                }
//
//                KPPlayerConfig config = new KPPlayerConfig(SERVICE_URL, UI_CONF_ID, PARTNER_ID).setEntryId(ENTRY_ID);
//
//                if (KS.length() > 0) {
//                    config.setKS(KS);
//                }
//                if (izsession.length() > 0) {
//                    config.addConfig("izsession", izsession);
//                }
//
//                // Set your flashvars here
//                config.addConfig("chromecast.receiverLogo", "true");
//                config.addConfig("fullScreenBtn.plugin", "false");
//
//                config.addConfig("doubleClick.plugin", "true");
//                config.addConfig("doubleClick.leadWithFlash", "false");
//                config.addConfig("doubleClick.adTagUrl", "https://pubads.g.doubleclick.net/gampad/live/ads?iu=/21707781519/LiveScore_App/LiveScore_App_TV/LiveScore_App_TV_LiveStream&description_url=http%3A%2F%2Fwww.livescore.com&tfcd=0&npa=0&sz=300x400%7C300x415%7C320x50%7C320x480%7C400x300%7C420x315%7C480x480%7C554x416%7C640x480%7C728x90&cust_params=LS_Match%3D8-207681%26LS_Sport%3Dsoccer%26LS_League%3DLeague%2BC%2BGroup%2B1%26LS_Team%3DCyprus%2CLuxembourg%26LS_SubSection%3Duefa-nations-league%26LS_Environment%3DTesting&gdfp_req=1&output=vast&unviewed_position_start=1&env=vp&impl=s&correlator=");
//
//
//
//                mPlayerView.initWithConfiguration(config);
//if (shouldResume){
//    mPlayerView.resumePlayer();
//    mPlayerView.resumeState();
//    mPlayerView.setPlaybackTime(playback);
//    mPlayerView.playFromCurrentPosition();
//}
                mPlayerView.setOnKPErrorEventListener(this);
                mPlayerView.setOnKPPlayheadUpdateEventListener(this);
                mPlayerView.setOnKPFullScreenToggledEventListener(this);
                mPlayerView.setOnKPStateChangedEventListener(this);

                mPlayerView.addKPlayerEventListener("bitrateChange", "bitrateChange", new PlayerViewController.EventListener() {
                    @Override
                    public void handler(String eventName, String params) {
                        Log.d("bitrateChange", eventName + " - " + params);
                    }
                });

                mPlayerView.addKPlayerEventListener("playerReady", "playerReady", new PlayerViewController.EventListener() {
                    @Override
                    public void handler(String eventName, String params) {
                        Log.d("playerReady", eventName + " - " + params);
                    }
                });

                LogUtils.enableDebugMode();
                LogUtils.enableWebViewDebugMode();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    WebView.setWebContentsDebuggingEnabled(true);
                }

        }
        return mPlayerView;
    }

    public void updateMedia(MediaBundle bundle){
          SERVICE_URL = bundle.SERVICE_URL;
          PARTNER_ID = bundle.PARTNER_ID;
          UI_CONF_ID = bundle.UI_CONF_ID;
          ENTRY_ID = bundle.ENTRY_ID;
          KS = bundle.KS;
          izsession = bundle.izsession;
          adURL = bundle.adURL;
          shouldResume = false;
          playback = 0.0;
         runMedia();
    }

    private void runMedia(){
        if (mPlayerView != null) {

            if (!SERVICE_URL.startsWith("http")) {
                SERVICE_URL = "http://" + SERVICE_URL;
            }

            KPPlayerConfig config = new KPPlayerConfig(SERVICE_URL, UI_CONF_ID, PARTNER_ID).setEntryId(ENTRY_ID);

            if (KS.length() > 0) {
                config.setKS(KS);
            }
            if (izsession.length() > 0) {
                config.addConfig("izsession", izsession);
            }

            // Set your flashvars here
            config.addConfig("chromecast.receiverLogo", "true");
            config.addConfig("fullScreenBtn.plugin", "false");

            if (!adURL.isEmpty()) {
                config.addConfig("doubleClick.plugin", "true");
                config.addConfig("doubleClick.leadWithFlash", "false");
                config.addConfig("doubleClick.adTagUrl", adURL);
            } else {
                config.addConfig("doubleClick.plugin", "false");
                config.addConfig("doubleClick.leadWithFlash", "false");
                config.addConfig("doubleClick.adTagUrl", null);
            }


            mPlayerView.initWithConfiguration(config);
            if (shouldResume){
                mPlayerView.resumePlayer();
                mPlayerView.resumeState();
                mPlayerView.setPlaybackTime(playback);
                mPlayerView.playFromCurrentPosition();
            }


        }
    }

    @Override
    public void onKPlayerError(PlayerViewController playerViewController, KPError error) {

    }

    @Override
    public void onKPlayerPlayheadUpdate(PlayerViewController playerViewController, long currentTimeMilliSeconds) {

    }

    @Override
    public void onKPlayerStateChanged(PlayerViewController playerViewController, KPlayerState state) {
Log.d("WRD", "New state = " + state);
    }

    @Override
    public void onKPlayerFullScreenToggled(PlayerViewController playerViewController, boolean isFullscreen) {

    }

    public class MyBinder extends Binder {

        public MyBinder(){
            Log.d("WRD", "creating binder");
        }

       public BackgroundPlayerService getService() {
           Log.d("WRD", "service getService");
            return BackgroundPlayerService.this;
        }
    }

}
