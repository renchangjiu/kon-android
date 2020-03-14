package com.htt.kon.activity;

import android.os.Bundle;

/**
 * 需要从 Bundle 中接收数据的 activity 务必实现此接口, 以明确数据传递的方式
 *
 * @author su
 * @date 2020/03/14 12:33
 */
public interface DataRequisiteActivity {

    /**
     * 通过该方法向目标 activity 传递数据
     *
     * @return Bundle
     */
    static Bundle putData() {
        throw new RuntimeException();
    }
}
