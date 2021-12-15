package com.shorincity.vibin.music_sharing.adapters;


import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.shorincity.vibin.music_sharing.R;
import com.shorincity.vibin.music_sharing.model.Item;
import com.shorincity.vibin.music_sharing.model.ModelData;
import com.shorincity.vibin.music_sharing.model.coverart.CoverArtImageResponse;
import com.shorincity.vibin.music_sharing.model.lastfm.Track;
import com.shorincity.vibin.music_sharing.model.lastfm.trackinfo.Album;
import com.shorincity.vibin.music_sharing.model.lastfm.trackinfo.TrackInfo;
import com.shorincity.vibin.music_sharing.model.lastfm.trackinfo.TrackInfoResponse;
import com.shorincity.vibin.music_sharing.service.DataAPI;
import com.shorincity.vibin.music_sharing.service.RetrofitAPI;
import com.shorincity.vibin.music_sharing.utils.AppConstants;
import com.shorincity.vibin.music_sharing.utils.Logging;

import java.util.ArrayList;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// playlist recycler view adapter
public class YoutubeSearchAdapter extends RecyclerView.Adapter<YoutubeSearchAdapter.ExampleViewHolder> {

    private Context mContext;
    private ArrayList<Track> list;
    private DataAPI apiService;

    public YoutubeSearchAdapter(Context context, ArrayList<Track> exampleList) {
        mContext = context;
        list = exampleList;
        apiService = RetrofitAPI.getLastFMData();
    }

    @NonNull
    @Override
    public ExampleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.adapter_youtube_search, parent, false);

        return new ExampleViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ExampleViewHolder holder, int position) {
        Track currentItem = list.get(position);
        String imageUrl = currentItem.getImage().get(0).getText();

        //holder.mTextViewTitle.setText(currentItem.getSnippet().getTitle());

        /*if (currentItem.getSnippet().getTitle().contains("&#39;")){
            String str = currentItem.getSnippet().getTitle().replaceAll("&#39;"," ");
            holder.mTextViewTitle.setText(str);
        }else if (currentItem.getSnippet().getTitle().contains("&quot")){
            String str = currentItem.getSnippet().getTitle().replaceAll("&quot;"," ");
            holder.mTextViewTitle.setText(str);
        }else if (currentItem.getSnippet().getTitle().contains("&amp;")){
            String str = currentItem.getSnippet().getTitle().replaceAll("&amp;"," ");
            holder.mTextViewTitle.setText(str);
        } else {
            holder.mTextViewTitle.setText(currentItem.getSnippet().getTitle());
        }*/
        holder.mTextViewTitle.setText(String.format("%s - %s", currentItem.getName(),
                currentItem.getArtist()));
        holder.mImageView.setImageResource(R.drawable.music_placeholder);

        if (currentItem.getModelData() == null) {
            if (!TextUtils.isEmpty(currentItem.getArtist()) && !TextUtils.isEmpty(currentItem.getName())) {
                /*apiService.getResurt("snippet", currentItem.getArtist() + "+" + currentItem.getName(),
                        "1", "video", "10", "true", AppConstants.YOUTUBE_KEY)*/
                apiService.callTrackInfoApi(AppConstants.LAST_FM_KEY, currentItem.getName(), currentItem.getArtist(), "json")
                        .enqueue(new Callback<TrackInfoResponse>() {
                            @Override
                            public void onResponse(Call<TrackInfoResponse> call, Response<TrackInfoResponse> response) {
                                TrackInfoResponse imageResponse = response.body();
//                                currentItem.setModelData(imageResponse);
//                                Logging.d("==>" + new Gson().toJson(imageResponse));
                                if (imageResponse != null && imageResponse.getTrack() != null && imageResponse.getTrack().getAlbum() != null) {
                                    Album trackInfo = imageResponse.getTrack().getAlbum();
                                    if (trackInfo.getImage().size() > 0) {
                                        String thumb = trackInfo.getImage().get(trackInfo.getImage().size() - 1).getText();
                                        Glide.with(mContext)
                                                .load(thumb)
                                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                                .into(holder.mImageView);
                                    }
                                } else {
                                    Glide.with(mContext).load(imageUrl).into(holder.mImageView);
                                }
                            }

                            @Override
                            public void onFailure(Call<TrackInfoResponse> call, Throwable t) {
                                Glide.with(mContext).load(imageUrl).into(holder.mImageView);
                            }
                        });
            } else {
                holder.mImageView.setImageResource(R.drawable.music_placeholder);
            }
        } else {
           /* ModelData imageResponse = currentItem.getModelData();
            String thumb = imageResponse.getItems().get(0).getSnippet().getThumbnails().getMedium().getUrl();
            Glide.with(mContext)
                    .load(thumb)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.mImageView);*/
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class ExampleViewHolder extends RecyclerView.ViewHolder {

        public ImageView mImageView;
        public TextView mTextViewTitle, mTextViewSubTitle;
        public RelativeLayout mainRl;

        public ExampleViewHolder(@NonNull View itemView) {
            super(itemView);
            mainRl = itemView.findViewById(R.id.main_layout);
            mImageView = itemView.findViewById(R.id.item_img);
            mTextViewTitle = itemView.findViewById(R.id.item_title);

            itemView.setOnClickListener(v -> customItemClickListener.onItemClick(v, getAdapterPosition()));

            try {
                if (mainRl.getTag() == null) {
                    mainRl.setBackgroundResource(R.color.clr_tungsten);
                    mainRl.setTag(getItemViewType());
                } else {
                    //holder.mainRl.setBackground(holder.mainRl.getBackground());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
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

