package com.shorincity.vibin.music_sharing.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.shorincity.vibin.music_sharing.R;
import com.shorincity.vibin.music_sharing.model.GetNotifications;

import java.util.ArrayList;

public class SwipeAdapter extends RecyclerView.Adapter<SwipeAdapter.SwipeViewHolder> {

    private Context context;
    private ArrayList<GetNotifications> GetNotificationss;
    private final ViewBinderHelper viewBinderHelper = new ViewBinderHelper();

    public SwipeAdapter(Context context, ArrayList<GetNotifications> GetNotificationss) {
        this.context = context;
        this.GetNotificationss = GetNotificationss;
    }

    public void setGetNotificationss(ArrayList<GetNotifications> GetNotificationss) {
        this.GetNotificationss = new ArrayList<>();
        this.GetNotificationss = GetNotificationss;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SwipeViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_notification_swipe, viewGroup, false);
        return new SwipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SwipeViewHolder swipeViewHolder, int i) {
        viewBinderHelper.setOpenOnlyOne(true);
        //viewBinderHelper.bind(swipeViewHolder.swipelayout, String.valueOf(GetNotificationss.get(i).getName()));
        //viewBinderHelper.closeLayout(String.valueOf(GetNotificationss.get(i).getName()));
        swipeViewHolder.bindData(GetNotificationss.get(i));
    }

    @Override
    public int getItemCount() {
        return GetNotificationss.size();
    }

    class SwipeViewHolder extends RecyclerView.ViewHolder {

        private TextView textView;
        private TextView txtEdit;
        private TextView txtDelete;
        private SwipeRevealLayout swipelayout;

        SwipeViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textView);
           // txtEdit = itemView.findViewById(R.id.txtEdit);
           // txtDelete = itemView.findViewById(R.id.txtDelete);
            swipelayout = itemView.findViewById(R.id.swipelayout);

            txtEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(context, "edit", Toast.LENGTH_SHORT).show();
                }
            });

            txtDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(context, "delete", Toast.LENGTH_SHORT).show();
                }
            });
        }

        void bindData(GetNotifications GetNotifications) {
            //textView.setText(GetNotifications.getName());
        }
    }
}
