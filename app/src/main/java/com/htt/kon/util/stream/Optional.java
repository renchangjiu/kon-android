package com.htt.kon.util.stream;


import java.util.NoSuchElementException;

/**
 * 简易 optional
 *
 * @author su
 * @date 2020/02/09 15:04
 */
public class Optional<T> {

    private final T value;

    private Optional(T value) {
        this.value = value;
    }

    public static <T> Optional<T> of(T value) {
        return new Optional<>(value);
    }


    public void ifPresent(Consumer<? super T> consumer) {
        if (value != null) {
            consumer.accept(value);
        }
    }

    public boolean isPresent() {
        return value != null;
    }

    public T get() {
        if (this.value == null) {
            throw new NoSuchElementException("No value present");
        } else {
            return this.value;
        }
    }
}
