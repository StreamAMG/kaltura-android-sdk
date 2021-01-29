package com.kaltura.playersdk.events;

import com.kaltura.playersdk.PlayerViewController;
import com.kaltura.playersdk.types.KPError;

public interface KPlayerServiceListener {
    void onKPlayerError(PlayerViewController playerViewController, KPError error);
    void onKPlayerFullScreenToggled(PlayerViewController playerViewController, boolean isFullscreen);
    void onKPlayerPlayheadUpdate(PlayerViewController playerViewController, long currentTimeMilliSeconds);
    void onKPlayerStateChanged(PlayerViewController playerViewController, KPlayerState state);
}
