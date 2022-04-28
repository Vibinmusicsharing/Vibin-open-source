package com.shorincity.vibin.music_sharing.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.slidingpanelayout.widget.SlidingPaneLayout;

public class CustomSlidePanLayout extends SlidingPaneLayout {
    boolean slidingEnable = false;

    public CustomSlidePanLayout(@NonNull Context context) {
        super(context);
    }

    public CustomSlidePanLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomSlidePanLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (!slidingEnable && event.getX() > (getWidth() / 5)) {
            return false;
        }
        return super.onInterceptTouchEvent(event);
    }

    public void setSlidingEnable(boolean slidingEnable) {
        this.slidingEnable = slidingEnable;
    }

    @Override
    public void setSliderFadeColor(int color) {
        super.setSliderFadeColor(color);
    }
}
