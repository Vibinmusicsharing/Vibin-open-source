package com.shorincity.vibin.music_sharing.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class VersionResponse {

    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("update_required")
    @Expose
    private boolean updateRequired;

    @SerializedName("update_mandatory")
    @Expose
    private boolean updateMandatory;

    @SerializedName("youtube")
    @Expose
    private String youtube;

    @SerializedName("giphy")
    @Expose
    private String giphy;

    @SerializedName("last_fm")
    @Expose
    private String lastFm;

    @SerializedName("message")
    @Expose
    private String message;


    public String getStatus() {
        return status;
    }

    public boolean isUpdateRequired() {
        return updateRequired;
    }

    public boolean isUpdateMandatory() {
        return updateMandatory;
    }

    public String getYoutube() {
        return youtube;
    }

    public String getGiphy() {
        return giphy;
    }

    public String getLastFm() {
        return lastFm;
    }

    public String getMessage() {
        return message;
    }
}
