package com.shorincity.vibin.music_sharing.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.giphy.sdk.core.models.enums.RenditionType;
import com.giphy.sdk.ui.Giphy;
import com.giphy.sdk.ui.views.GifView;
import com.shorincity.vibin.music_sharing.R;
import com.shorincity.vibin.music_sharing.model.MyPlaylistModel;
import com.shorincity.vibin.music_sharing.utils.AppConstants;

import java.util.ArrayList;

// playlist recycler view adapter
public class MyPlaylistAdapter extends RecyclerView.Adapter<MyPlaylistAdapter.ExampleViewHolder> {

    private Context mContext;
    private ArrayList<MyPlaylistModel> list;

    public MyPlaylistAdapter(Context context, ArrayList<MyPlaylistModel> exampleList){
        Giphy.INSTANCE.configure(context, AppConstants.GIPHY_API_KEY, true);
        mContext = context;
        list = exampleList;
    }

    @NonNull
    @Override
    public ExampleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.adapter_my_playlist,parent,false);

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
        MyPlaylistModel currentItem = list.get(position);
        String name = currentItem.getName();

        holder.mTextViewTitle.setText(name);

        String gifArraySplit[] = currentItem.getGifLink().split("/");
        String mediaId = gifArraySplit[gifArraySplit.length-1];
        holder.gifView.setMediaWithId(mediaId, RenditionType.preview, ContextCompat.getDrawable(mContext, R.color.light_gray));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class ExampleViewHolder extends RecyclerView.ViewHolder{

        public GifView gifView;
        public TextView mTextViewTitle;
        public View view;
        public ExampleViewHolder(@NonNull View itemView) {
            super(itemView);
            gifView = itemView.findViewById(R.id.gif_iv);
            mTextViewTitle = itemView.findViewById(R.id.title);
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
