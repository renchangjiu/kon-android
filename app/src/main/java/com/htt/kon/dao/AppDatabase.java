package com.htt.kon.dao;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.htt.kon.bean.Music;
import com.htt.kon.bean.MusicList;


/**
 * Google doc: https://developer.android.google.cn/training/data-storage/room
 *
 * @author su
 * @date 2020/02/07 13:58
 */
@Database(entities = {Music.class, MusicList.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    public abstract MusicDao musicDao();

    public abstract MusicListDao musicListDao();

    private static volatile AppDatabase instance = null;


    /**
     * 注意：如果您的应用在单个进程中运行，则在实例化 AppDatabase 对象时应遵循单例设计模式。<br/>
     * 每个 RoomDatabase 实例的成本相当高，而您几乎不需要在单个进程中访问多个实例。
     *
     * @param context Context
     * @return instance
     */
    public static AppDatabase of(Context context) {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "database-name").build();
                }
            }
        }
        return instance;
    }

}
