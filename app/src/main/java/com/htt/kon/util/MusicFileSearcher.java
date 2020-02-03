package com.htt.kon.util;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
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
