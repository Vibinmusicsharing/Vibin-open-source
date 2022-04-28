package com.shorincity.vibin.music_sharing.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.shorincity.vibin.music_sharing.R;
import com.shorincity.vibin.music_sharing.UI.custom.RoundedImageView;
import com.shorincity.vibin.music_sharing.model.UserLikeList;
import com.shorincity.vibin.music_sharing.utils.GlideApp;

import java.util.ArrayList;

import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;

// playlist recycler view adapter
public class UserLikesAdapter extends RecyclerView.Adapter<UserLikesAdapter.ExampleViewHolder> {

    private Context mContext;
    private ArrayList<UserLikeList.GotLikes> list;

    public UserLikesAdapter(Context context, ArrayList<UserLikeList.GotLikes> exampleList) {
        mContext = context;
        list = exampleList;
    }


    @NonNull
    @Override
    public ExampleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.layout_likes_listing, parent, false);
        final ExampleViewHolder mViewHolder = new ExampleViewHolder(v);
        return mViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ExampleViewHolder holder, int position) {
        UserLikeList.GotLikes gotLikes = list.get(position);
        ((ExampleViewHolder) holder).setData(gotLikes, position);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class ExampleViewHolder extends RecyclerView.ViewHolder {

        public TextView mUsername, mFullname;
        public CardView card_likes;
        public CardView collabCv;
        public CircularProgressButton addCollabBtn;
        public Button addCollabStatusBtn;
        public RoundedImageView mImageView;

        public ExampleViewHolder(@NonNull View itemView) {
            super(itemView);
            //mImageView = itemView.findViewById(R.id.item_img);
            mUsername = itemView.findViewById(R.id.item_username);
            mFullname = itemView.findViewById(R.id.item_fullname);
            collabCv = itemView.findViewById(R.id.add_collab_card_view);
            addCollabBtn = itemView.findViewById(R.id.add_collab_btn);
            addCollabStatusBtn = itemView.findViewById(R.id.add_collab_status);
            mImageView = itemView.findViewById(R.id.avatar_image);
            card_likes = itemView.findViewById(R.id.card_likes);
        }

        public void setData(UserLikeList.GotLikes currentItem, int position) {
            mUsername.setText("@" + currentItem.getUsername());
            mFullname.setText(currentItem.getFullname());

            card_likes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                }
            });
            String avatarUrl = currentItem.getAvatar_link();

            if (!TextUtils.isEmpty(avatarUrl)) {
                try {
                    GlideApp.with(mContext).load(avatarUrl).into(mImageView);
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
