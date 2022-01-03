package com.shorincity.vibin.music_sharing.adapters;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.daimajia.swipe.SwipeLayout;
import com.shorincity.vibin.music_sharing.R;
import com.shorincity.vibin.music_sharing.databinding.PlaylistCollabMenuBinding;
import com.shorincity.vibin.music_sharing.databinding.PlaylistSongMenuBinding;
import com.shorincity.vibin.music_sharing.model.PlaylistDetailModel;
import com.shorincity.vibin.music_sharing.utils.AppConstants;
import com.shorincity.vibin.music_sharing.utils.CommonUtils;

import java.util.ArrayList;

public class PlaylistSongsAdapter extends RecyclerView.Adapter<PlaylistSongsAdapter.ExampleViewHolder> {

    private Context mContext;
    private ArrayList<PlaylistDetailModel> list;
    private CustomItemClickListener customItemClickListener;
    private boolean isRealTime;
    private String trackId = "";

    public PlaylistSongsAdapter(Context context, ArrayList<PlaylistDetailModel> exampleList, boolean isRealTime, CustomItemClickListener customItemClickListener) {
        mContext = context;
        list = exampleList;
        this.isRealTime = isRealTime;
        this.customItemClickListener = customItemClickListener;
    }

    @NonNull
    @Override
    public PlaylistSongsAdapter.ExampleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_playlist_song, parent, false);
        return new ExampleViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistSongsAdapter.ExampleViewHolder holder, int position) {
        PlaylistDetailModel currentItem = list.get(position);

        holder.tvSongName.setText(currentItem.getName());
        holder.tvArtist.setText(currentItem.getArtistName());

        holder.tvDuration.setText(currentItem.getSongDuration());
        Glide.with(mContext).load(currentItem.getImage()).into(holder.ivSong);

        Glide.with(mContext)
                .load(currentItem.getCollabProfile())
                .circleCrop()
                .into(holder.ivCollabProfile);

        holder.ivLike.setSelected(currentItem.isLikedByViewer());
        holder.tvLikeCount.setText(String.valueOf(currentItem.getLikes()));
        holder.tvSongName.setSelected(currentItem.getTrackId().equalsIgnoreCase(trackId));
        holder.tvArtist.setSelected(currentItem.getTrackId().equalsIgnoreCase(trackId));
        holder.tvArtist.setVisibility(View.GONE);

        if (isRealTime) {
            holder.flLike.setVisibility(View.GONE);
            holder.ivMenu.setVisibility(View.GONE);
        } else {
            holder.flLike.setVisibility(View.VISIBLE);
            holder.ivMenu.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class ExampleViewHolder extends RecyclerView.ViewHolder {
        AppCompatImageView ivSong, ivLike, ivMenu, ivCollabProfile;
        AppCompatTextView tvSongName, tvArtist, tvDuration, tvLikeCount;
        FrameLayout flLike;
        LinearLayout llMain;
        SwipeLayout swipeLayout;

        public ExampleViewHolder(@NonNull View itemView) {
            super(itemView);
            ivSong = itemView.findViewById(R.id.ivSong);
            ivLike = itemView.findViewById(R.id.ivLike);
            ivMenu = itemView.findViewById(R.id.ivMenu);
            ivCollabProfile = itemView.findViewById(R.id.ivCollabProfile);
            tvSongName = itemView.findViewById(R.id.tvSongName);
            tvArtist = itemView.findViewById(R.id.tvArtist);
            tvDuration = itemView.findViewById(R.id.tvDuration);
            tvLikeCount = itemView.findViewById(R.id.tvLikeCount);
            flLike = itemView.findViewById(R.id.flLike);
            llMain = itemView.findViewById(R.id.llMain);
            swipeLayout = itemView.findViewById(R.id.swipeLayout);

            flLike.setOnClickListener(v -> customItemClickListener.onItemClick(0, getAdapterPosition()));
            ivMenu.setOnClickListener(v -> {
                LayoutInflater layoutInflater = (LayoutInflater) v.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                PlaylistSongMenuBinding popupView = DataBindingUtil.inflate(layoutInflater, R.layout.playlist_song_menu, null, false);

                PopupWindow popupWindow = new PopupWindow(
                        popupView.getRoot(),
                        CommonUtils.dpToPx(300, v.getContext()),
                        CommonUtils.dpToPx(110, v.getContext()));

                popupView.llDeletePlaylist.setOnClickListener(v1 -> {
                    popupWindow.dismiss();
                    customItemClickListener.onItemClick(1, getAdapterPosition());
                });
                popupView.llAddSong.setOnClickListener(v1 -> {
                    popupWindow.dismiss();
                    customItemClickListener.onItemClick(2, getAdapterPosition());
                });
                popupView.llShare.setOnClickListener(v1 -> {
                    popupWindow.dismiss();
                    customItemClickListener.onItemClick(3, getAdapterPosition());
                });

                popupWindow.setBackgroundDrawable(new BitmapDrawable());
                popupWindow.setOutsideTouchable(true);
                popupWindow.showAsDropDown(v);
            });
            llMain.setOnClickListener(v -> customItemClickListener.onItemClick(-1, getAdapterPosition()));
        }
    }

    public void setTrackId(String trackId) {
        this.trackId = trackId;
    }

    public interface CustomItemClickListener {
        void onItemClick(int type, int position);
    }
}
