package com.htt.kon.util;

import android.content.Context;
import android.os.storage.StorageManager;
import android.text.TextUtils;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @author su
 * @date 2020/02/03 10:41
 */
public class StorageUtils {

    /**
     * 获取外置存储根路径
     */
    public static String getExternalRootPath(Context context) {
        String path = "";
        try {
            File filesDir = context.getExternalFilesDir(null);
            path = filesDir.getParentFile().getParentFile().getParentFile().getParentFile().getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return path;
    }

    /**
     * 获取外置sd卡根路径
     */
    public static String getSdcardRootPathR(Context context) {
        String path = "";
        try {
            File[] filesDirs = context.getExternalFilesDirs(null);
            File filesDir = filesDirs[1];
            path = filesDir.getParentFile().getParentFile().getParentFile().getParentFile().getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return path;
    }

    /**
     * 获取外置sd卡根路径, 适用于 android6.0+
     */
    public static String getSdcardRootPath(Context context) {
        String path = "";
        //使用getSystemService(String)检索一个StorageManager用于访问系统存储功能。
        StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
        try {
            Class<?> storageVolumeClazz = Class.forName("android.os.storage.StorageVolume");
            Method getVolumeList = storageManager.getClass().getMethod("getVolumeList");
            Method getPath = storageVolumeClazz.getMethod("getPath");
            Method isRemovable = storageVolumeClazz.getMethod("isRemovable");
            Object result = getVolumeList.invoke(storageManager);

            for (int i = 0; i < Array.getLength(result); i++) {
                Object storageVolumeElement = Array.get(result, i);
                path = (String) getPath.invoke(storageVolumeElement);
                boolean removable = (Boolean) isRemovable.invoke(storageVolumeElement);
                if (removable) {
                    return path;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return path;
    }

    /**
     * 获取外置sd卡根路径, 适用于 android6.0以下
     */
    public static String getSdcardRootPath() {
        String path = "";
        try {
            Map<String, String> map = System.getenv();
            Set<String> set = System.getenv().keySet();
            for (String key : set) {
                String value = map.get(key);
                if ("SECONDARY_STORAGE".equals(key)) {
                    if (!TextUtils.isEmpty(value) && value.contains(":")) {
                        path = value.split(":")[0];
                    } else {
                        path = value;
                    }
                }
                if (!TextUtils.isEmpty(path)) {
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return path;
    }
}
