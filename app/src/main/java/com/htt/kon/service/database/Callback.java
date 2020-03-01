package com.htt.kon.service.database;


/**
 * @author su
 * @date 2020/03/01 13:49
 */
public interface Callback<T> {
    /**
     * 该方法在子线程中执行
     *
     * @param t t
     */
    void on(T t);
}
