package com.htt.kon.util;

/**
 * @author su
 * @date 2020/04/08 08:40
 */
public class CommonUtils {
    /**
     * 把秒级时间格式化为mm:ss形式
     */
    public static String formatTime(int totalSecond) {
        String minute = "00";
        String second = "00";
        if (totalSecond < 60) {
            if (totalSecond < 10) {
                second = "0" + totalSecond;
            } else {
                second = "" + totalSecond;
            }
        } else {
            int intMin = totalSecond / 60;
            int intSec = totalSecond % 60;
            if (intMin < 10) {
                minute = "0" + intMin;
            } else {
                minute = "" + intMin;
            }
            if (intSec < 10) {
                second = "0" + intSec;
            } else {
                second = "" + intSec;
            }
        }
        return minute + ":" + second;
    }
    //
    // # 返回以KB 或 MB表示的文件大小, size: 字节数
    // def format_size(size: int) -> str:
    //     if size < 1024 * 1024:
    //         size = str(int(size / 1024)) + "KB"
    //     else:
    //         size = str(round(size / 1024 / 1024, 1)) + "MB"
    //     return size
}
