package com.htt.kon.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
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
import com.htt.kon.bean.PlayMode;
import com.htt.kon.service.Playlist;
import com.htt.kon.constant.FragmentTagConstant;
import com.htt.kon.dialog.PlayListDialogFragment;
import com.htt.kon.service.MusicService;
import com.htt.kon.util.LogUtils;
import com.htt.kon.util.stream.Optional;

import java.util.List;


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

    private ViewPager viewPager;

    private ImageView imageViewBtn;

    private MusicServiceConnect msConn = new MusicServiceConnect();

    private MusicService msService;

    private Playlist playlist;

    private ImageView imageViewPlayList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewGroup mDecorView = (ViewGroup) getWindow().getDecorView();
        this.contentContainer = (FrameLayout) ((ViewGroup) mDecorView.getChildAt(0)).getChildAt(1);
        this.playBar = LayoutInflater.from(this).inflate(R.layout.layout_play_bar, null);

        App app = App.getApp();
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
        this.imageViewBtn = this.playBar.findViewById(R.id.lpb_imageViewBtn);
        this.imageViewPlayList = this.playBar.findViewById(R.id.lpb_imageViewPlayList);

        this.viewPager.setAdapter(new PlayBarAdapter());

        this.viewPager.setCurrentItem(this.playlist.getIndex(), true);

        this.imageViewBtn.setOnClickListener(v -> {
            LogUtils.e();
            this.msService.playOrPause();
            this.updatePlayBarInterface();
        });

        // 弹出播放列表对话框
        this.imageViewPlayList.setOnClickListener(v -> {
            PlayListDialogFragment of = PlayListDialogFragment.of();
            of.show(getSupportFragmentManager(), FragmentTagConstant.PLAYLIST_FRAGMENT);
            of.setOnClickListener(new PlayListDialogFragmentOnClickListener());
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
                    msService.next();
                    LogUtils.e("play next music.");
                } else if (position < pos) {
                    // 左翻页
                    msService.prev();
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
        this.msService.replace(musics, index);
        this.updatePlayBarViewPager();
        this.updatePlayBarInterface();
        this.showPlayBar();
    }

    /**
     * 下一首播放
     *
     * @param music music
     */
    public void nextPlay(Music music) {
        this.msService.nextPlay(music);
        showPlayBar();
        if (this.playlist.size() == 1) {
            this.msService.play();
        }
        this.updatePlayBarViewPager();
        this.updatePlayBarInterface();
    }

    /**
     * 下一首播放
     *
     * @param musics music list
     */
    public void nextPlay(List<Music> musics) {
        this.msService.nextPlay(musics);
        showPlayBar();
        if (this.playlist.size() == 1) {
            this.msService.play();
        }
        this.updatePlayBarViewPager();
        this.updatePlayBarInterface();
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

    /**
     * 处理播放列表弹出框的所有事件
     */
    private class PlayListDialogFragmentOnClickListener implements PlayListDialogFragment.OnClickListener {
        /**
         * 当播放列表的item 被点击时回调
         *
         * @param position position
         */
        @Override
        public void onItemClick(int position) {
            if (position != playlist.getIndex() || !msService.isPlaying()) {
                BaseActivity.this.msService.play(position);
                viewPager.setCurrentItem(playlist.getIndex());
            }
        }

        /**
         * 当播放模式按钮被点击时回调
         */
        @Override
        public void onPlayModeBtnClick() {
            PlayMode nextPlayMode = Playlist.getNextPlayMode(playlist.getMode(), BaseActivity.this);
            msService.setMode(nextPlayMode.getValue());
        }

        @Override
        public void onCollectBtnClick() {

        }

        /**
         * 清空播放列表
         */
        @Override
        public void onClearBtnClick() {
            msService.clear();
            Optional.of(viewPager.getAdapter()).ifPresent(PagerAdapter::notifyDataSetChanged);
            hidePlayBar();
        }

        /**
         * 当item 的定位按钮被点击时回调, 定位到某歌单或页面
         *
         * @param position pos
         */
        @Override
        public void onLocateBtnClick(int position) {
            LogUtils.e();
        }

        /**
         * 当item 的删除按钮被点击时回调, 删除播放列表中的某一个
         *
         * @param position pos
         * @return playlist's index.
         */
        @Override
        public int onDeleteBtnClick(int position) {
            int index = playlist.getIndex();
            msService.remove(position, false);
            Optional.of(viewPager.getAdapter()).ifPresent(PagerAdapter::notifyDataSetChanged);
            if (msService.isPlaying() && position == index) {
                msService.play();
            }
            if (playlist.isEmpty()) {
                hidePlayBar();
            }
            LogUtils.e(playlist);
            return playlist.getIndex();
        }
    }

    private class MusicServiceConnect implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
            msService = binder.getMusicService();
            updatePlayBarInterface();
            msService.setOnPreparedListener(new MusicService.OnPreparedListener() {
                @Override
                public void onPreparedStart(MediaPlayer mp) {
                    updatePlayBarViewPager();
                    // 使playbar 显示当前播放的歌曲的信息
                    viewPager.setCurrentItem(playlist.getIndex(), true);
                    imageViewBtn.setImageResource(R.drawable.playbar_paly);

                    PlayListDialogFragment dialog = (PlayListDialogFragment) getSupportFragmentManager()
                            .findFragmentByTag(FragmentTagConstant.PLAYLIST_FRAGMENT);
                    if (dialog != null) {
                        dialog.updateAdapterInterface();
                    }
                }

                @Override
                public void onPreparedFinish(MediaPlayer mp) {
                    imageViewBtn.setImageResource(R.drawable.playbar_paly);
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