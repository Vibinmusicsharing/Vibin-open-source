package com.shorincity.vibin.music_sharing.user_profile.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.giphy.sdk.core.models.enums.RenditionType;
import com.shorincity.vibin.music_sharing.R;
import com.shorincity.vibin.music_sharing.databinding.ItemUserPlaylistBinding;
import com.shorincity.vibin.music_sharing.model.MyPlaylistModel;
import com.shorincity.vibin.music_sharing.utils.Utility;

import java.util.List;

public class UserPlaylistAdapter extends RecyclerView.Adapter<UserPlaylistAdapter.UserPlaylistViewHolder> {

    private final List<MyPlaylistModel> playlists;
    private final UserPlaylistCallback callback;

    public UserPlaylistAdapter(Context context, List<MyPlaylistModel> list, UserPlaylistCallback callback) {
        Utility.configGiphy(context);
        this.playlists = list;
        this.callback = callback;
    }

    @NonNull
    @Override
    public UserPlaylistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new UserPlaylistViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_user_playlist, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull UserPlaylistViewHolder holder, int position) {
        holder.binding.tvPlaylistName.setText(playlists.get(position).getName());
        String gifArraySplit[] = playlists.get(position).getGifLink().split("/");
        String mediaId = gifArraySplit[gifArraySplit.length - 1];
        holder.binding.gifPlaylist.setMediaWithId(mediaId, RenditionType.preview,
                ContextCompat.getDrawable(holder.binding.getRoot().getContext(), R.color.light_gray), null);
        holder.binding.tvPlaylistCreator.setText(playlists.get(position).getAdminName());
        holder.binding.tvSongCount.setText(playlists.get(position).getSongs() + " songs");
        holder.itemView.setOnClickListener(view -> {
            callback.onPlaylistClick(position);
        });
    }

    @Override
    public int getItemCount() {
        return playlists.size();
    }

    public static class UserPlaylistViewHolder extends RecyclerView.ViewHolder {
        ItemUserPlaylistBinding binding;

        UserPlaylistViewHolder(ItemUserPlaylistBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public interface UserPlaylistCallback {
        void onPlaylistClick(int position);
    }
}
