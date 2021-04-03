package com.shorincity.vibin.music_sharing.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.shorincity.vibin.music_sharing.R;
import com.shorincity.vibin.music_sharing.model.PlaylistDetailModel;

import java.util.ArrayList;

public class PlayListDetailsAdapter extends RecyclerView.Adapter<PlayListDetailsAdapter.ViewHolder> {
    ArrayList<PlaylistDetailModel> playlist;
    private ItemListener mListener;
    Context context;
    boolean isChange = false;
    int setPos = 0;
    int mainpos =0;

    public PlayListDetailsAdapter(Context context, ArrayList<PlaylistDetailModel> playlist, ItemListener mListener) {
        this.context = context;
        this.playlist = playlist;
        this.mListener = mListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_playlist_detail, parent, false));
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

        TextView textView;
        ImageView imageView;
        PlaylistDetailModel item;

        ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            textView = (TextView) itemView.findViewById(R.id.txt_name_player);
            imageView = (ImageView) itemView.findViewById(R.id.img_icon);
        }

        void setData(PlaylistDetailModel item) {
            this.item = item;
            textView.setText(item.getName());

//            try {
//                if (mainpos == setPos) {
//                    textView.setTextColor(context.getResources().getColor(R.color.toolbarColor));
//                } else {
//                    textView.setTextColor(context.getResources().getColor(R.color.black));
//                }
//            } catch (Resources.NotFoundException e) {
//                e.printStackTrace();
//            }
            Glide.with(context).load(item.getImage()).into(imageView);
        }


        @Override
        public void onClick(View v) {
            if (mListener != null) {
                mListener.onItemClick(item);
            }
        }
    }
}
