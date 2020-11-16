package com.shorincity.vibin.music_sharing.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
// youtube medium class
public class Medium {

@SerializedName("url")
@Expose
private String url;


public String getUrl() {
return url;
}

public void setUrl(String url) {
this.url = url;
}

}