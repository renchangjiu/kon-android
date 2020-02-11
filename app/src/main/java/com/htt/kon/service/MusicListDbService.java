package com.htt.kon.service;

import android.content.Context;


import com.htt.kon.bean.Music;
import com.htt.kon.dao.AppDatabase;
import com.htt.kon.dao.MusicDao;
import com.htt.kon.dao.MusicListDao;

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


}
