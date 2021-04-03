package com.shorincity.vibin.music_sharing.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.shorincity.vibin.music_sharing.activity.YoutubeViewMoreArtistActivity;
import com.shorincity.vibin.music_sharing.model.HomeYoutubeModel;
import com.shorincity.vibin.music_sharing.R;

import java.util.ArrayList;

public class YoutubeArtistAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    final static int VIEW_MORE = 0;
    final static int NORMAL_VIEW = 1;
    private Context mContext;
    public ArrayList<HomeYoutubeModel.YoutubeCustomModel> list;
    public ArrayList<HomeYoutubeModel.YoutubeCustomModel> fulllist;

    public YoutubeArtistAdapter(Context context, ArrayList<HomeYoutubeModel.YoutubeCustomModel> halflist, ArrayList<HomeYoutubeModel.YoutubeCustomModel> fulllist) {
        mContext = context;
        list = halflist;
        this.fulllist = fulllist;
    }

    @Override
    public int getItemViewType(int position) {
        if (list.size() <= 10) {
            return NORMAL_VIEW;
        } else {
            if (position == list.size() - 1) {
                return VIEW_MORE;
            } else {
                return NORMAL_VIEW;
            }
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        if (VIEW_MORE == i) {
            View v = LayoutInflater.from(mContext).inflate(R.layout.view_more_artists, parent, false);
            return new ViewMore(v);
        } else {
            View v = LayoutInflater.from(mContext).inflate(R.layout.adapter_artists, parent, false);
            return new ExampleViewHolder(v);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        HomeYoutubeModel.YoutubeCustomModel currentItem = list.get(position);
        if (getItemViewType(position) == NORMAL_VIEW) {
            ((ExampleViewHolder) holder).setData(currentItem);
        } else {
            ((ViewMore) holder).setData(currentItem);
        }
    }


    @Override
    public int getItemCount() {
        return list.size();
    }


    public class ViewMore extends RecyclerView.ViewHolder {
        CardView cardview_more;

        public ViewMore(@NonNull View itemView) {
            super(itemView);
            cardview_more = itemView.findViewById(R.id.cardview_more);
        }

        public void setData(HomeYoutubeModel.YoutubeCustomModel currentItem) {
            cardview_more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, YoutubeViewMoreArtistActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("title", currentItem.getLanguage());
                    bundle.putParcelableArrayList("playlist", (ArrayList<? extends Parcelable>) fulllist);
                    intent.putExtras(bundle);
                    mContext.startActivity(intent);
                }
            });
        }
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

                Glide.with(mContext).load(currentItem.getThumbnail())
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
