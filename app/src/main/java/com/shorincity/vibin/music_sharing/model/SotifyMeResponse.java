package com.shorincity.vibin.music_sharing.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SotifyMeResponse {

    @SerializedName("country")
    @Expose
    private String country;
    @SerializedName("display_name")
    @Expose
    private String displayName;
    @SerializedName("explicit_content")
    @Expose
    private ExplicitContent explicitContent;
    @SerializedName("external_urls")
    @Expose
    private ExternalUrls externalUrls;
    @SerializedName("followers")
    @Expose
    private Followers followers;
    @SerializedName("href")
    @Expose
    private String href;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("images")
    @Expose
    private List<Object> images = null;
    @SerializedName("product")
    @Expose
    private String product;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("uri")
    @Expose
    private String uri;

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public ExplicitContent getExplicitContent() {
        return explicitContent;
    }

    public void setExplicitContent(ExplicitContent explicitContent) {
        this.explicitContent = explicitContent;
    }

    public ExternalUrls getExternalUrls() {
        return externalUrls;
    }

    public void setExternalUrls(ExternalUrls externalUrls) {
        this.externalUrls = externalUrls;
    }

    public Followers getFollowers() {
        return followers;
    }

    public void setFollowers(Followers followers) {
        this.followers = followers;
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

    public List<Object> getImages() {
        return images;
    }

    public void setImages(List<Object> images) {
        this.images = images;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
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

    public class ExplicitContent {

        @SerializedName("filter_enabled")
        @Expose
        private Boolean filterEnabled;
        @SerializedName("filter_locked")
        @Expose
        private Boolean filterLocked;

        public Boolean getFilterEnabled() {
            return filterEnabled;
        }

        public void setFilterEnabled(Boolean filterEnabled) {
            this.filterEnabled = filterEnabled;
        }

        public Boolean getFilterLocked() {
            return filterLocked;
        }

        public void setFilterLocked(Boolean filterLocked) {
            this.filterLocked = filterLocked;
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

    public class Followers {

        @SerializedName("href")
        @Expose
        private Object href;
        @SerializedName("total")
        @Expose
        private Integer total;

        public Object getHref() {
            return href;
        }

        public void setHref(Object href) {
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