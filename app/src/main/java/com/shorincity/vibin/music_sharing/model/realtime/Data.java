
package com.shorincity.vibin.music_sharing.model.realtime;

import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("admin_id")
    private Long mAdminId;
    @SerializedName("elapsed_song_time")
    private String mElapsedSongTime;
    @SerializedName("id")
    private Long mId;
    @SerializedName("invited")
    private Long mInvited;
    @SerializedName("is_repeat")
    private Boolean mIsRepeat;
    @SerializedName("is_shuffle")
    private Boolean mIsShuffle;
    @SerializedName("joined")
    private Long mJoined;
    @SerializedName("playlist_id")
    private Long mPlaylistId;
    @SerializedName("playlist_time")
    private String mPlaylistTime;
    @SerializedName("session_playback")
    private String mSessionPlayback;
    @SerializedName("session_status")
    private String mSessionStatus;
    @SerializedName("session_token")
    private String mSessionToken;
    @SerializedName("song_playing")
    private Long mSongPlaying;
    @SerializedName("playlist_name")
    private String mPlaylistName;


    public Long getAdminId() {
        return mAdminId;
    }

    public void setAdminId(Long adminId) {
        mAdminId = adminId;
    }

    public String getElapsedSongTime() {
        return mElapsedSongTime;
    }

    public void setElapsedSongTime(String elapsedSongTime) {
        mElapsedSongTime = elapsedSongTime;
    }

    public Long getId() {
        return mId;
    }

    public void setId(Long id) {
        mId = id;
    }

    public Long getInvited() {
        return mInvited;
    }

    public void setInvited(Long invited) {
        mInvited = invited;
    }

    public Boolean isRepeat() {
        return mIsRepeat;
    }

    public void setIsRepeat(Boolean isRepeat) {
        mIsRepeat = isRepeat;
    }

    public Boolean isShuffle() {
        return mIsShuffle;
    }

    public void setIsShuffle(Boolean isShuffle) {
        mIsShuffle = isShuffle;
    }

    public Long getJoined() {
        return mJoined;
    }

    public void setJoined(Long joined) {
        mJoined = joined;
    }

    public Long getPlaylistId() {
        return mPlaylistId;
    }

    public void setPlaylistId(Long playlistId) {
        mPlaylistId = playlistId;
    }

    public String getPlaylistTime() {
        return mPlaylistTime;
    }

    public void setPlaylistTime(String playlistTime) {
        mPlaylistTime = playlistTime;
    }

    public String getSessionPlayback() {
        return mSessionPlayback;
    }

    public void setSessionPlayback(String sessionPlayback) {
        mSessionPlayback = sessionPlayback;
    }

    public String getSessionStatus() {
        return mSessionStatus;
    }

    public void setSessionStatus(String sessionStatus) {
        mSessionStatus = sessionStatus;
    }

    public String getSessionToken() {
        return mSessionToken;
    }

    public void setSessionToken(String sessionToken) {
        mSessionToken = sessionToken;
    }

    public Long getSongPlaying() {
        return mSongPlaying;
    }

    public void setSongPlaying(Long songPlaying) {
        mSongPlaying = songPlaying;
    }

    public void setmPlaylistName(String mPlaylistName) {
        this.mPlaylistName = mPlaylistName;
    }

    public String getPlaylistName() {
        return mPlaylistName;
    }
}
