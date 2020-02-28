package com.htt.kon.util;

import android.text.Editable;
import android.text.TextWatcher;

/**
 * @author su
 * @date 2020/02/28 14:35
 */
public abstract class TextWatcherWrapper implements TextWatcher {

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public abstract void afterTextChanged(Editable s);
}
