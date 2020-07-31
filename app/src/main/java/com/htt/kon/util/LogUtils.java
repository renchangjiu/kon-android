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

    private static void print(int level, String tag, String message) {
        if (message.length() <= MAX_LENGTH) {
            print1(level, tag, message);
        } else {
            while (message.length() > MAX_LENGTH) {
                print1(level, tag, message.substring(0, MAX_LENGTH));
                message = message.substring(MAX_LENGTH);
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
        String className = "";
        String methodName = "";
        int lineNumber = -1;
        StackTraceElement[] stackTraces = Thread.currentThread().getStackTrace();
        if (stackTraces.length >= 5) {
            className = stackTraces[4].getClassName();
            className = className.substring(className.lastIndexOf(".") + 1);
            methodName = stackTraces[4].getMethodName();
            lineNumber = stackTraces[4].getLineNumber();
        }

        String defTag = lineNumber + "/" + className + "#" + methodName;
        if (defTag.length() >= 23) {
            defTag = defTag.substring(0, 20) + "...";
        } else {
            int diff = 23 - defTag.length();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < diff; i++) {
                sb.append("·");
            }
            defTag = defTag + sb.toString();
        }
        return defTag;
    }
}
