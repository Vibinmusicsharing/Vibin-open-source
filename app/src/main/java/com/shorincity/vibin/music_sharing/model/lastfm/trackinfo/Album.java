
package com.shorincity.vibin.music_sharing.model.lastfm.trackinfo;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Album {

    @SerializedName("artist")
    private String mArtist;
    @SerializedName("@attr")
    private Attr mAttr;
    @SerializedName("image")
    private List<Image> mImage;
    @SerializedName("mbid")
    private String mMbid;
    @SerializedName("title")
    private String mTitle;
    @SerializedName("url")
    private String mUrl;

    public String getArtist() {
        return mArtist;
    }

    public void setArtist(String artist) {
        mArtist = artist;
    }

    public Attr getAttr() {
        return mAttr;
    }

    public void setAttr(Attr attr) {
        mAttr = attr;
    }

    public List<Image> getImage() {
        return mImage;
    }

    public void setImage(List<Image> image) {
        mImage = image;
    }

    public String getMbid() {
        return mMbid;
    }

    public void setMbid(String mbid) {
        mMbid = mbid;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        mUrl = url;
    }

}
