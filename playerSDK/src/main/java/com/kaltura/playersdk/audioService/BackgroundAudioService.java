package com.kaltura.playersdk.audioService;

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
import android.widget.Toast;

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
import com.kaltura.playersdk.utils.LogUtils;

public class BackgroundAudioService extends Service implements KPErrorEventListener, KPPlayheadUpdateEventListener, KPStateChangedEventListener, KPFullScreenToggledEventListener {

    PlayerViewController mPlayerView = null;


    public String SERVICE_URL = "http://{your_mp}/";
    public String PARTNER_ID = "{partner_id}";
    public String UI_CONF_ID = "{ui_conf_id}";
    public String ENTRY_ID = "{entry_id}";

    int counter = 0;

    KPlayerState currentState = KPlayerState.UNKNOWN;


    private IBinder mBinder = new MyBinder();

    @Override
    public IBinder onBind(Intent intent) {
        Log.d("WRD", "service onBind");
        Toast.makeText(this, "Service OnBind()", Toast.LENGTH_LONG).show();
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_STICKY;

    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        Toast.makeText(this, "Service Destroyed ", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        // TODO Auto-generated method stub
        super.onStart(intent, startId);
        Log.d("WRD", "service onStart: " + counter);
     //   Toast.makeText(this, "Service Started ", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // TODO Auto-generated method stub
        return super.onUnbind(intent);
    }



    @Override
    public void onCreate() {
        super.onCreate();
        SERVICE_URL = "http://mp.streamamg.com";
        PARTNER_ID = "3001133";
        UI_CONF_ID = "30027349";
        ENTRY_ID = "0_9fcwzpij";
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
        startForeground(2, notification);
    }

    public void setupPlayer(Activity activity, PlayerViewController player){

        Log.d("WRD", "service setUpPlayer: " + counter);
        counter++;
        getPlayer(activity, player);
    }


    private PlayerViewController getPlayer(Activity activity, PlayerViewController player) {
     //   if (mPlayerView == null) {
        KPPlayerConfig config;
        boolean shouldResume = false;
        double playback = 0.0f;
        if (mPlayerView != null) {
            config = mPlayerView.getConfig();
            mPlayerView.releaseAndSavePosition(true);
            playback = mPlayerView.getCurrentPlaybackTime();
            shouldResume = true;
        } else {
            config = new KPPlayerConfig(SERVICE_URL, UI_CONF_ID, PARTNER_ID).setEntryId(ENTRY_ID);
        }

        Log.d("WRD", "Config: " + config);

            mPlayerView = player;

            Log.d("WRD", "service setUpPlayer");
    //    }
            if (mPlayerView != null) {
                mPlayerView.loadPlayerIntoActivity(activity);

                if (!SERVICE_URL.startsWith("http")) {
                    SERVICE_URL = "http://" + SERVICE_URL;
                }


//                if (KS.length() > 0) {
//                    config.setKS(KS);
//                }
//                if (izsession.length() > 0) {
//                    config.addConfig("izsession", izsession);
//                }
// CONFIG GOES HERE!
                // Set your flashvars here
                config.addConfig("chromecast.receiverLogo", "true");
                config.addConfig("fullScreenBtn.plugin", "false");

                mPlayerView.initWithConfiguration(config);
if (shouldResume){
    mPlayerView.resumePlayer();
    mPlayerView.resumeState();
    mPlayerView.setPlaybackTime(playback);
    mPlayerView.playFromCurrentPosition();
}
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

       public BackgroundAudioService getService() {
           Log.d("WRD", "service getService");
            return BackgroundAudioService.this;
        }
    }

}
