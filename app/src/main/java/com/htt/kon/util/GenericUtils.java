package com.htt.kon.util;

import java.util.List;
import java.util.Set;

/**
 * 强转成泛型, 以避免idea 警告
 *
 * @author su
 * @date 2020/02/27 20:55
 */
public class GenericUtils {

    public static <T> T of(Object o, Class<T> clazz) {
        return (T) o;
    }

    public static <T> List<T> ofList(Object o, Class<T> clazz) {
        return (List<T>) o;
    }

    public static <T> Set<T> ofSet(Object o, Class<T> clazz) {
        return (Set<T>) o;
    }
}
