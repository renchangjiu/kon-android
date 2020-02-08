package com.htt.kon.bean;

import android.content.Context;

import androidx.annotation.NonNull;

import com.htt.kon.util.JsonUtils;
import com.htt.kon.util.LogUtils;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * 播放列表, 单例
 *
 * @author su
 * @date 2020/02/04 10:21
 */
public class Playlist {
    private static final String PLAY_LIST_LOCAL_PATH = "/playlist.json";


    /**
     * 播放模式: 列表循环
     */
    private static final int MODE_LOOP = 1;

    /**
     * 播放模式: 随机播放
     */
    private static final int MODE_RANDOM = 2;

    /**
     * 播放模式: 单曲循环
     */
    private static final int MODE_SINGLE_LOOP = 3;

    private List<Music> musics;

    private int mode = MODE_LOOP;

    private int index = 0;

    public Music getCurMusic() {
        if (this.musics.isEmpty()) {
            return null;
        }
        return this.musics.get(this.index);
    }

    public Music getMusic(int position) {
        if (position > this.musics.size() - 1) {
            return null;
        }
        return this.musics.get(position);
    }

    public int size() {
        return this.musics.size();
    }

    /**
     * 播放下一首, 返回切换后的Music
     */
    public Music next() {
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
        return this.getCurMusic();
    }

    /**
     * 播放上一首, 返回切换后的Music
     */
    public Music prev() {
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
        return this.getCurMusic();
    }

    private Playlist() {
        this.musics = new ArrayList<>();
    }

    /**
     * 从磁盘初始化播放列表
     */
    public static Playlist init4Disk(Context context) {
        try {
            File file = new File(context.getFilesDir().getAbsolutePath() + PLAY_LIST_LOCAL_PATH);
            if (!file.exists()) {
                return new Playlist();
            }
            FileInputStream in = new FileInputStream(context.getFilesDir().getAbsolutePath() + PLAY_LIST_LOCAL_PATH);
            String json = IOUtils.toString(in, StandardCharsets.UTF_8);
            in.close();
            InnerPlaylist ipl = JsonUtils.json2Bean(json, InnerPlaylist.class);
            if (ipl == null) {
                return new Playlist();
            }
            Playlist playlist = new Playlist();
            playlist.setIndex(ipl.getIndex());
            playlist.setMode(ipl.getMode());
            playlist.setMusics(ipl.getMusics());
            return playlist;
        } catch (IOException e) {
            LogUtils.e(e);
        }
        return new Playlist();
    }

    /**
     * 将播放列表保存到本地文件中
     */
    public void save2disk(Context context) {
        try {
            InnerPlaylist ipl = new InnerPlaylist();
            ipl.setIndex(this.index);
            ipl.setMode(this.mode);
            ipl.setMusics(this.musics);
            String json = JsonUtils.bean2Json(ipl);
            FileOutputStream out = new FileOutputStream(context.getFilesDir().getAbsolutePath() + PLAY_LIST_LOCAL_PATH);
            out.write(json.getBytes());
            out.flush();
            out.close();
        } catch (IOException e) {
            LogUtils.e(e.getMessage());
        }
    }

    public List<Music> getMusics() {
        return musics;
    }

    public void setMusics(@NonNull List<Music> musics) {
        this.musics = musics;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @Getter
    @Setter
    private static class InnerPlaylist {
        private List<Music> musics;

        private int mode;

        private int index;
    }
}
