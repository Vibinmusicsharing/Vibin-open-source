package com.shorincity.vibin.music_sharing.adapters;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.shorincity.vibin.music_sharing.R;
import com.shorincity.vibin.music_sharing.UI.custom.RoundedImageView;
import com.shorincity.vibin.music_sharing.databinding.PlaylistCollabMenuBinding;
import com.shorincity.vibin.music_sharing.databinding.PlaylistDetailsMenuBinding;
import com.shorincity.vibin.music_sharing.utils.CommonUtils;

import java.util.ArrayList;

// view collab adapter
public class PlaylistCollaboratosAdapter extends RecyclerView.Adapter<PlaylistCollaboratosAdapter.ExampleViewHolder> {

    private Context mContext;
    private ArrayList<ViewCollab> mExampleList;
    private CustomItemClickListener callback;

    public PlaylistCollaboratosAdapter(Context context, ArrayList<ViewCollab> exampleList, CustomItemClickListener callback) {
        mContext = context;
        mExampleList = exampleList;
        this.callback = callback;
    }


    @NonNull
    @Override
    public ExampleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_collaborator, parent, false);
        return new ExampleViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ExampleViewHolder holder, int position) {
        ViewCollab currentItem = mExampleList.get(position);
        if (currentItem != null) {
            String avatarUrl = currentItem.getAvatarLink();
            String fullname = currentItem.getFullname();

            holder.tvName.setText(fullname);

            if (avatarUrl != null) {
                try {
                    Glide.with(mContext).load(avatarUrl).circleCrop().into(holder.ivProfile);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            Glide.with(mContext)
                    .load(ContextCompat.getDrawable(holder.ivProfile.getContext(), R.drawable.ic_add_collab))
                    .circleCrop()
                    .into(holder.ivProfile);
            holder.tvName.setText("Add friend");
        }
    }

    @Override
    public int getItemCount() {
        return mExampleList.size();
    }


    public class ExampleViewHolder extends RecyclerView.ViewHolder {
        AppCompatImageView ivProfile;
        AppCompatTextView tvName;

        public ExampleViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            ivProfile = itemView.findViewById(R.id.ivProfile);

            ivProfile.setOnClickListener(v -> callback.onItemClick(0, getAdapterPosition()));
            ivProfile.setOnLongClickListener(v -> {
                LayoutInflater layoutInflater = (LayoutInflater) ivProfile.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                PlaylistCollabMenuBinding popupView = DataBindingUtil.inflate(layoutInflater, R.layout.playlist_collab_menu, null, false);

                PopupWindow popupWindow = new PopupWindow(
                        popupView.getRoot(),
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        CommonUtils.dpToPx(60, v.getContext()));

                popupView.llRemove.setOnClickListener(v1 -> {
                    popupWindow.dismiss();
                    callback.onItemClick(1, getAdapterPosition());
                });

                popupWindow.setBackgroundDrawable(new BitmapDrawable());
                popupWindow.setOutsideTouchable(true);
                popupWindow.showAsDropDown(v);
                return false;
            });
        }
    }

    public interface CustomItemClickListener {
        void onItemClick(int type, int position);
    }
}
