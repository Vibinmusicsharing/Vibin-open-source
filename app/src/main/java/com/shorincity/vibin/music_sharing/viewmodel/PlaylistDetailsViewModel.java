package com.shorincity.vibin.music_sharing.viewmodel;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

import com.shorincity.vibin.music_sharing.UI.SharedPrefManager;
import com.shorincity.vibin.music_sharing.adapters.ViewCollab;
import com.shorincity.vibin.music_sharing.callbackclick.PlaylistDetailCallback;
import com.shorincity.vibin.music_sharing.model.APIResponse;
import com.shorincity.vibin.music_sharing.model.PlaylistDetailModel;
import com.shorincity.vibin.music_sharing.model.shareplaylist.PlaylistDetailResponse;
import com.shorincity.vibin.music_sharing.service.DataAPI;
import com.shorincity.vibin.music_sharing.service.RetrofitAPI;
import com.shorincity.vibin.music_sharing.utils.AppConstants;
import com.shorincity.vibin.music_sharing.utils.Logging;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlaylistDetailsViewModel implements Parcelable {
    private static final String TAG = PlaylistDetailsViewModel.class.getName();
    private ArrayList<PlaylistDetailModel> playlist;
    private ArrayList<ViewCollab> viewcollabList;
    private boolean isAdmin = false;
    private boolean isCollaborator = false;
    private String playlistID = "";
    private String sourceType = AppConstants.SOURCE_TYPE_IN_APP;
    private PlaylistDetailResponse playlistDetailResponse;
    private int searchUserId = -1;

    public PlaylistDetailsViewModel() {
        playlist = new ArrayList<>();
        viewcollabList = new ArrayList<>();
        viewcollabList.add(null);
    }

    protected PlaylistDetailsViewModel(Parcel in) {
        playlist = in.createTypedArrayList(PlaylistDetailModel.CREATOR);
        viewcollabList = in.createTypedArrayList(ViewCollab.CREATOR);
        isAdmin = in.readByte() != 0;
        isCollaborator = in.readByte() != 0;
        playlistID = in.readString();
        sourceType = in.readString();
        searchUserId = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(playlist);
        dest.writeTypedList(viewcollabList);
        dest.writeByte((byte) (isAdmin ? 1 : 0));
        dest.writeByte((byte) (isCollaborator ? 1 : 0));
        dest.writeString(playlistID);
        dest.writeString(sourceType);
        dest.writeInt(searchUserId);
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

    public void getPublicPlaylistDetail(Context context, PlaylistDetailCallback detailCallback) {

        DataAPI dataAPI = RetrofitAPI.getData();

        String token = AppConstants.TOKEN + SharedPrefManager.getInstance(context).getSharedPrefString(AppConstants.INTENT_USER_API_TOKEN);
        Call<PlaylistDetailResponse> callback = dataAPI.getPublicPlaylistDetail(token,
                SharedPrefManager.getInstance(context).getSharedPrefString(AppConstants.INTENT_USER_TOKEN),
                playlistID, sourceType);
        callback.enqueue(new Callback<PlaylistDetailResponse>() {
            @Override
            public void onResponse(Call<PlaylistDetailResponse> call, retrofit2.Response<PlaylistDetailResponse> response) {
                playlist.clear();
                if (response.body() != null && response.body().getStatus().equalsIgnoreCase("success")) {
                    playlistDetailResponse = response.body();
                    playlist.addAll(response.body().getTracks());
                    isCollaborator = response.body().isIsCollaborator();
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

    public void callAddCollabFromQR(Context context, String qrCode, PlaylistDetailCallback detailCallback) {
        DataAPI dataAPI = RetrofitAPI.getData();
        String token = AppConstants.TOKEN + SharedPrefManager.getInstance(context).getSharedPrefString(AppConstants.INTENT_USER_API_TOKEN);

        Call<PlaylistDetailResponse> callback = dataAPI.callAddCollabFromQR(token,
                SharedPrefManager.getInstance(context).getSharedPrefString(AppConstants.INTENT_USER_TOKEN),
                qrCode);
        callback.enqueue(new Callback<PlaylistDetailResponse>() {
            @Override
            public void onResponse(Call<PlaylistDetailResponse> call, retrofit2.Response<PlaylistDetailResponse> response) {
                playlist.clear();
                if (response.body() != null && response.body().getStatus().equalsIgnoreCase("success")) {
                    playlist.addAll(response.body().getTracks());
                    isCollaborator = response.body().isIsCollaborator();
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
                    detailCallback.onError("Something went wrong!");
                }
            }

            @Override
            public void onFailure(Call<List<ViewCollab>> call, Throwable t) {
//                more_image.setVisibility(View.GONE);
                detailCallback.onError("Something went wrong!");
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

    public void sendCollabRequestNotification(Context mContext, int playlistId, int searchedUserId) {

        DataAPI dataAPI = RetrofitAPI.getData();
        String headerToken = AppConstants.TOKEN + SharedPrefManager.getInstance(mContext).getSharedPrefString(AppConstants.INTENT_USER_API_TOKEN);
        int userId = SharedPrefManager.getInstance(mContext).getSharedPrefInt(AppConstants.INTENT_USER_ID);

        Call<APIResponse> callback = dataAPI.sendNotification(headerToken, userId, searchedUserId, playlistId, AppConstants.COLLAB_REQUEST);
        callback.enqueue(new Callback<APIResponse>() {
            @Override
            public void onResponse(Call<APIResponse> call, retrofit2.Response<APIResponse> response) {

                if (response.body() != null) {

                    if (response.body().getStatus().equalsIgnoreCase("success")) {
                        Toast.makeText(mContext, response.body().getMessage(), Toast.LENGTH_LONG).show();
                    } else if (response.body().getStatus().equalsIgnoreCase("failed")) {
                        Toast.makeText(mContext, response.body().getMessage(),
                                Toast.LENGTH_LONG).show();

                    }
                } else {
                }
            }

            @Override
            public void onFailure(Call<APIResponse> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

    public void callPinPlayList(Context mContext, int playlistId, String pinType, PlaylistDetailCallback detailCallback) {
        DataAPI dataAPI = RetrofitAPI.getData();
        String token = AppConstants.TOKEN + SharedPrefManager.getInstance(mContext).getSharedPrefString(AppConstants.INTENT_USER_API_TOKEN);
        dataAPI.callPinPlayList(token, SharedPrefManager.getInstance(mContext).getSharedPrefString(AppConstants.INTENT_USER_TOKEN),
                playlistId, pinType).enqueue(new Callback<APIResponse>() {
            @Override
            public void onResponse(Call<APIResponse> call, Response<APIResponse> response) {
                if (response.body() != null) {

                    if (response.body().getStatus().equalsIgnoreCase("success")) {
                        Toast.makeText(mContext, response.body().getMessage(), Toast.LENGTH_LONG).show();
                    } else if (response.body().getStatus().equalsIgnoreCase("failed")) {
                        Toast.makeText(mContext, response.body().getMessage(),
                                Toast.LENGTH_LONG).show();

                    }
                } else {
                }
            }

            @Override
            public void onFailure(Call<APIResponse> call, Throwable t) {

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

    public boolean isCollaborator() {
        return isCollaborator;
    }

    public String getPlaylistID() {
        return playlistID;
    }

    public void setPlaylistID(String playlistID) {
        this.playlistID = playlistID;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public PlaylistDetailResponse getPlaylistDetailResponse() {
        return playlistDetailResponse;
    }

    public int getSearchUserId() {
        return searchUserId;
    }

    public void setSearchUserId(int searchUserId) {
        this.searchUserId = searchUserId;
    }
}
