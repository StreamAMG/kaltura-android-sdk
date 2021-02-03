[![Build Status](https://travis-ci.org/kaltura/player-sdk-native-android.svg?branch=master)](https://travis-ci.org/kaltura/player-sdk-native-android)


Player SDK Native Android
=========================

**Note**: The Kaltura native player component is in beta. If your a Kaltura customer please contact your Kaltura customer success manager to help facilitate use of this component.

The Kaltura player-sdk-native component enables embedding the [kaltura player](http://player.kaltura.com) into native environments. This enables full HTML5 player platform, without limitations of HTML5 video tag API in Android platforms. Currently for Android this enables:
* Inline playback with HTML controls ( disable controls during ads etc. )
* Widevine DRM support
* AutoPlay
* Volume Control
* Full [player.kaltura.com](http://player.kaltura.com) feature set for themes and plugins
* HLS Playback
* DFP IMA SDK
* Background audio service


For a full list of native embed advantages see native controls table within the [player toolkit basic usage guide](http://knowledge.kaltura.com/kaltura-player-v2-toolkit-theme-skin-guide).

The Kaltura player-sdk-native component can be embedded into both native apps, and hybrid native apps ( via standard dynamic embed syntax )

Future support will include:
* PlayReady DRM
* Multiple stream playback
* Offline viewing

Architecture Overview
=====
![alt text](docs/architecture.png "Architecture Overview")


Quick Start Guide
======

```
1. git clone https://{username}@bitbucket.org/sukdev/kaltura-android-sdk.git to the same folder of your app.
```
```
2. Add reference to PlayerSDK module from your project:
```

##### Select _`settings.gradle`_ and add:

```
include ':googlemediaframework'
project(':googlemediaframework').projectDir=new File('kaltura-android-sdk/googlemediaframework')

include ':playerSDK'
project(':playerSDK').projectDir=new File('kaltura-android-sdk/playerSDK')

```
##### Right click on your app folder ->_`Open Module Settings`_.

![alt text](docs/module_settings.png)

##### Select _`Dependencies`_ tab -> click on the _`+`_ button and choos the _`playerSDK`_ module:
![alt text](docs/playersdk.png)

Now, you are linked to the playerSDK by reference. Be sure that you cloned the playerSDK to the same folder of your project.


Make sure that you cloned the **_kaltura-android-sdk_** project to the same folder of your project, if you prefer to clone it else where, you should update the _**`settings.gradle`**_.


Gradle implementation
=====

If you are using Gradle to get libraries into your build, you will need to:

Step 1. Add the StreamAMG maven repository to the list of repositories in Project build.gradle:

```
allprojects {
    repositories {
        ...
        maven {
            url "https://api.bitbucket.org/2.0/repositories/sukdev/kaltura-android-sdk/src/releases"
        }
    }
}
```

Step 2. Add the dependency information in Module app build.gradle:

```
implementation 'com.streamamg:playersdk:2.7.3'
```

If you are not using AndroidX in your app, you should exclude the following module to avoid incompatibilities:

```
implementation 'com.streamamg:playersdk:2.7.3', {
    exclude group: 'androidx.core', module: 'core'
    exclude group: 'androidx.media', module:'media'
}
```

Java 8 compatibility
=====

Recent versions of the PlayerSDK require Java 8 compatibility.

If you receive crashes when opening activities or fragments with the player embedded, please check your project contains the following in the build.gradle file:

```
android {

    ...

    compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
    }

    ...

```

API Overview
=====

### Loading Kaltura player into Fragment - OVP:
```

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        if(mFragmentView == null) {
            mFragmentView = inflater.inflate(R.layout.fragment_fullscreen, container, false);
        }

        mPlayerView = (PlayerViewController) mFragmentView.findViewById(R.id.player);
        mPlayerView.loadPlayerIntoActivity(getActivity());

        KPPlayerConfig config = new  KPPlayerConfig("http://{server_mp}", "{uiconf_id}", "{partner_id}");
        config.setEntryId("{entry_id}");
        mPlayerView.initWithConfiguration(config);        mPlayerView.addEventListener(new KPEventListener() {
            @Override
            public void onKPlayerStateChanged(PlayerViewController playerViewController, KPlayerState state) {
                Log.d("KPlayer State Changed", state.toString());
            }

            @Override
            public void onKPlayerPlayheadUpdate(PlayerViewController playerViewController, float currentTime) {
                Log.d("KPlayer State Changed", Float.toString(currentTime));
            }

            @Override
            public void onKPlayerFullScreenToggeled(PlayerViewController playerViewController, boolean isFullscreen) {
                Log.d("KPlayer toggeled", Boolean.toString(isFullscreen));
            }
        });
        return mFragmentView;
    }
```

### Loading Kaltura player into Fragment - OTT:
```

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        if(mFragmentView == null) {
            mFragmentView = inflater.inflate(R.layout.fragment_fullscreen, container, false);
        }

        mPlayerView = (PlayerViewController) mFragmentView.findViewById(R.id.player);
        mPlayerView.loadPlayerIntoActivity(getActivity());
        KPPlayerConfig config = null;
        try {
              config = KPPlayerConfig.fromJSONObject(new JSONObject(getConfigJson("123","456","tvpapi_000")));
        } catch (JSONException e) {
              e.printStackTrace();
        }

        mPlayerView.initWithConfiguration(config);        mPlayerView.addEventListener(new KPEventListener() {
            @Override
            public void onKPlayerStateChanged(PlayerViewController playerViewController, KPlayerState state) {
                Log.d("KPlayer State Changed", state.toString());
            }

            @Override
            public void onKPlayerPlayheadUpdate(PlayerViewController playerViewController, float currentTime) {
                Log.d("KPlayer State Changed", Float.toString(currentTime));
            }

            @Override
            public void onKPlayerFullScreenToggeled(PlayerViewController playerViewController, boolean isFullscreen) {
                Log.d("KPlayer toggeled", Boolean.toString(isFullscreen));
            }
        });
        return mFragmentView;
    }

    public String getConfigJson(String mediaID, String uiConfID, String tvpApi) {
     String json = "{\n" +
             "  \"base\": {\n" +
             "    \"server\": \"http://{your_mp}/html5.kaltura/mwEmbed/mwEmbedFrame.php\",\n" +
             "    \"partnerId\": \"\",\n" +
             "    \"uiConfId\": \"" + uiConfID + "\",\n" +
             "    \"entryId\": \"" + mediaID + "\"\n" +
             "  },\n" +
             "  \"extra\": {\n" +
             "    \"controlBarContainer.hover\": true,\n" +
             "    \"controlBarContainer.plugin\": true,\n" +
             "    \n" +
             "    \"liveCore.disableLiveCheck\": true,\n" +
             "    \"tvpapiGetLicensedLinks.plugin\": true,\n" +
             "    \"TVPAPIBaseUrl\": \"http://{tvpapi}/v3_9/gateways/jsonpostgw.aspx?m=\",\n" +
             "    \"proxyData\": {\n";

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2 /*4.3*/) {
//            json = json + "\"config\": {\n" +
//                    "                                    \"flavorassets\": {\n" +
//                    "                                        \"filters\": {\n" +
//                    "                                            \"include\": {\n" +
//                    "                                                \"Format\": [\n" +
//                    "                                                    \"dash Main\"\n" +
//                    "                                                ]\n" +
//                    "                                            }\n" +
//                    "                                        }\n" +
//                    "                                    }\n" +
//                    "                                },";
//        }
     json = json + "      \"MediaID\": \"" + mediaID + "\",\n" +
             "      \"iMediaID\": \"" + mediaID + "\",\n" +
             "      \"mediaType\": \"0\",\n" +
             "      \"picSize\": \"640x360\",\n" +
             "      \"withDynamic\": \"false\",\n" +
             "      \"initObj\": {\n" +
             "        \"ApiPass\": \"{api_pass}\",\n" +
             "        \"ApiUser\": \"" + tvpApi + "\",\n" +
             "        \"DomainID\": 0,\n" +
             "        \"Locale\": {\n" +
             "            \"LocaleCountry\": \"null\",\n" +
             "            \"LocaleDevice\": \"null\",\n" +
             "            \"LocaleLanguage\": \"null\",\n" +
             "            \"LocaleUserState\": \"Unknown\"\n" +
             "        },\n" +
             "        \"Platform\": \"Cellular\",\n" +
             "        \"SiteGuid\": \"\",\n" +
             "        \"UDID\": \"{udid}\"\n" +
             "      }\n" +
             "    }\n" +
             "  }\n" +
             "}\n";
     return json;
 }
```

### Player as a Service
The player can now be added to a project as a service, allowing continual audio playback when the app is in the background.

New convenience classes have been added to assist with running the service:

MediaBundle - A central package of information describing the video to pass to the service
FlashVar - A Key / Value Pair containing any flashvar information to pass to the player.

The activity or fragment (or controller / presenter, etc) must conform to KPlayerServiceListener.

```
// Kotlin
    var backgroundAudioService: BackgroundPlayerService? = null
    var serviceBound = false
    lateinit var mPlayerView: PlayerViewController


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kotlin)
    //... Set up view
        mPlayerView = findViewById(R.id.player)


        if(savedInstanceState == null) { // Only initialise the service once
            initPlayerService()
        }

    }

    // Initialisation of the Player as a Service
    private fun initPlayerService() {
        // Set an icon to show on the service notification (Android O and above)
        BackgroundPlayerService.setNotificationIcon(R.drawable.ic_cast) 
        // Start the background service
        val serviceIntent = Intent(this, BackgroundPlayerService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }
        // Bind to the service to interact with it
        bindService(serviceIntent, myConnection, BIND_AUTO_CREATE);
    }

    // Service connection to bind to
    val myConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName,
                                        service: IBinder) {
            val binder = service as BackgroundPlayerService.MyBinder
            backgroundAudioService = binder.service
            serviceBound = true
            // Once the service is bound, we can access the player
            setUpPlayer();
        }

        override fun onServiceDisconnected(name: ComponentName) {
            serviceBound = false
        }
    }

    // Initialise the player itself
    private fun setUpPlayer() {
        // Access the player through the bound service
        // setUpPlayer(activity: Activity, player: PlayerViewController, listener: KPlayerServiceListener)
        backgroundAudioService?.setupPlayer(this, mPlayerView, this)
        // Create a media bundle to pass to the player
        var bundle = MediaBundle(SERVICE_URL, PARTNER_ID, UI_CONF_ID, ENTRY_ID, KS, izsession)
        // include any ad links
        bundle.adURL = adLink
        // Include any Flash Vars - these will automatically be added to the player - ad flash vars are added automatically
        backgroundAudioService?.clearFlashVars()
        backgroundAudioService?.addFlashVar(FlashVar("chromecast.receiverLogo", "true"))
        // Send media to the player
        backgroundAudioService?.updateMedia(bundle)
    }

    override fun onResume() {
        super.onResume()
        // refresh the service if it exists
        backgroundAudioService?.refreshMedia()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Only destroy the service of the activity is destroyed
        if (mPlayerView.mediaControl != null) {
            if (mPlayerView.mediaControl.isPlaying) {
                mPlayerView.mediaControl.pause()
            }
        }
        // Ensure player is removed and service is destroyed
        mPlayerView.removePlayer()
        val serviceIntent = Intent(this, BackgroundPlayerService::class.java)
        stopService(serviceIntent)
        backgroundAudioService = null
    }


    // KPlayerServiceListener overrides

    override fun onKPlayerError(playerViewController: PlayerViewController?, error: KPError?) {}

    override fun onKPlayerPlayheadUpdate(playerViewController: PlayerViewController?, currentTimeMilliSeconds: Long) {}

    override fun onKPlayerStateChanged(playerViewController: PlayerViewController?, state: KPlayerState?) {}

    override fun onKPlayerFullScreenToggled(playerViewController: PlayerViewController?, isFullscreen: Boolean) {}
```


### Fetching duration:
For fetching the duration of a video, the player must be in READY state:

```
mPlayerView.addEventListener(new KPEventListener() {
            @Override
            public void onKPlayerStateChanged(PlayerViewController playerViewController, KPlayerState state) {
                Log.d("KPlayer State Changed", state.toString());
                if (state == KPlayerState.READY) {
                    Log.d("Duration", Double.toString(playerViewController.getDurationSec()) );
                }
            }

            @Override
            public void onKPlayerPlayheadUpdate(PlayerViewController playerViewController, float currentTime) {
                Log.d("KPlayer State Changed", Float.toString(currentTime));
            }

            @Override
            public void onKPlayerFullScreenToggeled(PlayerViewController playerViewController, boolean isFullscreen) {
                Log.d("KPlayer toggeled", Boolean.toString(isFullscreen));
            }
        });
```

### DRM license:
For loading the DRM license of a video, the config must have the izsession
izsession is an unique session that client gets after logging in to the website.
Throughout the izsession our service is recognizing if user is allowed to play content or not.


```
String izsession = "00000000-0000-0000-0000-000000000000"; // Replace with your izsession
config.addConfig("izsession", izsession);
```

### AndroidX support:
If your project is not migrated to AndroidX yet, you can still using the SDK changing:

##### In app build.gradle:
```
android {
    compileSdkVersion 28
    ...
    targetSdkVersion 28
    ...
    packagingOptions {
        ...
        exclude 'META-INF/androidx.*'
        exclude 'META-INF/proguard/androidx*'
    }
}

... 

dependencies {
    // change any com.android library version to 28.0.0
    ...
    implementation project(':playerSDK'), {
        exclude group: 'androidx.core', module: 'core'
        exclude group: 'androidx.media', module:'media'
    }
    ...
}
```

##### Error loading the SDK
![alt text](docs/usesCleartextTraffic.png "usesCleartextTraffic issue")

If you are facing the above issue, you must activate the usesCleartextTraffic option:
In your `AndroidManifest` file add the following line in your `application` tag
```
android:usesCleartextTraffic="true"
```

### How to enable Google Ad ###

Add the following configs (automatically added if using the background Player as a Service option):

```
    config.addConfig("doubleClick.plugin", "true");
    config.addConfig("doubleClick.leadWithFlash", "false");
    config.addConfig("doubleClick.adTagUrl", "tag-url");
```

Change Log:
===========

All notable changes to this project will be documented in this section.

### 2.7.3

* Background audio service added
* IMA Updated to latest version
* play-services-cast-framework updated to latest version

### 2.7.2

* Google Ads fix