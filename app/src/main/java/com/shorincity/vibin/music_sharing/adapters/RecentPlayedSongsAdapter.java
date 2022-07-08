package com.shorincity.vibin.music_sharing.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.shorincity.vibin.music_sharing.R;
import com.shorincity.vibin.music_sharing.databinding.ItemRecentPlayedSongsBinding;
import com.shorincity.vibin.music_sharing.model.RecentSongModel;
import com.shorincity.vibin.music_sharing.utils.GlideApp;
import com.shorincity.vibin.music_sharing.utils.Utility;

import java.util.List;

public class RecentPlayedSongsAdapter extends RecyclerView.Adapter<RecentPlayedSongsAdapter.ViewHolder> {

    private List<RecentSongModel> list;
    private MenuItemCallback callback;

    public RecentPlayedSongsAdapter(Context context, List<RecentSongModel> list, MenuItemCallback callback) {
        Utility.configGiphy(context);
        this.list = list;
        this.callback = callback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_recent_played_songs, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RecentSongModel mBean = list.get(position);

        holder.binding.tvSongName.setText(mBean.getSongName());
        GlideApp.with(holder.binding.getRoot().getContext())
                .load(mBean.getSongThumbnail())
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .into(holder.binding.ivThumbnail);

        holder.itemView.setOnClickListener(view -> {
            callback.onItemClick(position);
        });
        holder.binding.tvDuration.setText(mBean.getSongDuration().substring(3));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ItemRecentPlayedSongsBinding binding;

        ViewHolder(ItemRecentPlayedSongsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public interface MenuItemCallback {
        void onItemClick(int position);
    }
}
