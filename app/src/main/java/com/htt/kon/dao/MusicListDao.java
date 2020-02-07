package com.htt.kon.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.htt.kon.bean.Music;
import com.htt.kon.bean.MusicList;

/**
 * @author su
 * @date 2020/02/07 14:15
 */
@Dao
public interface MusicListDao {

    @Insert
    void insert(MusicList... musics);

    @Delete
    void delete(MusicList... musics);

    @Query("update MUSIC_LIST set DEL_FLAG = 1 where id = :id")
    void logicDelete(long id);

    @Query("SELECT * FROM MUSIC_LIST where DEL_FLAG = 2")
    MusicList[] list();
}
