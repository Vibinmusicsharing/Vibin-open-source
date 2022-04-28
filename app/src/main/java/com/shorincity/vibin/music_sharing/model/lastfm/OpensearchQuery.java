
package com.shorincity.vibin.music_sharing.model.lastfm;

import com.google.gson.annotations.SerializedName;

public class OpensearchQuery {

    @SerializedName("role")
    private String mRole;
    @SerializedName("startPage")
    private String mStartPage;
    @SerializedName("#text")
    private String mText;

    public String getRole() {
        return mRole;
    }

    public void setRole(String role) {
        mRole = role;
    }

    public String getStartPage() {
        return mStartPage;
    }

    public void setStartPage(String startPage) {
        mStartPage = startPage;
    }

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        mText = text;
    }

}
