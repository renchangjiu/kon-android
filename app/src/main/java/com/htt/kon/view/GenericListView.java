package com.htt.kon.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * getAdapter() 方法加上泛型
 *
 * @author su
 * @date 2020/02/02 19:28
 */
public class GenericListView<T extends ListAdapter> extends ListView {

    @Override
    public T getAdapter() {
        return (T) super.getAdapter();
    }

    public GenericListView(Context context) {
        super(context);
    }

    public GenericListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GenericListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public GenericListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
}
