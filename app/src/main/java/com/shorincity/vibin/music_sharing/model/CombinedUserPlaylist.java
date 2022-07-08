package com.shorincity.vibin.music_sharing.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CombinedUserPlaylist {

    @SerializedName("status")
    private String status;

    @SerializedName("pinned_playlists")
    private List<MyPlaylistModel> pinnedPlaylists;

    @SerializedName("private_playlists")
    private List<MyPlaylistModel> privatePlaylists;

    @SerializedName("public_playlists")
    private List<MyPlaylistModel> publicPlaylists;


    public List<MyPlaylistModel> getPinnedPlaylists() {
        return pinnedPlaylists;
    }

    public List<MyPlaylistModel> getPrivatePlaylists() {
        return privatePlaylists;
    }

    public List<MyPlaylistModel> getPublicPlaylists() {
        return publicPlaylists;
    }

    public String getStatus() {
        return status;
    }
}
