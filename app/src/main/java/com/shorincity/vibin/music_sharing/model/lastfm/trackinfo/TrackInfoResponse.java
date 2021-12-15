
package com.shorincity.vibin.music_sharing.model.lastfm.trackinfo;

import com.google.gson.annotations.SerializedName;

public class TrackInfoResponse {

    @SerializedName("track")
    private TrackInfo mTrackInfo;

    public TrackInfo getTrack() {
        return mTrackInfo;
    }

    public void setTrack(TrackInfo trackInfo) {
        mTrackInfo = trackInfo;
    }

}
