package com.shorincity.vibin.music_sharing.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.like.LikeButton;
import com.shorincity.vibin.music_sharing.UI.SharedPrefManager;
import com.shorincity.vibin.music_sharing.fragment.PublicPlaylistFragment;
import com.shorincity.vibin.music_sharing.model.MyPlaylistModel;
import com.shorincity.vibin.music_sharing.R;
import com.shorincity.vibin.music_sharing.model.PlayListDeleteModel;
import com.shorincity.vibin.music_sharing.service.DataAPI;
import com.shorincity.vibin.music_sharing.service.RetrofitAPI;
import com.shorincity.vibin.music_sharing.utils.AppConstants;
import com.shorincity.vibin.music_sharing.utils.Utility;
import com.giphy.sdk.core.models.enums.RenditionType;
import com.giphy.sdk.ui.Giphy;
import com.giphy.sdk.ui.views.GifView;
import com.shorincity.vibin.music_sharing.youtube_files.Search;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;

public class UserPlaylistAdapter extends RecyclerView.Adapter<UserPlaylistAdapter.ExampleViewHolder> {

    private Context mContext;
    private ArrayList<MyPlaylistModel> list;
    PublicPlaylistFragment publicPlaylistFrag;
    Search searchFrag;

    public UserPlaylistAdapter(Context context, ArrayList<MyPlaylistModel> exampleList, PublicPlaylistFragment publicPlaylistFragment) {
        Giphy.INSTANCE.configure(context, AppConstants.GIPHY_API_KEY, true);
        mContext = context;
        list = exampleList;
        this.publicPlaylistFrag = publicPlaylistFragment;
    }

    public UserPlaylistAdapter(Context context, ArrayList<MyPlaylistModel> exampleList, Search search) {
        Giphy.INSTANCE.configure(context, AppConstants.GIPHY_API_KEY, true);
        mContext = context;
        list = exampleList;
        this.searchFrag = search;
    }

    @NonNull
    @Override
    public ExampleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_my_playlist, parent, false);

        final ExampleViewHolder mViewHolder = new ExampleViewHolder(v);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customItemClickListener.onItemClick(v, mViewHolder.getPosition());
            }
        });
        return mViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ExampleViewHolder holder, int position) {
        MyPlaylistModel currentItem = list.get(position);
        String name = currentItem.getName();
        String firstname = "";
        String[] fullNameArray = name.split("\\s+");
        if (fullNameArray.length > 1) {
            StringBuilder firstNameBuilder = new StringBuilder();
            for (int i = 0; i < fullNameArray.length - 1; i++) {
                firstNameBuilder.append(fullNameArray[i]);
                if (i != fullNameArray.length - 2) {
                    firstNameBuilder.append(" ");
                }
            }
            firstname = firstNameBuilder.toString();
        } else {
            firstname = fullNameArray[0];
        }

        holder.adminNameTv.setText(currentItem.getAdminName());

        holder.mTextViewTitle.setText(firstname);
        NumberFormat formatter = new DecimalFormat("00");
        String duration = "";
        holder.like_btn.setLiked(currentItem.isLikedByUser());

        if (currentItem.getPlaylistDurationHours() > 0) {
            duration = formatter.format(currentItem.getPlaylistDurationHours() * 60 + currentItem.getPlaylistDurationMinutes()) + ":" +
                    formatter.format(currentItem.getPlaylistDurationSeconds());
        } else if (currentItem.getPlaylistDurationMinutes() > 0) {
            duration = formatter.format(currentItem.getPlaylistDurationMinutes()) + ":" +
                    formatter.format(currentItem.getPlaylistDurationSeconds());
        } else if (currentItem.getPlaylistDurationSeconds() > 0) {
            duration = formatter.format(currentItem.getPlaylistDurationSeconds());
        } else
            duration = "00:00";

        holder.durationTv.setText(duration + " - ");

        if (!TextUtils.isEmpty(currentItem.getDescription())) {
            // holder.descTxt.setVisibility(View.VISIBLE);
            //holder.descTxt.setText(currentItem.getDescription());
        } else {
            // holder.descTxt.setVisibility(View.GONE);
        }

        if (currentItem.getLikes() > 0) {
            holder.likeCountTxt.setText(Utility.prettyCount(currentItem.getLikes()) + " Likes");
        } else {
            holder.likeCountTxt.setText("");
        }

        String gifArraySplit[] = currentItem.getGifLink().split("/");
        String mediaId = gifArraySplit[gifArraySplit.length - 1];

        holder.gifView.setMediaWithId(mediaId, RenditionType.preview, ContextCompat.getDrawable(mContext, R.color.light_gray));
        holder.pauseGifBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
                alertDialog.setTitle(mContext.getResources().getString(R.string.app_name));
                alertDialog.setMessage("Are You Sure Want To Delete Playlist");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                deletePlayList(currentItem.getId());


                            }
                        });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
        });
