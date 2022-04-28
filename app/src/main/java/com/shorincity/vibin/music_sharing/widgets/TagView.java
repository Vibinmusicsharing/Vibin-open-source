package com.shorincity.vibin.music_sharing.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.flexbox.JustifyContent;
import com.shorincity.vibin.music_sharing.R;
import com.shorincity.vibin.music_sharing.widgets.enums.TagSeparator;
import com.shorincity.vibin.music_sharing.widgets.interfaces.TagClickListener;
import com.shorincity.vibin.music_sharing.widgets.interfaces.TagItemListener;

import java.util.ArrayList;
import java.util.List;


public class TagView extends FlexboxLayout implements TagClickListener {

    private List<String> mTagList = new ArrayList<>();

    private final String EMPTY_STRING = "";
    private final String DEFAULT_TAG_LIMIT_MESSAGE = "You reach maximum tags";

    /**
     * This is Default Tab separator.
     * User can add different tab separator
     * <p>
     * Tag separator must be a single character.
     * <p>
     * And the Tag separator will be special character
     */
    private String tagSeparator = TagSeparator.valueOf(TagSeparator.COMMA_SEPARATOR.name()).getValue();


    /**
     * The tagLimit define how many tag will show
     * Default it take maximum value.
     * It not Take 0/Zero value
     */
    private int tagLimit = Integer.MAX_VALUE;

    /**
     * Tag view Background color
     */
    private int tagBackgroundColor;

    private int mTagTextColor;

    private String mTagLimitMessage;

    /**
     * The both drawable and bitmap enable only when
     * other is null.
     * N.B: Both can be null
     */
    private Drawable mCrossDrawable;
    private Bitmap mCrossBitmap;

    private TagItemListener mTagItemListener;

    private List<String> mTagItemList = new ArrayList<>();

    public TagView(Context context) {
        super(context);
    }

    public TagView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setFlexWrap(FlexWrap.WRAP);

        setJustifyContent(JustifyContent.FLEX_START);

        tagBackgroundColor = getResources().getColor(R.color.counterColor);

        mTagTextColor = getResources().getColor(R.color.white);

        init(attrs);

    }

    private void init(AttributeSet attributeSet) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attributeSet, R.styleable.TagView);

        mTagTextColor = typedArray.getColor(R.styleable.TagView_tag_text_color, getResources().getColor(R.color.white));

        tagBackgroundColor = typedArray.getColor(R.styleable.TagView_tag_background_color, getResources().getColor(R.color.counterColor));

        tagLimit = typedArray.getInt(R.styleable.TagView_tag_limit, 1);

        mCrossDrawable = typedArray.getDrawable(R.styleable.TagView_close_icon);

        mTagLimitMessage = typedArray.getString(R.styleable.TagView_limit_error_text);


        int separator = typedArray.getInt(R.styleable.TagView_tag_separator, 0);

        tagSeparator = convertSeparator(separator);

        typedArray.recycle();
    }

    /**
     * Adding a tag view in Main view
     */
    private void addTagInView() {
        final String model = mTagList.get(mTagList.size() - 1);

        final LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View tagView = inflater.inflate(R.layout.tag_layout, null);

        TextView tagTextView = tagView.findViewById(R.id.text_view_tag);

        ImageView imageView = tagView.findViewById(R.id.image_view_cross);

        LinearLayout linearLayout = tagView.findViewById(R.id.tag_container);

        GradientDrawable drawable = (GradientDrawable) linearLayout.getBackground();

        drawable.setColor(tagBackgroundColor);

        tagTextView.setTextColor(mTagTextColor);

        if (mCrossBitmap != null) {
            imageView.setImageBitmap(mCrossBitmap);
        } else if (mCrossDrawable != null) {
            imageView.setImageDrawable(mCrossDrawable);
        }

        /*
         * Here we remove the tag
         * */
        imageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                removeView(tagView);
                mTagList.remove(model);

                if (mTagItemListener != null) {
                    mTagItemListener.onGetRemovedItem(model);
                }

                invalidate();
            }
        });

        tagTextView.setText("# " + model);

        this.addView(tagView, mTagList.indexOf(model));

        if (mTagItemListener != null) {
            mTagItemListener.onGetAddedItem(model);
        }

        // mTagViewList.add(view);

        invalidate();

    }

    private String convertSeparator(int separator) {
        String tagSeparator = "";
        switch (separator) {
            case 1:
                tagSeparator = TagSeparator.valueOf(TagSeparator.COMMA_SEPARATOR.name()).getValue();
                break;
            case 2:
                tagSeparator = TagSeparator.valueOf(TagSeparator.PLUS_SEPARATOR.name()).getValue();
                break;
            case 3:
                tagSeparator = TagSeparator.valueOf(TagSeparator.MINUS_SEPARATOR.name()).getValue();
                break;
            case 4:
                tagSeparator = TagSeparator.valueOf(TagSeparator.SPACE_SEPARATOR.name()).getValue();
                break;
            case 5:
                tagSeparator = TagSeparator.valueOf(TagSeparator.AT_SEPARATOR.name()).getValue();
                break;
            case 6:
                tagSeparator = TagSeparator.valueOf(TagSeparator.HASH_SEPARATOR.name()).getValue();
                break;
        }
        return tagSeparator;
    }

    public void addTag(String text) {
        if (text == null) {
            text = EMPTY_STRING;
        } else if (mTagList.contains(text)) {
            return;
        }
        mTagList.add(text);
        addTagInView();
    }

    public void addTags(List<String> list) {
        for (String item : list) {
            addTag(item);
        }
    }

    @Override
    public void onGetSelectTag(int position, String tagText) {
        // We get tag item. we have to add tag
        addTag(tagText);
    }

    /**
     * User can notify when Tag item added or removed
     *
     * @param listener {@link TagItemListener}
     */
    public void initTagListener(TagItemListener listener) {
        mTagItemListener = listener;
    }


    /**
     * User can get all selected Tag list
     *
     * @return {@link TagModel} of List
     */
    public List<String> getSelectedTags() {
        return mTagList;
    }

}
