package com.htt.kon.bean;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

/**
 * @author su
 * @date 2020/02/02 21:12
 */
@Entity(tableName = "MUSIC")
public class MusicDO {
    @PrimaryKey
    private Long id;

    /**
     * 所属歌单ID
     */
    @ColumnInfo(name = "mid")
    private Long mid;

    /**
     * 文件绝对路径
     */
    @ColumnInfo(name = "path")
    private String path;

    /**
     * 文件大小, 字节
     */
    @ColumnInfo(name = "size")
    private Integer size;


    /**
     * 音乐的封面, base64
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

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
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

    public Integer getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(Integer delFlag) {
        this.delFlag = delFlag;
    }

    @NotNull
    @Override
    public String toString() {
        return "Music{" +
                "id=" + id +
                ", mid=" + mid +
                ", path='" + path + '\'' +
                ", size=" + size +
                ", image='" + image + '\'' +
                ", title='" + title + '\'' +
                ", artist='" + artist + '\'' +
                ", album='" + album + '\'' +
                ", duration=" + duration +
                ", delFlag=" + delFlag +
                '}';
    }
}
