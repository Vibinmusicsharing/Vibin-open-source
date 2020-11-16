package com.shorincity.vibin.music_sharing.adapters;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

// view collab class
public class ViewCollab {

    @SerializedName("id")
    @Expose
    private Integer id;

    @SerializedName("email")
    @Expose
    private String email;

    @SerializedName("username")
    @Expose
    private String username;

    @SerializedName("fullname")
    @Expose
    private String fullname;

    @SerializedName("avatar_link")
    @Expose
    private String avatarLink;

//    public ViewCollab(int id, String email, String username, String fullname) {
//        this.id = id;
//        this.email = email;
//        this.username = username;
//        this.fullname = fullname;
//    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public String getAvatarLink() {
        return avatarLink;
    }

    public void setAvatarLink(String avatarLink) {
        this.avatarLink = avatarLink;
    }
}
