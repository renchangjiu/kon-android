package com.htt.kon.view;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.htt.kon.R;

/**
 * MusicFragment 页面下的分隔部分布局
 *
 * @author su
 * @date 2020/02/02 17:33
 */
public class ListViewSeparateLayout extends LinearLayout {

    private ImageView imageView;

    private TextView textView;

    private ImageView imageViewSetting;

    private static final String ARROW_DIRECTION_DOWN = "down";
    private static final String ARROW_DIRECTION_RIGHT = "right";

    private String curArrowDirection = ARROW_DIRECTION_DOWN;

    public ListViewSeparateLayout(Context context) {
        super(context);
    }

    public ListViewSeparateLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        View view = LayoutInflater.from(context).inflate(R.layout.layout_list_view_separate, this);
        this.imageView = view.findViewById(R.id.imageView);
        this.textView = view.findViewById(R.id.textView);
        this.imageViewSetting = view.findViewById(R.id.imageViewSetting);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ListViewSeparateLayout);
        String text = ta.getString(R.styleable.ListViewSeparateLayout_text);
        // 回收
        ta.recycle();

        this.setText(text);
    }

    public ListViewSeparateLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ListViewSeparateLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setText(String text) {
        this.textView.setText(text);
    }

    /**
     * 设置布局点击事件
     */
    public void setOnClickListener(OnClickListener listener) {
        View.OnClickListener cl = v -> {
            changeArrayDirection();
            listener.onCommonClick(v);
        };
        this.imageView.setOnClickListener(cl);
        this.textView.setOnClickListener(cl);
        this.imageViewSetting.setOnClickListener(listener::onSettingImageClick);
    }

    /**
     * 改变箭头方向
     */
    private void changeArrayDirection() {
        ObjectAnimator anim;
        // 变成向右
        if (this.curArrowDirection.equals(ARROW_DIRECTION_DOWN)) {
            anim = ObjectAnimator.ofFloat(this.imageView, "rotation", 0f, -90f);
            this.curArrowDirection = ARROW_DIRECTION_RIGHT;
        } else {
            // 变成向下
            anim = ObjectAnimator.ofFloat(this.imageView, "rotation", -90f, 0f);
            this.curArrowDirection = ARROW_DIRECTION_DOWN;
        }
        anim.setDuration(500);
        anim.start();
    }

    public interface OnClickListener {
        /**
         * 点击了除设置按钮外的部分
         */
        void onCommonClick(View v);

        /**
         * 点击了设置按钮
         */
        void onSettingImageClick(View v);
    }
}
