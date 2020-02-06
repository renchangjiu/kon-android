package com.htt.kon.bean;

import androidx.annotation.NonNull;

import java.util.List;

/**
 * 播放列表, 单例
 *
 * @author su
 * @date 2020/02/04 10:21
 */
public class PlayList {
    private PlayList() {
    }

    public static PlayList of() {
        return new PlayList();
    }

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

    private int index = 0;

    public MusicDO getCurMusic() {
        if (this.musics.isEmpty()) {
            return null;
        }
        return this.musics.get(this.index);
    }

    /**
     * 播放下一首
     */
    public void next() {
        switch (this.mode) {
            case MODE_LOOP:
                if (this.index >= this.musics.size() - 1) {
                    this.index = 0;
                } else {
                    this.index += 1;
                }
                break;
            default:
        }
    }

    /**
     * 播放上一首
     */
    public void prev() {
        switch (this.mode) {
            case MODE_LOOP:
                if (this.index <= 0) {
                    this.index = this.musics.size() - 1;
                } else {
                    this.index -= 1;
                }
                break;
            default:
        }
    }

    public List<MusicDO> getMusics() {
        return musics;
    }

    public void setMusics(@NonNull List<MusicDO> musics) {
        this.musics = musics;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }
}
