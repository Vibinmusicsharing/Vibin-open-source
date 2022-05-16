package com.shorincity.vibin.music_sharing.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PlaylistDetailModel implements Parcelable, Comparable<PlaylistDetailModel> {

    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("id")
    @Expose
    private Integer id;

    @SerializedName("type")
    @Expose
    private String type;

    @SerializedName("track_id")
    @Expose
    private String trackId;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("image")
    @Expose
    private String image;

    @SerializedName("song_duration")
    @Expose
    private String songDuration;

    @SerializedName("playlist")
    @Expose
    private Integer playlist;

    @SerializedName("added_by")
    @Expose
    private Integer added_by;

    @SerializedName("likes")
    @Expose
    private Integer likes;

    @SerializedName("is_liked_by_viewer")
    @Expose
    private boolean isLikedByViewer;

    @SerializedName("artist_name")
    @Expose
    private String artistName;

    @SerializedName("added_by_avatar")
    @Expose
    private String collabProfile;

    boolean isSelected = false;

    boolean isEditable = false;

    public Integer getAdded_by() {
        return added_by;
    }

    public void setAdded_by(Integer added_by) {
        this.added_by = added_by;
    }

    public boolean isEditable() {
        return isEditable;
    }

    public void setEditable(boolean editable) {
        isEditable = editable;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public PlaylistDetailModel(String name, String image, String trackId, String songDuration, String artistName) {
        this.name = name;
        this.image = image;
        this.trackId = trackId;
        this.songDuration = songDuration;
        this.artistName = artistName;
    }

    public PlaylistDetailModel(String name, String image, String trackId) {
        this.name = name;
        this.image = image;
        this.trackId = trackId;

    }

    public PlaylistDetailModel(String name, String artistName, String image, String trackId) {
        this.name = name;
        this.artistName = artistName;
        this.image = image;
        this.trackId = trackId;

    }

    PlaylistDetailModel(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        type = in.readString();
        trackId = in.readString();
        name = in.readString();
        image = in.readString();
        songDuration = in.readString();
        if (in.readByte() == 0) {
            playlist = null;
        } else {
            playlist = in.readInt();
        }
        if (in.readByte() == 0) {
            added_by = null;
        } else {
            added_by = in.readInt();
        }
        if (in.readByte() == 0) {
            likes = null;
        } else {
            likes = in.readInt();
        }

        isLikedByViewer = in.readByte() == 0;
        artistName = in.readString();
        collabProfile = in.readString();
    }

    public static final Creator<PlaylistDetailModel> CREATOR = new Creator<PlaylistDetailModel>() {
        @Override
        public PlaylistDetailModel createFromParcel(Parcel in) {
            return new PlaylistDetailModel(in);
        }

        @Override
        public PlaylistDetailModel[] newArray(int size) {
            return new PlaylistDetailModel[size];
        }
    };

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTrackId() {
        return trackId;
    }

    public void setTrackId(String trackId) {
        this.trackId = trackId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getSongDuration() {
        return songDuration;
    }

    public void setSongDuration(String songDuration) {
        this.songDuration = songDuration;
    }

    public Integer getPlaylist() {
        return playlist;
    }

    public void setPlaylist(Integer playlist) {
        this.playlist = playlist;
    }

    public Integer getLikes() {
        return likes;
    }

    public void setLikes(Integer likes) {
        this.likes = likes;
    }

    public boolean isLikedByViewer() {
        return isLikedByViewer;
    }

    public void setLikedByViewer(boolean likedByViewer) {
        isLikedByViewer = likedByViewer;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getCollabProfile() {
        return collabProfile;
    }

    public void setCollabProfile(String collabProfile) {
        this.collabProfile = collabProfile;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        if (id == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(id);
        }
        parcel.writeString(type);
        parcel.writeString(trackId);
        parcel.writeString(name);
        parcel.writeString(image);
        parcel.writeString(songDuration);
        if (playlist == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(playlist);
        }
        if (added_by == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(added_by);
        }

        if (likes == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(likes);
        }

        parcel.writeByte((byte) (isLikedByViewer ? 1 : 0));
        parcel.writeString(artistName);
        parcel.writeString(collabProfile);

    }

    @Override
    public int compareTo(PlaylistDetailModel o) {
        return this.likes.compareTo(o.likes);
    }
}