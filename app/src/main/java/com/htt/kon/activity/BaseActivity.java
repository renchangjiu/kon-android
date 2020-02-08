package com.htt.kon.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.htt.kon.App;
import com.htt.kon.R;
import com.htt.kon.adapter.pager.PlayBarAdapter;
import com.htt.kon.bean.Playlist;
import com.htt.kon.dialog.PlayListDialogFragment;
import com.htt.kon.service.MusicService;
import com.htt.kon.util.LogUtils;


/**
 * 自定义基类，实现底部播放栏及处理音乐播放相关功能
 *
 * @author su
 * @date 2020/02/04 09:59
 */
public class BaseActivity extends AppCompatActivity {
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

    private ImageView imageViewCover;

    private TextView textViewTitle;

    private TextView textViewArtist;

    private ViewPager viewPager;

    private ImageView imageViewBtn;

    private ImageView imageViewPlayList;


    private MusicServiceConnect msConn = new MusicServiceConnect();

    private MusicService msService;

    private App app;

    private Playlist playlist;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewGroup mDecorView = (ViewGroup) getWindow().getDecorView();
        this.contentContainer = (FrameLayout) ((ViewGroup) mDecorView.getChildAt(0)).getChildAt(1);
        this.playBar = LayoutInflater.from(this).inflate(R.layout.layout_play_bar, null);

        this.app = App.getApp();
        this.playlist = app.getPlaylist();
        this.initPlayBar();
        LogUtils.e();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.BOTTOM;
        this.contentContainer.addView(this.playBar, layoutParams);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, MusicService.class);
        bindService(intent, msConn, Context.BIND_AUTO_CREATE);
        LogUtils.e();
    }

    /**
     * 初始化playBar
     * TODO 播放列表为空时需要特殊处理
     */
    private void initPlayBar() {
        this.viewPager = this.playBar.findViewById(R.id.lpb_viewPager);
        this.imageViewBtn = this.playBar.findViewById(R.id.lpb_imageViewBtn);
        this.imageViewPlayList = this.playBar.findViewById(R.id.lpb_imageViewPlayList);

        this.viewPager.setAdapter(new PlayBarAdapter(this.playlist, this));

        this.viewPager.setCurrentItem(this.playlist.getIndex(), true);

        this.imageViewBtn.setOnClickListener(v -> {
            LogUtils.e();
            this.msService.playOrPause();
            this.updatePlayBarInterface();
        });

        this.imageViewPlayList.setOnClickListener(v -> {
            PlayListDialogFragment of = PlayListDialogFragment.of(this.msService);
            of.show(getSupportFragmentManager(), "");
        });

        this.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            private int pos = playlist.getIndex();

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                this.pos = playlist.getIndex();
                // 右翻页
                if (position > pos) {
                    msService.next(true);
                    LogUtils.e("play next music.");
                } else if (position < pos) {
                    // 左翻页
                    msService.prev(true);
                    LogUtils.e("play prev music.");
                }
                updatePlayBarInterface();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }


    /**
     * 根据当前播放状态, 修改playbar 的界面
     */
    public void updatePlayBarInterface() {
        if (this.msService != null) {
            if (this.msService.isPlaying()) {
                this.imageViewBtn.setImageResource(R.drawable.playbar_paly);
            } else {
                this.imageViewBtn.setImageResource(R.drawable.playbar_pause);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        LogUtils.e();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(msConn);
        this.playlist.save2disk(this);
        LogUtils.e();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtils.e();
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

    private class MusicServiceConnect implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
            msService = binder.getMusicService();
            msService.setPlayStateChangeListener(() -> {
                // 使playbar 显示当前播放的歌曲的信息
                viewPager.setCurrentItem(playlist.getIndex(), true);
                updatePlayBarInterface();
            });
            LogUtils.e();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            LogUtils.e();
        }
    }
}