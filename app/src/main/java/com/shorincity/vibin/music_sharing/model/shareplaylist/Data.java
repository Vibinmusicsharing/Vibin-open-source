package com.shorincity.vibin.music_sharing.model.shareplaylist;

import com.google.gson.annotations.SerializedName;

public class Data{

	@SerializedName("is_enabled")
	private boolean isEnabled;

	@SerializedName("unique_id")
	private String uniqueId;

	@SerializedName("full_url")
	private String fullUrl;

	@SerializedName("user_id")
	private int userId;

	@SerializedName("generated_date")
	private String generatedDate;

	@SerializedName("playlist_id")
	private int playlistId;

	@SerializedName("qr_code")
	private String qrCode;

	@SerializedName("id")
	private int id;

	@SerializedName("total_collabs_using_link")
	private int totalCollabsUsingLink;

	public boolean isIsEnabled(){
		return isEnabled;
	}

	public String getUniqueId(){
		return uniqueId;
	}

	public String getFullUrl(){
		return fullUrl;
	}

	public int getUserId(){
		return userId;
	}

	public String getGeneratedDate(){
		return generatedDate;
	}

	public int getPlaylistId(){
		return playlistId;
	}

	public String getQrCode(){
		return qrCode;
	}

	public int getId(){
		return id;
	}

	public int getTotalCollabsUsingLink(){
		return totalCollabsUsingLink;
	}
}