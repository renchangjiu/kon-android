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
import com.htt.kon.constant.MidConstant;
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

/**
 * @author su
 * @date 2020/02/06 19:55
 */
public class App extends Application {
    private static Context app;

    private static Playlist playlist;

    public static final String N_C_PLAY_ID = "1";
    public static final String N_C_PLAY_NAME = "play";

    @Override
    public void onCreate() {
        super.onCreate();
        AppPathManger.initPaths(this);
        this.createNotificationChannel();
        this.createLocalMusicListIfNotExist();
        this.whenInstall();
        LogUtils.e("App onCreate.");

        app = getApplicationContext();

        playlist = Playlist.init(this);

        Intent intent = new Intent(this, MusicService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }
    }

    public static App getApp() {
        return (App) app;
    }

    public Playlist getPlaylist() {
        return playlist;
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


    private void createLocalMusicListIfNotExist() {
        MusicListDbService service = MusicListDbService.of(this);
        service.getById(MidConstant.MID_LOCAL_MUSIC, musicList -> {
            if (service.getById(MidConstant.MID_LOCAL_MUSIC) == null) {
                MusicList ml = new MusicList();
                ml.setId(MidConstant.MID_LOCAL_MUSIC);
                ml.setName(getString(R.string.local_music));
                ml.setCreateTime(System.currentTimeMillis());
                ml.setPlayCount(0);
                ml.setDelFlag(2);
                service.insert(ml);
            }
        });
    }

    /**
     * 安装应用后做些操作
     */
    private void whenInstall() {
        new Thread(() -> {
            try {
                String key = "INSTALL_FLAG";
                String builtInMusic = "放課後ティータイム - Listen!!.mp3";
                boolean installFlag = SpUtils.getBoolean(this, key, true);
                if (!installFlag) {
                    return;
                }
                InputStream in = getAssets().open(builtInMusic);
                String path = AppPathManger.pathBuiltInMusic + builtInMusic;
                FileOutputStream out = new FileOutputStream(path);
                IOUtils.copy(in, out);
                in.close();
                out.close();

                MusicDbService service = MusicDbService.of(this);

                service.insert(path, MidConstant.MID_LOCAL_MUSIC);
                SpUtils.putBoolean(this, key, false);
            } catch (IOException e) {
                LogUtils.e(e);
            }
        }).start();
    }
}
