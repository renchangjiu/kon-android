package com.htt.kon.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

import com.htt.kon.bean.MusicDO;
import com.htt.kon.bean.PlayList;
import com.htt.kon.util.IdWorker;
import com.htt.kon.util.LogUtils;
import com.htt.kon.util.MusicFileSearcher;
import com.htt.kon.util.StorageUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author su
 * @date 2020/02/06 20:11
 */
public class MusicService extends Service {
    private MusicBinder binder = new MusicBinder();
    private PlayList playList;
    private MediaPlayer player;

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

        player = new MediaPlayer();

        // 初始化播放列表
        String sdcardRootPath = StorageUtils.getSdcardRootPathR(this);
        List<String> list1 = MusicFileSearcher.search(sdcardRootPath);

        String externalRootPath = StorageUtils.getExternalRootPath(this);
        List<String> list2 = MusicFileSearcher.search(externalRootPath);

        list1.addAll(list2);
        List<MusicDO> musicDOS = new ArrayList<>();
        for (String path : list1) {
            MusicDO music = new MusicDO();
            music.setId(IdWorker.singleNextId());
            music.setPath(path);
            musicDOS.add(music);
        }
        this.playList = PlayList.of();
        this.playList.setMusics(musicDOS);
        LogUtils.e();
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

    public class MusicBinder extends Binder {
        public MusicService getMusicService() {
            return MusicService.this;
        }
    }
}
