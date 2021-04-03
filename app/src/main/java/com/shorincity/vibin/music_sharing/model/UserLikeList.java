package com.shorincity.vibin.music_sharing.model;

import java.util.ArrayList;

public class UserLikeList {
    String status;
    ArrayList<GotLikes> profile_likes_i_got = new ArrayList<>();
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

    public class GotLikes {
        String id;
        String email;
        String username;
        String fullname;
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
