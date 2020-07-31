package com.htt.kon.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

import androidx.core.app.NotificationManagerCompat;

import com.htt.kon.App;
import com.htt.kon.bean.Music;
import com.htt.kon.bean.PlayMode;
import com.htt.kon.broadcast.BaseReceiver;
import com.htt.kon.broadcast.PlayStateChangeReceiver;
import com.htt.kon.broadcast.PlayNotificationReceiver;
import com.htt.kon.notification.PlayNotification;
import com.htt.kon.util.LogUtils;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import lombok.Setter;

/**
 * music play service.
 * 使用 OnPlayStateChangeListener 回调接口与 BaseActivity 通信, 使用 PlayStateChangeReceiver 广播与各组件通信
 * FIXME: OnPlayStateChangeListener 或存在重复回调的情况
 *
 * @author su
 * @date 2020/02/06 20:11
 */
public class PlayService extends Service {
    private static final int NOTIFICATION_ID = 1;

    /**
     * 服务是否在前台
     */
    private boolean isForeground = true;

    private MusicBinder binder = new MusicBinder();

    private MediaPlayer player;

    private Playlist playlist;

    @Setter
    private OnPreparedListener onPreparedListener;

    private NotificationManagerCompat notificationManager;

    private PlayNotificationReceiver receiver;

    @Setter
    private OnPlayStateChangeListener onPlayStateChangeListener;

    /**
     * 在 onCreate 方法中 调用 prepare 方法, 不会立即播放
     */
    private boolean playNow = false;

