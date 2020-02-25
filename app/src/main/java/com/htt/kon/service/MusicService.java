package com.htt.kon.service;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

import androidx.core.app.NotificationManagerCompat;

import com.htt.kon.App;
import com.htt.kon.bean.Music;
import com.htt.kon.broadcast.BaseReceiver;
import com.htt.kon.broadcast.MusicPlayStateReceiver;
import com.htt.kon.broadcast.PlayNotificationReceiver;
import com.htt.kon.notification.PlayNotification;
import com.htt.kon.util.LogUtils;
import com.htt.kon.util.stream.Optional;

import java.io.IOException;
import java.util.List;

import lombok.Setter;

/**
 * @author su
 * @date 2020/02/06 20:11
 */
public class MusicService extends Service {
    private static final int NOTIFICATION_ID = 1;

    private Notification notification;

    /**
     * 服务是否在前台
     */
    private boolean isForeground = true;

    private MusicBinder binder = new MusicBinder();

    private MediaPlayer player;

    private Playlist playlist;

    private volatile boolean playNow;

    @Setter
    private OnPreparedListener onPreparedListener;

    private NotificationManagerCompat notificationManager;

    private PlayNotificationReceiver receiver;


    public MusicService() {
    }

    /**
     * 创建通知栏
     * TODO: 通知栏
     */
    private void createNotification() {
        this.receiver = new PlayNotificationReceiver();
        BaseReceiver.register(this, this.receiver, PlayNotificationReceiver.ACTION);
        this.receiver.setOnReceiveListener(() -> {
            LogUtils.e(this.playlist);
        });

        this.notification = PlayNotification.of(PlayNotification.STYLE_1, this, this.playlist.getCurMusic());
        this.notificationManager = NotificationManagerCompat.from(this);
        startForeground(NOTIFICATION_ID, this.notification);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.playlist = App.getApp().getPlaylist();

        this.createNotification();
        if (this.playlist.isEmpty()) {
            stopForeground(true);
            this.isForeground = false;
        }
        LogUtils.e("MusicService onCreate.");
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
            LogUtils.e("Music play finished.");
            this.next();
        });

        this.player.setOnPreparedListener(mp -> {
            if (this.playNow) {
                mp.start();
                this.playNow = false;
            }
            Optional.of(this.onPreparedListener).ifPresent(v -> v.onPreparedFinish(this.player));
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtils.e("MusicService onStartCommand.");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        LogUtils.e("MusicService onBind.");
        return this.binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        LogUtils.e("MusicService onUnbind.");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        LogUtils.e("MusicService onDestroy.");
        super.onDestroy();
        BaseReceiver.unregister(this, this.receiver);
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
     */
    public void next() {
        this.playlist.next();
        this.play();
    }


    /**
     * 切换到上一首
     */
    public void prev() {
        this.playlist.prev();
        this.play();
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
        if (this.playlist.isEmpty()) {
            stopForeground(true);
            this.isForeground = false;
        }

        MusicPlayStateReceiver.send(this, MusicPlayStateReceiver.FLAG_REMOVE);
    }

    /**
     * 清空播放列表
     */
    public void clear() {
        this.playlist.clear();
        this.player.stop();
        this.player.reset();

        stopForeground(true);
        this.isForeground = false;

        MusicPlayStateReceiver.send(this, MusicPlayStateReceiver.FLAG_CLEAR);
        LogUtils.e(this.playlist);
    }


    /**
     * 使用新的歌曲集合替换当前播放的列表, 并立即播放
     *
     * @param musics musics
     * @param index  index
     */
    public void replace(List<Music> musics, int index) {
        this.playlist.replace(musics, index);
        this.play();

        this.startForegroundIfNot();
    }

    /**
     * 添加到下一首播放
     *
     * @param music music
     */
    public void nextPlay(Music music) {
        this.playlist.add(music, this.playlist.getIndex() + 1);

        this.startForegroundIfNot();
    }

    /**
     * 添加到下一首播放
     *
     * @param musics music list
     */
    public void nextPlay(List<Music> musics) {
        this.playlist.add(musics, this.playlist.getIndex() + 1);

        this.startForegroundIfNot();
    }

    /**
     * 若当前不在前台, 则设置为前台
     */
    private void startForegroundIfNot() {
        if (!this.isForeground) {
            startForeground(NOTIFICATION_ID, notification);
            this.isForeground = true;
        }
    }


    /**
     * 立即播放当前歌曲
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
                this.player.prepareAsync();
                this.playNow = true;
                this.onPreparedListener.onPreparedStart(this.player);

                // 发出广播
                MusicPlayStateReceiver.send(this, MusicPlayStateReceiver.FLAG_PLAY);
                this.notificationManager.notify(NOTIFICATION_ID, PlayNotification.of(PlayNotification.STYLE_1, this, this.playlist.getCurMusic()));
            }
        } catch (IOException e) {
            LogUtils.e(e);
        }
    }


    public void setMode(int mode) {
        this.playlist.setMode(mode);
    }


    public class MusicBinder extends Binder {
        public MusicService getMusicService() {
            return MusicService.this;
        }
    }

    public interface OnPreparedListener {
        /**
         * 当 MediaPlayer 开始准备时调用
         */
        void onPreparedStart(MediaPlayer mp);

        /**
         * 当 MediaPlayer 准备完成后调用
         */
        void onPreparedFinish(MediaPlayer mp);
    }

}
