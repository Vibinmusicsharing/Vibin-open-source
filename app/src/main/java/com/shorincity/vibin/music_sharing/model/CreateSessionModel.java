package com.shorincity.vibin.music_sharing.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CreateSessionModel {
    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("session_key")
    @Expose
    private String sessionKey;

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public String getSessionKey() {
        return sessionKey;
    }
}
