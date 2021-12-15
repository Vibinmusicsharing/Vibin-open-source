package com.shorincity.vibin.music_sharing.viewmodel;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.shorincity.vibin.music_sharing.UI.SharedPrefManager;
import com.shorincity.vibin.music_sharing.adapters.ViewCollab;
import com.shorincity.vibin.music_sharing.callbackclick.PlaylistDetailCallback;
import com.shorincity.vibin.music_sharing.fragment.PlaylistDetailFragmentNew;
import com.shorincity.vibin.music_sharing.model.PlaylistDetailModel;
import com.shorincity.vibin.music_sharing.service.DataAPI;
import com.shorincity.vibin.music_sharing.service.RetrofitAPI;
import com.shorincity.vibin.music_sharing.utils.AppConstants;
import com.shorincity.vibin.music_sharing.utils.Logging;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class PlaylistDetailsViewModel implements Parcelable {
    private static final String TAG = PlaylistDetailsViewModel.class.getName();
    private ArrayList<PlaylistDetailModel> playlist;
    private ArrayList<ViewCollab> viewcollabList;
    private boolean isAdmin = false;

    public PlaylistDetailsViewModel() {
        playlist = new ArrayList<>();
        viewcollabList = new ArrayList<>();
        viewcollabList.add(null);
    }

    protected PlaylistDetailsViewModel(Parcel in) {
        playlist = in.createTypedArrayList(PlaylistDetailModel.CREATOR);
        viewcollabList = in.createTypedArrayList(ViewCollab.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(playlist);
        dest.writeTypedList(viewcollabList);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PlaylistDetailsViewModel> CREATOR = new Creator<PlaylistDetailsViewModel>() {
        @Override
        public PlaylistDetailsViewModel createFromParcel(Parcel in) {
            return new PlaylistDetailsViewModel(in);
        }

        @Override
        public PlaylistDetailsViewModel[] newArray(int size) {
            return new PlaylistDetailsViewModel[size];
        }
    };

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
                Logging.d("Avatar response-->" + response.body());
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

    public void getCollaboratorsList(Context mContext, String playlistId, PlaylistDetailCallback detailCallback) {

        DataAPI dataAPI = RetrofitAPI.getData();

        String token = AppConstants.TOKEN + SharedPrefManager.getInstance(mContext).getSharedPrefString(AppConstants.INTENT_USER_API_TOKEN);
        Call<List<ViewCollab>> callback = dataAPI.getCollaboratorsList(token, SharedPrefManager.getInstance(mContext).getSharedPrefString(AppConstants.INTENT_USER_TOKEN), playlistId);
        callback.enqueue(new Callback<List<ViewCollab>>() {
            @Override
            public void onResponse(Call<List<ViewCollab>> call, retrofit2.Response<List<ViewCollab>> response) {
                Logging.d("Avatar response-->" + response.body());
                if (response.body() != null && response.body().size() > 0) {
                    viewcollabList.clear();
                    viewcollabList.addAll(response.body());
                    if (isAdmin)
                        viewcollabList.add(null);
                    detailCallback.onResponse();
                } else {
                    detailCallback.onError();
                }
            }

            @Override
            public void onFailure(Call<List<ViewCollab>> call, Throwable t) {
//                more_image.setVisibility(View.GONE);
                detailCallback.onError();
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

    public ArrayList<PlaylistDetailModel> getPlaylist() {
        return playlist;
    }

    public ArrayList<ViewCollab> getViewcollabList() {
        return viewcollabList;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }
}
