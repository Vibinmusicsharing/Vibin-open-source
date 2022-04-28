package com.shorincity.vibin.music_sharing.model;

import com.google.gson.annotations.SerializedName;

public class RealTimeNotificationCount {
    @SerializedName("message")
    private String message;
    @SerializedName("notification_count")
    private Integer notificationCount;
    @SerializedName("status")
    private String status;
    @SerializedName("user")
    private Integer user;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getNotificationCount() {
        return notificationCount;
    }

    public void setNotificationCount(Integer notificationCount) {
        this.notificationCount = notificationCount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getUser() {
        return user;
    }

    public void setUser(Integer user) {
        this.user = user;
    }
}
