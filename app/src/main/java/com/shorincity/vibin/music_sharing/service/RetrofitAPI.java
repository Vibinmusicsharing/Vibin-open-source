package com.shorincity.vibin.music_sharing.service;

// youtube retrofit api
public class RetrofitAPI {

    private static final String YOUTUBE_Url = "https://www.googleapis.com/youtube/v3/";

    public static DataAPI getYoutubeData() {
        return Retrofitlnit.getclient(YOUTUBE_Url).create(DataAPI.class);
    }

    private static final String LAST_FM_URL = "http://ws.audioscrobbler.com/";

    public static DataAPI getLastFMData() {
        return Retrofitlnit.getclient(LAST_FM_URL).create(DataAPI.class);
    }

    private static final String COVER_ART_URL = "https://coverartarchive.org";

    public static DataAPI getCoverArtData() {
        return Retrofitlnit.getclient(COVER_ART_URL).create(DataAPI.class);
    }


    public static final String SPOTIFY_Url = "https://api.spotify.com/v1/";

    public static DataAPI getSpotifydata() {
        return Retrofitlnit.getclient(SPOTIFY_Url).create(DataAPI.class);
    }

    public static String BASE_URL = "https://staging.vibin.in/";
    public static DataAPI getData() {
        return Retrofitlnit.getclient(BASE_URL).create(DataAPI.class);
    }

}
