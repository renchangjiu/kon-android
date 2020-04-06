package com.htt.kon.util;

import java.util.HashMap;

/**
 * 当调用 startActivity() 时, 向目标 activity 传递数据(因为 Bundle 可存放的数据有限)
 *
 * @author su
 * @date 2020/04/04 20:12
 * @deprecated 实用性太小
 */
@Deprecated
public class DataContainer {

    private static final HashMap<Class, Object> dc = new HashMap<>();


    public static void set(Object data) {
        dc.put(getInvokeClass(), data);
    }

    public static <T> T get(Class<T> c) {
        T res = (T) dc.get(getInvokeClass());
        dc.put(getInvokeClass(), null);
        return res;
    }

    private static Class getInvokeClass() {
        Class<?> clazz = null;
        try {
            String cn = Thread.currentThread().getStackTrace()[3].getClassName();
            clazz = Class.forName(cn);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return clazz;
    }

}
