package com.shorincity.vibin.music_sharing.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class MyPlaylistModel implements Serializable {

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
}
