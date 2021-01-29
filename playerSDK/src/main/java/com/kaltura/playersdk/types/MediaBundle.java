package com.kaltura.playersdk.types;

public class MediaBundle {
    public String SERVICE_URL = "";
    public String PARTNER_ID = "";
    public String UI_CONF_ID = "";
    public String ENTRY_ID = "";
    public String KS = "";
    public String izsession = "";

    public String adURL = "";

    public MediaBundle(String serviceURL, String partnerID, String UIConfID, String EntryID, String kS, String izSession){
        SERVICE_URL = serviceURL;
        PARTNER_ID = partnerID;
        UI_CONF_ID = UIConfID;
        ENTRY_ID = EntryID;
        KS = kS;
        izsession = izSession;
    }

    public MediaBundle(String serviceURL, String partnerID, String UIConfID, String EntryID){
        SERVICE_URL = serviceURL;
        PARTNER_ID = partnerID;
        UI_CONF_ID = UIConfID;
        ENTRY_ID = EntryID;
        KS = "";
        izsession = "";
    }

}
