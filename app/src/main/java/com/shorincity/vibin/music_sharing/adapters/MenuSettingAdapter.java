package com.shorincity.vibin.music_sharing.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.shorincity.vibin.music_sharing.R;
import com.shorincity.vibin.music_sharing.model.MenuSettingModel;

import java.util.List;

public class MenuSettingAdapter extends RecyclerView.Adapter<MenuSettingAdapter.ViewHolder> {

    private List<MenuSettingModel> list;
    private MenuItemCallback callback;

    public MenuSettingAdapter(List<MenuSettingModel> list, MenuItemCallback callback) {
        this.list = list;
        this.callback = callback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_setting_menu, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MenuSettingModel mBean = list.get(position);
        holder.tvTitle.setText(mBean.getTitle());
        holder.tvSubTitle.setText(mBean.getSubTitle());
        holder.ivIcon.setImageDrawable(ContextCompat.getDrawable(holder.ivIcon.getContext(), mBean.getImageRes()));

        holder.itemView.setOnClickListener(view -> {
            callback.onItemClick(position);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        AppCompatTextView tvTitle, tvSubTitle;
        AppCompatImageView ivIcon;

        ViewHolder(View view) {
            super(view);
            tvTitle = view.findViewById(R.id.tvTitle);
            tvSubTitle = view.findViewById(R.id.tvSubTitle);
            ivIcon = view.findViewById(R.id.ivIcon);
        }
    }

    public interface MenuItemCallback {
        void onItemClick(int position);
    }
}
