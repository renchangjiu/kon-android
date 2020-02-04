package com.htt.kon.bean;

import java.util.List;

/**
 * @author su
 * @date 2020/02/04 10:21
 */
public class PlayList {
    /**
     * 播放模式: 列表循环
     */
    private static final int MODE_LOOP = 1;

    /**
     * 播放模式: 随机播放
     */
    private static final int MODE_RANDOM = 2;

    /**
     * 播放模式: 顺序播放(列表播放完则停止)
     */
    private static final int MODE_SEQUENTIAL = 3;

    private List<MusicDO> musics;

    private int mode = MODE_LOOP;


}
