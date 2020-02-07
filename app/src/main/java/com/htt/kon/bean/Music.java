package com.htt.kon.bean;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.fasterxml.jackson.databind.util.BeanUtil;

import org.jetbrains.annotations.NotNull;

import java.util.Date;

/**
 * @author su
 * @date 2020/02/02 21:12
 */
@Entity(tableName = "MUSIC")
public class Music {
    @PrimaryKey
    private Long id;

    /**
     * 所属歌单ID
     */
    @ColumnInfo(name = "mid")
    private Long mid;

    /**
     * 文件的绝对路径
     */
    @ColumnInfo(name = "path")
    private String path;

    /**
     * 文件大小, 字节
     */
    @ColumnInfo(name = "size")
    private Long size;


    /**
     * 歌曲封面的绝对路径
     */
    @ColumnInfo(name = "image")
    private String image;

    /**
     * title
     */
    @ColumnInfo(name = "title")
    private String title;

    /**
     * artist
     */
    @ColumnInfo(name = "artist")
    private String artist;

    /**
     * album
     */
    @ColumnInfo(name = "album")
    private String album;

    /**
     * 时长, 毫秒
     */
    @ColumnInfo(name = "duration")
    private Integer duration;

    /**
     * 创建日期(毫秒级时间戳)
     */
    @ColumnInfo(name = "CREATE_TIME")
    private Long createTime;
    @Ignore
    private String createTimeLabel;

    /**
     * 是否删除, 1是/2否
     */
    @ColumnInfo(name = "del_flag")
    private Integer delFlag;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMid() {
        return mid;
    }

    public void setMid(Long mid) {
        this.mid = mid;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
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

    public Integer getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(Integer delFlag) {
        this.delFlag = delFlag;
    }
}
