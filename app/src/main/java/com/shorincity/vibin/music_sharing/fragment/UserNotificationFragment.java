package com.shorincity.vibin.music_sharing.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.shorincity.vibin.music_sharing.model.APIResponse;
import com.shorincity.vibin.music_sharing.model.GetNotifications;
import com.shorincity.vibin.music_sharing.R;
import com.shorincity.vibin.music_sharing.UI.SharedPrefManager;
import com.shorincity.vibin.music_sharing.UI.spotify;
import com.shorincity.vibin.music_sharing.UI.youtube;
import com.shorincity.vibin.music_sharing.adapters.NotificationsAdapter;
import com.shorincity.vibin.music_sharing.service.DataAPI;
import com.shorincity.vibin.music_sharing.service.RetrofitAPI;
import com.shorincity.vibin.music_sharing.utils.AppConstants;
import com.shorincity.vibin.music_sharing.utils.Logging;
import com.google.gson.Gson;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserNotificationFragment extends Fragment {

    View view;
    RecyclerView unreadNotificationRv, readNotificationRv;
    NotificationsAdapter unreadNotificationsAdapter, readNotificationsAdapter;
    ArrayList<GetNotifications> unreadNotificationList , readNotificationList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        if(view == null) {

            view = inflater.inflate(R.layout.fragment_notifications, container, false);
            unreadNotificationRv = view.findViewById(R.id.notification_unread_rv);
            readNotificationRv = view.findViewById(R.id.notification_read_rv);

            view.findViewById(R.id.mark_read_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callMarkAllNotificationReadAPI();
                }
            });

            setAdapter();
        }

        return  view;
    }

    private void setAdapter() {
        unreadNotificationList = new ArrayList<>();
        unreadNotificationRv.setLayoutManager(new LinearLayoutManager(getActivity()));

        unreadNotificationsAdapter = new NotificationsAdapter(getActivity(), unreadNotificationList);
        unreadNotificationsAdapter.setCustomItemClickListener(new NotificationsAdapter.CustomItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {

            }

            @Override
            public void onChildItemClick(View v, int position, String viewName) {
                if(viewName.equalsIgnoreCase("ACCEPTED")) {

                }
            }
        });
        unreadNotificationRv.setAdapter(unreadNotificationsAdapter);

        readNotificationList = new ArrayList<>();
        readNotificationRv.setLayoutManager(new LinearLayoutManager(getActivity()));

        readNotificationsAdapter = new NotificationsAdapter(getActivity(), readNotificationList);
        readNotificationsAdapter.setCustomItemClickListener(new NotificationsAdapter.CustomItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {

            }

            @Override
            public void onChildItemClick(View v, int position, String viewName) {

            }
        });
        readNotificationRv.setAdapter(readNotificationsAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();

        if(getActivity() instanceof youtube) {
            ((youtube)getActivity()).mSlidingLayout.setSlidingEnable(false);
            ((youtube)getActivity()).mSlidingLayout.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });
        } else if(getActivity() instanceof spotify) {
            ((spotify)getActivity()).mSlidingLayout.setSlidingEnable(false);
            ((spotify)getActivity()).mSlidingLayout.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // It must to call to close setting navigation from Notification.
        if(getActivity() instanceof youtube) {
            ((youtube) getActivity()).mSlidingLayout.closePane();
        } else if(getActivity() instanceof spotify) {
            ((spotify) getActivity()).mSlidingLayout.closePane();
        }

        // By calling in resume, API will always refresh whenever user will come in notification
        callGetNotificationsAPI();
    }

    // API to get all the notifications
    private void callGetNotificationsAPI() {

        ((ProgressBar) view.findViewById(R.id.progressbar)).setVisibility(View.VISIBLE);
        DataAPI dataAPI = RetrofitAPI.getData();

        String token = AppConstants.TOKEN + SharedPrefManager.getInstance(getActivity()).getSharedPrefString(AppConstants.INTENT_USER_API_TOKEN);
        int userId =  SharedPrefManager.getInstance(getActivity()).getSharedPrefInt
         (AppConstants
                .INTENT_USER_ID);

        Call<ArrayList<GetNotifications>> callback = dataAPI.getNotifications(token, userId);
        callback.enqueue(new Callback<ArrayList<GetNotifications>>() {
            @Override
            public void onResponse(Call<ArrayList<GetNotifications>> call, Response<ArrayList<GetNotifications>> response) {
                ((ProgressBar) view.findViewById(R.id.progressbar)).setVisibility(View.GONE);
                unreadNotificationList.clear();
                readNotificationList.clear();
               // Logging.d("TEST","callGetNotificationsAPI res-->"+new Gson().toJson(response.body()));

                if (response != null && response.body()!= null && response.body().size() > 0) {

                    Logging.d("TEST","callGetNotificationsAPI onResponse Called");
                    // Filtering the list as need to display separately

                    for (int i = 0; i < response.body().size(); i++) {
                        if(response.body().get(i).getReadStatus()) {
                            readNotificationList.add(response.body().get(i));
                            Logging.d("TEST", "callGetNotificationsAPI onResponse for loop Called");
                        }else
                        { unreadNotificationList.add(response.body().get(i));}
                    }

                    // Displaying Unread Notifications List
                    if(unreadNotificationList.size() == 0) {
                        Logging.d("TEST","callGetNotificationsAPI onResponse Size 0 Called");
                        view.findViewById(R.id.unread_head_hldr).setVisibility(View.GONE);
                        unreadNotificationRv.setVisibility(View.GONE);
                    } else {
                        Logging.d("TEST","callGetNotificationsAPI onResponse Size 0 else Called");
                        view.findViewById(R.id.unread_head_hldr).setVisibility(View.VISIBLE);
                        unreadNotificationRv.setVisibility(View.VISIBLE);
                    }

                    // Displaying Read Notifications List
                    if(readNotificationList.size() == 0) {
                        view.findViewById(R.id.read_head_hldr).setVisibility(View.GONE);
                        readNotificationRv.setVisibility(View.GONE);
                    } else {
                        view.findViewById(R.id.read_head_hldr).setVisibility(View.VISIBLE);
                        readNotificationRv.setVisibility(View.VISIBLE);
                    }

                    unreadNotificationsAdapter.notifyDataSetChanged();
                    readNotificationsAdapter.notifyDataSetChanged();
                } else {

                }
            }

            @Override
            public void onFailure(Call<ArrayList<GetNotifications>> call, Throwable t) {
                ((ProgressBar) view.findViewById(R.id.progressbar)).setVisibility(View.GONE);
                Logging.d("TEST","callGetNotificationsAPI onFailure Called");
        }
        });
    }


    // API to mark all the notifications as read
    private void callMarkAllNotificationReadAPI() {

        DataAPI dataAPI = RetrofitAPI.getData();
        String headerToken = AppConstants.TOKEN + SharedPrefManager.getInstance(getActivity()).getSharedPrefString(AppConstants.INTENT_USER_API_TOKEN);
        int userId = SharedPrefManager.getInstance(getActivity()).getSharedPrefInt(AppConstants.INTENT_USER_ID);

        Call<APIResponse> callback = dataAPI.markAllNotificationRead(headerToken,  userId);
        callback.enqueue(new Callback<APIResponse>() {
            @Override
            public void onResponse(Call<APIResponse> call, Response<APIResponse> response) {
                //progressBar.setVisibility(View.GONE);
                if (response != null && response.body() != null) {

                    if (response.body().getStatus().equalsIgnoreCase("success")) {
                        Logging.d("TEST","callMarkAllNotificationReadAPI onResponse Called");
                        SharedPrefManager.getInstance(getActivity()).setSharedPrefInt(AppConstants.INTENT_NOTIFICATION_UNREAD_COUNT, 0);

                        readNotificationList.addAll(0, unreadNotificationList);

                        unreadNotificationList.clear();
                        view.findViewById(R.id.unread_head_hldr).setVisibility(View.GONE);
                        unreadNotificationRv.setVisibility(View.GONE);

                        view.findViewById(R.id.read_head_hldr).setVisibility(View.VISIBLE);
                        readNotificationRv.setVisibility(View.VISIBLE);

                        unreadNotificationsAdapter.notifyDataSetChanged();
                        readNotificationsAdapter.notifyDataSetChanged();

                    } else {
                        Logging.d("TEST","callMarkAllNotificationReadAPI onResponse else Called");
                    }
                } else {
                }
            }

            @Override
            public void onFailure(Call<APIResponse> call, Throwable t) {
                Log.i("Error: " , "ADD NOTIFICATION "+t.getMessage());
                Logging.d("TEST","callMarkAllNotificationReadAPI onFailure Called");
            }
        });

    }
}
