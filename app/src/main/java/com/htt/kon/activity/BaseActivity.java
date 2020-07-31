package com.htt.kon.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.htt.kon.App;
import com.htt.kon.R;
import com.htt.kon.adapter.pager.PlayBarAdapter;
import com.htt.kon.bean.Music;
import com.htt.kon.dialog.PlayListDialog;
import com.htt.kon.service.PlayService;
import com.htt.kon.service.Playlist;
import com.htt.kon.constant.FragmentTagConstant;
import com.htt.kon.util.LogUtils;
import com.htt.kon.util.stream.Optional;

import java.util.List;


/**
 * base activity, 实现底部播放栏及处理音乐播放相关功能
 *
 * @author su
 * @date 2020/02/04 09:59
 */
public class BaseActivity extends AppCompatActivity {
    /**
     * 根视图
     */
    private FrameLayout contentContainer;
    /**
     * 浮动播放栏
     */
    private View playBar;

    private ViewPager viewPager;

    private ImageView imageViewPlay;

    private MusicServiceConnect msConn = new MusicServiceConnect();

    private PlayService playService;

    protected Playlist playlist;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewGroup mDecorView = (ViewGroup) getWindow().getDecorView();
        this.contentContainer = (FrameLayout) ((ViewGroup) mDecorView.getChildAt(0)).getChildAt(1);
        this.playBar = LayoutInflater.from(this).inflate(R.layout.play_bar, contentContainer, false);

        this.playlist = App.getPlaylist();
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
        Intent intent = new Intent(this, PlayService.class);
        bindService(intent, msConn, Context.BIND_AUTO_CREATE);
        if (this.playlist.isEmpty()) {
            this.hidePlayBar();
        } else {
            this.showPlayBar();
        }
        this.updatePlayBarViewPager();
        LogUtils.e();
    }

    /**
     * 初始化playBar
     */
    private void initPlayBar() {
        this.viewPager = this.playBar.findViewById(R.id.lpb_viewPager);
        this.imageViewPlay = this.playBar.findViewById(R.id.lpb_imageViewPlay);
        ImageView imageViewPlayList = this.playBar.findViewById(R.id.lpb_imageViewPlayList);

        this.viewPager.setAdapter(new PlayBarAdapter());

        this.viewPager.setCurrentItem(this.playlist.getIndex(), true);

        this.imageViewPlay.setOnClickListener(v -> {
            this.playService.playOrPause();
            imageViewPlay.setImageResource(playService.isPlaying() ? R.drawable.playbar_paly : R.drawable.playbar_pause);
        });

        // 点击弹出播放列表对话框
        imageViewPlayList.setOnClickListener(v -> PlayListDialog.of(this.playService).show(getSupportFragmentManager(), FragmentTagConstant.PLAYLIST_FRAGMENT));

        this.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                int pos = playlist.getIndex();
                // 右翻页
                if (position > pos) {
                    playService.next();
                    LogUtils.e("play next music.");
                } else if (position < pos) {
                    // 左翻页
                    playService.prev();
                    LogUtils.e("play prev music.");
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
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
        this.playlist.save(this);
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
     * 使用新的歌曲集合替换当前播放的列表, 并立即播放
     *
     * @param musics musics
     * @param index  index
     */
    public void replacePlaylist(List<Music> musics, int index) {
        this.playService.replace(musics, index);
    }

    /**
     * 添加到下一首播放
     *
     * @param music music
     */
    public void nextPlay(Music music) {
        this.playService.nextPlay(music);
    }

    /**
     * 添加到下一首播放
     *
     * @param musics music list
     */
    public void nextPlay(List<Music> musics) {
        this.playService.nextPlay(musics);
    }


    /**
     * 根据当前播放状态, 修改playbar 的界面
     */
    public void updatePlayBarInterface() {
        if (this.playService != null) {
            imageViewPlay.setImageResource(playService.isPlaying() ? R.drawable.playbar_paly : R.drawable.playbar_pause);
        }
    }

    /**
     * 当增或删播放列表后需更新viewPager
     */
    public void updatePlayBarViewPager() {
        Optional.of(viewPager.getAdapter()).ifPresent(PagerAdapter::notifyDataSetChanged);
        viewPager.setCurrentItem(this.playlist.getIndex(), true);
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


    private class MusicServiceConnect implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            PlayService.MusicBinder binder = (PlayService.MusicBinder) service;
            playService = binder.getMusicService();

            updatePlayBarInterface();
            playService.setOnPreparedListener(new PlayService.OnPreparedListener() {
                @Override
                public void onPreparedStart(MediaPlayer mp) {
                }

                @Override
                public void onPreparedFinish(MediaPlayer mp) {
                    imageViewPlay.setImageResource(playService.isPlaying() ? R.drawable.playbar_paly : R.drawable.playbar_pause);
                }
            });

            playService.setOnPlayStateChangeListener((flag) -> {
                switch (flag) {
                    case PlayService.OnPlayStateChangeListener.FLAG_2:
                        viewPager.setCurrentItem(playlist.getIndex(), true);
                        break;
                    case PlayService.OnPlayStateChangeListener.FLAG_3:
                        Optional.of(viewPager.getAdapter()).ifPresent(PagerAdapter::notifyDataSetChanged);
                        viewPager.setCurrentItem(playlist.getIndex(), true);
                        playBar.setVisibility(playlist.isNotEmpty() ? View.VISIBLE : View.GONE);
                        break;
                    default:
                }
            });

            LogUtils.e("MusicServiceConnect onServiceConnected.");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            LogUtils.e();
        }
    }
}