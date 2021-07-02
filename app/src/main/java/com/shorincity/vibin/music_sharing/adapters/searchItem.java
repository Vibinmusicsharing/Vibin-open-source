package com.shorincity.vibin.music_sharing.adapters;

public class searchItem {

    String mTitle,playlistID,privatepublic;

    public searchItem(String mTitle, String playlistID, String privatepublic) {
        this.mTitle = mTitle;
        this.playlistID = playlistID;
        this.privatepublic = privatepublic;
    }

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getPlaylistID() {
        return playlistID;
    }

    public void setPlaylistID(String playlistID) {
        this.playlistID = playlistID;
    }

    public String getPrivatepublic() {
        return privatepublic;
    }

    public void setPrivatepublic(String privatepublic) {
        this.privatepublic = privatepublic;
    }
}
