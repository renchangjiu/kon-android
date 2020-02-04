package com.htt.kon.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.htt.kon.R;

/**
 * 自定义基类，实现无缝切换底部播放栏
 *
 * @author su
 * @date 2020/02/04 09:59
 */
public class PlayBarActivity extends AppCompatActivity {
    /**
     * windowManager对象
     */
    private WindowManager windowManager;
    /**
     * 根视图
     */
    private FrameLayout contentContainer;
    /**
     * 浮动播放栏
     */
    private View playBar;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewGroup mDecorView = (ViewGroup) getWindow().getDecorView();
        this.contentContainer = (FrameLayout) ((ViewGroup) mDecorView.getChildAt(0)).getChildAt(1);
        this.playBar = LayoutInflater.from(getBaseContext()).inflate(R.layout.layout_float_play_bar, null);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.BOTTOM;
        this.contentContainer.addView(playBar, layoutParams);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    /***
     * 实现前进Activity时候的无动画切换
     */
    @Override
    public void startActivity(Intent intent) {
        //设置切换没有动画，用来实现活动之间的无缝切换
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        super.startActivity(intent);
    }

    /**
     * 按下返回键，或者返回button时无动画
     */
    @Override
    public void finish() {
        super.finish();
        //设置返回没有动画
        overridePendingTransition(0, 0);
    }

    /**
     * 隐藏播放栏
     */
    public void hidePlayBar() {
        this.playBar.setVisibility(View.GONE);
    }

    /**
     * 显示播放栏
     */
    public void showPlayBar() {
        this.playBar.setVisibility(View.VISIBLE);
    }

    /**
     * 显示或隐藏播放栏
     */
    public void togglePlayBar() {
        if (this.playBar.getVisibility() == View.VISIBLE) {
            this.playBar.setVisibility(View.GONE);
        } else {
            this.playBar.setVisibility(View.VISIBLE);
        }
    }
}