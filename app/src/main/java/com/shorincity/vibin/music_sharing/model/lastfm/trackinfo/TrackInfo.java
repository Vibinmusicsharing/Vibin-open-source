
package com.shorincity.vibin.music_sharing.model.lastfm.trackinfo;

import com.google.gson.annotations.SerializedName;

public class TrackInfo {

    @SerializedName("album")
    private Album mAlbum;
    @SerializedName("artist")
    private Artist mArtist;
    @SerializedName("duration")
    private String mDuration;
    @SerializedName("listeners")
    private String mListeners;
    @SerializedName("mbid")
    private String mMbid;
    @SerializedName("name")
    private String mName;
    @SerializedName("playcount")
    private String mPlaycount;
    @SerializedName("streamable")
    private Streamable mStreamable;
    @SerializedName("toptags")
    private Toptags mToptags;
    @SerializedName("url")
    private String mUrl;
    @SerializedName("wiki")
    private Wiki mWiki;

    public Album getAlbum() {
        return mAlbum;
    }

    public void setAlbum(Album album) {
        mAlbum = album;
    }

    public Artist getArtist() {
        return mArtist;
    }

    public void setArtist(Artist artist) {
        mArtist = artist;
    }

    public String getDuration() {
        return mDuration;
    }

    public void setDuration(String duration) {
        mDuration = duration;
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

    public String getPlaycount() {
        return mPlaycount;
    }

    public void setPlaycount(String playcount) {
        mPlaycount = playcount;
    }

    public Streamable getStreamable() {
        return mStreamable;
    }

    public void setStreamable(Streamable streamable) {
        mStreamable = streamable;
    }

    public Toptags getToptags() {
        return mToptags;
    }

    public void setToptags(Toptags toptags) {
        mToptags = toptags;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        mUrl = url;
    }

    public Wiki getWiki() {
        return mWiki;
    }

    public void setWiki(Wiki wiki) {
        mWiki = wiki;
    }

}
