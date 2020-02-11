package com.htt.kon.util;

/**
 * @author su
 * @date 2020/02/11 21:11
 */
public class ThreadUtil {

    /**
     * 仅测试用
     */
    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
