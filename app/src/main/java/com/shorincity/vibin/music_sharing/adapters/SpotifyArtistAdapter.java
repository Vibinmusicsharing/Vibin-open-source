package com.shorincity.vibin.music_sharing.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.shorincity.vibin.music_sharing.model.SpotifyArtistModel;
import com.shorincity.vibin.music_sharing.R;
import com.shorincity.vibin.music_sharing.utils.Utility;

import java.util.ArrayList;

public class SpotifyArtistAdapter extends RecyclerView.Adapter<SpotifyArtistAdapter.ExampleViewHolder> {

    private Context mContext;
    private ArrayList<SpotifyArtistModel.Item> list;

    public SpotifyArtistAdapter(Context context, ArrayList<SpotifyArtistModel.Item> exampleList){
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
        SpotifyArtistModel.Item currentItem = list.get(position);
        String imageUrl = currentItem.getImages().get(0).getUrl();
        String name = currentItem.getName();


        holder.mTextViewTitle.setText(name);
        Glide.with(mContext).load(imageUrl).into(holder.mImageView);

        holder.mTextViewSubtitle.setText(mContext.getResources().getString(R.string.aired_on)+ Utility.dateToString(Utility.stringToDate(currentItem.getReleaseDate())));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class ExampleViewHolder extends RecyclerView.ViewHolder{

        public ImageView mImageView;
        public TextView mTextViewTitle, mTextViewSubtitle;
        public View view;
        public ExampleViewHolder(@NonNull View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.imageViewaddtoplaylist);
            mTextViewTitle = itemView.findViewById(R.id.textViewtitle);
            mTextViewSubtitle = itemView.findViewById(R.id.textViewSubtitle);
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
