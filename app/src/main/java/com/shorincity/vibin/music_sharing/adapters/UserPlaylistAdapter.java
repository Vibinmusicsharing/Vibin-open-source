package com.shorincity.vibin.music_sharing.adapters;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.giphy.sdk.core.models.enums.RenditionType;
import com.giphy.sdk.ui.Giphy;
import com.giphy.sdk.ui.views.GifView;
import com.google.android.material.button.MaterialButton;
import com.shorincity.vibin.music_sharing.R;
import com.shorincity.vibin.music_sharing.UI.SharedPrefManager;
import com.shorincity.vibin.music_sharing.model.MyPlaylistModel;
import com.shorincity.vibin.music_sharing.model.PlayListDeleteModel;
import com.shorincity.vibin.music_sharing.service.DataAPI;
import com.shorincity.vibin.music_sharing.service.RetrofitAPI;
import com.shorincity.vibin.music_sharing.utils.AppConstants;
import com.shorincity.vibin.music_sharing.utils.Utility;

import java.util.ArrayList;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;

public class UserPlaylistAdapter extends RecyclerView.Adapter<UserPlaylistAdapter.ExampleViewHolder> {

    private final Context mContext;
    private final ArrayList<MyPlaylistModel> list;
    private CustomItemClickListener customItemClickListener;

    public UserPlaylistAdapter(Context context, ArrayList<MyPlaylistModel> exampleList) {
        Utility.configGiphy(context);
        mContext = context;
        list = exampleList;
    }

    @NonNull
    @Override
    public ExampleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_my_playlist, parent, false);

        final ExampleViewHolder mViewHolder = new ExampleViewHolder(v);
        v.setOnClickListener(v1 -> customItemClickListener.onItemClick(v1, mViewHolder.getPosition()));
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

        holder.mTextViewTitle.setText(firstname);
        holder.like_btn.setSelected(currentItem.isLikedByUser());

        holder.durationTv.setText(String.format(Locale.US, "%d hour %d min", currentItem.getPlaylistDurationHours(), currentItem.getPlaylistDurationMinutes()));
        holder.itemSongs.setText(String.format(Locale.US, "%d songs", currentItem.getSongs()));


        String[] gifArraySplit = currentItem.getGifLink().split("/");
        String mediaId = gifArraySplit[gifArraySplit.length - 1];

        holder.gifView.setMediaWithId(mediaId, RenditionType.preview, ContextCompat.getDrawable(mContext, R.color.light_gray),null);
        String finalFirstname = firstname;
        holder.ivDelete.setOnClickListener(view -> {
            AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
            View view1 = LayoutInflater.from(holder.ivDelete.getContext()).inflate(R.layout.dialog_delete_playlist, null, false);
            alertDialog.setView(view1);
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            alertDialog.setCancelable(false);

            TextView tvTitle = view1.findViewById(R.id.tvTitle);
            MaterialButton btnCancel = view1.findViewById(R.id.btnCancel);
            MaterialButton btnDelete = view1.findViewById(R.id.btnDelete);

            tvTitle.setText(String.format("Are you sure you want to delete \"%s\" playlist?", finalFirstname));
            btnCancel.setOnClickListener(v -> alertDialog.dismiss());
            btnDelete.setOnClickListener(v -> {
                deletePlayList(v, currentItem.getId());
                alertDialog.dismiss();
            });
            alertDialog.show();
        });
        holder.play_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customItemClickListener.onPlayClicked(v, position);
            }
        });

        holder.like_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customItemClickListener.onLikeClick(v, position);
            }
        });
    }

    private void deletePlayList(View v, Integer id) {
        DataAPI dataAPI = RetrofitAPI.getData();

        String userToken = SharedPrefManager.getInstance(mContext).getSharedPrefString(AppConstants.INTENT_USER_TOKEN);
        String userTokenapi = AppConstants.TOKEN + SharedPrefManager.getInstance(mContext).getSharedPrefString(AppConstants.INTENT_USER_API_TOKEN);
        Log.d("yash", "deletePlayList: " + userTokenapi);

        ProgressDialog showMe = new ProgressDialog(v.getContext());
        showMe.setMessage("Please wait");
        showMe.setCancelable(true);
        showMe.setCanceledOnTouchOutside(false);
        showMe.show();


        Call<PlayListDeleteModel> callback = dataAPI.getDeletePlaylist(userTokenapi, userToken, id);
        callback.enqueue(new Callback<PlayListDeleteModel>() {
            @Override
            public void onResponse(Call<PlayListDeleteModel> call, retrofit2.Response<PlayListDeleteModel> response) {
                showMe.dismiss();
                try {
                    System.out.println(response.body());
                    if (response.body().getStatus() != null) {
                        if (response.body().getStatus().equalsIgnoreCase("success")) {
                            Intent intent = new Intent();
                            intent.setAction("DeletePlaylist");
                            mContext.sendBroadcast(intent);
                        }
                        Toast.makeText(mContext, response.body().getMessage(), +2000).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onFailure(Call<PlayListDeleteModel> call, Throwable t) {
                showMe.dismiss();
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
        public ImageView user_dp_iv, ivDelete, img_more;
        public TextView mTextViewTitle, likeCountTxt, adminNameTv, durationTv, itemSongs;
        private final ImageView play_btn;
        private final ImageView like_btn;

        public ExampleViewHolder(@NonNull View itemView) {
            super(itemView);
            gifView = itemView.findViewById(R.id.gif_iv);
            user_dp_iv = itemView.findViewById(R.id.user_dp_iv);
            like_btn = itemView.findViewById(R.id.ivLike);
            ivDelete = itemView.findViewById(R.id.ivDelete);
            mTextViewTitle = itemView.findViewById(R.id.item_title);
            adminNameTv = itemView.findViewById(R.id.item_admin_name);
            durationTv = itemView.findViewById(R.id.item_duration);
            mTextViewTitle = itemView.findViewById(R.id.item_title);
            likeCountTxt = itemView.findViewById(R.id.like_count_txt);
            itemSongs = itemView.findViewById(R.id.item_songs);
            play_btn = itemView.findViewById(R.id.play_btn);

        }
    }

    public void setCustomItemClickListener(CustomItemClickListener customItemClickListener) {
        this.customItemClickListener = customItemClickListener;
    }


    public interface CustomItemClickListener {
        void onItemClick(View v, int position);

        void onLikeClick(View v, int position);

        void onPlayClicked(View v, int position);
    }
}
