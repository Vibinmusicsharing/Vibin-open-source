package com.shorincity.vibin.music_sharing.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.shorincity.vibin.music_sharing.model.MyPlaylistModel;
import com.shorincity.vibin.music_sharing.R;
import com.shorincity.vibin.music_sharing.utils.AppConstants;
import com.shorincity.vibin.music_sharing.utils.Utility;
import com.giphy.sdk.core.models.enums.RenditionType;
import com.giphy.sdk.ui.Giphy;
import com.giphy.sdk.ui.views.GifView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

public class UserPlaylistAdapter extends RecyclerView.Adapter<UserPlaylistAdapter.ExampleViewHolder> {

    private Context mContext;
    private ArrayList<MyPlaylistModel> list;

    public UserPlaylistAdapter(Context context, ArrayList<MyPlaylistModel> exampleList){
        Giphy.INSTANCE.configure(context, AppConstants.GIPHY_API_KEY, true);
        mContext = context;
        list = exampleList;
    }

    @NonNull
    @Override
    public ExampleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_my_playlist,parent,false);

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

        holder.adminNameTv.setText(currentItem.getAdminName());

        holder.mTextViewTitle.setText(name);
        NumberFormat formatter = new DecimalFormat("00");
        String duration = "";

        if(currentItem.getPlaylistDurationHours() > 0) {
            duration = formatter.format(currentItem.getPlaylistDurationHours()) + ":" +
                    formatter.format(currentItem.getPlaylistDurationMinutes()) + ":" +
                    formatter.format(currentItem.getPlaylistDurationSeconds());
        } else if(currentItem.getPlaylistDurationMinutes() > 0) {
            duration = formatter.format(currentItem.getPlaylistDurationMinutes()) + ":" +
                    formatter.format(currentItem.getPlaylistDurationSeconds());
        } else if (currentItem.getPlaylistDurationSeconds() > 0) {
            duration = formatter.format(currentItem.getPlaylistDurationSeconds());
        } else
            duration = "00:00";

        holder.durationTv.setText(duration);

        if (!TextUtils.isEmpty(currentItem.getDescription())) {
           // holder.descTxt.setVisibility(View.VISIBLE);
            //holder.descTxt.setText(currentItem.getDescription());
        } else {
           // holder.descTxt.setVisibility(View.GONE);
        }

        if (currentItem.getLikes() > 0) {
            holder.likeCountTxt.setText(Utility.prettyCount(currentItem.getLikes()));
        } else {
            holder.likeCountTxt.setText("");
        }

        String gifArraySplit[] = currentItem.getGifLink().split("/");
        String mediaId = gifArraySplit[gifArraySplit.length-1];

        holder.gifView.setMediaWithId(mediaId, RenditionType.preview, ContextCompat.getDrawable(mContext, R.color.light_gray));

        holder.pauseGifBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                GifView gifView = ((GifView)holder.gifView);
                if(gifView.getTag() == null || (gifView.getTag() != null && gifView.getTag().equals("playing"))) {
                    holder.pauseGifBtn.setColorFilter(ContextCompat.getColor(mContext, R.color.gph_white));
                    gifView.setColorFilter(ContextCompat.getColor(mContext, R.color.gph_white), android.graphics.PorterDuff.Mode.MULTIPLY);
                    gifView.pause();
                    gifView.setTag("pause");
                } else {
                    holder.pauseGifBtn.setColorFilter(ContextCompat.getColor(mContext, R.color.light_gray));
                    gifView.setColorFilter(ContextCompat.getColor(mContext, R.color.light_gray), android.graphics.PorterDuff.Mode.MULTIPLY);
                    gifView.play();
                    gifView.setTag("playing");
                }
            }
        });holder.play_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                GifView gifView = ((GifView)holder.gifView);
                if(gifView.getTag() == null || (gifView.getTag() != null && gifView.getTag().equals("playing"))) {
                    holder.pauseGifBtn.setColorFilter(ContextCompat.getColor(mContext, R.color.gph_white));
                    gifView.setColorFilter(ContextCompat.getColor(mContext, R.color.gph_white), android.graphics.PorterDuff.Mode.MULTIPLY);
                    gifView.pause();
                    gifView.setTag("pause");
                } else {
                    holder.pauseGifBtn.setColorFilter(ContextCompat.getColor(mContext, R.color.light_gray));
                    gifView.setColorFilter(ContextCompat.getColor(mContext, R.color.light_gray), android.graphics.PorterDuff.Mode.MULTIPLY);
                    gifView.play();
                    gifView.setTag("playing");
                }
            }
        });

        holder.like_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCustomItemClickListener().onLikeClick(v,position);
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

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class ExampleViewHolder extends RecyclerView.ViewHolder{

        public GifView gifView;
        public ImageView user_dp_iv, like_btn, pauseGifBtn;
        public TextView mTextViewTitle, likeCountTxt, adminNameTv, durationTv;
        private Button play_btn;
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
    }
}
