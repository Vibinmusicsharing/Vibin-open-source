package com.shorincity.vibin.music_sharing.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.shorincity.vibin.music_sharing.R;
import com.shorincity.vibin.music_sharing.activity.ChannelsPlaylistItemActivity;
import com.shorincity.vibin.music_sharing.model.HomeYoutubeModel;
import com.shorincity.vibin.music_sharing.utils.AppConstants;

import java.util.ArrayList;

public class YoutubeArtistPerticulerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    public ArrayList<HomeYoutubeModel.YoutubeCustomModel> list;
    public int position;

    public YoutubeArtistPerticulerAdapter(Context context, ArrayList<HomeYoutubeModel.YoutubeCustomModel> exampleList) {
        mContext = context;
        list = exampleList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.youtube_artist_perticuler_list, parent, false);
        return new ExampleViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        HomeYoutubeModel.YoutubeCustomModel currentItem = list.get(position);
        ((YoutubeArtistPerticulerAdapter.ExampleViewHolder) holder).setData(currentItem);


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


        }

        public void setData(HomeYoutubeModel.YoutubeCustomModel currentItem) {
            String name = currentItem.getGenre();
            mTextViewTitle.setText(name);

            mainRl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    aritstPerticulerCallback.onChannelPlaylistClick(currentItem, currentItem.getGenre(), currentItem.getThumbnail());
 /*                   Intent intent = new Intent(mContext, ChannelsPlaylistItemActivity.class);
                    intent.putExtra(AppConstants.INTENT_YOUTUBE_PLAYLIST_DATA, (Parcelable) currentItem);
                    intent.putExtra("title", currentItem.getGenre());
                    intent.putExtra("thumbnail", currentItem.getThumbnail());
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent);*/
                }
            });
            if (!currentItem.getThumbnail().equalsIgnoreCase("THUMBNAIL_URI")) {

                Glide.with(mContext).load(currentItem.getThumbnail())
                        .into(mImageView);
            } else
                mImageView.setImageResource(R.drawable.music_placeholder);
        }

    }
    private AritstPerticulerCallback aritstPerticulerCallback;

    public void setAritstPerticulerCallback(AritstPerticulerCallback aritstPerticulerCallback) {
        this.aritstPerticulerCallback = aritstPerticulerCallback;
    }

    public interface AritstPerticulerCallback {
        public void onChannelPlaylistClick(HomeYoutubeModel.YoutubeCustomModel currentItem, String genre, String thumbnail);
    }
}
