package com.shorincity.vibin.music_sharing.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.shorincity.vibin.music_sharing.R;
import com.shorincity.vibin.music_sharing.model.YoutubePlaylistItemModel;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

// playlist recycler view adapter
public class YoutubArtistPlayListAdapter extends RecyclerView.Adapter<YoutubArtistPlayListAdapter.ExampleViewHolder> {

    private Context mContext;
    private ArrayList<YoutubePlaylistItemModel.Item> list;

    public YoutubArtistPlayListAdapter(Context context, ArrayList<YoutubePlaylistItemModel.Item> exampleList){
        mContext = context;
        list = exampleList;
    }



    @NonNull
    @Override
    public ExampleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_trending_list_adapter,parent,false);

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
       YoutubePlaylistItemModel.Snippet currentItem = list.get(position).getSnippet();
       if(currentItem!=null) {
           try {
               String imageUrl = currentItem.getThumbnails().getDefault().getUrl();
               Glide.with(mContext).load(imageUrl).into(holder.mImageView);
           } catch (Exception e) {
               e.printStackTrace();
           }

           String name = currentItem.getTitle();


           holder.mTextViewTitle.setText(name);

       }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class ExampleViewHolder extends RecyclerView.ViewHolder{

        public ImageView mImageView;
        public TextView mTextViewTitle;
        public View view;
        public ExampleViewHolder(@NonNull View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.imageViewaddtoplaylist);
            mTextViewTitle = itemView.findViewById(R.id.textViewtitle);
            view = itemView.findViewById(R.id.view);

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
