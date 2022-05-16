package com.shorincity.vibin.music_sharing.model.realtime;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class RTListnerMain {

    @SerializedName("update_type")
    private String mUpdateType;

    @SerializedName("users")
    private ArrayList<RTListner> rtListnerList;

    public ArrayList<RTListner> getRtListnerList() {
        return rtListnerList;
    }
}
