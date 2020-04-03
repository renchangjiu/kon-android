package com.htt.kon.adapter.list;


import com.htt.kon.bean.Music;

import java.util.List;

/**
 * @author su
 * @date 2020/02/23 19:42
 */
public interface CommonAdapter {

    /**
     * 设置左侧按钮的点击事件监听器
     *
     * @param listener OnOptionClickListener
     */
    void setOnOptionClickListener(OnOptionClickListener listener);

    /**
     * 更新数据
     *
     * @param musics music list
     */
    void updateRes(List<Music> musics);

}
