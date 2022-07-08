package com.shorincity.vibin.music_sharing.model.profile;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.shorincity.vibin.music_sharing.model.MyPlaylistModel;
import com.shorincity.vibin.music_sharing.model.RecentSongModel;

import java.util.List;

public class UserProfileResponse {

    @SerializedName("recently_played_songs")
    private List<RecentSongModel> recentlyPlayedSongs;

    @SerializedName("playlists")
    private List<MyPlaylistModel> playlists;

    @SerializedName("customer_basic_details")
    private CustomerBasicDetails customerBasicDetails;

    @SerializedName("status")
    private String status;

    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("latest_liked_music_thumbnail")
    private String latestLikedMusicThumbnail;

    public List<RecentSongModel> getRecentlyPlayedSongs() {
        return recentlyPlayedSongs;
    }

    public List<MyPlaylistModel> getPlaylists() {
        return playlists;
    }

    public CustomerBasicDetails getCustomerBasicDetails() {
        return customerBasicDetails;
    }

    public String getStatus() {
        return status;
    }

    public String getLatestLikedMusicThumbnail() {
        return latestLikedMusicThumbnail;
    }

    public String getMessage() {
        return message;
    }
}