//        holder.pauseGifBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                GifView gifView = ((GifView) holder.gifView);
//                if (gifView.getTag() == null || (gifView.getTag() != null && gifView.getTag().equals("playing"))) {
//                    holder.pauseGifBtn.setColorFilter(ContextCompat.getColor(mContext, R.color.gph_white));
//                    gifView.setColorFilter(ContextCompat.getColor(mContext, R.color.gph_white), android.graphics.PorterDuff.Mode.MULTIPLY);
//                    gifView.pause();
//                    gifView.setTag("pause");
//                } else {
//                    holder.pauseGifBtn.setColorFilter(ContextCompat.getColor(mContext, R.color.light_gray));
//                    gifView.setColorFilter(ContextCompat.getColor(mContext, R.color.light_gray), android.graphics.PorterDuff.Mode.MULTIPLY);
//                    gifView.play();
//                    gifView.setTag("playing");
//                }
//            }
//        });
        holder.play_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCustomItemClickListener().onPlayClicked(v, position);

//                GifView gifView = ((GifView) holder.gifView);
//                if (gifView.getTag() == null || (gifView.getTag() != null && gifView.getTag().equals("playing"))) {
//                    holder.pauseGifBtn.setColorFilter(ContextCompat.getColor(mContext, R.color.gph_white));
//                    gifView.setColorFilter(ContextCompat.getColor(mContext, R.color.gph_white), android.graphics.PorterDuff.Mode.MULTIPLY);
//                    gifView.pause();
//                    gifView.setTag("pause");
//                } else {
//                    holder.pauseGifBtn.setColorFilter(ContextCompat.getColor(mContext, R.color.light_gray));
//                    gifView.setColorFilter(ContextCompat.getColor(mContext, R.color.light_gray), android.graphics.PorterDuff.Mode.MULTIPLY);
//                    gifView.play();
//                    gifView.setTag("playing");
//                }
            }
        });

        holder.like_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCustomItemClickListener().onLikeClick(v, position);
            }
        });


        String avatarUrl = currentItem.getAvatarLink();

        if (!TextUtils.isEmpty(avatarUrl)) {
            try {
                Glide.with(mContext).load(avatarUrl).into(holder.user_dp_iv);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void deletePlayList(Integer id) {
        DataAPI dataAPI = RetrofitAPI.getData();

        String userToken = SharedPrefManager.getInstance(mContext).getSharedPrefString(AppConstants.INTENT_USER_TOKEN);
        String userTokenapi = AppConstants.TOKEN + SharedPrefManager.getInstance(mContext).getSharedPrefString(AppConstants.INTENT_USER_API_TOKEN);
        Log.d("yash", "deletePlayList: " + userTokenapi);
        Call<PlayListDeleteModel> callback = dataAPI.getDeletePlaylist(userTokenapi, userToken, id);
//        J72EB5A5JM1K1QD6AIS6LG37
        callback.enqueue(new Callback<PlayListDeleteModel>() {
            @Override
            public void onResponse(Call<PlayListDeleteModel> call, retrofit2.Response<PlayListDeleteModel> response) {
                try {
                    System.out.println(response.body());
                    if (response.body().getStatus() != null) {
                        if (response.body().getStatus().equalsIgnoreCase("success")) {
                            Intent intent = new Intent();
                            intent.setAction("DeletePlaylist");
                            mContext.sendBroadcast(intent);
                            Toast.makeText(mContext, response.body().getMessage(), +2000).show();
                        } else {
                            Toast.makeText(mContext, response.body().getMessage(), +2000).show();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onFailure(Call<PlayListDeleteModel> call, Throwable t) {
                System.out.println(t.getMessage());
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class ExampleViewHolder extends RecyclerView.ViewHolder {

        public GifView gifView;
        public ImageView user_dp_iv, pauseGifBtn, img_more;
        public TextView mTextViewTitle, likeCountTxt, adminNameTv, durationTv;
        private Button play_btn;
        LikeButton like_btn;

        public ExampleViewHolder(@NonNull View itemView) {
            super(itemView);
            gifView = itemView.findViewById(R.id.gif_iv);
            user_dp_iv = itemView.findViewById(R.id.user_dp_iv);
            like_btn = itemView.findViewById(R.id.like_btn);
            pauseGifBtn = itemView.findViewById(R.id.pause_gif_btn);
            mTextViewTitle = itemView.findViewById(R.id.item_title);
            adminNameTv = itemView.findViewById(R.id.item_admin_name);
            durationTv = itemView.findViewById(R.id.item_duration);
            mTextViewTitle = itemView.findViewById(R.id.item_title);
            likeCountTxt = itemView.findViewById(R.id.like_count_txt);
            play_btn = itemView.findViewById(R.id.play_btn);

            // descTxt = itemView.findViewById(R.id.item_desc);
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
        public void onItemClick(View v, int position);

        public void onLikeClick(View v, int position);

        public void onPlayClicked(View v, int position);
    }
}
