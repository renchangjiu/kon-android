package com.htt.kon.bean;


import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author su
 * @date 2020/02/09 10:12
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
@EqualsAndHashCode(of = "value")
public class PlayMode {

    private int value;

    private String label;
}
