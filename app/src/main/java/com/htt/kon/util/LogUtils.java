package com.htt.kon.util;

import android.util.Log;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @author su
 * @date 2020/01/14 13:03
 */
public class LogUtils {
    private static final boolean OPEN_LOG = true;
    private static final String DEFAULT_MSG = "---";

    private static final int MAX_LENGTH = 1024;
    private static final int LEVEL_ERROR = 1;
    private static final int LEVEL_DEBUG = 2;

    public static void e(String tag, Object o) {
        if (OPEN_LOG) {
            print(LEVEL_ERROR, tag, o.toString());
        }
    }

    public static void e(Object o) {
        if (OPEN_LOG) {
            print(LEVEL_ERROR, getDefaultTag(), o.toString());
        }

    }

    public static void e() {
        if (OPEN_LOG) {
            print(LEVEL_ERROR, getDefaultTag(), DEFAULT_MSG);
        }
    }

    public static void e(Throwable throwable) {
        if (OPEN_LOG) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            throwable.printStackTrace(pw);
            print(LEVEL_ERROR, getDefaultTag(), sw.toString());
            try {
                pw.close();
                sw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void d(String tag, Object o) {
        if (OPEN_LOG) {
            print(LEVEL_DEBUG, tag, o.toString());
        }
    }

    public static void d(Object o) {
        if (OPEN_LOG) {
            print(LEVEL_DEBUG, getDefaultTag(), o.toString());
        }
    }


    public static void d() {
        if (OPEN_LOG) {
            print(LEVEL_DEBUG, getDefaultTag(), DEFAULT_MSG);
        }
    }

    private static void print(int level, String tag, String string) {
        if (string.length() <= MAX_LENGTH) {
            print1(level, tag, string);
        } else {
            while (string.length() > MAX_LENGTH) {
                print1(level, tag, string.substring(0, MAX_LENGTH));
                string = string.substring(MAX_LENGTH);
            }
        }
    }

    private static void print1(int level, String tag, String string) {
        switch (level) {
            case LEVEL_ERROR:
                Log.e(tag, string);
                break;
            case LEVEL_DEBUG:
                Log.d(tag, string);
                break;
            default:
        }
    }


    private static String getDefaultTag() {
        String defTag = getClassName() + "#" + getMethodName();
        if (defTag.length() >= 23) {
            defTag = defTag.substring(0, 20) + "...";
        }
        return defTag;
    }

    private static String getClassName() {
        StackTraceElement[] stackTraces = Thread.currentThread().getStackTrace();
        if (stackTraces.length >= 6) {
            String className = stackTraces[5].getClassName();
            className = className.substring(className.lastIndexOf(".") + 1);
            return className;
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
