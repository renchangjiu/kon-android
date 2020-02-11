package com.htt.kon.service;

import android.content.Context;

import androidx.room.Insert;

import com.htt.kon.bean.Music;
import com.htt.kon.dao.AppDatabase;
import com.htt.kon.dao.MusicDao;
import com.htt.kon.dao.MusicListDao;

import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author su
 * @date 2020/02/11 13:40
 */
public class MusicDbService {

    private MusicListDao musicListDao;
    private MusicDao musicDao;

    private static volatile MusicDbService instance = null;

    public static MusicDbService of(Context context) {
        if (instance == null) {
            synchronized (MusicDbService.class) {
                if (instance == null) {
                    instance = new MusicDbService();
                    instance.musicListDao = AppDatabase.of(context).musicListDao();
                    instance.musicDao = AppDatabase.of(context).musicDao();
                }
            }
        }
        return instance;
    }

    public List<Music> list() {
        return new ArrayList<>(Arrays.asList(this.musicDao.list()));
    }

    /**
     * list by mid
     */
    public List<Music> list(long mid) {
        return new ArrayList<>(Arrays.asList(this.musicDao.list(mid)));
    }

    public void insert(List<Music> list) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        this.musicDao.insert(list);
    }

    public void insert(Music music) {
        this.musicDao.insert(music);
    }
}
