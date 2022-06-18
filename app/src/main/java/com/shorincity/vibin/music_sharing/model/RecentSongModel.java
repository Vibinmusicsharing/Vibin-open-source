package com.shorincity.vibin.music_sharing.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RecentSongModel {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("customer_linked")
    @Expose
    private Integer customerLinked;
    @SerializedName("song_type")
    @Expose
    private String songType;
    @SerializedName("song_name")
    @Expose
    private String songName;
    @SerializedName("song_id")
    @Expose
    private String songId;
    @SerializedName("song_uri")
    @Expose
    private String songUri;
    @SerializedName("song_thumbnail")
    @Expose
    private String songThumbnail;
    @SerializedName("song_details")
    @Expose
    private String songDetails;
    @SerializedName("song_listened_time")
    @Expose
    private String songListenedTime;
    @SerializedName("no_times_played")
    @Expose
    private Integer noTimesPlayed;
    @SerializedName("is_liked")
    @Expose
    private Object isLiked;
    @SerializedName("song_duration")
    @Expose
    private String songDuration;
    @SerializedName("artist_name")
    @Expose
    private String artistName;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCustomerLinked() {
        return customerLinked;
    }

    public void setCustomerLinked(Integer customerLinked) {
        this.customerLinked = customerLinked;
    }

    public String getSongType() {
        return songType;
    }

    public void setSongType(String songType) {
        this.songType = songType;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getSongId() {
        return songId;
    }

    public void setSongId(String songId) {
        this.songId = songId;
    }

    public String getSongUri() {
        return songUri;
    }

    public void setSongUri(String songUri) {
        this.songUri = songUri;
    }

    public String getSongThumbnail() {
        return songThumbnail;
    }

    public void setSongThumbnail(String songThumbnail) {
        this.songThumbnail = songThumbnail;
    }

    public String getSongDetails() {
        return songDetails;
    }

    public void setSongDetails(String songDetails) {
        this.songDetails = songDetails;
    }

    public String getSongListenedTime() {
        return songListenedTime;
    }

    public void setSongListenedTime(String songListenedTime) {
        this.songListenedTime = songListenedTime;
    }

    public Integer getNoTimesPlayed() {
        return noTimesPlayed;
    }

    public void setNoTimesPlayed(Integer noTimesPlayed) {
        this.noTimesPlayed = noTimesPlayed;
    }

    public Object getIsLiked() {
        return isLiked;
    }

    public void setIsLiked(Object isLiked) {
        this.isLiked = isLiked;
    }

    public String getArtistName() {
        return artistName;
    }

    public String getSongDuration() {
        return songDuration;
    }
}