package com.htt.kon;

import android.app.Application;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

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

    @Override
    public void onCreate() {
        super.onCreate();
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
}
