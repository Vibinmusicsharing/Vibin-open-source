package com.shorincity.vibin.music_sharing.model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SpotifySearchModel {

    @SerializedName("tracks")
    @Expose
    private Tracks tracks;

    public Tracks getTracks() {
        return tracks;
    }

    public void setTracks(Tracks tracks) {
        this.tracks = tracks;
    }

    public class Album {

        @SerializedName("album_type")
        @Expose
        private String albumType;
        @SerializedName("artists")
        @Expose
        private List<Artist> artists = null;
        @SerializedName("external_urls")
        @Expose
        private ExternalUrls_ externalUrls;
        @SerializedName("href")
        @Expose
        private String href;
        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("images")
        @Expose
        private List<Image> images = null;
        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("release_date")
        @Expose
        private String releaseDate;
        @SerializedName("release_date_precision")
        @Expose
        private String releaseDatePrecision;
        @SerializedName("total_tracks")
        @Expose
        private Integer totalTracks;
        @SerializedName("type")
        @Expose
        private String type;
        @SerializedName("uri")
        @Expose
        private String uri;

        public String getAlbumType() {
            return albumType;
        }

        public void setAlbumType(String albumType) {
            this.albumType = albumType;
        }

        public List<Artist> getArtists() {
            return artists;
        }

        public void setArtists(List<Artist> artists) {
            this.artists = artists;
        }

        public ExternalUrls_ getExternalUrls() {
            return externalUrls;
        }

        public void setExternalUrls(ExternalUrls_ externalUrls) {
            this.externalUrls = externalUrls;
        }

        public String getHref() {
            return href;
        }

        public void setHref(String href) {
            this.href = href;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public List<Image> getImages() {
            return images;
        }

        public void setImages(List<Image> images) {
            this.images = images;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getReleaseDate() {
            return releaseDate;
        }

        public void setReleaseDate(String releaseDate) {
            this.releaseDate = releaseDate;
        }

        public String getReleaseDatePrecision() {
            return releaseDatePrecision;
        }

        public void setReleaseDatePrecision(String releaseDatePrecision) {
            this.releaseDatePrecision = releaseDatePrecision;
        }

        public Integer getTotalTracks() {
            return totalTracks;
        }

        public void setTotalTracks(Integer totalTracks) {
            this.totalTracks = totalTracks;
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

        public void setUri(String uri) {
            this.uri = uri;
        }

    }

    public class Artist {

        @SerializedName("external_urls")
        @Expose
        private ExternalUrls externalUrls;
        @SerializedName("href")
        @Expose
        private String href;
        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("type")
        @Expose
        private String type;
        @SerializedName("uri")
        @Expose
        private String uri;

        public ExternalUrls getExternalUrls() {
            return externalUrls;
        }

        public void setExternalUrls(ExternalUrls externalUrls) {
            this.externalUrls = externalUrls;
        }

        public String getHref() {
            return href;
        }

        public void setHref(String href) {
            this.href = href;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
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

        public void setUri(String uri) {
            this.uri = uri;
        }

    }

    public class Artist_ {

        @SerializedName("external_urls")
        @Expose
        private ExternalUrls__ externalUrls;
        @SerializedName("href")
        @Expose
        private String href;
        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("type")
        @Expose
        private String type;
        @SerializedName("uri")
        @Expose
        private String uri;

        public ExternalUrls__ getExternalUrls() {
            return externalUrls;
        }

        public void setExternalUrls(ExternalUrls__ externalUrls) {
            this.externalUrls = externalUrls;
        }

        public String getHref() {
            return href;
        }

        public void setHref(String href) {
            this.href = href;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
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

        public void setUri(String uri) {
            this.uri = uri;
        }

    }

    public class ExternalIds {

        @SerializedName("isrc")
        @Expose
        private String isrc;

        public String getIsrc() {
            return isrc;
        }

        public void setIsrc(String isrc) {
            this.isrc = isrc;
        }

    }

    public class ExternalUrls {

        @SerializedName("spotify")
        @Expose
        private String spotify;

        public String getSpotify() {
            return spotify;
        }

        public void setSpotify(String spotify) {
            this.spotify = spotify;
        }

    }

    public class ExternalUrls_ {

        @SerializedName("spotify")
        @Expose
        private String spotify;

        public String getSpotify() {
            return spotify;
        }

        public void setSpotify(String spotify) {
            this.spotify = spotify;
        }

    }

    public class ExternalUrls__ {

        @SerializedName("spotify")
        @Expose
        private String spotify;

        public String getSpotify() {
            return spotify;
        }

        public void setSpotify(String spotify) {
            this.spotify = spotify;
        }

    }

    public class ExternalUrls___ {

        @SerializedName("spotify")
        @Expose
        private String spotify;

        public String getSpotify() {
            return spotify;
        }

        public void setSpotify(String spotify) {
            this.spotify = spotify;
        }

    }

    public class Image {

        @SerializedName("height")
        @Expose
        private Integer height;
        @SerializedName("url")
        @Expose
        private String url;
        @SerializedName("width")
        @Expose
        private Integer width;

        public Integer getHeight() {
            return height;
        }

        public void setHeight(Integer height) {
            this.height = height;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public Integer getWidth() {
            return width;
        }

        public void setWidth(Integer width) {
            this.width = width;
        }

    }

    public class Item {

        @SerializedName("album")
        @Expose
        private Album album;
        @SerializedName("artists")
        @Expose
        private List<Artist_> artists = null;
        @SerializedName("disc_number")
        @Expose
        private Integer discNumber;
        @SerializedName("duration_ms")
        @Expose
        private Integer durationMs;
        @SerializedName("explicit")
        @Expose
        private Boolean explicit;
        @SerializedName("external_ids")
        @Expose
        private ExternalIds externalIds;
        @SerializedName("external_urls")
        @Expose
        private ExternalUrls___ externalUrls;
        @SerializedName("href")
        @Expose
        private String href;
        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("is_local")
        @Expose
        private Boolean isLocal;
        @SerializedName("is_playable")
        @Expose
        private Boolean isPlayable;
        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("popularity")
        @Expose
        private Integer popularity;
        @SerializedName("preview_url")
        @Expose
        private String previewUrl;
        @SerializedName("track_number")
        @Expose
        private Integer trackNumber;
        @SerializedName("type")
        @Expose
        private String type;
        @SerializedName("uri")
        @Expose
        private String uri;

        public Album getAlbum() {
            return album;
        }

        public void setAlbum(Album album) {
            this.album = album;
        }

        public List<Artist_> getArtists() {
            return artists;
        }

        public void setArtists(List<Artist_> artists) {
            this.artists = artists;
        }

        public Integer getDiscNumber() {
            return discNumber;
        }

        public void setDiscNumber(Integer discNumber) {
            this.discNumber = discNumber;
        }

        public Integer getDurationMs() {
            return durationMs;
        }

        public void setDurationMs(Integer durationMs) {
            this.durationMs = durationMs;
        }

        public Boolean getExplicit() {
            return explicit;
        }

        public void setExplicit(Boolean explicit) {
            this.explicit = explicit;
        }

        public ExternalIds getExternalIds() {
            return externalIds;
        }

        public void setExternalIds(ExternalIds externalIds) {
            this.externalIds = externalIds;
        }

        public ExternalUrls___ getExternalUrls() {
            return externalUrls;
        }

        public void setExternalUrls(ExternalUrls___ externalUrls) {
            this.externalUrls = externalUrls;
        }

        public String getHref() {
            return href;
        }

        public void setHref(String href) {
            this.href = href;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public Boolean getIsLocal() {
            return isLocal;
        }

        public void setIsLocal(Boolean isLocal) {
            this.isLocal = isLocal;
        }

        public Boolean getIsPlayable() {
            return isPlayable;
        }

        public void setIsPlayable(Boolean isPlayable) {
            this.isPlayable = isPlayable;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getPopularity() {
            return popularity;
        }

        public void setPopularity(Integer popularity) {
            this.popularity = popularity;
        }

        public String getPreviewUrl() {
            return previewUrl;
        }

        public void setPreviewUrl(String previewUrl) {
            this.previewUrl = previewUrl;
        }

        public Integer getTrackNumber() {
            return trackNumber;
        }

        public void setTrackNumber(Integer trackNumber) {
            this.trackNumber = trackNumber;
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

        public void setUri(String uri) {
            this.uri = uri;
        }

    }

    public class Tracks {

        @SerializedName("href")
        @Expose
        private String href;
        @SerializedName("items")
        @Expose
        private List<Item> items = null;
        @SerializedName("limit")
        @Expose
        private Integer limit;
        @SerializedName("next")
        @Expose
        private String next;
        @SerializedName("offset")
        @Expose
        private Integer offset;
        @SerializedName("previous")
        @Expose
        private String previous;
        @SerializedName("total")
        @Expose
        private Integer total;

        public String getHref() {
            return href;
        }

        public void setHref(String href) {
            this.href = href;
        }

        public List<Item> getItems() {
            return items;
        }

        public void setItems(List<Item> items) {
            this.items = items;
        }

        public Integer getLimit() {
            return limit;
        }

        public void setLimit(Integer limit) {
            this.limit = limit;
        }

        public String getNext() {
            return next;
        }

        public void setNext(String next) {
            this.next = next;
        }

        public Integer getOffset() {
            return offset;
        }

        public void setOffset(Integer offset) {
            this.offset = offset;
        }

        public String getPrevious() {
            return previous;
        }

        public void setPrevious(String previous) {
            this.previous = previous;
        }

        public Integer getTotal() {
            return total;
        }

        public void setTotal(Integer total) {
            this.total = total;
        }

    }
}