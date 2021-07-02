package com.shorincity.vibin.music_sharing.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class HomeYoutubeModel {

    @SerializedName("english_hits")
    @Expose
    private List<YoutubeCustomModel> englishHits = null;
    @SerializedName("bollywood")
    @Expose
    private List<YoutubeCustomModel> bollywood = null;
    @SerializedName("charts")
    @Expose
    private List<YoutubeCustomModel> charts = null;
    @SerializedName("punjabi")
    @Expose
    private List<YoutubeCustomModel> punjabi = null;
    @SerializedName("artists")
    @Expose
    private List<YoutubeCustomModel> artists = null;
    @SerializedName("others")
    @Expose
    private List<YoutubeCustomModel> others = null;

    @SerializedName("")
    public List<YoutubeCustomModel> getEnglishHits() {
        return englishHits;
    }

    public void setEnglishHits(List<YoutubeCustomModel> englishHits) {
        this.englishHits = englishHits;
    }

    public List<YoutubeCustomModel> getBollywood() {
        return bollywood;
    }

    public void setBollywood(List<YoutubeCustomModel> bollywood) {
        this.bollywood = bollywood;
    }

    public List<YoutubeCustomModel> getCharts() {
        return charts;
    }

    public void setCharts(List<YoutubeCustomModel> charts) {
        this.charts = charts;
    }

    public List<YoutubeCustomModel> getPunjabi() {
        return punjabi;
    }

    public void setPunjabi(List<YoutubeCustomModel> punjabi) {
        this.punjabi = punjabi;
    }

    public List<YoutubeCustomModel> getArtists() {
        return artists;
    }

    public void setArtists(List<YoutubeCustomModel> artists) {
        this.artists = artists;
    }

    public List<YoutubeCustomModel> getOthers() {
        return others;
    }

    public void setOthers(List<YoutubeCustomModel> others) {
        this.others = others;
    }

    public static class YoutubeCustomModel implements Serializable, Parcelable {
        public YoutubeCustomModel(String genre, String language, String url, String thumbnail) {
            this.genre = genre;
            this.language = language;
            this.url = url;
            this.thumbnail = thumbnail;
        }
        @SerializedName("genre")
        @Expose
        private String genre;
        @SerializedName("language")
        @Expose
        private String language;
        @SerializedName("url")
        @Expose
        private String url;

        protected YoutubeCustomModel(Parcel in) {
            genre = in.readString();
            language = in.readString();
            url = in.readString();
            thumbnail = in.readString();
        }

        public static final Creator<YoutubeCustomModel> CREATOR = new Creator<YoutubeCustomModel>() {
            @Override
            public YoutubeCustomModel createFromParcel(Parcel in) {
                return new YoutubeCustomModel(in);
            }

            @Override
            public YoutubeCustomModel[] newArray(int size) {
                return new YoutubeCustomModel[size];
            }
        };

        public String getThumbnail() {
            return thumbnail;
        }

        public void setThumbnail(String thumbnail) {
            this.thumbnail = thumbnail;
        }

        @SerializedName("thumbnail")
        @Expose
        private String thumbnail;

        public String getGenre() {
            return genre;
        }

        public void setGenre(String genre) {
            this.genre = genre;
        }

        public String getLanguage() {
            return language;
        }

        public void setLanguage(String language) {
            this.language = language;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeString(genre);
            parcel.writeString(language);
            parcel.writeString(url);
            parcel.writeString(thumbnail);
        }
    }


}