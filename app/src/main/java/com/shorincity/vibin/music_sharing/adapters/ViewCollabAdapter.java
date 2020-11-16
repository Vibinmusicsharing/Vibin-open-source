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
import com.shorincity.vibin.music_sharing.R;
import com.shorincity.vibin.music_sharing.utils.Logging;
import com.shorincity.vibin.music_sharing.UI.custom.RoundedImageView;
import java.util.ArrayList;
// view collab adapter
public class ViewCollabAdapter extends RecyclerView.Adapter<ViewCollabAdapter.ExampleViewHolder> {

    private Context mContext;
    private ArrayList<ViewCollab> mExampleList;

    public ViewCollabAdapter(Context context, ArrayList<ViewCollab> exampleList){
        mContext = context;
        mExampleList = exampleList;
    }


    @NonNull
    @Override
    public ExampleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_collab_xml,parent,false);
        final ExampleViewHolder mViewHolder = new ExampleViewHolder(v);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customItemClickListener.onItemClick(v, mViewHolder.getAdapterPosition());
            }
        });
        return mViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ExampleViewHolder holder, int position) {
        ViewCollab currentItem = mExampleList.get(position);
        String avatarUrl = currentItem.getAvatarLink();
        String fullname = currentItem.getFullname();

        holder.mTextViewTitle.setText(fullname);
        holder.mTextViewTitle.setVisibility(View.VISIBLE);
       // Logging.d("Avtar-->"+avatarUrl);
       // Logging.d("fullname-->"+fullname);
        //Logging.d("getUsername-->"+currentItem.getUsername());

        if (avatarUrl != null) {
            try {
                Glide.with(mContext).load(avatarUrl).into(holder.user_dp_iv);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int getItemCount() {
        return mExampleList.size();
    }


    public class ExampleViewHolder extends RecyclerView.ViewHolder{


        public TextView mTextViewTitle;
        public RoundedImageView user_dp_iv;
        public ExampleViewHolder(@NonNull View itemView) {
            super(itemView);
            mTextViewTitle = itemView.findViewById(R.id.textViewtitle);
            user_dp_iv = itemView.findViewById(R.id.user_dp_iv);
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
