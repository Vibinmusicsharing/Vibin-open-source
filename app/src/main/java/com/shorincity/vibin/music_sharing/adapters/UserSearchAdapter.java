package com.shorincity.vibin.music_sharing.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.shorincity.vibin.music_sharing.R;
import com.shorincity.vibin.music_sharing.base.prefs.SharedPrefManager;
import com.shorincity.vibin.music_sharing.UI.custom.RoundedImageView;
import com.shorincity.vibin.music_sharing.model.APIResponse;
import com.shorincity.vibin.music_sharing.model.UserSearchModel;
import com.shorincity.vibin.music_sharing.service.DataAPI;
import com.shorincity.vibin.music_sharing.service.RetrofitAPI;
import com.shorincity.vibin.music_sharing.utils.AppConstants;
import com.shorincity.vibin.music_sharing.utils.GlideApp;
import com.shorincity.vibin.music_sharing.utils.Logging;

import java.util.ArrayList;

import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;
import retrofit2.Call;
import retrofit2.Callback;

public class UserSearchAdapter extends RecyclerView.Adapter<UserSearchAdapter.ExampleViewHolder> {

    private Context mContext;
    private ArrayList<UserSearchModel> list;
    private int playlistId;
    private boolean isCollab = false;

    public UserSearchAdapter(Context context, ArrayList<UserSearchModel> exampleList) {
        mContext = context;
        list = exampleList;
    }

    public UserSearchAdapter(Context context, ArrayList<UserSearchModel> exampleList, int playlistId, boolean isCollab) {
        mContext = context;
        list = exampleList;
        this.playlistId = playlistId;
        this.isCollab = isCollab;
    }

    @NonNull
    @Override
    public ExampleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_user_search, parent, false);
        return new ExampleViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ExampleViewHolder holder, int position) {
        UserSearchModel currentItem = list.get(position);
        ((ExampleViewHolder) holder).setData(currentItem, position);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class ExampleViewHolder extends RecyclerView.ViewHolder {

        public TextView mUsername, mFullname;
        public CardView card_likes;
        public CardView collabCv;
        public CircularProgressButton addCollabBtn;
        public Button addCollabStatusBtn;
        public RoundedImageView mImageView;

        public ExampleViewHolder(@NonNull View itemView) {
            super(itemView);
            //mImageView = itemView.findViewById(R.id.item_img);
            mUsername = itemView.findViewById(R.id.item_username);
            mFullname = itemView.findViewById(R.id.item_fullname);
            collabCv = itemView.findViewById(R.id.add_collab_card_view);
            addCollabBtn = itemView.findViewById(R.id.add_collab_btn);
            addCollabStatusBtn = itemView.findViewById(R.id.add_collab_status);
            mImageView = itemView.findViewById(R.id.avatar_image);
            card_likes = itemView.findViewById(R.id.card_likes);
        }

        public void setData(UserSearchModel currentItem, int position) {
            mUsername.setText("@" + currentItem.getUsername());
            mFullname.setText(currentItem.getFullname());

            card_likes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    customItemClickListener.onItemClick(view, position);
                }
            });
            if (isCollab)
                collabCv.setVisibility(View.VISIBLE);
            else
                collabCv.setVisibility(View.GONE);

            addCollabBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    customItemClickListener.onItemClick(v, position);
                    Logging.d("TEST", "Call ");
                    sendCollabRequestNotification(v, addCollabStatusBtn, playlistId, list.get(position).getId(), AppConstants.COLLAB_INVITE);// collab _invite
                }
            });

            String avatarUrl = currentItem.getAvatarLink();

            if (!TextUtils.isEmpty(avatarUrl)) {
                try {
                    GlideApp.with(mContext).load(avatarUrl).into(mImageView);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                mImageView.setImageResource(R.drawable.default_img_black);
            }
        }
    }

    public CustomItemClickListener getCustomItemClickListener() {
        return customItemClickListener;
    }

    public void setCustomItemClickListener(CustomItemClickListener customItemClickListener) {
        this.customItemClickListener = customItemClickListener;
    }

    CustomItemClickListener customItemClickListener;

    public interface CustomItemClickListener {
        void onItemClick(View v, int position);
    }


    private void sendCollabRequestNotification(View view, Button addCollabButtonStatus, int playlistId, int searchedUserId, String notifyType) {

        CircularProgressButton circularProgressButton = (CircularProgressButton) view;
        circularProgressButton.startAnimation();

        DataAPI dataAPI = RetrofitAPI.getData();
        String headerToken = AppConstants.TOKEN + SharedPrefManager.getInstance(mContext).getSharedPrefString(AppConstants.INTENT_USER_API_TOKEN);
        int userId = SharedPrefManager.getInstance(mContext).getSharedPrefInt(AppConstants.INTENT_USER_ID);

        Call<APIResponse> callback = dataAPI.sendNotification(headerToken, userId, searchedUserId, playlistId, notifyType);
        callback.enqueue(new Callback<APIResponse>() {
            @Override
            public void onResponse(Call<APIResponse> call, retrofit2.Response<APIResponse> response) {
                circularProgressButton.revertAnimation();
                circularProgressButton.setBackgroundResource(R.color.orange);
                Logging.d("User Search res-->" + new Gson().toJson(response.body()));
                if (response != null && response.body() != null) {

                    if (response.body().getStatus().equalsIgnoreCase("success")) {

                        circularProgressButton.setVisibility(View.GONE);
                        addCollabButtonStatus.setVisibility(View.VISIBLE);

                    } else if (response.body().getStatus().equalsIgnoreCase("failed")) {
                        Toast.makeText(mContext, response.body().getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                }
            }

            @Override
            public void onFailure(Call<APIResponse> call, Throwable t) {
                circularProgressButton.revertAnimation();
                circularProgressButton.setBackgroundResource(R.color.orange);
            }
        });
    }
}

