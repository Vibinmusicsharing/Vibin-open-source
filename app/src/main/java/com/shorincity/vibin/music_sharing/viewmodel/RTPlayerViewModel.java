package com.shorincity.vibin.music_sharing.viewmodel;

import android.content.Context;
import android.util.Log;

import com.shorincity.vibin.music_sharing.UI.SharedPrefManager;
import com.shorincity.vibin.music_sharing.callbackclick.PlaylistDetailCallback;
import com.shorincity.vibin.music_sharing.model.PlaylistDetailModel;
import com.shorincity.vibin.music_sharing.model.realtime.RTListner;
import com.shorincity.vibin.music_sharing.service.DataAPI;
import com.shorincity.vibin.music_sharing.service.RetrofitAPI;
import com.shorincity.vibin.music_sharing.utils.AppConstants;
import com.shorincity.vibin.music_sharing.utils.Logging;

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
    private String songName;
    private boolean isStart = false;

    public RTPlayerViewModel() {
        playlist = new ArrayList<>();
        rtListnersList = new ArrayList<>();
        chatList = new ArrayList<>();
    }

    public void getPublicPlaylistDetail(Context context, String playlistID, PlaylistDetailCallback detailCallback) {

        DataAPI dataAPI = RetrofitAPI.getData();

        String token = AppConstants.TOKEN + SharedPrefManager.getInstance(context).getSharedPrefString(AppConstants.INTENT_USER_API_TOKEN);
        Call<ArrayList<PlaylistDetailModel>> callback = dataAPI.getPublicPlaylistDetail(token,
                SharedPrefManager.getInstance(context).getSharedPrefString(AppConstants.INTENT_USER_TOKEN),
                playlistID);
        callback.enqueue(new Callback<ArrayList<PlaylistDetailModel>>() {
            @Override
            public void onResponse(Call<ArrayList<PlaylistDetailModel>> call, retrofit2.Response<ArrayList<PlaylistDetailModel>> response) {
                playlist.clear();
                if (response != null && response.body() != null && response.body().size() > 0) {
                    playlist.addAll(response.body());
                    detailCallback.onResponse();
                } else {
                    detailCallback.onError();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<PlaylistDetailModel>> call, Throwable t) {
//                more_image.setVisibility(View.GONE);
                Log.d(TAG, "onFailure: " + t.getMessage());
                detailCallback.onError();
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
}
