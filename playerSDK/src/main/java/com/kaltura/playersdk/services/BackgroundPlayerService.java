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
import com.kaltura.playersdk.events.KPlayerServiceListener;
import com.kaltura.playersdk.events.KPlayerState;
import com.kaltura.playersdk.types.FlashVar;
import com.kaltura.playersdk.types.KPError;
import com.kaltura.playersdk.types.MediaBundle;
import com.kaltura.playersdk.utils.LogUtils;

import java.util.ArrayList;

public class BackgroundPlayerService extends Service implements KPErrorEventListener, KPPlayheadUpdateEventListener, KPStateChangedEventListener, KPFullScreenToggledEventListener {

    PlayerViewController mPlayerView = null;

    KPlayerServiceListener mPlayerListener = null;

    public static int customNotificationIcon = 0;


    public String SERVICE_URL = "";
    public String PARTNER_ID = "";
    public String UI_CONF_ID = "";
    public String ENTRY_ID = "";
    public String KS = "";
    public String izsession = "";
    public String adURL = "";

    public ArrayList<FlashVar> flashVars = new ArrayList<>();

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

        } else {
            Log.d("WRD",
                    "with a null intent. It has been probably restarted by the system."
            );
        }
        return Service.START_STICKY;

    }

    @Override
    public void onDestroy() {
        Log.d("WRD", "Svs On Destroy");
        super.onDestroy();
//        if (mPlayerView != null){
//            mPlayerView.removePlayer();
//        }
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
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O)
            startMyOwnForeground();
        else
            startForeground(1, new Notification());
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private void startMyOwnForeground()
    {
        String NOTIFICATION_CHANNEL_ID = "player.permanence";
        String channelName = "Background Service";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification;
        if (customNotificationIcon != 0){
            notification = notificationBuilder.setOngoing(true)
                    .setContentTitle("App is running in background")
                    .setSmallIcon(customNotificationIcon)
                    .setPriority(NotificationManager.IMPORTANCE_MIN)
                    .setCategory(Notification.CATEGORY_SERVICE)
                    .build();
        } else {
             notification = notificationBuilder.setOngoing(true)
                    .setContentTitle("App is running in background")
                    .setPriority(NotificationManager.IMPORTANCE_MIN)
                    .setCategory(Notification.CATEGORY_SERVICE)
                    .build();
        }
        startForeground(1, notification);
    }

    public void setupPlayer(Activity activity, PlayerViewController player, KPlayerServiceListener listener){

        Log.d("WRD", "service setUpPlayer: " + counter);
        counter++;
        mPlayerListener = listener;
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

    public void clearFlashVars(){
        flashVars.clear();
    }

    public void addFlashVar(FlashVar flashVar){
        flashVars.add(flashVar);
    }

    public void updateMedia(MediaBundle bundle){
          Boolean isTheSameMedia = (ENTRY_ID == bundle.ENTRY_ID);
          SERVICE_URL = bundle.SERVICE_URL;
          PARTNER_ID = bundle.PARTNER_ID;
          UI_CONF_ID = bundle.UI_CONF_ID;
          ENTRY_ID = bundle.ENTRY_ID;
          KS = bundle.KS;
          izsession = bundle.izsession;
          adURL = bundle.adURL;
          if (!isTheSameMedia) {
              shouldResume = false;
              playback = 0.0;
          } else {
              shouldResume = true;
          }
         runMedia();
    }

    private void runMedia(){
        if (mPlayerView != null) {

            mPlayerView.freeze();  //changeMedia(ENTRY_ID);

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
            //config.addConfig("fullScreenBtn.plugin", "false");

            if (!adURL.isEmpty()) {
                config.addConfig("doubleClick.plugin", "true");
                config.addConfig("doubleClick.leadWithFlash", "false");
                config.addConfig("doubleClick.adTagUrl", adURL);
            } else {
                config.addConfig("doubleClick.plugin", "false");
                config.addConfig("doubleClick.leadWithFlash", "false");
                config.addConfig("doubleClick.adTagUrl", null);
            }

            for (FlashVar flashVar : flashVars){
                config.addConfig(flashVar.key, flashVar.value);
            }

   //         config.addConfig("IframeCustomPluginCss1", "https://devpto.streamamg.com/assets/css/video-player.css");

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
if (mPlayerListener != null){
    mPlayerListener.onKPlayerError(playerViewController, error);
}
    }

    @Override
    public void onKPlayerPlayheadUpdate(PlayerViewController playerViewController, long currentTimeMilliSeconds) {
        if (mPlayerListener != null){
            mPlayerListener.onKPlayerPlayheadUpdate(playerViewController, currentTimeMilliSeconds);
        }
    }

    @Override
    public void onKPlayerStateChanged(PlayerViewController playerViewController, KPlayerState state) {
        if (mPlayerListener != null){
            mPlayerListener.onKPlayerStateChanged(playerViewController, state);
        }
    }

    @Override
    public void onKPlayerFullScreenToggled(PlayerViewController playerViewController, boolean isFullscreen) {
        if (mPlayerListener != null){
            mPlayerListener.onKPlayerFullScreenToggled(playerViewController, isFullscreen);
        }
    }

    public void refreshMedia() {
        if (mPlayerView != null) {

            shouldResume = true;
            playback = mPlayerView.getCurrentPlaybackTime();
            runMedia();
        }
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
