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

import com.bumptech.glide.Glide;
import com.shorincity.vibin.music_sharing.model.Item;
import com.shorincity.vibin.music_sharing.R;

import java.util.ArrayList;
import java.util.Random;

// playlist recycler view adapter
public class YoutubeSearchAdapter extends RecyclerView.Adapter<YoutubeSearchAdapter.ExampleViewHolder> {

    private Context mContext;
    private ArrayList<Item> list;

    public YoutubeSearchAdapter(Context context, ArrayList<Item> exampleList){
        mContext = context;
        list = exampleList;
    }

    @NonNull
    @Override
    public ExampleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.adapter_youtube_search,parent,false);

        final ExampleViewHolder mViewHolder = new ExampleViewHolder(v);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customItemClickListener.onItemClick(v, mViewHolder.getPosition());
            }
        });

        try {
            if(mViewHolder.mainRl.getTag() == null ) {
                mViewHolder.mainRl.setBackgroundResource(R.color.clr_tungsten);
                mViewHolder.mainRl.setTag(i);
            } else {
                //holder.mainRl.setBackground(holder.mainRl.getBackground());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ExampleViewHolder holder, int position) {
        Item currentItem = list.get(position);
        String imageUrl = currentItem.getSnippet().getThumbnails().getMedium().getUrl();

        //holder.mTextViewTitle.setText(currentItem.getSnippet().getTitle());

        if (currentItem.getSnippet().getTitle().contains("&#39;")){
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
        }
        Glide.with(mContext).load(imageUrl).into(holder.mImageView);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class ExampleViewHolder extends RecyclerView.ViewHolder{

        public ImageView mImageView;
        public TextView mTextViewTitle, mTextViewSubTitle;
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

