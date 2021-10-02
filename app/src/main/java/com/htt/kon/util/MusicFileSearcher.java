package com.htt.kon.util;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import com.htt.kon.bean.Mp3Metadata;
import com.htt.kon.bean.Music;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 从磁盘中搜索音乐文件
 *
 * @author su
 * @date 2020/02/03 10:07
 */
public class MusicFileSearcher {
    private static final String[] EXT = new String[]{"mp3", "flac"};


    /**
     * 使用 MediaProvider 搜索音乐文件.
     * 优势: 搜索速度快.
     * 缺陷: 数据更新不及时.
     */
    public static List<String> search(Context context) {
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                , null, null, null, null);
        List<String> paths = new ArrayList<>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                paths.add(path);
            }
            cursor.close();
        }
        return paths;
    }

    /**
     * 从指定根目录及子目录中搜索音乐文件
     *
     * @param path 根目录
     */
    public static List<String> search(String path) {
        List<String> container = new ArrayList<>();
        File file = new File(path);
        if (!file.exists()) {
            return container;
        }

        File[] files = file.listFiles();
        LinkedList<File> dirs = new LinkedList<>();
        if (files != null) {
            for (File f : files) {
                if (!f.getName().startsWith(".")) {
                    if (f.isDirectory()) {
                        dirs.add(f);
                    } else {
                        if (FilenameUtils.isExtension(f.getAbsolutePath().toLowerCase(), EXT)) {
                            container.add(f.getAbsolutePath());
                        }
                    }
                }
            }
        }
        File dir;
        File[] listFiles;
        while (!dirs.isEmpty()) {
            dir = dirs.removeFirst();
            listFiles = dir.listFiles();
            if (listFiles == null) {
                continue;
            }
            for (File f : listFiles) {
                if (!f.getName().startsWith(".")) {
                    if (f.isDirectory()) {
                        dirs.add(f);
                    } else {
                        if (FilenameUtils.isExtension(f.getAbsolutePath().toLowerCase(), EXT)) {
                            container.add(f.getAbsolutePath());
                        }
                    }
                }
            }
        }
        return container;
    }
}
