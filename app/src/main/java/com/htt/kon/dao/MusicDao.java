package com.htt.kon.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.htt.kon.bean.Music;

import java.util.List;


/**
 * @author su
 * @date 2020/02/07 13:58
 */
@Dao
public interface MusicDao {

    @Insert
    void insert(List<Music> musics);

    @Insert
    void insert(Music musics);


    @Delete
    @Deprecated
    void delete(Music... musics);

    @Query("update MUSIC set DEL_FLAG = 1 where ID = :id")
    void logicDelete(long id);


    @Query("SELECT * FROM MUSIC where DEL_FLAG = 2")
    List<Music> list();

    /**
     * list by mid.
     */
    @Query("SELECT * FROM MUSIC where DEL_FLAG = 2 and MID = :mid")
    List<Music> list(long mid);

    @Query("SELECT * FROM MUSIC where id = :id")
    Music selectByKey(long id);

}
