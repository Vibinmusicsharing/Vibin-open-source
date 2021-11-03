package com.shorincity.vibin.music_sharing.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class PlaylistSongCollabDeleteModel {
    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("deleted_songs")
    @Expose
    ArrayList<Integer> deletedSongs;

    @SerializedName("removed_collaborators")
    @Expose
    ArrayList<Integer> deletedCollaborator;

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

    public List<Integer> getDeletedSongs() {
        return deletedSongs;
    }

    public void setDeletedSongs(ArrayList<Integer> deletedSongs) {
        this.deletedSongs = deletedSongs;
    }

    public List<Integer> getDeletedCollaborator() {
        return deletedCollaborator;
    }

    public void setDeletedCollaborator(ArrayList<Integer> deletedCollaborator) {
        this.deletedCollaborator = deletedCollaborator;
    }
}
