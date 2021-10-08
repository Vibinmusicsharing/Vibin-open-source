package com.shorincity.vibin.music_sharing.model;

import androidx.annotation.DrawableRes;

public class MusicLanguageModel {
    private int imgUnselected;
    private int imgSelected;
    private String name;
    private boolean isSelected;

    public MusicLanguageModel(@DrawableRes int imgUnselected, @DrawableRes int imgSelected, String name, boolean isSelected) {
        this.imgUnselected = imgUnselected;
        this.imgSelected = imgSelected;
        this.name = name;
        this.isSelected = isSelected;
    }

    public int getImgUnselected() {
        return imgUnselected;
    }

    public String getName() {
        return name;
    }

    public int getImgSelected() {
        return imgSelected;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
