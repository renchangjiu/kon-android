package com.htt.kon.service.database;

import android.content.Context;


import androidx.annotation.Nullable;

import com.htt.kon.App;
import com.htt.kon.bean.Music;
import com.htt.kon.bean.MusicList;
import com.htt.kon.dao.AppDatabase;
import com.htt.kon.dao.MusicDao;
import com.htt.kon.dao.MusicListDao;
import com.htt.kon.util.stream.Optional;

import org.apache.commons.collections4.CollectionUtils;

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

    private static volatile MusicListDbService of = null;

    private App app;

    public static MusicListDbService of(Context context) {
        if (of == null) {
            synchronized (MusicListDbService.class) {
                if (of == null) {
                    of = new MusicListDbService();
                    of.app = (App) context.getApplicationContext();
                    of.musicListDao = AppDatabase.of(context).musicListDao();
                    of.musicDao = AppDatabase.of(context).musicDao();
                }
            }
        }
        return of;
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

    public void insert(MusicList ml) {
        ml.setPlayCount(0);
        ml.setDelFlag(2);
        ml.setCreateTime(System.currentTimeMillis());
        this.musicListDao.insert(ml);
    }

    public void insert(MusicList musicList, @Nullable Callback<MusicList> call) {
        App.getPoolExecutor().execute(() -> {
            this.insert(musicList);
            Optional.of(call).ifPresent(v -> v.on(musicList));
        });
    }


    public MusicList getById(long id) {
        MusicList ret = this.musicListDao.selectByKey(id);
        this.putData(ret);
        return ret;
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
        List<Music> list = this.musicDao.list(musicList.getId());
        musicList.setMusics(list);
    }

}
