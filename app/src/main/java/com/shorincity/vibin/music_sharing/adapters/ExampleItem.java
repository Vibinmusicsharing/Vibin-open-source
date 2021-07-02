package com.shorincity.vibin.music_sharing.adapters;
// example item class
public class ExampleItem {
    private String mImageUrl;
    private String mTitle,mShortDescription;
    private int mRating,mPrice;
    String uri,id,type;

    String playlistID;
    public ExampleItem(String imageUrl, String title, int rating, int price, String shortDescription){
        mImageUrl = imageUrl;
        mTitle = title;
        mRating = rating;
        mPrice = price;
        mShortDescription = shortDescription;
    }

    public ExampleItem(String mTitle, String playlistID) {
        this.mTitle = mTitle;
        this.playlistID = playlistID;
    }

    public ExampleItem(String mImageUrl, String mTitle, String uri) {
        this.mImageUrl = mImageUrl;
        this.mTitle = mTitle;
        this.uri = uri;
    }
    public ExampleItem(String mImageUrl, String mTitle, String id, String uri) {
        this.mImageUrl = mImageUrl;
        this.mTitle = mTitle;
        this.id = id;
        this.uri = uri;
    }


    public ExampleItem(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getPlaylistID() {
        return playlistID;
    }

    public void setPlaylistID(String playlistID) {
        this.playlistID = playlistID;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUri() {
        return uri;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getmImageUrl() {
        return mImageUrl;
    }

    public void setmImageUrl(String mImageUrl) {
        this.mImageUrl = mImageUrl;
    }



    public String getImageUrl(){
        return  mImageUrl;

    }
    public String getmShortDescription(){
        return  mShortDescription;

    }
    public int getPrice(){
        return  mPrice;

    }

    public String getmTitle(){
        return mTitle;
    }
    public int getRating(){
        return mRating;
    }

}
