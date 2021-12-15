
package com.shorincity.vibin.music_sharing.model.coverart;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class CoverArtImageResponse {

    @SerializedName("images")
    private List<Image> mImages;
    @SerializedName("release")
    private String mRelease;

    public List<Image> getImages() {
        return mImages;
    }

    public void setImages(List<Image> images) {
        mImages = images;
    }

    public String getRelease() {
        return mRelease;
    }

    public void setRelease(String release) {
        mRelease = release;
    }

}
