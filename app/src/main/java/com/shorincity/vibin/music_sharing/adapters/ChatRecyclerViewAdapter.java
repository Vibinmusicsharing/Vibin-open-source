package com.shorincity.vibin.music_sharing.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.shorincity.vibin.music_sharing.R;
import com.shorincity.vibin.music_sharing.model.realtime.ChatResponse;
import com.shorincity.vibin.music_sharing.model.realtime.RTJoinUpdate;
import com.shorincity.vibin.music_sharing.utils.GlideApp;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;


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
        int lastPos = position - 1;

        Object lastObj = null;
        if (lastPos > 0) {
            lastObj = chatMessages.get(lastPos);
        }

        try {
            if (holder instanceof JoinChatViewHolder) {
                ((JoinChatViewHolder) holder).contents.setText(((RTJoinUpdate) obj).getMessage());
            } else if (holder instanceof RightChatViewHolder) {
                ChatResponse chatResponse = (ChatResponse) obj;
                ((RightChatViewHolder) holder).contents.setText(chatResponse.getMessageData().getSenderMessage());
                ((RightChatViewHolder) holder).tvSenderName.setText(chatResponse.getMessageData().getSenderName());
                GlideApp.with(mContext).load(chatResponse.getMessageData().getSenderAvatar()).circleCrop().into(((RightChatViewHolder) holder).ivProfile);
                if (lastObj instanceof ChatResponse &&
                        ((ChatResponse) lastObj).getMessageData().getSenderId().equals(((ChatResponse) obj).getMessageData().getSenderId())) {
                    ((RightChatViewHolder) holder).ivProfile.setVisibility(View.INVISIBLE);
                } else {
                    ((RightChatViewHolder) holder).ivProfile.setVisibility(View.VISIBLE);
                }
            } else if (holder instanceof LeftChatViewHolder) {
                ChatResponse chatResponse = (ChatResponse) obj;
                ((LeftChatViewHolder) holder).contents.setText(chatResponse.getMessageData().getSenderMessage());
                ((LeftChatViewHolder) holder).tvSenderName.setText(chatResponse.getMessageData().getSenderName());
                GlideApp.with(mContext).load(chatResponse.getMessageData().getSenderAvatar()).circleCrop().into(((LeftChatViewHolder) holder).ivProfile);
                if (lastObj instanceof ChatResponse &&
                        ((ChatResponse) lastObj).getMessageData().getSenderId().equals(((ChatResponse) obj).getMessageData().getSenderId())) {
                    ((LeftChatViewHolder) holder).ivProfile.setVisibility(View.INVISIBLE);
                } else {
                    ((LeftChatViewHolder) holder).ivProfile.setVisibility(View.VISIBLE);
                }
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
        TextView contents, tvSenderName;
        AppCompatImageView ivProfile;

        public LeftChatViewHolder(View itemView) {
            super(itemView);
            contents = itemView.findViewById(R.id.chatItem_right_text);
            tvSenderName = itemView.findViewById(R.id.tvSenderName);
            ivProfile = itemView.findViewById(R.id.ivProfile);
        }
    }

    static class RightChatViewHolder extends ChatViewHolder {
        LinearLayout llBackground;
        TextView contents, tvSenderName;
        AppCompatImageView ivProfile;

        public RightChatViewHolder(View itemView) {
            super(itemView);
            llBackground = itemView.findViewById(R.id.llBackground);
            contents = itemView.findViewById(R.id.chatItem_right_text);
            tvSenderName = itemView.findViewById(R.id.tvSenderName);
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