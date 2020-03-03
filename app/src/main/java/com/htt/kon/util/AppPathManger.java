package com.htt.kon.util;

import android.content.Context;

import java.io.File;

/**
 * @author su
 * @date 2020/03/03 21:23
 */
public class AppPathManger {

    /**
     * 音乐文件的封面文件路径
     */
    public static String pathCoverImage = "";

    /**
     * 内置音乐文件的路径
     */
    public static String pathBuiltInMusic = "";

    /**
     * 初始化各个路径, 不存在的目录将被创建
     */
    public static void initPaths(Context context) {
        pathCoverImage = context.getExternalFilesDir(null) + "/image/";
        createDirIfNotExist(pathCoverImage);

        pathBuiltInMusic = context.getExternalFilesDir(null) + "/music/";
        createDirIfNotExist(pathBuiltInMusic);
    }

    private static void createDirIfNotExist(String path) {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdir();
        }
    }
}
