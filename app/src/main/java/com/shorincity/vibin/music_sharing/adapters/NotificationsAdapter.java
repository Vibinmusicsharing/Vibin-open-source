package com.shorincity.vibin.music_sharing.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.shorincity.vibin.music_sharing.UI.custom.RoundedImageView;
import com.shorincity.vibin.music_sharing.model.APIResponse;
import com.shorincity.vibin.music_sharing.model.GetNotifications;
import com.shorincity.vibin.music_sharing.model.UpdateNotificationModel;
import com.shorincity.vibin.music_sharing.R;
import com.shorincity.vibin.music_sharing.UI.SharedPrefManager;
import com.shorincity.vibin.music_sharing.activity.OtherUserProfileActivity;
import com.shorincity.vibin.music_sharing.activity.RealTimePlayer;
import com.shorincity.vibin.music_sharing.service.DataAPI;
import com.shorincity.vibin.music_sharing.service.RetrofitAPI;
import com.shorincity.vibin.music_sharing.utils.AppConstants;
import com.shorincity.vibin.music_sharing.utils.Logging;


import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.ExampleViewHolder> {

    private Context mContext;
    private ArrayList<GetNotifications> list;
    int playlistId, receiverID,senderID;
    private final ViewBinderHelper viewBinderHelper = new ViewBinderHelper();

    public NotificationsAdapter(Context context, ArrayList<GetNotifications> exampleList){
        mContext = context;
        list = exampleList;
    }

    @NonNull
    @Override
    public ExampleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_notification_swipe,parent,false);

        final ExampleViewHolder mViewHolder = new ExampleViewHolder(v);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customItemClickListener.onItemClick(v, mViewHolder.getPosition());
            }
        });

        return mViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ExampleViewHolder holder, int position) {
        GetNotifications currentItem = list.get(position);



        if (!TextUtils.isEmpty(currentItem.getIsAccepted()) && currentItem.getIsAccepted().equalsIgnoreCase(AppConstants.REGECTED)) {
            holder.mainRl.setLayoutParams(new AbsListView.LayoutParams(-1, 1));
            holder.mainRl.setVisibility(View.GONE);
        } else {

            String avatarUrl = currentItem.getAvatarLink();

            if (!TextUtils.isEmpty(avatarUrl)) {
                try {
                    Glide.with(mContext).load(avatarUrl).into(holder.mImageView);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            holder.mainRl.setVisibility(View.VISIBLE);

            // making first word click as it's an user name

            String msgStr = currentItem.getMessage();
            String arr[] = msgStr.split(" ", 2);

            String firstWord = arr[0];

            SpannableString ss = new SpannableString(msgStr);
            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(View textView) {
                    mContext.startActivity(new Intent(mContext, OtherUserProfileActivity.class)
                                    .putExtra(AppConstants.INTENT_SEARCHED_USER_ID, currentItem.getSender())
                                    .putExtra(AppConstants.INTENT_SEARCHED_USER_NAME, firstWord)
                            //.putExtra(AppConstants.INTENT_SEARCHED_FULL_NAME,usersList.get(position).getFullname())
                    );
                }
            };
            ss.setSpan(clickableSpan, 0, firstWord.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ss.setSpan(new ForegroundColorSpan(Color.parseColor("#4698FF")), 0, firstWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            holder.titleTv.setMovementMethod(LinkMovementMethod.getInstance());
            holder.titleTv.setText(ss);

            holder.notifyStatusTv.setVisibility(View.GONE);

            if ((currentItem.getType().equalsIgnoreCase(AppConstants.COLLAB_REQUEST) || currentItem.getType().equalsIgnoreCase(AppConstants.COLLAB_INVITE) || currentItem.getType().equalsIgnoreCase(AppConstants.REAL_TIME_INVITE)
                    || currentItem.getType().equalsIgnoreCase(AppConstants.COLLAB_REQUEST_RESPONDED)
                    || currentItem.getType().equalsIgnoreCase(AppConstants.COLLAB_INVITE_RESPONDED))
                    && currentItem.getIsAccepted().equalsIgnoreCase("PENDING")) {
               // holder.acceptIgnoreHldr.setVisibility(View.VISIBLE);
                holder.swipelayout.setLockDrag(false);
            } else if (currentItem.getIsAccepted().equalsIgnoreCase("ACCEPTED")) {
               // holder.acceptIgnoreHldr.setVisibility(View.GONE);
                holder.swipelayout.setLockDrag(true);
                holder.notifyStatusTv.setVisibility(View.VISIBLE);
            } else {
               // holder.acceptIgnoreHldr.setVisibility(View.GONE);
                holder.swipelayout.setLockDrag(true);
            }

            if (currentItem.getReadStatus())
                holder.mainCardView.setAlpha(1f);
            else {
                holder.mainCardView.setAlpha(0.5f);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        callMarkAllNotificationReadAPI(holder.mainCardView, position);
                    }
                }, 1 * 1000);
            }

            holder.acceptBtn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    holder.swipelayout.close(true);
                    holder.swipelayout.setLockDrag(true);
                    receiverID =  list.get(position).getSender();//Swap for notify
                    playlistId = list.get(position).getPlaylist();
                    senderID = list.get(position).getReceiver();//Swap for notify


                    getCustomItemClickListener().onChildItemClick(v, position, AppConstants.ACCEPTED);

                    if (list.get(position).getType().equals(AppConstants.COLLAB_REQUEST) ||
                            list.get(position).getType().equals(AppConstants.COLLAB_INVITE)) {

                        callAddCollaborateAPI(list.get(position), AppConstants.ACCEPTED,
                                AppConstants.UPDATE);

                    } else if (list.get(position).getType().equals(AppConstants.REAL_TIME_INVITE)||
                            list.get(position).getType().equals(AppConstants.COLLAB_ACCEPTED)) {

                        callUpdateCollabAPI(list.get(position), AppConstants.ACCEPTED, AppConstants.GET);
                    }


                    sendCollabAcceptedNotification(senderID,receiverID,playlistId);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                          //  holder.acceptIgnoreHldr.setVisibility(View.GONE);
                            //holder.swipelayout.setLockDrag(true);
                            holder.swipelayout.close(true);
                            holder.swipelayout.setLockDrag(true);
                            holder.notifyStatusTv.setVisibility(View.VISIBLE);
                        }
                    }, 2000);
                }
            });

            holder.ignoreBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.swipelayout.close(true);
                    holder.swipelayout.setLockDrag(true);
                    getCustomItemClickListener().onChildItemClick(v, position, "IGNORED");

                    callUpdateCollabAPI(list.get(position), AppConstants.REGECTED, AppConstants.UPDATE);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                           // holder.acceptIgnoreHldr.setVisibility(View.GONE);
                            holder.swipelayout.close(true);
                            holder.swipelayout.setLockDrag(true);
                            list.remove(position);
                            notifyDataSetChanged();
                        }
                    }, 1000);

                }
            });
        }

    }


    private void sendCollabAcceptedNotification(int senderID,int receiverID, int playlistId) {

//        @Header("Authorization") String token,
//        @Field("sender") int senderId,
//        @Field("receiver") int receiverId,
//        @Field("playlist") int playlist,
//        @Field("type") String type);

        DataAPI dataAPI = RetrofitAPI.getData();
        String headerToken = AppConstants.TOKEN + SharedPrefManager.getInstance(mContext).getSharedPrefString(AppConstants.INTENT_USER_API_TOKEN);

        Logging.d("searchedUserId:"+receiverID+ " playlistId:"+playlistId +" userId:"+senderID);

        Call<APIResponse> callback = dataAPI.sendNotification(headerToken, senderID,receiverID,
                playlistId, AppConstants.COLLAB_ACCEPTED);
        callback.enqueue(new Callback<APIResponse>() {
            @Override
            public void onResponse(Call<APIResponse> call, retrofit2.Response<APIResponse> response) {
                //progressBar.setVisibility(View.GONE);
                Logging.d("Send Noti response:"+response);
                if (response != null && response.body() != null) {

//                    if (response.body().getStatus().equalsIgnoreCase("success")) {
//                        Toast.makeText(mContext,response.body().getMessage(),Toast.LENGTH_LONG).show();
//                        Logging.d("TEST","success : " + response.body().getMessage());
//
//                    } else {
//                        Logging.d("TEST","Request couldn't be placed, Please try again later!");
//
//                        Toast.makeText(mContext,"Request couldn't be placed, Please try again later!",Toast.LENGTH_LONG).show();
//                    }
                } else {
                    Logging.d("TEST","Something went wrong ");

                }
            }

            @Override
            public void onFailure(Call<APIResponse> call, Throwable t) {
                Logging.d("TEST","Error : " + t.getMessage());
                //Log.i("Error: " , "ADD NOTIFICATION "+t.getMessage());
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }



    public class ExampleViewHolder extends RecyclerView.ViewHolder{

        public TextView titleTv, notifyStatusTv;
        public CardView mainRl;
        public LinearLayout acceptIgnoreHldr;
        public Button acceptBtn, ignoreBtn;
        public CardView mainCardView;
        public RoundedImageView mImageView;
        public SwipeRevealLayout swipelayout;

        public ExampleViewHolder(@NonNull View itemView) {
            super(itemView);
            mainRl = itemView.findViewById(R.id.item_view);
            mainCardView = itemView.findViewById(R.id.main_view);
            acceptIgnoreHldr = itemView.findViewById(R.id.accept_ignore_hldr);
            titleTv = itemView.findViewById(R.id.item_title);
            notifyStatusTv = itemView.findViewById(R.id.notify_status);
            acceptBtn = itemView.findViewById(R.id.accept_btn);
            ignoreBtn = itemView.findViewById(R.id.ignore_btn);
            mImageView = itemView.findViewById(R.id.avatar_image);
            swipelayout = itemView.findViewById(R.id.swipelayout);
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
        public void onChildItemClick(View v, int position, String viewName);

    }

    private void callMarkAllNotificationReadAPI(View mainView, int position) {

        DataAPI dataAPI = RetrofitAPI.getData();
        String headerToken = AppConstants.TOKEN + SharedPrefManager.getInstance(mContext).getSharedPrefString(AppConstants.INTENT_USER_API_TOKEN);

        Call<APIResponse> callback = dataAPI.markNotificationRead(headerToken,  list.get(position).getId());
        callback.enqueue(new Callback<APIResponse>() {
            @Override
            public void onResponse(Call<APIResponse> call, Response<APIResponse> response) {
                //progressBar.setVisibility(View.GONE);
                if (response != null && response.body() != null) {

                    if (response.body().getStatus().equalsIgnoreCase("success")) {

                        //mainView.setBackgroundColor(ContextCompat.getColor(mContext,R.color.nav_white));
                        try {
                            mainView.setAlpha(1f);
                            if(list!=null && !list.isEmpty()){
                                list.get(position).setReadStatus(true);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } else {
                    }
                } else {
                }
            }

            @Override
            public void onFailure(Call<APIResponse> call, Throwable t) {
                Log.i("Error: " , "ADD NOTIFICATION "+t.getMessage());
            }
        });

    }

    private void callAddCollaborateAPI(GetNotifications getNotifications, String status, String type) {

        DataAPI dataAPI = RetrofitAPI.getData();

        String token = AppConstants.TOKEN + SharedPrefManager.getInstance(mContext).getSharedPrefString(AppConstants.INTENT_USER_API_TOKEN);

        Call<APIResponse> callback;

        if (getNotifications.getType().equals(AppConstants.COLLAB_REQUEST)) {
            callback = dataAPI.addCollab(token, getNotifications.getPlaylist(), getNotifications.getSender());
        } else {
            callback = dataAPI.addCollab(token, getNotifications.getPlaylist(), getNotifications.getReceiver());
        }

        callback.enqueue(new Callback<APIResponse>() {
            @Override
            public void onResponse(Call<APIResponse> call, retrofit2.Response<APIResponse> response) {
                if (response != null
                        && response.body() != null) {
                    if (response.body().getStatus().equalsIgnoreCase("success")) {
                        callUpdateCollabAPI(getNotifications, status, type);
                    }
                    else if(response.body().getStatus().equalsIgnoreCase("failed") && !TextUtils.isEmpty(response.body().getMessage()))
                        Toast.makeText(mContext,response.body().getMessage(), Toast.LENGTH_LONG).show();
                    else
                        Toast.makeText(mContext,"Something went wrong!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<APIResponse> call, Throwable t) {
                Toast.makeText(mContext,"Something went wrong!", Toast.LENGTH_LONG).show();
            }
        });

    }

    private void callUpdateCollabAPI(GetNotifications getNotifications, String status, String type) {

        DataAPI dataAPI = RetrofitAPI.getData();

        String token = AppConstants.TOKEN + SharedPrefManager.getInstance(mContext).getSharedPrefString(AppConstants.INTENT_USER_API_TOKEN);

        Call<UpdateNotificationModel> callback = dataAPI.updateCollabNotifyStatus(token,
                getNotifications.getPlaylist(), getNotifications.getSender(), getNotifications.getReceiver(), getNotifications.getId(), status, type);

        callback.enqueue(new Callback<UpdateNotificationModel>() {
            @Override
            public void onResponse(Call<UpdateNotificationModel> call, retrofit2.Response<UpdateNotificationModel> response) {
                if (response != null
                        && response.body() != null) {
                    if (response.body().getStatus().equalsIgnoreCase("success")) {

                        if (status.equals(AppConstants.ACCEPTED) && getNotifications.getType().equals(AppConstants.REAL_TIME_INVITE)) {

                            UpdateNotificationModel updateNotificationModel = response.body();
                            mContext.startActivity(new Intent(mContext, RealTimePlayer.class)
                                    .putExtra(AppConstants.INTENT_COMING_FROM, AppConstants.NOTIFICATION)
                                    .putExtra(AppConstants.INTENT_SESSION_KEY, updateNotificationModel.getSessionKey())
                                    .putExtra(AppConstants.INTENT_USER_KEY, updateNotificationModel.getUserSessionKeys())
                                    .putExtra(AppConstants.INTENT_PLAYLIST_ID, updateNotificationModel.getPlaylistId()));
                        }
                    }
                    else
                        Toast.makeText(mContext,"Something went wrong!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<UpdateNotificationModel> call, Throwable t) {
                Toast.makeText(mContext,"Something went wrong!", Toast.LENGTH_LONG).show();
            }
        });

    }

}

