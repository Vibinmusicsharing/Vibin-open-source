package com.shorincity.vibin.music_sharing.adapters;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.shorincity.vibin.music_sharing.R;

import java.util.ArrayList;

public class SpotifyTrackAdapter extends RecyclerView.Adapter<SpotifyTrackAdapter.ExampleViewHolder> {

    private Context mContext;
    private ArrayList<SpotifyCustomTrackModel> list;

    public SpotifyTrackAdapter(Context context, ArrayList<SpotifyCustomTrackModel> exampleList){
        mContext = context;
        list = exampleList;
    }

    @NonNull
    @Override
    public ExampleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_track_list_adapter,parent,false);

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

        SpotifyCustomTrackModel currentItem = list.get(position);
        String imageUrl = currentItem.getThumbnail();
        String name = currentItem.getTitle();

        if(currentItem.getDuration() <= 0)
            holder.durationTv.setVisibility(View.GONE);
        else {
            holder.durationTv.setVisibility(View.VISIBLE);
            long time = Long.valueOf(currentItem.getDuration());
            String duration =  DateUtils.formatElapsedTime(time / 1000);
            holder.durationTv.setText(duration);
        }
        // TODO --> Add Number Seq
        /*int count = position + 1;
        holder.textCount.setText(count);
        Log.d("TEST","position -  " + position);
        Log.d("TEST","count - " + count);*/
        holder.mTextViewTitle.setText(name);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class ExampleViewHolder extends RecyclerView.ViewHolder{

        public TextView mTextViewTitle, durationTv,textCount;
        public View view;
        public ExampleViewHolder(@NonNull View itemView) {
            super(itemView);
            mTextViewTitle = itemView.findViewById(R.id.textViewtitle);
            durationTv = itemView.findViewById(R.id.tv_duration);
            view = itemView.findViewById(R.id.view);
            textCount = itemView.findViewById(R.id.textCount);

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
