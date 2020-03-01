package com.htt.kon.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.htt.kon.bean.MusicList;

import java.util.List;

/**
 * @author su
 * @date 2020/02/07 14:15
 */
@Dao
public interface MusicListDao {


    @Insert
    void insert(List<MusicList> musics);

    @Insert
    void insert(MusicList musics);

    @Delete
    void delete(MusicList... musics);

    @Query("update MUSIC_LIST set DEL_FLAG = 1 where id = :id")
    void logicDelete(long id);

    @Query("SELECT * FROM MUSIC_LIST where DEL_FLAG = 2")
    List<MusicList> list();

    @Query("SELECT * FROM MUSIC_LIST where id = :id")
    MusicList selectByKey(long id);
}
