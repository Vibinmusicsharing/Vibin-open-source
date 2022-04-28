package com.shorincity.vibin.music_sharing.model.shareplaylist;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SharePlaylistResponse {

    @SerializedName("data")
    private Data data;

    @SerializedName("status")
    private String status;

    @SerializedName("message")
    @Expose
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Data getData() {
        return data;
    }

    public String getStatus() {
        return status;
    }
}