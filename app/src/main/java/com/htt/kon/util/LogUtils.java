package com.htt.kon.util;

import android.util.Log;

/**
 * @author su
 * @date 2020/01/14 13:03
 */
public class LogUtils {
    private static final boolean OPEN_LOG = true;
    private static final String DEFAULT_MSG = "---";

    public static void e(String tag, Object o) {
        if (OPEN_LOG) {
            Log.e(tag, o.toString());
        }
    }

    public static void e(Object o) {
        if (OPEN_LOG) {
            Log.e(getDefaultTag(), o.toString());
        }
    }


    public static void e() {
        if (OPEN_LOG) {
            Log.e(getDefaultTag(), DEFAULT_MSG);
        }
    }

    public static void d(String tag, Object o) {
        if (OPEN_LOG) {
            Log.d(tag, o.toString());
        }
    }

    public static void d(Object o) {
        if (OPEN_LOG) {
            Log.d(getDefaultTag(), o.toString());
        }
    }


    public static void d() {
        if (OPEN_LOG) {
            Log.d(getDefaultTag(), DEFAULT_MSG);
        }
    }


    private static String getDefaultTag() {
        return getClassName() + "#" + getMethodName();
    }

    private static String getClassName() {
        StackTraceElement[] stackTraces = Thread.currentThread().getStackTrace();
        if (stackTraces.length >= 6) {
            return stackTraces[5].getClassName();
        }
        return "";
    }

    private static String getMethodName() {
        StackTraceElement[] stackTraces = Thread.currentThread().getStackTrace();
        if (stackTraces.length >= 6) {
            return stackTraces[5].getMethodName();
        }
        return "";
    }

}
