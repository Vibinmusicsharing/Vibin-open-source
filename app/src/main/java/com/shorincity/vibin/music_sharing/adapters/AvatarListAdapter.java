package com.shorincity.vibin.music_sharing.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.shorincity.vibin.music_sharing.R;
import com.shorincity.vibin.music_sharing.UI.custom.RoundedImageView;
import com.shorincity.vibin.music_sharing.model.avatar.Avatar;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

// playlist recycler view adapter
public class AvatarListAdapter extends RecyclerView.Adapter<AvatarListAdapter.ViewHolder> {

    private Context mContext;
    private List<Avatar> list;

    public AvatarListAdapter(Context context) {
        mContext = context;
    }

    public void setList(List<Avatar> exampleList) {
        list = exampleList;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_avatar, parent, false);

        final ViewHolder mViewHolder = new ViewHolder(v);

        return mViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Avatar currentItem = list.get(position);
        if (currentItem != null) {
            try {
                String imageUrl = currentItem.getLink();
                Glide.with(mContext).load(imageUrl).into(holder.mImageView);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (currentItem.isSelected()) {
                holder.avatar_hover_image.setVisibility(View.VISIBLE);
            } else {
                holder.avatar_hover_image.setVisibility(View.GONE);
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setSelected(position);
                    if (customItemClickListener != null) {
                        customItemClickListener.onItemClick(currentItem, position);
                    }
                }
            });

        }
    }

    private void setSelected(int position) {
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                if (position == i) {
                    list.get(i).setSelected(true);
                } else {
                    list.get(i).setSelected(false);
                }

            }
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public RoundedImageView mImageView;
        public ImageView avatar_hover_image;
        public RelativeLayout avatar_view;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            avatar_hover_image = itemView.findViewById(R.id.avatar_hover_image);
            mImageView = itemView.findViewById(R.id.avatar_image);
            avatar_view = itemView.findViewById(R.id.avatar_view);

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
        public void onItemClick(Avatar item, int position);
    }
}