    /**
     * 创建通知栏
     */
    private void createNotification() {
        this.receiver = new PlayNotificationReceiver();
        BaseReceiver.register(this, this.receiver, PlayNotificationReceiver.ACTION);
        this.receiver.setOnReceiveListener((flag) -> {
            switch (flag) {
                case CLOSE:
                    this.pause();
                    stopForeground(true);
                    this.notificationManager.cancel(NOTIFICATION_ID);
                    this.isForeground = false;
                    break;
                case LIKE:
                    break;
                case PREV:
                    this.prev();
                    break;
                case PLAY:
                    this.playOrPause();
                    break;
                case NEXT:
                    this.next();
                case LRC:
                    break;
                default:
            }
        });
        this.notificationManager = NotificationManagerCompat.from(this);
        startForeground(NOTIFICATION_ID, PlayNotification.of(PlayNotification.Style.ONE, this, this.playlist.getCurMusic(), this.isPlaying()));
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.playlist = App.getPlaylist();
        this.player = new MediaPlayer();
        this.createNotification();
        if (this.playlist.isEmpty() || !this.isPlaying()) {
            stopForeground(true);
            this.isForeground = false;
        }

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
            this.next();
            LogUtils.e("One music play finished.");
        });

        this.player.setOnPreparedListener(mp -> {
            if (playNow) {
                mp.start();
            }
            playNow = true;
            if (this.isPlaying()) {
                this.notificationManager.notify(NOTIFICATION_ID, PlayNotification.of(PlayNotification.Style.ONE, this, this.playlist.getCurMusic(), this.isPlaying()));
            }
            Optional.ofNullable(this.onPreparedListener).ifPresent(v -> v.onPreparedFinish(mp));
            PlayStateChangeReceiver.send(this, PlayStateChangeReceiver.Flag.PREPARED);
        });
        LogUtils.e("PlayService onCreate.");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtils.e("PlayService onStartCommand.");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        LogUtils.e("PlayService onBind.");
        return this.binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        LogUtils.e("PlayService onUnbind.");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        LogUtils.e("PlayService onDestroy.");
        super.onDestroy();
        BaseReceiver.unregister(this, this.receiver);
    }

    public void playOrPause() {
        if (this.player.isPlaying()) {
            this.pause();
            LogUtils.e("Play pause, music path: " + this.playlist.getCurMusic().getPath());
        } else {
            this.start();
            LogUtils.e("Play start, music path: " + this.playlist.getCurMusic().getPath());
        }
        this.notificationManager.notify(NOTIFICATION_ID, PlayNotification.of(PlayNotification.Style.ONE, this, this.playlist.getCurMusic(), this.isPlaying()));
    }

    public void pause() {
        this.player.pause();
        this.onPlayStateChangeListener.onChange(OnPlayStateChangeListener.FLAG_1);
        PlayStateChangeReceiver.send(this, PlayStateChangeReceiver.Flag.PAUSE);
    }

    public void start() {
        this.player.start();
        this.onPlayStateChangeListener.onChange(OnPlayStateChangeListener.FLAG_1);
        PlayStateChangeReceiver.send(this, PlayStateChangeReceiver.Flag.PLAY);
    }

    /**
     * 因为存在 player#prepareAsync 的情况, 所以该方法并不一定准确
     */
    public boolean isPlaying() {
        return this.player.isPlaying();
    }

    /**
     * 切换到下一首
     */
    public void next() {
        this.playlist.next();
        this.play();
        this.onPlayStateChangeListener.onChange(OnPlayStateChangeListener.FLAG_2);
    }


    /**
     * 切换到上一首
     */
    public void prev() {
        this.playlist.prev();
        this.play();
        this.onPlayStateChangeListener.onChange(OnPlayStateChangeListener.FLAG_2);
    }


    /**
     * 移除指定位置的歌曲
     */
    public void remove(int position) {
        int index = playlist.getIndex();
        this.playlist.remove(position);
        if (this.isPlaying() && position == index) {
            this.play();
        }
        if (this.playlist.isEmpty()) {
            stopForeground(true);
            this.notificationManager.cancel(NOTIFICATION_ID);
            this.isForeground = false;
        }
        PlayStateChangeReceiver.send(this, PlayStateChangeReceiver.Flag.REMOVE);
        this.onPlayStateChangeListener.onChange(OnPlayStateChangeListener.FLAG_3);
    }

    /**
     * 清空播放列表
     */
    public void clear() {
        this.playlist.clear();
        this.player.stop();
        this.player.reset();

        stopForeground(true);
        this.notificationManager.cancel(NOTIFICATION_ID);
        this.isForeground = false;

        PlayStateChangeReceiver.send(this, PlayStateChangeReceiver.Flag.CLEAR);
        this.onPlayStateChangeListener.onChange(OnPlayStateChangeListener.FLAG_3);
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
        this.onPlayStateChangeListener.onChange(OnPlayStateChangeListener.FLAG_3);
    }

    /**
     * 添加到下一首播放, 若添加之前播放列表为空, 则立即播放
     *
     * @param music music
     */
    public void nextPlay(Music music) {
        this.playlist.add(music, this.playlist.getIndex() + 1);

        if (this.playlist.size() == 1) {
            this.play();
        }

        this.startForegroundIfNot();
        this.onPlayStateChangeListener.onChange(OnPlayStateChangeListener.FLAG_3);
    }

    /**
     * 添加到下一首播放, 若添加之前播放列表为空, 则立即播放
     *
     * @param musics music list
     */
    public void nextPlay(List<Music> musics) {
        boolean empty = this.playlist.isEmpty();
        this.playlist.add(musics, this.playlist.getIndex() + 1);
        if (empty) {
            this.play();
        }
        this.startForegroundIfNot();
        this.onPlayStateChangeListener.onChange(OnPlayStateChangeListener.FLAG_3);
    }

    /**
     * 若当前不在前台, 则设置为前台
     */
    private void startForegroundIfNot() {
        if (!this.isForeground) {
            startForeground(NOTIFICATION_ID, PlayNotification.of(PlayNotification.Style.ONE, this, this.playlist.getCurMusic(), this.isPlaying()));
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
     * 停止正在播放的歌曲(如果有), 然后从头播放指定位置的歌曲
     */
    public void play(int position) {
        int oldIndex = this.playlist.getIndex();
        this.playlist.setIndex(position);
        Music curMusic = this.playlist.getCurMusic();
        try {
            this.player.stop();
            this.player.reset();
            if (curMusic != null) {
                this.player.setDataSource(curMusic.getPath());
                this.player.prepareAsync();
                this.onPreparedListener.onPreparedStart(this.player);

                // 发出广播
                PlayStateChangeReceiver.send(this, PlayStateChangeReceiver.Flag.PLAY);
                if (oldIndex == position) {
                    this.onPlayStateChangeListener.onChange(OnPlayStateChangeListener.FLAG_1);
                } else {
                    this.onPlayStateChangeListener.onChange(OnPlayStateChangeListener.FLAG_2);
                }
            }
        } catch (IOException e) {
            LogUtils.e(e);
        }
    }

    /**
     * 按顺序将播放模式设置为下一个模式
     */
    public void setMode() {
        List<PlayMode> modes = Playlist.getModes(this);
        modes.addAll(Playlist.getModes(this));
        int i = modes.indexOf(new PlayMode().setValue(this.playlist.getMode()));
        PlayMode nextMode = modes.get(i + 1);
        this.playlist.setMode(nextMode.getValue());
    }

    public void setMode(int mode) {
        this.playlist.setMode(mode);
    }


    public int getDuration() {
        return this.player.getDuration();
    }

    public int getCurrentPosition() {
        return this.player.getCurrentPosition();
    }

    public void seekTo(int msec) {
        player.seekTo(msec);
    }

    public class MusicBinder extends Binder {
        public PlayService getMusicService() {
            return PlayService.this;
        }
    }

    public interface OnPreparedListener {
        /**
         * 当 MediaPlayer 开始准备时调用
         *
         * @param mp MediaPlayer
         */
        void onPreparedStart(MediaPlayer mp);

        /**
         * 当 MediaPlayer 准备完成后调用
         * * @param mp MediaPlayer
         */
        void onPreparedFinish(MediaPlayer mp);
    }

    /**
     * 当播放状态变化的回调接口
     */
    public interface OnPlayStateChangeListener {
        /**
         * 1. 开始播放或暂停播放                                 -> updatePlayBarInterface
         */
        int FLAG_1 = 1;

        /**
         * 2. 停止当前播放, 然后开始播放上一首或下一首或播放任一首  -> updatePlayBarInterface & viewPager#setCurrentItem
         */
        int FLAG_2 = 2;

        /**
         * 3. 修改了播放列表的内容                               -> updatePlayBarInterface & viewPager#setCurrentItem  & viewPager.notify...
         */
        int FLAG_3 = 3;

        void onChange(int flag);
    }


}
