package com.shorincity.vibin.music_sharing.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.shorincity.vibin.music_sharing.R;
import com.shorincity.vibin.music_sharing.model.HomeYoutubeModel;
import com.shorincity.vibin.music_sharing.utils.GlideApp;

import java.util.ArrayList;

public class YoutubeOtherArtistAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    public ArrayList<HomeYoutubeModel.YoutubeCustomModel> list;

    public YoutubeOtherArtistAdapter(Context context, ArrayList<HomeYoutubeModel.YoutubeCustomModel> exampleList) {
        mContext = context;
        list = exampleList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.adapter_artists, parent, false);
        return new ExampleViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        HomeYoutubeModel.YoutubeCustomModel currentItem = list.get(position);
        ((ExampleViewHolder) holder).setData(currentItem);
    }


    @Override
    public int getItemCount() {
        return list.size();
    }


    public class ExampleViewHolder extends RecyclerView.ViewHolder {

        ImageView mImageView;
        TextView mTextViewTitle;
        RelativeLayout mainRl;


        public ExampleViewHolder(@NonNull View itemView) {
            super(itemView);
            mainRl = itemView.findViewById(R.id.main_layout_artist);
            mImageView = itemView.findViewById(R.id.item_img_artist);
            mTextViewTitle = itemView.findViewById(R.id.item_title_artist);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    customItemClickListener.onItemClick(v, getAdapterPosition());
                }
            });

        }

        public void setData(HomeYoutubeModel.YoutubeCustomModel currentItem) {
            String name = currentItem.getGenre();
            mTextViewTitle.setText(name);
            if (!currentItem.getThumbnail().equalsIgnoreCase("THUMBNAIL_URI")) {

                GlideApp.with(mContext).load(currentItem.getThumbnail())
                        .into(mImageView);
            } else
                mImageView.setImageResource(R.drawable.music_placeholder);
        }

    }

    public CustomItemClickListener getCustomItemClickListener() {
        return customItemClickListener;
    }

    public void setCustomItemClickListener(CustomItemClickListener customItemClickListener) {
        this.customItemClickListener = customItemClickListener;
    }

    CustomItemClickListener customItemClickListener;

    public interface CustomItemClickListener {
        public void onItemClick(View v, int position);
    }
}
