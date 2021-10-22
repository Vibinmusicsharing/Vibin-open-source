package com.shorincity.vibin.music_sharing.adapters;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

// view collab class
public class ViewCollab implements Parcelable {

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
    boolean isSelected = false;

    boolean isEditable = false;

    public ViewCollab() {

    }

    protected ViewCollab(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        email = in.readString();
        username = in.readString();
        fullname = in.readString();
        avatarLink = in.readString();
        isSelected = in.readByte() != 0;
        isEditable = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(id);
        }
        dest.writeString(email);
        dest.writeString(username);
        dest.writeString(fullname);
        dest.writeString(avatarLink);
        dest.writeByte((byte) (isSelected ? 1 : 0));
        dest.writeByte((byte) (isEditable ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ViewCollab> CREATOR = new Creator<ViewCollab>() {
        @Override
        public ViewCollab createFromParcel(Parcel in) {
            return new ViewCollab(in);
        }

        @Override
        public ViewCollab[] newArray(int size) {
            return new ViewCollab[size];
        }
    };

    public void setId(Integer id) {
        this.id = id;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isEditable() {
        return isEditable;
    }

    public void setEditable(boolean editable) {
        isEditable = editable;
    }
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
