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
import java.util.Random;

public class YoutubeBollywoodAdapter extends RecyclerView.Adapter<YoutubeBollywoodAdapter.ExampleViewHolder> {

    private Context mContext;
    private ArrayList<HomeYoutubeModel.YoutubeCustomModel> list;

    public YoutubeBollywoodAdapter(Context context, ArrayList<HomeYoutubeModel.YoutubeCustomModel> exampleList) {
        mContext = context;
        list = exampleList;
    }

    @NonNull
    @Override
    public ExampleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.adapter_youtube_home_bollywood, parent, false);

        final ExampleViewHolder mViewHolder = new ExampleViewHolder(v);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customItemClickListener.onItemClick(v, mViewHolder.getPosition());
            }
        });
        return mViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ExampleViewHolder holder, int position) {
        HomeYoutubeModel.YoutubeCustomModel currentItem = list.get(position);
        String name = currentItem.getGenre();

        holder.mTextViewTitle.setText(name);

        if (!currentItem.getThumbnail().equalsIgnoreCase("THUMBNAIL_URI"))
            GlideApp.with(mContext).load(currentItem.getThumbnail()).into(holder.mImageView);
        else
            holder.mImageView.setImageResource(R.drawable.music_placeholder);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class ExampleViewHolder extends RecyclerView.ViewHolder {

        public ImageView mImageView;
        public TextView mTextViewTitle;
        public RelativeLayout mainRl;

        public ExampleViewHolder(@NonNull View itemView) {
            super(itemView);
            mainRl = itemView.findViewById(R.id.main_layout);
            mImageView = itemView.findViewById(R.id.item_img);
            mTextViewTitle = itemView.findViewById(R.id.item_title);
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

    public static Integer getRandom() {
        Integer[] array = new Integer[]{R.color.color1,
                R.color.color2,
                R.color.color3,
                R.color.color4,
                R.color.color5,
                R.color.color6,
                R.color.color7,
                R.color.color8,
                R.color.color9,
                R.color.color10,
                R.color.color11,
                R.color.color12,
                R.color.color13,
                R.color.color14,
                R.color.color15,
                R.color.color16,
                R.color.color17,
                R.color.color18,
                R.color.color19,
                R.color.color20,
                R.color.color21,
                R.color.color22,
                R.color.color23,
                R.color.color24
        };
        int rnd = new Random().nextInt(array.length);
        return array[rnd];
    }
}
