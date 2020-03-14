package com.htt.kon.adapter;


/**
 * 声明该 adapter 为异步获取数据的 adapter
 *
 * @author su
 * @date 2020/03/14 14:25
 */
public interface AsyncAdapter {

    /**
     * 更新数据
     */
    void updateRes();

    /**
     * 清空数据
     */
    void clearRes();

}
