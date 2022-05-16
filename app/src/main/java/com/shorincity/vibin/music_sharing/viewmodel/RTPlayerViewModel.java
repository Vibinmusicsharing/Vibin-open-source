package com.shorincity.vibin.music_sharing.viewmodel;

import android.content.Context;
import android.util.Log;

import com.shorincity.vibin.music_sharing.UI.SharedPrefManager;
import com.shorincity.vibin.music_sharing.callbackclick.PlaylistDetailCallback;
import com.shorincity.vibin.music_sharing.model.PlaylistDetailModel;
import com.shorincity.vibin.music_sharing.model.realtime.RTListner;
import com.shorincity.vibin.music_sharing.model.shareplaylist.PlaylistDetailResponse;
import com.shorincity.vibin.music_sharing.service.DataAPI;
import com.shorincity.vibin.music_sharing.service.RetrofitAPI;
import com.shorincity.vibin.music_sharing.utils.AppConstants;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;

public class RTPlayerViewModel {
    private static final String TAG = RTPlayerViewModel.class.getName();
    private ArrayList<PlaylistDetailModel> playlist;
    private ArrayList<RTListner> rtListnersList;
    private ArrayList<Object> chatList;
    private int currentIndex = 0;
    private int lengthms = 270000;
    private int elapsedSongTime = 0;
    private String songName, trackId = "";
    private boolean isStart = false, isNewMsg = false;
    private int userId;

    public RTPlayerViewModel() {
        playlist = new ArrayList<>();
        rtListnersList = new ArrayList<>();
        chatList = new ArrayList<>();
    }

    public void getPublicPlaylistDetail(Context context, String playlistID, PlaylistDetailCallback detailCallback) {

        DataAPI dataAPI = RetrofitAPI.getData();

        String token = AppConstants.TOKEN + SharedPrefManager.getInstance(context).getSharedPrefString(AppConstants.INTENT_USER_API_TOKEN);
        Call<PlaylistDetailResponse> callback = dataAPI.getPublicPlaylistDetail(token,
                SharedPrefManager.getInstance(context).getSharedPrefString(AppConstants.INTENT_USER_TOKEN),
                playlistID, AppConstants.SOURCE_TYPE_IN_APP);
        callback.enqueue(new Callback<PlaylistDetailResponse>() {
            @Override
            public void onResponse(Call<PlaylistDetailResponse> call, retrofit2.Response<PlaylistDetailResponse> response) {
                playlist.clear();
                if (response.body() != null && response.body().getStatus().equalsIgnoreCase("success")) {
                    playlist.addAll(response.body().getTracks());
                    detailCallback.onResponse();
                } else {
                    detailCallback.onError((response.body() != null && response.body().getMessage() != null) ? response.body().getMessage() : "Something went wrong!");
                }
            }

            @Override
            public void onFailure(Call<PlaylistDetailResponse> call, Throwable t) {
//                more_image.setVisibility(View.GONE);
                Log.d(TAG, "onFailure: " + t.getMessage());
                detailCallback.onError("Something went wrong!");
            }
        });
    }

    public ArrayList<PlaylistDetailModel> getPlaylist() {
        return playlist;
    }

    public ArrayList<RTListner> getRtListnersList() {
        return rtListnersList;
    }

    public void setRtListnersList(ArrayList<RTListner> rtListnersList) {
        this.rtListnersList = rtListnersList;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public void setCurrentIndex(int currentIndex) {
        if (currentIndex >= playlist.size()) {
            this.currentIndex = 0;
        } else if (currentIndex < 0)
            this.currentIndex = playlist.size() - 1;
        else
            this.currentIndex = currentIndex;
    }

    public int getLengthms() {
        return lengthms;
    }

    public void setLengthms(int lengthms) {
        this.lengthms = lengthms;
    }

    public int getElapsedSongTime() {
        return elapsedSongTime;
    }

    public void setElapsedSongTime(int elapsedSongTime) {
        this.elapsedSongTime = elapsedSongTime;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public ArrayList<Object> getChatList() {
        return chatList;
    }

    public void addChatResponse(Object chatObj) {
        chatList.add(chatObj);
    }

    public boolean isStart() {
        return isStart;
    }

    public void setStart(boolean start) {
        isStart = start;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public boolean isNewMsg() {
        return isNewMsg;
    }

    public void setNewMsg(boolean newMsg) {
        isNewMsg = newMsg;
    }

    public String getTrackId() {
        return trackId;
    }

    public void setTrackId(String trackId) {
        this.trackId = trackId;
    }
}
