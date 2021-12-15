
package com.shorincity.vibin.music_sharing.model.lastfm;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Trackmatches {

    @SerializedName("track")
    private List<Track> mTrack;

    public List<Track> getTrack() {
        return mTrack;
    }

    public void setTrack(List<Track> track) {
        mTrack = track;
    }

}
