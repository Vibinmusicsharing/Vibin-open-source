
package com.shorincity.vibin.music_sharing.model.realtime;

import com.google.gson.annotations.SerializedName;

public class RTJoinUpdate {

    @SerializedName("message")
    private String mMessage;
    @SerializedName("total_joined")
    private Long mTotalJoined;
    @SerializedName("type")
    private String mType;
    @SerializedName("user_id_left")
    private Long mUserIdLeft;

    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String message) {
        mMessage = message;
    }

    public Long getTotalJoined() {
        return mTotalJoined;
    }

    public void setTotalJoined(Long totalJoined) {
        mTotalJoined = totalJoined;
    }

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        mType = type;
    }

    public Long getUserIdLeft() {
        return mUserIdLeft;
    }

    public void setUserIdLeft(Long mUserIdLeft) {
        this.mUserIdLeft = mUserIdLeft;
    }
}
