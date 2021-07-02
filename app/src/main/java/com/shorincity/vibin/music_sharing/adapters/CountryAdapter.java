package com.shorincity.vibin.music_sharing.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.shorincity.vibin.music_sharing.model.CountryModel;
import com.shorincity.vibin.music_sharing.R;

import java.util.ArrayList;
import java.util.Random;

// playlist recycler view adapter
public class CountryAdapter extends RecyclerView.Adapter<CountryAdapter.ExampleViewHolder> {

    private Context mContext;
    private ArrayList<CountryModel> list;

    public CountryAdapter(Context context, ArrayList<CountryModel> exampleList){
        mContext = context;
        list = exampleList;
    }

    @NonNull
    @Override
    public ExampleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.adapter_country,parent,false);

        final ExampleViewHolder mViewHolder = new ExampleViewHolder(v);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customItemClickListener.onItemClick(v, mViewHolder.getPosition());
            }
        });

        /*try {
            if(mViewHolder.mainRl.getTag() == null ) {
                mViewHolder.mainRl.setBackgroundResource(getRandom());
                mViewHolder.mainRl.setTag(i);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        return mViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ExampleViewHolder holder, int position) {
        CountryModel currentItem = list.get(position);

        //holder.mTextViewTitle.setText(currentItem.getName());
        holder.mImageView.setImageResource(currentItem.getCountry_image());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class ExampleViewHolder extends RecyclerView.ViewHolder{

        public ImageView mImageView;
        public TextView mTextViewTitle;
        public RelativeLayout mainRl;
        public ExampleViewHolder(@NonNull View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.item_img);
            mainRl = itemView.findViewById(R.id.main_layout);
            mTextViewTitle = itemView.findViewById(R.id.item_title);
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

    public static Integer getRandom() {
        Integer[] array = new Integer[]{R.color.color1,
                R.color.color2,
                R.color.color3,
                R.color.color4,
                R.color.color5,
                R.color.color6,
                R.color.color7,
                R.color.color8,
                R.color.color9,
                R.color.color10,
                R.color.color11,
                R.color.color12,
                R.color.color13,
                R.color.color14,
                R.color.color15,
                R.color.color16,
                R.color.color17,
                R.color.color18,
                R.color.color19,
                R.color.color20,
                R.color.color21,
                R.color.color22,
                R.color.color23,
                R.color.color24
        };
        int rnd = new Random().nextInt(array.length);
        return array[rnd];
    }
}
