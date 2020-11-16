package com.shorincity.vibin.music_sharing.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.shorincity.vibin.music_sharing.R;
import com.shorincity.vibin.music_sharing.utils.AppConstants;

import java.util.ArrayList;

// playlist recycler view adapter
public class PublicPlaylistItemAdapter extends RecyclerView.Adapter<PublicPlaylistItemAdapter.ExampleViewHolder> {

    private Context mContext;
    private ArrayList<PlaylistDetailModel> list;

    public PublicPlaylistItemAdapter(Context context, ArrayList<PlaylistDetailModel> exampleList){
        mContext = context;
        list = exampleList;
    }

    @NonNull
    @Override
    public PublicPlaylistItemAdapter.ExampleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.adapter_recent_played,parent,false);

        final PublicPlaylistItemAdapter.ExampleViewHolder mViewHolder = new PublicPlaylistItemAdapter.ExampleViewHolder(v);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customItemClickListener.onItemClick(v, mViewHolder.getPosition());
            }
        });
        return mViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PublicPlaylistItemAdapter.ExampleViewHolder holder, int position) {
        PlaylistDetailModel currentItem = list.get(position);
        String imageUrl = currentItem.getImage();
        String name = currentItem.getName();


        holder.mTextViewTitle.setText(name);

        if(currentItem.getType().equalsIgnoreCase(AppConstants.YOUTUBE)) {
            holder.songTypeIv.setImageResource(R.drawable.youtube);
            //holder.songTypeIv.setBackgroundColor(ContextCompat.getColor(mContext, R.color.yt_red));
        } else if(currentItem.getType().equalsIgnoreCase(AppConstants.SPOTIFY)) {
            holder.songTypeIv.setImageResource(R.drawable.spotify4);
            //holder.songTypeIv.setBackgroundColor(ContextCompat.getColor(mContext, R.color.spot_green));
        }

        Glide.with(mContext).load(imageUrl).into(holder.mImageView);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class ExampleViewHolder extends RecyclerView.ViewHolder{

        public ImageView mImageView, songTypeIv;
        public TextView mTextViewTitle;
        public View view;
        public ExampleViewHolder(@NonNull View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.imageViewaddtoplaylist);
            songTypeIv = itemView.findViewById(R.id.iv_song_type);
            mTextViewTitle = itemView.findViewById(R.id.textViewtitle);
            view = itemView.findViewById(R.id.view);

        }
    }

    public PublicPlaylistItemAdapter.CustomItemClickListener getCustomItemClickListener() {
        return customItemClickListener;
    }

    public void setCustomItemClickListener(PublicPlaylistItemAdapter.CustomItemClickListener customItemClickListener) {
        this.customItemClickListener = customItemClickListener;
    }

    PublicPlaylistItemAdapter.CustomItemClickListener customItemClickListener;
    public interface CustomItemClickListener {
        public void onItemClick(View v, int position);
    }
}
