package com.shorincity.vibin.music_sharing.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.shorincity.vibin.music_sharing.R;
import com.shorincity.vibin.music_sharing.UI.custom.RoundedImageView;
import com.shorincity.vibin.music_sharing.utils.GlideApp;

import java.util.ArrayList;

// view collab adapter
public class ViewCollabAdapter extends RecyclerView.Adapter<ViewCollabAdapter.ExampleViewHolder> {

    private Context mContext;
    private ArrayList<ViewCollab> mExampleList;

    public ViewCollabAdapter(Context context, ArrayList<ViewCollab> exampleList) {
        mContext = context;
        mExampleList = exampleList;
    }


    @NonNull
    @Override
    public ExampleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_collab_xml, parent, false);
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

        if (currentItem.isEditable()) {
            holder.checkbox_collabs.setVisibility(View.VISIBLE);
        } else {
            holder.checkbox_collabs.setVisibility(View.GONE);
        }
        if (currentItem.isSelected()) {
            holder.checkbox_collabs.setChecked(true);
        } else {
            holder.checkbox_collabs.setChecked(false);
        }

        holder.checkbox_collabs.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                ((PlaylistDetailActivity) mContext).selectedcollabsID(currentItem.getId(), b);
                customItemClickListener.onSelectedcollabsID(currentItem.getId(), b);
                currentItem.setSelected(b);
            }
        });

        if (avatarUrl != null) {
            try {
                GlideApp.with(mContext).load(avatarUrl).into(holder.user_dp_iv);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int getItemCount() {
        return mExampleList.size();
    }


    public class ExampleViewHolder extends RecyclerView.ViewHolder {

        public CheckBox checkbox_collabs;
        public TextView mTextViewTitle;
        public RoundedImageView user_dp_iv;

        public ExampleViewHolder(@NonNull View itemView) {
            super(itemView);
            mTextViewTitle = itemView.findViewById(R.id.textViewtitle);
            user_dp_iv = itemView.findViewById(R.id.user_dp_iv);
            checkbox_collabs = itemView.findViewById(R.id.checkbox_collabs);
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

        void onSelectedcollabsID(int ids, boolean selected);
    }
}
