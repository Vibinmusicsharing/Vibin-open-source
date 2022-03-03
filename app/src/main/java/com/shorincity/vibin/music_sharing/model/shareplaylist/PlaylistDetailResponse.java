package com.shorincity.vibin.music_sharing.model.shareplaylist;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.shorincity.vibin.music_sharing.model.MyPlaylistModel;
import com.shorincity.vibin.music_sharing.model.PlaylistDetailModel;

public class PlaylistDetailResponse{

	@SerializedName("is_collaborator")
	private boolean isCollaborator;

	@SerializedName("tracks")
	private List<PlaylistDetailModel> tracks;

	@SerializedName("status")
	private String status;

	@SerializedName("playlist_details")
	private MyPlaylistModel myPlaylistModel;

	@SerializedName("message")
	@Expose
	private String message;

	public boolean isIsCollaborator(){
		return isCollaborator;
	}

	public List<PlaylistDetailModel> getTracks(){
		return tracks;
	}

	public String getStatus(){
		return status;
	}

	public MyPlaylistModel getMyPlaylistModel() {
		return myPlaylistModel;
	}

	public String getMessage() {
		return message;
	}
}