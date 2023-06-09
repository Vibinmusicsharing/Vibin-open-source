
package com.shorincity.vibin.music_sharing.model.realtime;

import com.google.gson.annotations.SerializedName;

public class RTConnect {

    @SerializedName("data")
    private Data mData;
    @SerializedName("status")
    private String mStatus;
    @SerializedName("type")
    private String mType;

    public Data getData() {
        return mData;
    }

    public void setData(Data data) {
        mData = data;
    }

    public String getStatus() {
        return mStatus;
    }

    public void setStatus(String status) {
        mStatus = status;
    }

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        mType = type;
    }

}
