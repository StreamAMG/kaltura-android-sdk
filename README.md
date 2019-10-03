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


For a full list of native embed advantages see native controls table within the [player toolkit basic usage guide](http://knowledge.kaltura.com/kaltura-player-v2-toolkit-theme-skin-guide).

The Kaltura player-sdk-native component can be embedded into both native apps, and hybrid native apps ( via standard dynamic embed syntax )

Future support will include:
* PlayReady DRM
* Multiple stream playback
* Offline viewing

Architecture Overview
=====
![alt text](http://html5video.org/presentations/HTML5PartDeux.FOSDEM.2014/koverview.jpg "Architecture Overview")


Quick Start Guide
======

```
1. git clone https://{username}@bitbucket.org/sukdev/kaltura-android-sdk.git to the same folder of your app.
```
```
2. Add reference to PlayerSDK module from your project:
```

#####Select _`settings.gradle`_ and add:

```
include ':googlemediaframework'
project(':googlemediaframework').projectDir=new File('kaltura-android-sdk/googlemediaframework')

include ':playerSDK'
project(':playerSDK').projectDir=new File('kaltura-android-sdk/playerSDK')

```
#####Right click on your app folder ->_`Open Module Settings`_.

![alt text](https://camo.githubusercontent.com/c695400a03cb1ea519a653b3f1e0d77d4f4dc0aa/68747470733a2f2f39653737303466612d612d36326362336131612d732d73697465732e676f6f676c6567726f7570732e636f6d2f736974652f6b616c74757261696d616765732f736861726569636f6e732f4d6f64756c6553657474696e67732e706e673f617474616368617574683d414e6f5937636f334669626534735a634959354b31514255374c373459344a703731574a624d4a34764b6167636b6873597a413271787a4154356d79654b65697a517255734f716e37632d4d434e55366a4b4a692d535a774d574876324a4d636d4d3778732d4f32466b51556f6562644437534653634e64725556387366646141713047724e59677253456b305f34533062594572586267306e457a4c6c4f484c4f5552774d7a685a7345764d46646a6a5f516536766655437346646c4f6d3642484f5638466a724138617a62782d797750576e3133536972467256443731506d62724d66746d76364e69764a4f7a616573396c6f6973253344266174747265646972656374733d30)

#####Select _`Dependencies`_ tab -> click on the _`+`_ button and choos the _`playerSDK`_ module:
![alt text](https://camo.githubusercontent.com/8bafc1009ef079e7046ab039d9269748006c4dc8/68747470733a2f2f39653737303466612d612d36326362336131612d732d73697465732e676f6f676c6567726f7570732e636f6d2f736974652f6b616c74757261696d616765732f736861726569636f6e732f416464446570656e64656e636965732e706e673f617474616368617574683d414e6f593763714457797030576b2d4b2d4563734c7166314961643731486d3857585335356e6d706b614b6a77364d6537394f5842506f5562385f7574436f6c4b51674c48432d4e4c3851344d44366a6162716555766e596957396e414e415f6b636a476267783874466e64782d5f6e7772644b4c61776d704a594e3234584d6c32673945765236536656774c704d484f796d556e4e3836387976494a5169494f655970566a744b573637467231337444336d56564d537a716f5043316862546e4d694a452d72366d7372496b717934535a467354586b333973774d6561375541454e3168656236755f416473552d5578554266547967253344266174747265646972656374733d30)

Now, you are linked to the playerSDK by reference. Be sure that you cloned the playerSDK to the same folder of your project.


Make sure that you cloned the **_kaltura-android-sdk_** project to the same folder of your project, if you prefer to clone it else where, you should update the _**`settings.gradle`**_.

API Overview
=====

###Loading Kaltura player into Fragment - OVP:
```

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        if(mFragmentView == null) {
            mFragmentView = inflater.inflate(R.layout.fragment_fullscreen, container, false);
        }

        mPlayerView = (PlayerViewController) mFragmentView.findViewById(R.id.player);
        mPlayerView.loadPlayerIntoActivity(getActivity());

        KPPlayerConfig config = new  KPPlayerConfig("http://cdnapi.kaltura.com", "26698911", "1831271");
        config.setEntryId("1_o426d3i4");
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

###Loading Kaltura player into Fragment - OTT:
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
             "    \"server\": \"http://192.168.160.160/html5.kaltura/mwEmbed/mwEmbedFrame.php\",\n" +
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
             "    \"TVPAPIBaseUrl\": \"http://tvpapi-stg.as.tvinci.com/v3_9/gateways/jsonpostgw.aspx?m=\",\n" +
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
             "        \"ApiPass\": \"11111\",\n" +
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
             "        \"UDID\": \"aa5e1b6c96988d68\"\n" +
             "      }\n" +
             "    }\n" +
             "  }\n" +
             "}\n";
     return json;
 }
```


###Fetching duration:
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

###DRM license:
For loading the DRM license of a video, the config must have the izsession
izsession is an unique session that client gets after logging in to the website.
Throughout the izsession our service is recognizing if user is allowed to play content or not.


```
String izsession = "00000000-0000-0000-0000-000000000000"; // Replace with your izsession
config.addConfig("izsession", izsession);
```

###Play audio in background:
In order to play audio in background, the activity that uses the PlayerViewController must have the _`onPause()`_ and _`onResume()`_ override methods implemented in this way:

Implementation example:
```
    private boolean backgroundAudioEnabled = true;
    
    @Override
    protected void onPause() {
        if (mPlayer != null && !backgroundAudioEnabled) {
            mPlayer.releaseAndSavePosition(true);
        }
        super.onPause();
    }
    
    @Override
    protected void onResume() {
        if (mPlayer != null && !backgroundAudioEnabled) {
            mPlayer.resumePlayer();
        }
        super.onResume();
    }
```