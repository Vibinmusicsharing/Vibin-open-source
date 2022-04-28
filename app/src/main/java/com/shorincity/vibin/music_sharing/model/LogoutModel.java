package com.shorincity.vibin.music_sharing.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LogoutModel {

    @SerializedName("Logged_Out")
    @Expose
    private Boolean loggedOut;

    public Boolean getLoggedOut() {
        return loggedOut;
    }

    public void setLoggedOut(Boolean loggedOut) {
        this.loggedOut = loggedOut;
    }

}
