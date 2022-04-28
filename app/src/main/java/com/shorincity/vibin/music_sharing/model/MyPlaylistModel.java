package com.shorincity.vibin.music_sharing.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class MyPlaylistModel implements Serializable {

    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("private")
    @Expose
    private String _private;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("gif_link")
    @Expose
    private String gifLink;
    @SerializedName("playlist_duration_hours")
    @Expose
    private Integer playlistDurationHours;
    @SerializedName("playlist_duration_minutes")
    @Expose
    private Integer playlistDurationMinutes;
    @SerializedName("playlist_duration_seconds")
    @Expose
    private Integer playlistDurationSeconds;
    @SerializedName("likes")
    @Expose
    private Integer likes;
    @SerializedName("admin_name")
    @Expose
    private String adminName;
    @SerializedName("admin_avatar_link")
    @Expose
    private String avatarLink;
    @SerializedName("admin_id")
    @Expose
    private int admin_id;
    @SerializedName("isLikedByUser")
    @Expose
    private boolean isLikedByUser;
    @SerializedName("no_of_songs")
    @Expose
    private int songs;
    @SerializedName("playlist_tags")
    @Expose
    private String playListTags;
    @SerializedName("admin_profile_verified")
    @Expose
    private boolean adminProfileVerified;
    @SerializedName("is_pinned_playlist")
    @Expose
    private boolean isPinnedPlaylist;

    public int getAdmin_id() {
        return admin_id;
    }

    public void setAdmin_id(int admin_id) {
        this.admin_id = admin_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPrivate() {
        return _private;
    }

    public void setPrivate(String _private) {
        this._private = _private;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getGifLink() {
        return gifLink;
    }

    public void setGifLink(String gifLink) {
        this.gifLink = gifLink;
    }

    public Integer getPlaylistDurationHours() {
        return playlistDurationHours;
    }

    public void setPlaylistDurationHours(Integer playlistDurationHours) {
        this.playlistDurationHours = playlistDurationHours;
    }

    public Integer getPlaylistDurationMinutes() {
        return playlistDurationMinutes;
    }

    public void setPlaylistDurationMinutes(Integer playlistDurationMinutes) {
        this.playlistDurationMinutes = playlistDurationMinutes;
    }

    public Integer getPlaylistDurationSeconds() {
        return playlistDurationSeconds;
    }

    public void setPlaylistDurationSeconds(Integer playlistDurationSeconds) {
        this.playlistDurationSeconds = playlistDurationSeconds;
    }

    public Integer getLikes() {
        return likes;
    }

    public void setLikes(Integer likes) {
        this.likes = likes;
    }

    public String getAdminName() {
        return adminName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    public String get_private() {
        return _private;
    }

    public void set_private(String _private) {
        this._private = _private;
    }

    public String getAvatarLink() {
        return avatarLink;
    }

    public void setAvatarLink(String avatarLink) {
        this.avatarLink = avatarLink;
    }

    public boolean isLikedByUser() {
        return isLikedByUser;
    }

    public void setLikedByUser(boolean likedByUser) {
        isLikedByUser = likedByUser;
    }

    public int getSongs() {
        return songs;
    }

    public void setSongs(int songs) {
        this.songs = songs;
    }

    public String getPlayListTags() {
        return playListTags;
    }

    public void setPlayListTags(String playListTags) {
        this.playListTags = playListTags;
    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isAdminProfileVerified() {
        return adminProfileVerified;
    }

    public void setAdminProfileVerified(boolean adminProfileVerified) {
        this.adminProfileVerified = adminProfileVerified;
    }

    public boolean isPinnedPlaylist() {
        return isPinnedPlaylist;
    }
}
