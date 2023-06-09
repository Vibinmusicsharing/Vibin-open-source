package com.shorincity.vibin.music_sharing.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.giphy.sdk.core.models.enums.RenditionType;
import com.giphy.sdk.ui.views.GifView;
import com.shorincity.vibin.music_sharing.R;
import com.shorincity.vibin.music_sharing.model.MyPlaylistModel;

import java.util.ArrayList;
import java.util.Random;

// playlist recycler view adapter
public class AddToPlaylistAdapter extends RecyclerView.Adapter<AddToPlaylistAdapter.ExampleViewHolder> {

    private Context mContext;
    private ArrayList<MyPlaylistModel> mExampleList;

    public AddToPlaylistAdapter(Context context, ArrayList<MyPlaylistModel> exampleList) {
        mContext = context;
        mExampleList = exampleList;
    }


    @NonNull
    @Override
    public ExampleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_playlist_dialod_vertical, parent, false);
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
        MyPlaylistModel currentItem = mExampleList.get(position);
//        String imageUrl = currentItem.getImageUrl();
        String name = currentItem.getName();
        String[] gifArraySplit = currentItem.getGifLink().split("/");
        String mediaId = gifArraySplit[gifArraySplit.length - 1];
        holder.gifView.setMediaWithId(mediaId, RenditionType.preview, ContextCompat.getDrawable(mContext, R.color.light_gray), null);

        holder.mImageView.setVisibility(View.GONE);
        holder.imagebtn.setVisibility(View.GONE);
        holder.imagebtn.setText(String.valueOf(name.charAt(0)).toUpperCase());
        holder.imagebtn.setBackgroundColor(getRandom());

        holder.mTextViewTitle.setText(name);
        holder.tvAdminName.setText(currentItem.getAdminName());
//        Glide.with(mContext).load(imageUrl).into(holder.mImageView);


    }

    @Override
    public int getItemCount() {
        return mExampleList.size();
    }


    public class ExampleViewHolder extends RecyclerView.ViewHolder {

        public ImageView mImageView;
        public GifView gifView;
        public TextView mTextViewTitle, tvAdminName;
        public View view;
        public Button imagebtn;

        public ExampleViewHolder(@NonNull View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.imageViewaddtoplaylist);
            gifView = itemView.findViewById(R.id.gif_iv);
            mTextViewTitle = itemView.findViewById(R.id.textViewtitle);
            tvAdminName = itemView.findViewById(R.id.tvAdminName);
            imagebtn = itemView.findViewById(R.id.imagebtn);
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
