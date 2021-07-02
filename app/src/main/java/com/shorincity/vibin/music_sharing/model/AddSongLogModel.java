package com.shorincity.vibin.music_sharing.model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AddSongLogModel {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("song_id")
    @Expose
    private String songId;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSongId() {
        return songId;
    }

    public void setSongId(String songId) {
        this.songId = songId;
    }

}