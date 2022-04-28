
package com.shorincity.vibin.music_sharing.model.realtime;

import com.google.gson.annotations.SerializedName;

public class ChatResponse {

    @SerializedName("message_data")
    private MessageData mMessageData;
    @SerializedName("type")
    private String mType;

    public MessageData getMessageData() {
        return mMessageData;
    }

    public void setMessageData(MessageData messageData) {
        mMessageData = messageData;
    }

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        mType = type;
    }

}
