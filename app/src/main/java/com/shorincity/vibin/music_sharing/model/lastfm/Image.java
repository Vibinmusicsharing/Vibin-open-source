
package com.shorincity.vibin.music_sharing.model.lastfm;

import com.google.gson.annotations.SerializedName;

public class Image {

    @SerializedName("size")
    private String mSize;
    @SerializedName("#text")
    private String mText;

    public String getSize() {
        return mSize;
    }

    public void setSize(String size) {
        mSize = size;
    }

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        mText = text;
    }

}
