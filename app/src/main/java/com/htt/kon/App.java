package com.htt.kon;

import android.annotation.TargetApi;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationManagerCompat;

import com.htt.kon.service.Playlist;
import com.htt.kon.service.MusicService;
import com.htt.kon.util.LogUtils;

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
        this.createNotificationChannel();
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
}
