package com.htt.kon.bean;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * 普通对话框的item
 *
 * @author su
 * @date 2020/02/05 20:14
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class CommonDialogItem {

    private int id;

    private String name;

    private int imageId;

    /**
     * 携带的数据, json 格式
     */
    private String data;

}
