package com.shorincity.vibin.music_sharing.model.avatar;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AvatarDetails {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("avatars")
    @Expose
    private List<Avatar> avatars = null;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Avatar> getAvatars() {
        return avatars;
    }

    public void setAvatars(List<Avatar> avatars) {
        this.avatars = avatars;
    }

}