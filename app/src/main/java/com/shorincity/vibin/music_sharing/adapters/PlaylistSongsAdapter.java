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
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
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

    public PlaylistSongsAdapter(Context context, ArrayList<PlaylistDetailModel> exampleList, CustomItemClickListener customItemClickListener) {
        mContext = context;
        list = exampleList;
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

        holder.ivLike.setSelected(currentItem.isLikedByViewer());
        if (currentItem.isLikedByViewer()) {
            holder.tvLikeCount.setVisibility(View.VISIBLE);
            holder.tvLikeCount.setText(String.valueOf(currentItem.getLikes()));
        } else {
            holder.tvLikeCount.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class ExampleViewHolder extends RecyclerView.ViewHolder {
        AppCompatImageView ivSong, ivLike, ivMenu;
        AppCompatTextView tvSongName, tvArtist, tvDuration, tvLikeCount;
        FrameLayout flLike;

        public ExampleViewHolder(@NonNull View itemView) {
            super(itemView);
            ivSong = itemView.findViewById(R.id.ivSong);
            ivLike = itemView.findViewById(R.id.ivLike);
            ivMenu = itemView.findViewById(R.id.ivMenu);
            tvSongName = itemView.findViewById(R.id.tvSongName);
            tvArtist = itemView.findViewById(R.id.tvArtist);
            tvDuration = itemView.findViewById(R.id.tvDuration);
            tvLikeCount = itemView.findViewById(R.id.tvLikeCount);
            flLike = itemView.findViewById(R.id.flLike);

            flLike.setOnClickListener(v -> customItemClickListener.onItemClick(0, getAdapterPosition()));
            ivMenu.setOnClickListener(v -> {
                LayoutInflater layoutInflater = (LayoutInflater) v.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                PlaylistSongMenuBinding popupView = DataBindingUtil.inflate(layoutInflater, R.layout.playlist_song_menu, null, false);

                PopupWindow popupWindow = new PopupWindow(
                        popupView.getRoot(),
                        CommonUtils.dpToPx(300, v.getContext()),
                        ViewGroup.LayoutParams.WRAP_CONTENT);

                popupView.llDeletePlaylist.setOnClickListener(v1 -> customItemClickListener.onItemClick(1, getAdapterPosition()));
                popupView.llAddSong.setOnClickListener(v1 -> customItemClickListener.onItemClick(2, getAdapterPosition()));
                popupView.llShare.setOnClickListener(v1 -> customItemClickListener.onItemClick(3, getAdapterPosition()));

                popupWindow.setBackgroundDrawable(new BitmapDrawable());
                popupWindow.setOutsideTouchable(true);
                popupWindow.showAsDropDown(v);
            });
            itemView.setOnClickListener(v -> customItemClickListener.onItemClick(-1, getAdapterPosition()));
        }
    }


    public interface CustomItemClickListener {
        void onItemClick(int type, int position);
    }
}
