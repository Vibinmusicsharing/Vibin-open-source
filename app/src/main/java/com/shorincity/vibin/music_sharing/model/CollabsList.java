package com.shorincity.vibin.music_sharing.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class CollabsList {
    @SerializedName("status")
    @Expose
    String status;
    @SerializedName("private_playlist_colaborators")
    @Expose
    ArrayList<UserData> private_playlist_colaborators;
    @SerializedName("public_playlist_colaborators")
    @Expose
    ArrayList<UserData> public_playlist_colaborators;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ArrayList<UserData> getPrivate_playlist_colaborators() {
        return private_playlist_colaborators;
    }

    public void setPrivate_playlist_colaborators(ArrayList<UserData> private_playlist_colaborators) {
        this.private_playlist_colaborators = private_playlist_colaborators;
    }

    public ArrayList<UserData> getPublic_playlist_colaborators() {
        return public_playlist_colaborators;
    }

    public void setPublic_playlist_colaborators(ArrayList<UserData> public_playlist_colaborators) {
        this.public_playlist_colaborators = public_playlist_colaborators;
    }

    public class UserData {
        @SerializedName("id")
        @Expose
        String id;
        @SerializedName("email")
        @Expose
        String email;
        @SerializedName("username")
        @Expose
        String username;
        @SerializedName("fullname")
        @Expose
        String fullname;
        @SerializedName("avatar_link")
        @Expose
        String avatar_link;
        @SerializedName("type")
        @Expose
        String type;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getFullname() {
            return fullname;
        }

        public void setFullname(String fullname) {
            this.fullname = fullname;
        }

        public String getAvatar_link() {
            return avatar_link;
        }

        public void setAvatar_link(String avatar_link) {
            this.avatar_link = avatar_link;
        }
    }
}
