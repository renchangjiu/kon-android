package com.htt.kon.activity;

import android.app.Activity;

/**
 * 需要从 Bundle 中接收数据的 activity 务必实现此接口, 以明确数据传递的方式
 *
 * @author su
 * @date 2020/03/14 12:33
 */
public interface DataRequisiteActivity {

    /**
     * 通过该方法向目标 activity 传递数据, 然后启动目标 activity
     *
     * @param source source
     * @param args   args
     */
    static void start(Activity source, Object... args) {
        throw new RuntimeException();
    }
}
