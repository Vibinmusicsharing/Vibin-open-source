package com.shorincity.vibin.music_sharing.model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SongLikeModel {

    @SerializedName("like")
    @Expose
    private Boolean like;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("liked_songs")
    @Expose
    private Integer likedSongs;

    public Boolean getLike() {
        return like;
    }

    public void setLike(Boolean like) {
        this.like = like;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getLikedSongs() {
        return likedSongs;
    }

    public void setLikedSongs(Integer likedSongs) {
        this.likedSongs = likedSongs;
    }

}
