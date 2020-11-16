package com.shorincity.vibin.music_sharing.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UpdateNotificationModel {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("playlist_id")
    @Expose
    private Integer playlistId;
    @SerializedName("admin_id")
    @Expose
    private Integer adminId;
    @SerializedName("session_key")
    @Expose
    private String sessionKey;
    @SerializedName("session_token")
    @Expose
    private String sessionToken;
    @SerializedName("user_ids")
    @Expose
    private Integer userIds;
    @SerializedName("user_session_keys")
    @Expose
    private String userSessionKeys;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getPlaylistId() {
        return playlistId;
    }

    public void setPlaylistId(Integer playlistId) {
        this.playlistId = playlistId;
    }

    public Integer getAdminId() {
        return adminId;
    }

    public void setAdminId(Integer adminId) {
        this.adminId = adminId;
    }

    public String getSessionKey() {
        return sessionKey;
    }

    public void setSessionKey(String sessionKey) {
        this.sessionKey = sessionKey;
    }

    public String getSessionToken() {
        return sessionToken;
    }

    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }

    public Integer getUserIds() {
        return userIds;
    }

    public void setUserIds(Integer userIds) {
        this.userIds = userIds;
    }

    public String getUserSessionKeys() {
        return userSessionKeys;
    }

    public void setUserSessionKeys(String userSessionKeys) {
        this.userSessionKeys = userSessionKeys;
    }

}