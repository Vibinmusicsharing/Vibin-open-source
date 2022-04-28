package com.shorincity.vibin.music_sharing.model;

import androidx.annotation.DrawableRes;

public class MenuSettingModel {
    private String title;
    private String subTitle;
    private int imageRes;
    private int type;

    public MenuSettingModel(String title, String subTitle, @DrawableRes int imageRes, int type) {
        this.title = title;
        this.subTitle = subTitle;
        this.imageRes = imageRes;
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public int getImageRes() {
        return imageRes;
    }

    public void setImageRes(int imageRes) {
        this.imageRes = imageRes;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
