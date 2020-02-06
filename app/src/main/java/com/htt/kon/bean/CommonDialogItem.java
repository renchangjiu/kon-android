package com.htt.kon.bean;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * 普通对话框的item data
 *
 * @author su
 * @date 2020/02/05 20:14
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
@AllArgsConstructor
public class CommonDialogItem {

    private int id;

    private String name;

    private int imageId;

    /**
     * 动态显示在item 上的参数, 可能需要
     */
    private String data;

}
