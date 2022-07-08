package com.shorincity.vibin.music_sharing.adapters;

import android.content.Context;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.giphy.sdk.core.models.enums.RenditionType;
import com.ovenbits.quickactionview.Action;
import com.ovenbits.quickactionview.QuickActionView;
import com.shorincity.vibin.music_sharing.R;
import com.shorincity.vibin.music_sharing.databinding.ItemProfilePlaylistViewBinding;
import com.shorincity.vibin.music_sharing.model.MyPlaylistModel;
import com.shorincity.vibin.music_sharing.utils.Utility;

import java.util.List;

public class UserProfilePlaylistAdapter extends RecyclerView.Adapter<UserProfilePlaylistAdapter.ViewHolder> {

    private List<MyPlaylistModel> list;
    private MenuItemCallback callback;

    public UserProfilePlaylistAdapter(Context context, List<MyPlaylistModel> list, MenuItemCallback callback) {
        Utility.configGiphy(context);
        this.list = list;
        this.callback = callback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_profile_playlist_view, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Context context = holder.itemView.getContext();
        MyPlaylistModel mBean = list.get(position);

        holder.binding.tvPlaylistName.setText(mBean.getName());
        String gifArraySplit[] = mBean.getGifLink().split("/");
        String mediaId = gifArraySplit[gifArraySplit.length - 1];
        holder.binding.gifPlaylist.setMediaWithId(mediaId, RenditionType.preview,
                ContextCompat.getDrawable(holder.binding.getRoot().getContext(), R.color.light_gray), null);

        holder.binding.ivPin.setVisibility(mBean.isPinnedPlaylist() ? View.VISIBLE : View.GONE);

        holder.itemView.setOnClickListener(view -> {
            callback.onItemClick(position);
        });

        QuickActionView.make(context)
                .addActions(R.menu.menu_playlist_actions)
                .setOnShowListener(new QuickActionView.OnShowListener() {
                    @Override
                    public void onShow(QuickActionView quickActionView) {
                        Vibrator vibe = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                        vibe.vibrate(30);
                    }
                })
                .setOnActionSelectedListener(new QuickActionView.OnActionSelectedListener() {
                    @Override
                    public void onActionSelected(Action action, QuickActionView quickActionView) {
                        callback.onHoverActionSelected(action, mBean);
                    }
                })
                .setBackgroundColor(context.getResources().getColor(R.color.black))
                .setScrimColor(context.getResources().getColor(R.color.gph_transparent))
                .register(holder.itemView);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ItemProfilePlaylistViewBinding binding;

        ViewHolder(ItemProfilePlaylistViewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public interface MenuItemCallback {
        void onItemClick(int position);
        void onHoverActionSelected(Action action, MyPlaylistModel playlist);
    }
}
