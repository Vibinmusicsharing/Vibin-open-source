
package com.shorincity.vibin.music_sharing.model.realtime;

import com.google.gson.annotations.SerializedName;

public class RTListner {

    @SerializedName("is_admin")
    private Boolean mIsAdmin;
    @SerializedName("user_avatar")
    private String mUserAvatar;
    @SerializedName("user_id")
    private Long mUserId;
    @SerializedName("user_name")
    private String mUserName;

    public Boolean getIsAdmin() {
        return mIsAdmin;
    }

    public void setIsAdmin(Boolean isAdmin) {
        mIsAdmin = isAdmin;
    }

    public String getUserAvatar() {
        return mUserAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        mUserAvatar = userAvatar;
    }

    public Long getUserId() {
        return mUserId;
    }

    public void setUserId(Long userId) {
        mUserId = userId;
    }

    public String getUserName() {
        return mUserName;
    }

    public void setUserName(String userName) {
        mUserName = userName;
    }

}
