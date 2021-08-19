package com.shorincity.vibin.music_sharing.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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

    public PublicPlaylistItemAdapter(Context context, ArrayList<PlaylistDetailModel> exampleList) {
        mContext = context;
        list = exampleList;
    }

    @NonNull
    @Override
    public PublicPlaylistItemAdapter.ExampleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.publicclistplaylistsong_layout, parent, false);

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
        int serial = holder.getAdapterPosition() + 1;

        holder.mTextViewTitle.setText(name);

        holder.txt_serial.setText(String.valueOf(serial));

        if (currentItem.isEditable()) {
            holder.checkbox_songs.setVisibility(View.VISIBLE);
        } else {
            holder.checkbox_songs.setVisibility(View.GONE);
        }
        if (currentItem.isSelected()) {
            holder.checkbox_songs.setChecked(true);
        } else {
            holder.checkbox_songs.setChecked(false);
        }

        holder.checkbox_songs.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                ((PlaylistDetailActivity) mContext).selectedplaylistID(currentItem.getId(), b);
                customItemClickListener.onSelectedplaylistID(currentItem.getId(), b);
                currentItem.setSelected(b);
            }
        });
        if (currentItem.getType().equalsIgnoreCase(AppConstants.YOUTUBE)) {
            holder.songTypeIv.setImageResource(R.drawable.youtube);
            //holder.songTypeIv.setBackgroundColor(ContextCompat.getColor(mContext, R.color.yt_red));
        } else if (currentItem.getType().equalsIgnoreCase(AppConstants.SPOTIFY)) {
            holder.songTypeIv.setImageResource(R.drawable.spotify4);
            //holder.songTypeIv.setBackgroundColor(ContextCompat.getColor(mContext, R.color.spot_green));
        }
        holder.txt_duration.setText(currentItem.getSongDuration());
        Glide.with(mContext).load(imageUrl).into(holder.mImageView);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class ExampleViewHolder extends RecyclerView.ViewHolder {

        public ImageView mImageView, songTypeIv;
        public TextView mTextViewTitle, txt_duration, txt_serial;
        public View view;
        private CheckBox checkbox_songs;

        public ExampleViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_serial = itemView.findViewById(R.id.serialNumber);
            mImageView = itemView.findViewById(R.id.imageViewaddtoplaylist);
            songTypeIv = itemView.findViewById(R.id.iv_song_type);
            mTextViewTitle = itemView.findViewById(R.id.textViewtitle);
            txt_duration = itemView.findViewById(R.id.txt_duration);
            view = itemView.findViewById(R.id.view);
            checkbox_songs = itemView.findViewById(R.id.checkbox_songs);

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

        void onSelectedplaylistID(int ids, boolean selected);
    }
}
