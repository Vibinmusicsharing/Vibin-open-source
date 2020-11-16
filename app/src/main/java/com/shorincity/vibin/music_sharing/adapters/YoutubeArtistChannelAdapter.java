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
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.shorincity.vibin.music_sharing.model.YoutubeChannelModel;
import com.shorincity.vibin.music_sharing.R;

import java.util.ArrayList;
import java.util.Random;
import java.util.StringTokenizer;

// playlist recycler view adapter
public class YoutubeArtistChannelAdapter extends RecyclerView.Adapter<YoutubeArtistChannelAdapter.ExampleViewHolder> {

    private Context mContext;
    private ArrayList<YoutubeChannelModel.Item> list;

    public YoutubeArtistChannelAdapter(Context context, ArrayList<YoutubeChannelModel.Item> exampleList){
        mContext = context;
        list = exampleList;
    }

    @NonNull
    @Override
    public ExampleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.adapter_youtube_channel,parent,false);

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
        YoutubeChannelModel.Item currentItem = list.get(position);
        String imageUrl = currentItem.getSnippet().getThumbnails().getDefault().getUrl();
        String name = currentItem.getSnippet().getTitle();

        holder.mTextViewTitle.setText(currentItem.getSnippet().getTitle());


        StringTokenizer st = new StringTokenizer(currentItem.getSnippet().getTitle(), "|");
        String subTitle = st.nextToken();

        if(TextUtils.isEmpty(subTitle)) {
            holder.mTextViewSubTitle.setVisibility(View.GONE);
        } else {
            holder.mTextViewSubTitle.setVisibility(View.VISIBLE);
            holder.mTextViewSubTitle.setText(subTitle);
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
            mTextViewSubTitle = itemView.findViewById(R.id.item_sub_title);
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
