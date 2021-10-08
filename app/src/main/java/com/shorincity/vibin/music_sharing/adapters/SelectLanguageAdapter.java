package com.shorincity.vibin.music_sharing.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.shorincity.vibin.music_sharing.R;
import com.shorincity.vibin.music_sharing.model.MusicLanguageModel;

import java.util.List;

public class SelectLanguageAdapter extends RecyclerView.Adapter<SelectLanguageAdapter.ViewHolder> {

    private List<MusicLanguageModel> list;
    private SelectLanguageCallback selectLanguageCallback;

    public SelectLanguageAdapter(List<MusicLanguageModel> list, SelectLanguageCallback selectLanguageCallback) {
        this.list = list;
        this.selectLanguageCallback = selectLanguageCallback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_select_music_language, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MusicLanguageModel mBean = list.get(position);
        if (mBean.isSelected()) {
            holder.imgLanguage.setImageResource(mBean.getImgSelected());
        } else {
            holder.imgLanguage.setImageResource(mBean.getImgUnselected());
        }

        holder.itemView.setOnClickListener(v -> selectLanguageCallback.onItemClick(holder.getAdapterPosition()));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        AppCompatImageView imgLanguage;

        ViewHolder(View view) {
            super(view);
            imgLanguage = view.findViewById(R.id.image);
        }
    }

    public interface SelectLanguageCallback {
        void onItemClick(int position);
    }
}
