package com.htt.kon.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.htt.kon.bean.Music;


/**
 * @author su
 * @date 2020/02/07 13:58
 */
@Dao
public interface MusicDao {

    @Insert
    void insert(Music... musics);

    @Delete
    void delete(Music... musics);

    @Query("update music set del_flag = 1 where id = :id")
    void logicDelete(long id);

    @Query("SELECT * FROM Music where del_flag = 2")
    Music[] list();

}
