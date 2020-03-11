package com.htt.kon.function;


/**
 * 函数式接口, 无参数和一个返回值
 *
 * @author su
 * @date 2020/03/11 21:20
 */
public interface Function0<R> {

    /**
     * callback function
     *
     * @return r
     */
    R invoke();
}
