package com.shorincity.vibin.music_sharing.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.shorincity.vibin.music_sharing.R;
import com.shorincity.vibin.music_sharing.UI.custom.RoundedImageView;
import com.shorincity.vibin.music_sharing.model.CollabsList;
import com.shorincity.vibin.music_sharing.utils.GlideApp;

import java.util.ArrayList;

public class CollaboratorListing extends RecyclerView.Adapter<CollaboratorListing.ExampleViewHolder> {
    Context context;
    ArrayList<CollabsList.UserData> maincollabslist;

    public CollaboratorListing(Context applicationContext, ArrayList<CollabsList.UserData> maincollabslist) {
        this.context = applicationContext;
        this.maincollabslist = maincollabslist;
    }

    @NonNull
    @Override
    public ExampleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(context).inflate(R.layout.collabslisting, parent, false);

        final ExampleViewHolder mViewHolder = new ExampleViewHolder(v);
        return mViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ExampleViewHolder holder, int position) {
        CollabsList.UserData userData = maincollabslist.get(position);
        ((ExampleViewHolder) holder).setData(userData, position);
    }

    @Override
    public int getItemCount() {
        return maincollabslist.size();
    }


    public class ExampleViewHolder extends RecyclerView.ViewHolder {

        public TextView mUsername, mFullname, type_playlist;
        public CardView card_collabs;
        public RoundedImageView mImageView;

        public ExampleViewHolder(@NonNull View itemView) {
            super(itemView);
            mUsername = itemView.findViewById(R.id.item_username_collab);
            type_playlist = itemView.findViewById(R.id.type_playlist);
            mFullname = itemView.findViewById(R.id.item_fullname_collab);
            mImageView = itemView.findViewById(R.id.avatar_image_collab);
            card_collabs = itemView.findViewById(R.id.card_collabs);
        }

        public void setData(CollabsList.UserData currentItem, int position) {
            mUsername.setText(currentItem.getUsername());
            mFullname.setText(currentItem.getFullname());
            type_playlist.setText(currentItem.getType());
            card_collabs.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });

            String avatarUrl = currentItem.getAvatar_link();

            if (!TextUtils.isEmpty(avatarUrl)) {
                try {
                    GlideApp.with(context).load(avatarUrl).into(mImageView);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                mImageView.setImageResource(R.drawable.default_img_black);
            }
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
