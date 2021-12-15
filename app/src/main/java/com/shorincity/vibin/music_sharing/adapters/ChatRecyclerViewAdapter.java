package com.shorincity.vibin.music_sharing.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.shorincity.vibin.music_sharing.R;
import com.shorincity.vibin.music_sharing.model.ChatMessage;
import com.shorincity.vibin.music_sharing.model.realtime.ChatResponse;
import com.shorincity.vibin.music_sharing.model.realtime.RTJoinUpdate;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


public class ChatRecyclerViewAdapter extends RecyclerView.Adapter<ChatRecyclerViewAdapter.ChatViewHolder> {

    private static final int ITEM_LEFT = 0;
    private static final int ITEM_RIGHT = 1;
    private static final int ITEM_JOIN = 2;

    private Context mContext;
    private ArrayList<Object> chatMessages;
    private int userId;

    public ChatRecyclerViewAdapter(Context context, ArrayList<Object> chatMessages, int userId) {
        mContext = context;
        this.chatMessages = chatMessages;
        this.userId = userId;
    }

    @Override
    public int getItemViewType(int position) {
        Object mBean = chatMessages.get(position);
        if (mBean instanceof RTJoinUpdate) {
            return ITEM_JOIN;
        } else if (mBean instanceof ChatResponse) {
            return ((ChatResponse) mBean).getMessageData().getSenderId() == userId ? ITEM_RIGHT : ITEM_LEFT;
        } else
            return ITEM_RIGHT;
    }

    @Override
    public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        switch (viewType) {
            case ITEM_LEFT:
                return new LeftChatViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.chatitem_left, parent, false));
            case ITEM_RIGHT:
                return new RightChatViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.chatitem_right, parent, false));
            case ITEM_JOIN:
                return new JoinChatViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.chatitem_join_user, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(ChatViewHolder holder, int position) {
        Object obj = chatMessages.get(position);

        try {
            if (holder instanceof JoinChatViewHolder) {
                ((JoinChatViewHolder) holder).contents.setText(((RTJoinUpdate) obj).getMessage());
            } else if (holder instanceof RightChatViewHolder) {
                ChatResponse chatResponse = (ChatResponse) obj;
                ((RightChatViewHolder) holder).contents.setText(chatResponse.getMessageData().getSenderMessage());
                Glide.with(mContext).load(chatResponse.getMessageData().getSenderAvatar()).circleCrop().into(((RightChatViewHolder) holder).ivProfile);
            } else if (holder instanceof LeftChatViewHolder) {
                ChatResponse chatResponse = (ChatResponse) obj;
                ((LeftChatViewHolder) holder).contents.setText(chatResponse.getMessageData().getSenderMessage());
                Glide.with(mContext).load(chatResponse.getMessageData().getSenderAvatar()).circleCrop().into(((LeftChatViewHolder) holder).ivProfile);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder {
        public ChatViewHolder(View itemView) {
            super(itemView);
        }
    }

    static class LeftChatViewHolder extends ChatViewHolder {
        TextView contents;
        AppCompatImageView ivProfile;

        public LeftChatViewHolder(View itemView) {
            super(itemView);
            contents = itemView.findViewById(R.id.chatItem_right_text);
            ivProfile = itemView.findViewById(R.id.ivProfile);
        }
    }

    static class RightChatViewHolder extends ChatViewHolder {
        TextView contents;
        AppCompatImageView ivProfile;

        public RightChatViewHolder(View itemView) {
            super(itemView);
            contents = (TextView) itemView.findViewById(R.id.chatItem_right_text);
            ivProfile = itemView.findViewById(R.id.ivProfile);
        }
    }

    static class JoinChatViewHolder extends ChatViewHolder {
        TextView contents;

        public JoinChatViewHolder(View itemView) {
            super(itemView);
            contents = (TextView) itemView.findViewById(R.id.chatItem_right_text);
        }
    }

    public String getRequiredTime(String timeStampStr) {
        try {
            DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date netDate = (new Date(Long.parseLong(timeStampStr)));
            return sdf.format(netDate);
        } catch (Exception ignored) {
            return "xx";
        }
    }

}