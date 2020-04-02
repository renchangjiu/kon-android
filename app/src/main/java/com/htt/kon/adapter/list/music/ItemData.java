package com.htt.kon.adapter.list.music;

import com.htt.kon.bean.Music;

import java.util.List;

import lombok.Data;

/**
 * 4个页面统一的 adapter数据结构
 *
 * @author su
 * @date 2020/04/02 13:09
 */
@Data
public class ItemData {

    /**
     * title, 在单曲页为空, 在歌手页为歌手名, 在专辑页为专辑名, 在文件夹页为目录名
     */
    private String title;

    /**
     * 该项包含的音乐列表
     */
    private List<Music> musics;

}
