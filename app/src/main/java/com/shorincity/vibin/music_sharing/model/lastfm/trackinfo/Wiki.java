
package com.shorincity.vibin.music_sharing.model.lastfm.trackinfo;

import com.google.gson.annotations.SerializedName;

public class Wiki {

    @SerializedName("content")
    private String mContent;
    @SerializedName("published")
    private String mPublished;
    @SerializedName("summary")
    private String mSummary;

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        mContent = content;
    }

    public String getPublished() {
        return mPublished;
    }

    public void setPublished(String published) {
        mPublished = published;
    }

    public String getSummary() {
        return mSummary;
    }

    public void setSummary(String summary) {
        mSummary = summary;
    }

}
