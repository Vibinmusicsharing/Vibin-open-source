package com.shorincity.vibin.music_sharing.model.shareplaylist;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.shorincity.vibin.music_sharing.model.MyPlaylistModel;
import com.shorincity.vibin.music_sharing.model.PlaylistDetailModel;

import java.util.List;

public class PlaylistDetailResponse {

    @SerializedName("is_collaborator")
    @Expose
    private boolean isCollaborator;

    @SerializedName("tracks")
    @Expose
    private List<PlaylistDetailModel> tracks;

    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("playlist_details")
    @Expose
    private MyPlaylistModel myPlaylistModel;

    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("admin_profile_verified")
    @Expose
    private boolean adminProfileVerified;

    public boolean isIsCollaborator() {
        return isCollaborator;
    }

    public List<PlaylistDetailModel> getTracks() {
        return tracks;
    }

    public String getStatus() {
        return status;
    }

    public MyPlaylistModel getMyPlaylistModel() {
        return myPlaylistModel;
    }

    public String getMessage() {
        return message;
    }

    public boolean isAdminProfileVerified() {
        return adminProfileVerified;
    }

    public void setAdminProfileVerified(boolean adminProfileVerified) {
        this.adminProfileVerified = adminProfileVerified;
    }
}