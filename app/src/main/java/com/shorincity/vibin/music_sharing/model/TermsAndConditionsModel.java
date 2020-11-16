package com.shorincity.vibin.music_sharing.model;

/**
 * Created by Aditya S.Gangasagar
 * On 09-August-2020
 **/
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TermsAndConditionsModel {

    @SerializedName("status") @Expose private String status;
    @SerializedName("details") @Expose private List<TermsAndConditionsDetailsModel> details = null;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<TermsAndConditionsDetailsModel> getDetails() {
        return details;
    }

    public void setDetails(List<TermsAndConditionsDetailsModel> details) {
        this.details = details;
    }

}