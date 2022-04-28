package com.shorincity.vibin.music_sharing.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.daimajia.swipe.SwipeLayout;
import com.shorincity.vibin.music_sharing.R;
import com.shorincity.vibin.music_sharing.model.PlaylistDetailModel;
import com.shorincity.vibin.music_sharing.utils.GlideApp;

import java.util.ArrayList;

public class PlayListDetailsAdapter extends RecyclerView.Adapter<PlayListDetailsAdapter.ViewHolder> {
    ArrayList<PlaylistDetailModel> playlist;
    private ItemListener mListener;
    Context context;
    boolean isChange = false;
    int setPos = 0;
    int mainpos = 0;

    public PlayListDetailsAdapter(Context context, ArrayList<PlaylistDetailModel> playlist, int position, ItemListener mListener) {
        this.context = context;
        this.playlist = playlist;
        this.mListener = mListener;
        this.setPos = position;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_playlist_song, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        mainpos = position;
        holder.setData(playlist.get(position));
    }


    public void setTextViewColor(int pos) {
        setPos = pos;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return playlist.size();
    }

    public interface ItemListener {
        void onItemClick(PlaylistDetailModel item);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        PlaylistDetailModel item;
        AppCompatImageView ivSong, ivLike, ivMenu, ivCollabProfile;
        AppCompatTextView tvSongName, tvArtist, tvDuration, tvLikeCount;
        FrameLayout flLike;
        LinearLayout llMain;
        SwipeLayout swipeLayout;

        ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
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
            swipeLayout.setSwipeEnabled(false);
            ivMenu.setVisibility(View.GONE);
        }

        void setData(PlaylistDetailModel item) {
            this.item = item;
            tvSongName.setText(item.getName());
            tvArtist.setText(item.getArtistName());

            tvDuration.setText(item.getSongDuration() == null ? "00:00" : item.getSongDuration());
            GlideApp.with(tvDuration.getContext()).load(item.getImage()).into(ivSong);

            GlideApp.with(tvDuration.getContext())
                    .load(item.getCollabProfile())
                    .circleCrop()
                    .into(ivCollabProfile);

            ivLike.setSelected(item.isLikedByViewer());
            tvLikeCount.setText(String.valueOf(item.getLikes()));
            try {
                if (mainpos == setPos) {
                    tvSongName.setSelected(true);
                    tvArtist.setSelected(true);
                } else {
                    tvSongName.setSelected(false);
                    tvArtist.setSelected(false);
                }
            } catch (Resources.NotFoundException e) {
                e.printStackTrace();
            }

            tvArtist.setVisibility(View.GONE);


            flLike.setVisibility(View.GONE);
//            ivMenu.setVisibility(View.GONE);
        }


        @Override
        public void onClick(View v) {
            if (mListener != null) {
                mListener.onItemClick(item);
            }
        }
    }
}
