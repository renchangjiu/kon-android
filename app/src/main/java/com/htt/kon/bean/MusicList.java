package com.htt.kon.bean;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author su
 * @date 2020/02/02 21:11
 */
@Entity(tableName = "MUSIC_LIST")
public class MusicList {

    @PrimaryKey
    private Long id;

    /**
     * 歌单名
     */
    @ColumnInfo(name = "NAME")
    private String name;

    /**
     * 创建日期(毫秒级时间戳)
     */
    @ColumnInfo(name = "CREATE_TIME")
    private Long createTime;
    @Ignore
    private String createTimeLabel;


    /**
     * 播放次数
     */
    @ColumnInfo(name = "PLAY_COUNT")
    private Integer playCount;

    /**
     * 是否删除, 1是/2否
     */
    @ColumnInfo(name = "DEL_FLAG")
    private Integer delFlag;

    /**
     * 歌单下所属的music 集合
     */
    @Ignore
    private List<Music> musics;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public String getCreateTimeLabel() {
        return createTimeLabel;
    }

    public void setCreateTimeLabel(String createTimeLabel) {
        this.createTimeLabel = createTimeLabel;
    }

    public Integer getPlayCount() {
        return playCount;
    }

    public void setPlayCount(Integer playCount) {
        this.playCount = playCount;
    }

    public Integer getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(Integer delFlag) {
        this.delFlag = delFlag;
    }

    public List<Music> getMusics() {
        return musics;
    }

    public void setMusics(List<Music> musics) {
        this.musics = musics;
    }
}
