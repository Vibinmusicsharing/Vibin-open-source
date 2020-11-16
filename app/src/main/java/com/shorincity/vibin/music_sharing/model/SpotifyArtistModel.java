package com.shorincity.vibin.music_sharing.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class SpotifyArtistModel implements Parcelable{

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

    protected SpotifyArtistModel(Parcel in) {
        href = in.readString();
        items = in.createTypedArrayList(Item.CREATOR);
        if (in.readByte() == 0) {
            limit = null;
        } else {
            limit = in.readInt();
        }
        next = in.readString();
        if (in.readByte() == 0) {
            offset = null;
        } else {
            offset = in.readInt();
        }
        if (in.readByte() == 0) {
            total = null;
        } else {
            total = in.readInt();
        }
    }

    public static final Creator<SpotifyArtistModel> CREATOR = new Creator<SpotifyArtistModel>() {
        @Override
        public SpotifyArtistModel createFromParcel(Parcel in) {
            return new SpotifyArtistModel(in);
        }

        @Override
        public SpotifyArtistModel[] newArray(int size) {
            return new SpotifyArtistModel[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(href);
        parcel.writeTypedList(items);
        if (limit == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(limit);
        }
        parcel.writeString(next);
        if (offset == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(offset);
        }
        if (total == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(total);
        }
    }


    public static class Artist implements Parcelable{

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

        protected Artist(Parcel in) {
            href = in.readString();
            id = in.readString();
            name = in.readString();
            type = in.readString();
            uri = in.readString();
        }

        public static final Creator<Artist> CREATOR = new Creator<Artist>() {
            @Override
            public Artist createFromParcel(Parcel in) {
                return new Artist(in);
            }

            @Override
            public Artist[] newArray(int size) {
                return new Artist[size];
            }
        };

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

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeString(href);
            parcel.writeString(id);
            parcel.writeString(name);
            parcel.writeString(type);
            parcel.writeString(uri);
        }
    }


    public static class ExternalUrls implements Parcelable{

        @SerializedName("spotify")
        @Expose
        private String spotify;

        protected ExternalUrls(Parcel in) {
            spotify = in.readString();
        }

        public static final Creator<ExternalUrls> CREATOR = new Creator<ExternalUrls>() {
            @Override
            public ExternalUrls createFromParcel(Parcel in) {
                return new ExternalUrls(in);
            }

            @Override
            public ExternalUrls[] newArray(int size) {
                return new ExternalUrls[size];
            }
        };

        public String getSpotify() {
            return spotify;
        }

        public void setSpotify(String spotify) {
            this.spotify = spotify;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeString(spotify);
        }
    }

    public static class ExternalUrls_ implements Parcelable {

        @SerializedName("spotify")
        @Expose
        private String spotify;

        protected ExternalUrls_(Parcel in) {
            spotify = in.readString();
        }

        public static final Creator<ExternalUrls_> CREATOR = new Creator<ExternalUrls_>() {
            @Override
            public ExternalUrls_ createFromParcel(Parcel in) {
                return new ExternalUrls_(in);
            }

            @Override
            public ExternalUrls_[] newArray(int size) {
                return new ExternalUrls_[size];
            }
        };

        public String getSpotify() {
            return spotify;
        }

        public void setSpotify(String spotify) {
            this.spotify = spotify;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeString(spotify);
        }
    }

    public static class Image implements Parcelable{

        @SerializedName("height")
        @Expose
        private Integer height;
        @SerializedName("url")
        @Expose
        private String url;
        @SerializedName("width")
        @Expose
        private Integer width;

        public Image(Parcel in) {
            if (in.readByte() == 0) {
                height = null;
            } else {
                height = in.readInt();
            }
            url = in.readString();
            if (in.readByte() == 0) {
                width = null;
            } else {
                width = in.readInt();
            }
        }

        public static final Creator<Image> CREATOR = new Creator<Image>() {
            @Override
            public Image createFromParcel(Parcel in) {
                return new Image(in);
            }

            @Override
            public Image[] newArray(int size) {
                return new Image[size];
            }
        };

        public Image() {

        }

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

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            if (height == null) {
                parcel.writeByte((byte) 0);
            } else {
                parcel.writeByte((byte) 1);
                parcel.writeInt(height);
            }
            parcel.writeString(url);
            if (width == null) {
                parcel.writeByte((byte) 0);
            } else {
                parcel.writeByte((byte) 1);
                parcel.writeInt(width);
            }
        }
    }

    public static class Item implements Parcelable
    {

        @SerializedName("album_group")
        @Expose
        private String albumGroup;
        @SerializedName("album_type")
        @Expose
        private String albumType;
        @SerializedName("artists")
        @Expose
        private List<Artist> artists = null;
        @SerializedName("available_markets")
        @Expose
        private List<String> availableMarkets = null;
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
        private List<Image> images = new ArrayList<Image>();
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

        public Item(Parcel in) {
            albumGroup = in.readString();
            albumType = in.readString();
            artists = in.createTypedArrayList(Artist.CREATOR);
            availableMarkets = in.createStringArrayList();
            externalUrls = in.readParcelable(ExternalUrls_.class.getClassLoader());
            href = in.readString();
            id = in.readString();
            images = in.createTypedArrayList(Image.CREATOR);
            name = in.readString();
            releaseDate = in.readString();
            releaseDatePrecision = in.readString();
            if (in.readByte() == 0) {
                totalTracks = null;
            } else {
                totalTracks = in.readInt();
            }
            type = in.readString();
            uri = in.readString();
        }

        public static final Creator<Item> CREATOR = new Creator<Item>() {
            @Override
            public Item createFromParcel(Parcel in) {
                return new Item(in);
            }

            @Override
            public Item[] newArray(int size) {
                return new Item[size];
            }
        };

        public Item() {

        }

        public String getAlbumGroup() {
            return albumGroup;
        }

        public void setAlbumGroup(String albumGroup) {
            this.albumGroup = albumGroup;
        }

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

        public List<String> getAvailableMarkets() {
            return availableMarkets;
        }

        public void setAvailableMarkets(List<String> availableMarkets) {
            this.availableMarkets = availableMarkets;
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

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeString(albumGroup);
            parcel.writeString(albumType);
            parcel.writeTypedList(artists);
            parcel.writeStringList(availableMarkets);
            parcel.writeParcelable(externalUrls, i);
            parcel.writeString(href);
            parcel.writeString(id);
            parcel.writeTypedList(images);
            parcel.writeString(name);
            parcel.writeString(releaseDate);
            parcel.writeString(releaseDatePrecision);
            if (totalTracks == null) {
                parcel.writeByte((byte) 0);
            } else {
                parcel.writeByte((byte) 1);
                parcel.writeInt(totalTracks);
            }
            parcel.writeString(type);
            parcel.writeString(uri);
        }
    }
}
