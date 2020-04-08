package com.htt.kon.service;

import android.content.Context;
import android.util.SparseArray;

import com.htt.kon.R;
import com.htt.kon.bean.Music;
import com.htt.kon.bean.PlayMode;
import com.htt.kon.util.JsonUtils;
import com.htt.kon.util.LogUtils;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 播放列表, 单例. 其各种设置属性的方法务必仅在 MusicService 中调用, 以防止逻辑混乱
 *
 * @author su
 * @date 2020/02/04 10:21
 */
@ToString
public class Playlist {
    private static final String PLAY_LIST_LOCAL_PATH = "/playlist.json";


    /**
     * 播放模式: 列表循环
     */
    public static final int MODE_LOOP = 1;

    /**
     * 播放模式: 随机播放
     */
    public static final int MODE_RANDOM = 2;

    /**
     * 播放模式: 单曲循环
     */
    public static final int MODE_SINGLE_LOOP = 3;

    @Getter
    private List<Music> musics;

    @Getter
    private int mode = MODE_LOOP;

    @Getter
    private int index = -1;


    /**
     * 切换到下一首
     *
     * @return current music
     */
    Music next() {
        switch (this.mode) {
            case MODE_LOOP:
                if (this.index >= this.musics.size() - 1) {
                    this.index = 0;
                } else {
                    this.index += 1;
                }
                break;
            case MODE_RANDOM:
                this.index = new Random().nextInt(this.size());
                break;
            case MODE_SINGLE_LOOP:
                break;
            default:
        }
        return this.getCurMusic();
    }

    /**
     * 切换到上一首
     *
     * @return current music
     */
    Music prev() {
        switch (this.mode) {
            case MODE_LOOP:
                if (this.index <= 0) {
                    this.index = this.musics.size() - 1;
                } else {
                    this.index -= 1;
                }
                break;
            case MODE_RANDOM:
                this.index = new Random().nextInt(this.size());
                break;
            case MODE_SINGLE_LOOP:
                break;
            default:
        }
        return this.getCurMusic();
    }

    /**
     * 根据pos 删除列表中的某一项
     *
     * @param index pos
     */
    void remove(int index) {
        this.musics.remove(index);
        if (index < this.index) {
            this.index -= 1;
        }
        if (this.isEmpty()) {
            this.index = -1;
        }
    }

    /**
     * 清空
     */
    void clear() {
        this.musics.clear();
        this.index = -1;
    }

    /**
     * 使用新的歌曲集合替换当前播放的列表
     *
     * @param musics musics
     * @param index  index
     */
    void replace(List<Music> musics, int index) {
        this.musics = new ArrayList<>(musics);
        this.index = index;
    }


    /**
     * 添加到播放列表的最后一位
     *
     * @param music music
     */
    void add(Music music) {
        this.musics.add(music);
    }

    /**
     * 添加到播放列表的指定位置
     *
     * @param music music
     */
    void add(Music music, int index) {
        this.musics.add(index, music);
        if (this.index == -1) {
            this.index = 0;
        }
    }

    /**
     * 添加到播放列表的指定位置
     *
     * @param musics music list
     * @param index  index at which to insert the first element from the specified collection
     */
    void add(List<Music> musics, int index) {
        this.musics.addAll(index, musics);
        if (this.index == -1) {
            this.index = 0;
        }
    }

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

    public boolean isEmpty() {
        return this.musics.isEmpty();
    }

    public boolean isNotEmpty() {
        return !this.isEmpty();
    }


    private Playlist() {
        this.musics = new ArrayList<>();
    }

    /**
     * 从磁盘初始化播放列表
     */
    public static Playlist init(Context context) {
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
            LogUtils.e("Init playlist.");
            return playlist;
        } catch (IOException e) {
            LogUtils.e(e);
        }
        return new Playlist();
    }

    /**
     * 将播放列表保存到本地文件中
     */
    public void save(Context context) {
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
            LogUtils.e("Save playlist.");
        } catch (IOException e) {
            LogUtils.e(e);
        }
    }

    private void setMusics(List<Music> musics) {
        this.musics = musics;
    }

    void setMode(int mode) {
        this.mode = mode;
    }

    void setIndex(int index) {
        this.index = index;
    }

    public static List<PlayMode> getModes(Context context) {
        List<PlayMode> modes = new ArrayList<>();
        modes.add(new PlayMode().setValue(MODE_LOOP).setLabel(context.getString(R.string.mode_loop)));
        modes.add(new PlayMode().setValue(MODE_RANDOM).setLabel(context.getString(R.string.mode_random)));
        modes.add(new PlayMode().setValue(MODE_SINGLE_LOOP).setLabel(context.getString(R.string.mode_single_loop)));
        return modes;
    }

    public static PlayMode getModeByValue(int mode, Context context) {
        List<PlayMode> modes = getModes(context);
        return modes.get(modes.indexOf(new PlayMode().setValue(mode)));
    }

    @Getter
    @Setter
    private static class InnerPlaylist {
        private List<Music> musics;

        private int mode;

        private int index;
    }
}
