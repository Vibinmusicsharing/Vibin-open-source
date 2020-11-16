package com.shorincity.vibin.music_sharing.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserProfileModel {

    @SerializedName("button_status")
    @Expose
    private String buttonStatus;
    @SerializedName("user_liked_count")
    @Expose
    private Integer userLikedCount;
    @SerializedName("how_many_likes_user")
    @Expose
    private Integer howManyLikesUser;
    @SerializedName("collaborator_count")
    @Expose
    private Integer collaboratorCount;

    @SerializedName("avatar_link")
    @Expose
    private String avatarLink;

    public String getButtonStatus() {
        return buttonStatus;
    }

    public void setButtonStatus(String buttonStatus) {
        this.buttonStatus = buttonStatus;
    }

    public Integer getUserLikedCount() {
        return userLikedCount;
    }

    public void setUserLikedCount(Integer userLikedCount) {
        this.userLikedCount = userLikedCount;
    }

    public Integer getHowManyLikesUser() {
        return howManyLikesUser;
    }

    public void setHowManyLikesUser(Integer howManyLikesUser) {
        this.howManyLikesUser = howManyLikesUser;
    }

    public Integer getCollaboratorCount() {
        return collaboratorCount;
    }

    public void setCollaboratorCount(Integer collaboratorCount) {
        this.collaboratorCount = collaboratorCount;
    }

    public String getAvatarLink() {
        return avatarLink;
    }

    public void setAvatarLink(String avatarLink) {
        this.avatarLink = avatarLink;
    }
}