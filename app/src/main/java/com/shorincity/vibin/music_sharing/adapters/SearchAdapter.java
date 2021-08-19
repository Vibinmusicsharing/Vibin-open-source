package com.shorincity.vibin.music_sharing.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.shorincity.vibin.music_sharing.R;

import java.util.ArrayList;
// search adapter for spotify
public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ExampleViewHolder> {

    private Context mContext;
    private ArrayList<searchItem> mExampleList;

    public SearchAdapter(Context context, ArrayList<searchItem> exampleList){
        mContext = context;
        mExampleList = exampleList;
    }


    @NonNull
    @Override
    public ExampleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_searchproduct_vertical,parent,false);
        return new ExampleViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ExampleViewHolder holder, int position) {
        searchItem currentItem = mExampleList.get(position);
        String title = currentItem.getmTitle();
        String id = currentItem.getPlaylistID();
        String priv = currentItem.getPrivatepublic();
        holder.mTextViewTitle.setText(title);
        if(priv.equals("true")) {
            holder.lockunlock.setImageResource(R.drawable.ic_lock_outline_black_24dp);
        }else{
            holder.lockunlock.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return mExampleList.size();
    }


    public class ExampleViewHolder extends RecyclerView.ViewHolder{

        public ImageView mImageView,lockunlock;
        public TextView mTextViewTitle;
        public ExampleViewHolder(@NonNull View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.imageView);
            mTextViewTitle = itemView.findViewById(R.id.textViewtitle);
            lockunlock = itemView.findViewById(R.id.lockunlock);

        }
    }
}
