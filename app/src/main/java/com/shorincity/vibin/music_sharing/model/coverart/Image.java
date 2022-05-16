
package com.shorincity.vibin.music_sharing.model.coverart;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Image {

    @SerializedName("approved")
    private Boolean mApproved;
    @SerializedName("back")
    private Boolean mBack;
    @SerializedName("comment")
    private String mComment;
    @SerializedName("edit")
    private Long mEdit;
    @SerializedName("front")
    private Boolean mFront;
    @SerializedName("id")
    private Long mId;
    @SerializedName("image")
    private String mImage;
    @SerializedName("types")
    private List<String> mTypes;

    public Boolean getApproved() {
        return mApproved;
    }

    public void setApproved(Boolean approved) {
        mApproved = approved;
    }

    public Boolean getBack() {
        return mBack;
    }

    public void setBack(Boolean back) {
        mBack = back;
    }

    public String getComment() {
        return mComment;
    }

    public void setComment(String comment) {
        mComment = comment;
    }

    public Long getEdit() {
        return mEdit;
    }

    public void setEdit(Long edit) {
        mEdit = edit;
    }

    public Boolean getFront() {
        return mFront;
    }

    public void setFront(Boolean front) {
        mFront = front;
    }

    public Long getId() {
        return mId;
    }

    public void setId(Long id) {
        mId = id;
    }

    public String getImage() {
        return mImage;
    }

    public void setImage(String image) {
        mImage = image;
    }

    public List<String> getTypes() {
        return mTypes;
    }

    public void setTypes(List<String> types) {
        mTypes = types;
    }

}
