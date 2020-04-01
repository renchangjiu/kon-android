package com.htt.kon.function;


/**
 * 函数式接口, 一个参数和一个返回值
 *
 * @author su
 * @date 2020/03/11 21:20
 */
@FunctionalInterface
public interface Function1<R, T> {

    /**
     * callback function
     *
     * @param t t
     * @return r
     */
    R invoke(T t);
}
