
package com.shorincity.vibin.music_sharing.model.lastfm.trackinfo;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Toptags {

    @SerializedName("tag")
    private List<Tag> mTag;

    public List<Tag> getTag() {
        return mTag;
    }

    public void setTag(List<Tag> tag) {
        mTag = tag;
    }

}
