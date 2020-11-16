package com.shorincity.vibin.music_sharing.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SignUpModel {

    @SerializedName("User_Created")
    @Expose
    private Boolean userCreated;
    @SerializedName("User_ID")
    @Expose
    private Integer userID;
    @SerializedName("added_additional_fields_user")
    @Expose
    private Boolean addedAdditionalFieldsUser;

    public Boolean getUserCreated() {
        return userCreated;
    }

    public void setUserCreated(Boolean userCreated) {
        this.userCreated = userCreated;
    }

    public Integer getUserID() {
        return userID;
    }

    public void setUserID(Integer userID) {
        this.userID = userID;
    }

    public Boolean getAddedAdditionalFieldsUser() {
        return addedAdditionalFieldsUser;
    }

    public void setAddedAdditionalFieldsUser(Boolean addedAdditionalFieldsUser) {
        this.addedAdditionalFieldsUser = addedAdditionalFieldsUser;
    }

}