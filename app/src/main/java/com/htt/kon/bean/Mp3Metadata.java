package com.htt.kon.bean;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author su
 * @date 2020/02/02 20:54
 */
@Getter
@Setter
@ToString(exclude = {"image"})
@Accessors(chain = true)
public class Mp3Metadata {
    /**
     * 封面
     */
    private byte[] image;
    /**
     * 标题/歌名
     */
    private String title;
    /**
     * 歌手
     */
    private String artist;
    /**
     * 专辑
     */
    private String album;
    /**
     * 时长, 毫秒
     */
    private Integer duration;
    /**
     * 比特率, 单位: kbps
     */
    private Integer bitRate;
}
