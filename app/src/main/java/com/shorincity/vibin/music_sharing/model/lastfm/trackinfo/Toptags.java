
package com.shorincity.vibin.music_sharing.model.lastfm.trackinfo;

import java.util.List;
import com.google.gson.annotations.SerializedName;

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
