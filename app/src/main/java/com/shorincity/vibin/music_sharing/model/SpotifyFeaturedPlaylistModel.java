package com.shorincity.vibin.music_sharing.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class SpotifyFeaturedPlaylistModel implements Serializable {

    @SerializedName("playlists")
    @Expose
    private Playlists playlists;

    public Playlists getPlaylists() {
        return playlists;
    }

    public void setPlaylists(Playlists playlists) {
        this.playlists = playlists;
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

    public class Image {

        @SerializedName("height")
        @Expose
        private Object height;
        @SerializedName("url")
        @Expose
        private String url;
        @SerializedName("width")
        @Expose
        private Object width;

        public Object getHeight() {
            return height;
        }

        public void setHeight(Object height) {
            this.height = height;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public Object getWidth() {
            return width;
        }

        public void setWidth(Object width) {
            this.width = width;
        }

    }

    public class Item implements Serializable{

        @SerializedName("collaborative")
        @Expose
        private Boolean collaborative;
        @SerializedName("description")
        @Expose
        private String description;
        @SerializedName("external_urls")
        @Expose
        private ExternalUrls externalUrls;
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
        @SerializedName("owner")
        @Expose
        private Owner owner;
        @SerializedName("primary_color")
        @Expose
        private Object primaryColor;
        @SerializedName("public")
        @Expose
        private Object _public;
        @SerializedName("snapshot_id")
        @Expose
        private String snapshotId;
        @SerializedName("tracks")
        @Expose
        private Tracks tracks;
        @SerializedName("type")
        @Expose
        private String type;
        @SerializedName("uri")
        @Expose
        private String uri;

        public Boolean getCollaborative() {
            return collaborative;
        }

        public void setCollaborative(Boolean collaborative) {
            this.collaborative = collaborative;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

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

        public Owner getOwner() {
            return owner;
        }

        public void setOwner(Owner owner) {
            this.owner = owner;
        }

        public Object getPrimaryColor() {
            return primaryColor;
        }

        public void setPrimaryColor(Object primaryColor) {
            this.primaryColor = primaryColor;
        }

        public Object getPublic() {
            return _public;
        }

        public void setPublic(Object _public) {
            this._public = _public;
        }

        public String getSnapshotId() {
            return snapshotId;
        }

        public void setSnapshotId(String snapshotId) {
            this.snapshotId = snapshotId;
        }

        public Tracks getTracks() {
            return tracks;
        }

        public void setTracks(Tracks tracks) {
            this.tracks = tracks;
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

    public class Owner {

        @SerializedName("display_name")
        @Expose
        private String displayName;
        @SerializedName("external_urls")
        @Expose
        private ExternalUrls_ externalUrls;
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

        public String getDisplayName() {
            return displayName;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
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

    public class Playlists {

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

    public class Tracks {

        @SerializedName("href")
        @Expose
        private String href;
        @SerializedName("total")
        @Expose
        private Integer total;

        public String getHref() {
            return href;
        }

        public void setHref(String href) {
            this.href = href;
        }

        public Integer getTotal() {
            return total;
        }

        public void setTotal(Integer total) {
            this.total = total;
        }

    }
}