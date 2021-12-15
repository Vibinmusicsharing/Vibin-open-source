
package com.shorincity.vibin.music_sharing.model.realtime;

import com.google.gson.annotations.SerializedName;

public class MessageData {

    @SerializedName("sender_avatar")
    private String mSenderAvatar;
    @SerializedName("sender_id")
    private Long mSenderId;
    @SerializedName("sender_message")
    private String mSenderMessage;
    @SerializedName("sender_name")
    private String mSenderName;
    @SerializedName("sending_time")
    private String mSendingTime;
    @SerializedName("session_token")
    private String mSessionToken;
    @SerializedName("is_admin")
    private boolean isAdmin = false;

    public String getSenderAvatar() {
        return mSenderAvatar;
    }

    public void setSenderAvatar(String senderAvatar) {
        mSenderAvatar = senderAvatar;
    }

    public Long getSenderId() {
        return mSenderId;
    }

    public void setSenderId(Long senderId) {
        mSenderId = senderId;
    }

    public String getSenderMessage() {
        return mSenderMessage;
    }

    public void setSenderMessage(String senderMessage) {
        mSenderMessage = senderMessage;
    }

    public String getSenderName() {
        return mSenderName;
    }

    public void setSenderName(String senderName) {
        mSenderName = senderName;
    }

    public String getSendingTime() {
        return mSendingTime;
    }

    public void setSendingTime(String sendingTime) {
        mSendingTime = sendingTime;
    }

    public String getSessionToken() {
        return mSessionToken;
    }

    public void setSessionToken(String sessionToken) {
        mSessionToken = sessionToken;
    }

    public boolean isAdmin() {
        return isAdmin;
    }
}
