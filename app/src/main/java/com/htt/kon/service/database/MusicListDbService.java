package com.htt.kon.service.database;

import android.content.Context;


import androidx.annotation.Nullable;

import com.htt.kon.bean.MusicList;
import com.htt.kon.dao.AppDatabase;
import com.htt.kon.dao.MusicDao;
import com.htt.kon.dao.MusicListDao;
import com.htt.kon.util.stream.Optional;

import org.apache.commons.collections4.CollectionUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * 单例
 *
 * @author su
 * @date 2020/02/11 13:40
 */
public class MusicListDbService {

    private MusicListDao musicListDao;
    private MusicDao musicDao;

    private static volatile MusicListDbService instance = null;

    public static MusicListDbService of(Context context) {
        if (instance == null) {
            synchronized (MusicListDbService.class) {
                if (instance == null) {
                    instance = new MusicListDbService();
                    instance.musicListDao = AppDatabase.of(context).musicListDao();
                    instance.musicDao = AppDatabase.of(context).musicDao();
                }
            }
        }
        return instance;
    }

    public List<MusicList> list() {
        List<MusicList> list = this.musicListDao.list();
        this.putData(list);
        return list;
    }

    public void list(Callback<List<MusicList>> call) {
        new Thread(() -> {
            call.on(this.list());
        }).start();
    }

    public void insert(MusicList music) {
        this.musicListDao.insert(music);
    }

    public void insert(MusicList musicList, @Nullable Callback<MusicList> call) {
        new Thread(() -> {
            this.insert(musicList);
            Optional.of(call).ifPresent(v -> v.on(musicList));
        }).start();
    }


    public MusicList getById(long id) {
        return this.musicListDao.selectByKey(id);
    }

    public void getById(long id, Callback<MusicList> call) {
        new Thread(() -> {
            call.on(this.getById(id));
        }).start();


    }

    private void putData(List<MusicList> list) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        for (MusicList musicList : list) {
            this.putData(musicList);
        }
    }

    private void putData(MusicList musicList) {
        if (musicList == null) {
            return;
        }
    }

}
