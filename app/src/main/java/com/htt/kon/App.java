package com.htt.kon;

import android.annotation.TargetApi;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationManagerCompat;

import com.htt.kon.bean.Music;
import com.htt.kon.bean.MusicList;
import com.htt.kon.constant.CommonConstant;

import com.htt.kon.service.Playlist;
import com.htt.kon.service.MusicService;
import com.htt.kon.service.database.Callback;
import com.htt.kon.service.database.MusicDbService;
import com.htt.kon.service.database.MusicListDbService;
import com.htt.kon.util.AppPathManger;
import com.htt.kon.util.LogUtils;
import com.htt.kon.util.SpUtils;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import lombok.Getter;

/**
 * @author su
 * @date 2020/02/06 19:55
 */
public class App extends Application {

    @Getter
    private static App app;

    @Getter
    private static Playlist playlist;

    @Getter
    private static ThreadPoolExecutor poolExecutor;

    public static final String N_C_PLAY_ID = "1";
    public static final String N_C_PLAY_NAME = "play";

    @Override
    public void onCreate() {
        super.onCreate();
        AppPathManger.initPaths(this);
        this.createNotificationChannel();
        this.whenInstall();
        LogUtils.e("App onCreate.");

        app = (App) getApplicationContext();

        playlist = Playlist.init(this);

        poolExecutor = new ThreadPoolExecutor(5, 10, 1, TimeUnit.SECONDS, new ArrayBlockingQueue<>(100), new ThreadPoolExecutor.CallerRunsPolicy());

        Intent intent = new Intent(this, MusicService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }
    }

    /**
     * 适配 Android 8.0 通知
     */
    @TargetApi(Build.VERSION_CODES.O)
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelDesc = "kon通知栏";
            NotificationChannel channel = new NotificationChannel(N_C_PLAY_ID, N_C_PLAY_NAME, NotificationManager.IMPORTANCE_LOW);
            channel.setDescription(channelDesc);

            NotificationManagerCompat manager = NotificationManagerCompat.from(this);
            manager.createNotificationChannel(channel);
        }
    }


    /**
     * 安装应用后做些操作
     */
    private void whenInstall() {
        new Thread(() -> {
            String key = "INSTALL_FLAG";
            boolean installFlag = SpUtils.getBoolean(this, key, true);
            if (!installFlag) {
                return;
            }
            this.work1();
            this.work2();
            SpUtils.putBoolean(this, key, false);
        }).start();
    }

    /**
     * 插入歌单: "本地音乐" & "我喜欢的音乐"
     */
    private void work2() {
        MusicListDbService service = MusicListDbService.of(this);

        if (service.getById(CommonConstant.MID_LOCAL_MUSIC) == null) {
            MusicList ml1 = new MusicList();
            ml1.setId(CommonConstant.MID_LOCAL_MUSIC);
            ml1.setName(getString(R.string.local_music));
            service.insert(ml1);
        }
        if (service.getById(CommonConstant.MID_MY_FAVORITE) == null) {
            MusicList ml2 = new MusicList();
            ml2.setId(CommonConstant.MID_MY_FAVORITE);
            ml2.setName(getString(R.string.my_favorite_music));
            service.insert(ml2);
        }
    }

    /**
     * 插入一首应用自带的歌曲
     */
    private void work1() {
        try {
            String builtInMusic = "放課後ティータイム - Listen!!.mp3";
            InputStream in = getAssets().open(builtInMusic);
            String path = AppPathManger.pathBuiltInMusic + builtInMusic;
            FileOutputStream out = new FileOutputStream(path);
            IOUtils.copy(in, out);
            in.close();
            out.close();
            MusicDbService service = MusicDbService.of(this);
            service.insert(path, CommonConstant.MID_LOCAL_MUSIC);
        } catch (IOException e) {
            LogUtils.e(e);
        }
    }
}
