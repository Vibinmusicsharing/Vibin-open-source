package com.shorincity.vibin.music_sharing.model;

public class CountryModel {
    private String name;
    private String country_code;
    private Integer country_image;

    public CountryModel(String name, String country_code, Integer country_image) {
        this.name = name;
        this.country_code = country_code;
        this.country_image = country_image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry_code() {
        return country_code;
    }

    public void setCountry_code(String country_code) {
        this.country_code = country_code;
    }

    public Integer getCountry_image() {
        return country_image;
    }

    public void setCountry_image(Integer country_image) {
        this.country_image = country_image;
    }
}
