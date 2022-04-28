
package com.shorincity.vibin.music_sharing.model.lastfm;

import com.google.gson.annotations.SerializedName;
import com.shorincity.vibin.music_sharing.model.ModelData;

import java.util.List;

public class Track {

    @SerializedName("artist")
    private String mArtist;
    @SerializedName("image")
    private List<Image> mImage;
    @SerializedName("listeners")
    private String mListeners;
    @SerializedName("mbid")
    private String mMbid;
    @SerializedName("name")
    private String mName;
    @SerializedName("streamable")
    private String mStreamable;
    @SerializedName("url")
    private String mUrl;

    private ModelData modelData;

    public String getArtist() {
        return mArtist;
    }

    public void setArtist(String artist) {
        mArtist = artist;
    }

    public List<Image> getImage() {
        return mImage;
    }

    public void setImage(List<Image> image) {
        mImage = image;
    }

    public String getListeners() {
        return mListeners;
    }

    public void setListeners(String listeners) {
        mListeners = listeners;
    }

    public String getMbid() {
        return mMbid;
    }

    public void setMbid(String mbid) {
        mMbid = mbid;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getStreamable() {
        return mStreamable;
    }

    public void setStreamable(String streamable) {
        mStreamable = streamable;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        mUrl = url;
    }

    public ModelData getModelData() {
        return modelData;
    }

    public void setModelData(ModelData modelData) {
        this.modelData = modelData;
    }
}
