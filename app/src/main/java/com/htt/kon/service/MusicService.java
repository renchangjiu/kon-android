package com.htt.kon.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

import com.htt.kon.App;
import com.htt.kon.bean.Music;
import com.htt.kon.bean.Playlist;
import com.htt.kon.util.IdWorker;
import com.htt.kon.util.LogUtils;
import com.htt.kon.util.MusicFileSearcher;
import com.htt.kon.util.StorageUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lombok.Setter;

/**
 * @author su
 * @date 2020/02/06 20:11
 */
public class MusicService extends Service {

    private MusicBinder binder = new MusicBinder();

    private MediaPlayer player;

    private App app;

    private Playlist playlist;

    /**
     * 播放状态变化的监听器
     */
    @Setter
    private OnPlayStateChangeListener playStateChangeListener;


    public MusicService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        LogUtils.e();
        return this.binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.app = App.getApp();
        this.playlist = this.app.getPlaylist();
        LogUtils.e();
        this.player = new MediaPlayer();
        if (this.playlist.isNotEmpty()) {
            try {
                this.player.setDataSource(this.playlist.getCurMusic().getPath());
                this.player.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // 监听播放完毕事件
        this.player.setOnCompletionListener(mp -> {
            this.next(true);
            this.emit();
        });
    }


    public void playOrPause() {
        if (this.player.isPlaying()) {
            this.player.pause();
            LogUtils.e("Play pause, music path: " + this.playlist.getCurMusic().getPath());
        } else {
            this.player.start();
            LogUtils.e("Play start, music path: " + this.playlist.getCurMusic().getPath());
        }
    }

    public boolean isPlaying() {
        return this.player.isPlaying();
    }

    /**
     * 切换到下一首
     *
     * @param autoPlay 切换完成后, 是否立即播放
     */
    public void next(boolean autoPlay) {
        Music curMusic = this.playlist.next();
        try {
            this.player.stop();
            this.player.reset();
            this.player.setDataSource(curMusic.getPath());
            this.player.prepare();
            if (autoPlay) {
                this.playOrPause();
            }
        } catch (IOException e) {
            LogUtils.e(e);
        }
    }

    /**
     * 切换到上一首
     *
     * @param autoPlay 切换完成后, 是否立即播放
     */
    public void prev(boolean autoPlay) {
        Music curMusic = this.playlist.prev();
        try {
            this.player.stop();
            this.player.reset();
            this.player.setDataSource(curMusic.getPath());
            this.player.prepare();
            if (autoPlay) {
                this.playOrPause();
            }
        } catch (IOException e) {
            LogUtils.e(e);
        }
    }


    /**
     * 移除指定位置的歌曲
     *
     * @param autoPlay 移除后, 是否立即播放当前index 的歌曲
     */
    public void remove(int position, boolean autoPlay) {
        this.playlist.remove(position);
        if (autoPlay) {
            play();
        }
    }

    /**
     * 播放当前歌曲
     */
    public void play() {
        this.play(this.playlist.getIndex());
    }

    /**
     * 播放指定位置的歌曲
     */
    public void play(int position) {
        this.playlist.setIndex(position);
        Music curMusic = this.playlist.getCurMusic();
        try {
            this.player.stop();
            this.player.reset();
            if (curMusic != null) {
                this.player.setDataSource(curMusic.getPath());
                this.player.prepare();
                this.playOrPause();
            }
        } catch (IOException e) {
            LogUtils.e(e);
        }
    }

    public void setMode(int mode) {
        this.playlist.setMode(mode);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtils.e();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtils.e();
    }


    private void search() {
        // 初始化播放列表
        String sdcardRootPath = StorageUtils.getSdcardRootPathR(this);
        List<String> list1 = MusicFileSearcher.search(sdcardRootPath);

        String externalRootPath = StorageUtils.getExternalRootPath(this);
        List<String> list2 = MusicFileSearcher.search(externalRootPath);

        list1.addAll(list2);
        List<Music> musicDOS = new ArrayList<>();
        for (String path : list1) {
            Music music = new Music();
            music.setId(IdWorker.singleNextId());
            music.setPath(path);
            musicDOS.add(music);
        }
    }


    public class MusicBinder extends Binder {
        public MusicService getMusicService() {
            return MusicService.this;
        }
    }

    /**
     * 发出播放另一首歌曲的信号
     */
    private void emit() {
        if (this.playStateChangeListener != null) {
            this.playStateChangeListener.onPlayAnotherMusic();
        }
    }

    /**
     * 播放状态监听器
     */
    public interface OnPlayStateChangeListener {

        /**
         * 当播放另一首歌曲时调用(非用户手动切换, 而是程序自动切换)
         */
        void onPlayAnotherMusic();

    }
}
