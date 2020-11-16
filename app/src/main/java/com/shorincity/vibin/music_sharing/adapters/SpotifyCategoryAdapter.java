package com.shorincity.vibin.music_sharing.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.shorincity.vibin.music_sharing.model.SpotifyCategoryModel;
import com.shorincity.vibin.music_sharing.R;

import java.util.ArrayList;

public class SpotifyCategoryAdapter extends RecyclerView.Adapter<SpotifyCategoryAdapter.ExampleViewHolder> {

    private Context mContext;
    private ArrayList<SpotifyCategoryModel.Item> list;

    public SpotifyCategoryAdapter(Context context, ArrayList<SpotifyCategoryModel.Item> exampleList){
        mContext = context;
        list = exampleList;
    }

    @NonNull
    @Override
    public ExampleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.adapter_category,parent,false);

        final ExampleViewHolder mViewHolder = new ExampleViewHolder(v);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customItemClickListener.onItemClick(v, mViewHolder.getPosition());
            }
        });

        /*try {
            if(mViewHolder.mainRl.getTag() == null ) {
                mViewHolder.mainRl.setBackgroundResource(R.color.clr_tungsten);
                mViewHolder.mainRl.setTag(i);
            } else {
                //holder.mainRl.setBackground(holder.mainRl.getBackground());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        return mViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ExampleViewHolder holder, int position) {
        SpotifyCategoryModel.Item currentItem = list.get(position);
        String imageUrl = currentItem.getIcons().get(0).getUrl();
        Glide.with(mContext).load(imageUrl).into(holder.mImageView);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class ExampleViewHolder extends RecyclerView.ViewHolder{

        public ImageView mImageView;
        public RelativeLayout mainRl;
        public ExampleViewHolder(@NonNull View itemView) {
            super(itemView);
            mainRl = itemView.findViewById(R.id.main_layout);
            mImageView = itemView.findViewById(R.id.item_img);
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
