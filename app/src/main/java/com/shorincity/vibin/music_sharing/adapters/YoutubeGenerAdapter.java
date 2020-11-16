package com.shorincity.vibin.music_sharing.adapters;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.shorincity.vibin.music_sharing.model.HomeYoutubeModel;
import com.shorincity.vibin.music_sharing.R;

import java.util.ArrayList;

public class YoutubeGenerAdapter extends RecyclerView.Adapter<YoutubeGenerAdapter.ExampleViewHolder> {

    private Context mContext;
    private ArrayList<HomeYoutubeModel.YoutubeCustomModel> list;

    public YoutubeGenerAdapter(Context context, ArrayList<HomeYoutubeModel.YoutubeCustomModel> exampleList){
        mContext = context;
        list = exampleList;
    }

    @NonNull
    @Override
    public ExampleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.adapter_gener,parent,false);

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

        if(!currentItem.getThumbnail().equalsIgnoreCase("THUMBNAIL_URI"))
            Glide.with(mContext).load(currentItem.getThumbnail()).into(holder.mImageView).onLoadFailed(new ColorDrawable(mContext.getResources().getColor(R.color.light_gray)));
        else
            holder.mImageView.setImageResource(R.drawable.music_placeholder);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class ExampleViewHolder extends RecyclerView.ViewHolder{

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
}
