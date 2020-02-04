package com.htt.kon.bean;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author su
 * @date 2020/02/02 21:11
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class MusicListDO {

    private Integer id;

    /**
     * 歌单名
     */
    private String name;

    /**
     * 创建日期(秒级时间戳)
     */
    private Date createTime;
    private Date createTimeLabel;

    /**
     * 播放次数
     */
    private Integer playCount;

    /**
     * 歌单所属音乐列表
     */
    private Date musics;
}
