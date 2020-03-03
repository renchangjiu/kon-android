package com.htt.kon.service.database;

import android.content.Context;

import com.htt.kon.R;
import com.htt.kon.bean.Mp3Metadata;
import com.htt.kon.bean.Music;
import com.htt.kon.dao.AppDatabase;
import com.htt.kon.dao.MusicDao;
import com.htt.kon.dao.MusicListDao;
import com.htt.kon.util.AppPathManger;
import com.htt.kon.util.IdWorker;
import com.htt.kon.util.LogUtils;
import com.htt.kon.util.MusicFileMetadataParser;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author su
 * @date 2020/02/11 13:40
 */
public class MusicDbService {

    private MusicListDao musicListDao;

    private MusicDao musicDao;

    private static volatile MusicDbService of = null;

    private Context context;

    public static MusicDbService of(Context context) {
        if (of == null) {
            synchronized (MusicDbService.class) {
                if (of == null) {
                    of = new MusicDbService();
                    of.context = context.getApplicationContext();
                    of.musicListDao = AppDatabase.of(context).musicListDao();
                    of.musicDao = AppDatabase.of(context).musicDao();
                }
            }
        }
        return of;
    }

    public List<Music> list() {
        List<Music> list = this.musicDao.list();
        this.putData(list);
        return list;
    }

    public void list(Callback<List<Music>> call) {
        new Thread(() -> {
            call.on(this.list());
        }).start();
    }

    /**
     * list by mid
     */
    public List<Music> list(long mid) {
        List<Music> list = this.musicDao.list(mid);
        this.putData(list);
        return list;
    }


    public void insert(Music music) {
        this.setDataIfEmpty(music);
        this.musicDao.insert(music);
    }

    public void insert(List<Music> list) {
        for (Music music : list) {
            this.setDataIfEmpty(music);
        }
        this.musicDao.insert(list);
    }

    /**
     * 解析指定路径的音乐文件, 然后插入数据库
     *
     * @param path path
     * @param mid  music list id
     * @return success or failed
     */
    public boolean insert(String path, long mid) {
        try {
            Mp3Metadata metadata = MusicFileMetadataParser.parse(path);
            Music music = new Music();
            music.setId(IdWorker.singleNextId());
            music.setMid(mid);
            music.setPath(path);
            music.setSize(new File(path).length());
            if (metadata.getImage() != null) {
                File imageFile = new File(AppPathManger.pathCoverImage + music.getId() + ".png");
                FileOutputStream out = new FileOutputStream(imageFile);
                out.write(metadata.getImage());
                out.flush();
                out.close();
                music.setImage(imageFile.getAbsolutePath());
            }
            music.setTitle(metadata.getTitle());
            music.setArtist(metadata.getArtist());
            music.setAlbum(metadata.getAlbum());
            music.setDuration(metadata.getDuration());
            music.setBitRate(metadata.getBitRate());
            music.setCreateTime(System.currentTimeMillis());
            music.setDelFlag(2);
            if (StringUtils.isEmpty(music.getTitle())) {
                return false;
            } else {
                this.insert(music);
                return true;
            }
        } catch (IOException e) {
            LogUtils.e(e);
            return false;
        }
    }

    public Music getById(long id) {
        return this.musicDao.selectByKey(id);
    }


    /**
     * 将给定集合按 artist 分组, 若某 artist 以 / 分隔, 则视为两人
     *
     * @param list music list.
     * @return Map<artist, music list>
     */
    public Map<String, List<Music>> listGroupByArtist(List<Music> list) {
        Map<String, List<Music>> map = new HashMap<>();
        for (Music music : list) {
            String artist = music.getArtist();
            String[] split = artist.split("/");
            for (String s : split) {
                if (StringUtils.isEmpty(s)) {
                    continue;
                }
                if (map.containsKey(s)) {
                    map.get(s).add(music);
                } else {
                    List<Music> r = new LinkedList<>();
                    r.add(music);
                    map.put(s, r);
                }
            }
        }
        return map;
    }


    /**
     * 将给定集合按 album 分组
     *
     * @param list music list.
     * @return Map<album, music list>
     */
    public Map<String, List<Music>> listGroupByAlbum(List<Music> list) {
        Map<String, List<Music>> map = new HashMap<>();
        for (Music music : list) {
            String album = music.getAlbum();
            if (map.containsKey(album)) {
                map.get(album).add(music);
            } else {
                List<Music> r = new LinkedList<>();
                r.add(music);
                map.put(album, r);
            }
        }
        return map;
    }

    /**
     * 将给定集合按 dir 分组
     *
     * @param list music list.
     * @return Map<dir, music list>
     */
    public Map<String, List<Music>> listGroupByDir(List<Music> list) {
        Map<String, List<Music>> map = new HashMap<>();
        for (Music music : list) {
            String path = new File(music.getPath()).getParent();
            if (map.containsKey(path)) {
                map.get(path).add(music);
            } else {
                List<Music> r = new LinkedList<>();
                r.add(music);
                map.put(path, r);
            }
        }
        return map;
    }


    private void setDataIfEmpty(Music music) {
        if (StringUtils.isEmpty(music.getArtist())) {
            music.setArtist(this.context.getString(R.string.artist_unknown));
        }
        if (StringUtils.isEmpty(music.getAlbum())) {
            music.setAlbum(this.context.getString(R.string.unknown));
        }
    }


    private void putData(List<Music> list) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        for (Music music : list) {
            this.putData(music);
        }
    }

    private void putData(Music music) {
        if (music == null) {
            return;
        }
    }
}
