package com.shorincity.vibin.music_sharing.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class UserLikeList implements Serializable {
    @SerializedName("status")
    @Expose
    String status;
    @SerializedName("profile_likes_i_got")
    @Expose
    ArrayList<GotLikes> profile_likes_i_got = new ArrayList<>();
    @SerializedName("profiles_i_liked")
    @Expose
    ArrayList<GotLikes> profile__i_liked = new ArrayList<>();

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ArrayList<GotLikes> getProfile_likes_i_got() {
        return profile_likes_i_got;
    }

    public void setProfile_likes_i_got(ArrayList<GotLikes> profile_likes_i_got) {
        this.profile_likes_i_got = profile_likes_i_got;
    }

    public ArrayList<GotLikes> getProfile__i_liked() {
        return profile__i_liked;
    }

    public void setProfile__i_liked(ArrayList<GotLikes> profile__i_liked) {
        this.profile__i_liked = profile__i_liked;
    }

    public class GotLikes implements Serializable {
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
