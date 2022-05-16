package com.shorincity.vibin.music_sharing.adapters;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.shorincity.vibin.music_sharing.R;
import com.shorincity.vibin.music_sharing.databinding.PlaylistCollabMenuBinding;
import com.shorincity.vibin.music_sharing.utils.CommonUtils;
import com.shorincity.vibin.music_sharing.utils.GlideApp;

import java.util.ArrayList;

// view collab adapter
public class PlaylistCollaboratosAdapter extends RecyclerView.Adapter<PlaylistCollaboratosAdapter.ExampleViewHolder> {

    private Context mContext;
    private ArrayList<ViewCollab> mExampleList;
    private CustomItemClickListener callback;
    private boolean isAdmin;
    private int userId;

    public PlaylistCollaboratosAdapter(Context context, ArrayList<ViewCollab> exampleList, boolean isAdmin, int userId, CustomItemClickListener callback) {
        mContext = context;
        mExampleList = exampleList;
        this.callback = callback;
        this.isAdmin = isAdmin;
        this.userId = userId;
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
                    GlideApp.with(mContext).load(avatarUrl).circleCrop().into(holder.ivProfile);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            holder.ivAdminBorder.setVisibility(currentItem.isAdmin() ? View.VISIBLE : View.INVISIBLE);
        } else {
            GlideApp.with(mContext)
                    .load(ContextCompat.getDrawable(holder.ivProfile.getContext(), R.drawable.ic_add_collab))
                    .circleCrop()
                    .into(holder.ivProfile);
            holder.tvName.setText("Add friend");
            holder.ivAdminBorder.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return mExampleList.size();
    }


    public class ExampleViewHolder extends RecyclerView.ViewHolder {
        AppCompatImageView ivProfile, ivAdminBorder;
        AppCompatTextView tvName;

        public ExampleViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            ivProfile = itemView.findViewById(R.id.ivProfile);
            ivAdminBorder = itemView.findViewById(R.id.ivAdminBorder);

            ivProfile.setOnClickListener(v -> callback.onItemClick(0, getAdapterPosition()));
            ivProfile.setOnLongClickListener(v -> {
                if (isAdmin && mExampleList.get(getAdapterPosition()) != null && mExampleList.get(getAdapterPosition()).getId() != userId) {

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
                }
                return false;
            });
        }
    }

    public interface CustomItemClickListener {
        void onItemClick(int type, int position);
    }
}
