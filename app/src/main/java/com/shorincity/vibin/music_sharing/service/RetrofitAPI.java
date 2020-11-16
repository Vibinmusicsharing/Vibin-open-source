package com.shorincity.vibin.music_sharing.service;
// youtube retrofit api
public class RetrofitAPI {
    public static final String Url = "https://www.googleapis.com/youtube/v3/";
    //public static final String Url = AppConstants.BASE_URL+"";

    public static final String YOUTUBE_Url = "https://www.googleapis.com/youtube/v3/";
    public static DataAPI getYoutubeData() {
        return Retrofitlnit.getclient(YOUTUBE_Url).create(DataAPI.class);
    }

    public static final String SPOTIFY_Url = "https://api.spotify.com/v1/";
    public static DataAPI getSpotifydata() {
        return Retrofitlnit.getclient(SPOTIFY_Url).create(DataAPI.class);
    }

    public static String BASE_URL = "https://harshkant882.pythonanywhere.com/";
    public static DataAPI getData() {
        return Retrofitlnit.getclient(BASE_URL).create(DataAPI.class);
    }
}
