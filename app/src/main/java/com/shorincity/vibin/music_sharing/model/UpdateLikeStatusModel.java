package com.shorincity.vibin.music_sharing.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UpdateLikeStatusModel {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("status_like")
    @Expose
    private String statusLike;
    @SerializedName("button_status")
    @Expose
    private String buttonStatus;
    @SerializedName("user_liked_count")
    @Expose
    private Integer userLikedCount;
    @SerializedName("how_many_likes_user")
    @Expose
    private Integer howManyLikesUser;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusLike() {
        return statusLike;
    }

    public void setStatusLike(String statusLike) {
        this.statusLike = statusLike;
    }

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

}