package com.shorincity.vibin.music_sharing.model.profile;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CustomerBasicDetails {

    @SerializedName("user_username")
    @Expose
    private String userUsername;

    @SerializedName("user_avatar_link")
    @Expose
    private String userAvatarLink;

    @SerializedName("collaborator_count")
    @Expose
    private int collaboratorCount;

    @SerializedName("user_fullname")
    @Expose
    private String userFullname;

    @SerializedName("user_cover_link")
    @Expose
    private String userCoverLink;

    @SerializedName("likes_received")
    @Expose
    private int likesReceived;

    @SerializedName("like_button_status")
    @Expose
    private String likeButtonStatus;

    @SerializedName("show_recently_played")
    @Expose
    private Boolean isShowRecentSong;

    @SerializedName("is_user_cover_url_avail")
    @Expose
    private Boolean isCoverImageAvailable;

    @SerializedName("is_profile_verified")
    @Expose
    private Boolean isProfileVerified;

    public Boolean getIsCoverImageAvailable() {
        return isCoverImageAvailable;
    }

    public String getUserUsername() {
        return userUsername;
    }

    public String getUserAvatarLink() {
        return userAvatarLink;
    }

    public int getCollaboratorCount() {
        return collaboratorCount;
    }

    public String getUserFullname() {
        return userFullname;
    }

    public String getUserCoverLink() {
        return userCoverLink;
    }

    public int getLikesReceived() {
        return likesReceived;
    }

    public String getLikeButtonStatus() {
        return likeButtonStatus;
    }

    public Boolean getShowRecentSong() {
        return isShowRecentSong;
    }

    public Boolean getProfileVerified() {
        return isProfileVerified;
    }
}