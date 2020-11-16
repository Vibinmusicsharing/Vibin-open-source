package com.shorincity.vibin.music_sharing.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SpotifyPlaylistTracksModel {

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
    private Object previous;
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

    public Object getPrevious() {
        return previous;
    }

    public void setPrevious(Object previous) {
        this.previous = previous;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }


    public class AddedBy {

        @SerializedName("external_urls")
        @Expose
        private ExternalUrls externalUrls;
        @SerializedName("href")
        @Expose
        private String href;
        @SerializedName("id")
        @Expose
        private String id;
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

    public class Album {

        @SerializedName("album_type")
        @Expose
        private String albumType;
        @SerializedName("artists")
        @Expose
        private List<Artist> artists = null;
        @SerializedName("available_markets")
        @Expose
        private List<Object> availableMarkets = null;
        @SerializedName("external_urls")
        @Expose
        private ExternalUrls__ externalUrls;
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

        public List<Object> getAvailableMarkets() {
            return availableMarkets;
        }

        public void setAvailableMarkets(List<Object> availableMarkets) {
            this.availableMarkets = availableMarkets;
        }

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
        private ExternalUrls_ externalUrls;
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
        private ExternalUrls___ externalUrls;
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

    public class ExternalUrls____ {

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

        @SerializedName("added_at")
        @Expose
        private String addedAt;
        @SerializedName("added_by")
        @Expose
        private AddedBy addedBy;
        @SerializedName("is_local")
        @Expose
        private Boolean isLocal;
        @SerializedName("primary_color")
        @Expose
        private Object primaryColor;
        @SerializedName("track")
        @Expose
        private Track track;
        @SerializedName("video_thumbnail")
        @Expose
        private VideoThumbnail videoThumbnail;

        public String getAddedAt() {
            return addedAt;
        }

        public void setAddedAt(String addedAt) {
            this.addedAt = addedAt;
        }

        public AddedBy getAddedBy() {
            return addedBy;
        }

        public void setAddedBy(AddedBy addedBy) {
            this.addedBy = addedBy;
        }

        public Boolean getIsLocal() {
            return isLocal;
        }

        public void setIsLocal(Boolean isLocal) {
            this.isLocal = isLocal;
        }

        public Object getPrimaryColor() {
            return primaryColor;
        }

        public void setPrimaryColor(Object primaryColor) {
            this.primaryColor = primaryColor;
        }

        public Track getTrack() {
            return track;
        }

        public void setTrack(Track track) {
            this.track = track;
        }

        public VideoThumbnail getVideoThumbnail() {
            return videoThumbnail;
        }

        public void setVideoThumbnail(VideoThumbnail videoThumbnail) {
            this.videoThumbnail = videoThumbnail;
        }

    }


    public class Track {

        @SerializedName("album")
        @Expose
        private Album album;
        @SerializedName("artists")
        @Expose
        private List<Artist_> artists = null;
        @SerializedName("available_markets")
        @Expose
        private List<Object> availableMarkets = null;
        @SerializedName("disc_number")
        @Expose
        private Integer discNumber;
        @SerializedName("duration_ms")
        @Expose
        private Integer durationMs;
        @SerializedName("episode")
        @Expose
        private Boolean episode;
        @SerializedName("explicit")
        @Expose
        private Boolean explicit;
        @SerializedName("external_ids")
        @Expose
        private ExternalIds externalIds;
        @SerializedName("external_urls")
        @Expose
        private ExternalUrls____ externalUrls;
        @SerializedName("href")
        @Expose
        private String href;
        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("is_local")
        @Expose
        private Boolean isLocal;
        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("popularity")
        @Expose
        private Integer popularity;
        @SerializedName("preview_url")
        @Expose
        private Object previewUrl;
        @SerializedName("track")
        @Expose
        private Boolean track;
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

        public List<Object> getAvailableMarkets() {
            return availableMarkets;
        }

        public void setAvailableMarkets(List<Object> availableMarkets) {
            this.availableMarkets = availableMarkets;
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

        public Boolean getEpisode() {
            return episode;
        }

        public void setEpisode(Boolean episode) {
            this.episode = episode;
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

        public ExternalUrls____ getExternalUrls() {
            return externalUrls;
        }

        public void setExternalUrls(ExternalUrls____ externalUrls) {
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

        public Object getPreviewUrl() {
            return previewUrl;
        }

        public void setPreviewUrl(Object previewUrl) {
            this.previewUrl = previewUrl;
        }

        public Boolean getTrack() {
            return track;
        }

        public void setTrack(Boolean track) {
            this.track = track;
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

    public class VideoThumbnail {

        @SerializedName("url")
        @Expose
        private Object url;

        public Object getUrl() {
            return url;
        }

        public void setUrl(Object url) {
            this.url = url;
        }

    }
}