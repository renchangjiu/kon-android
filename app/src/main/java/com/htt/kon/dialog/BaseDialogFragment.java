package com.htt.kon.dialog;


import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import androidx.fragment.app.DialogFragment;


/**
 * 实现弹出框从底部弹出
 *
 * @author su
 * @date 2020/02/08 17:50
 */
public class BaseDialogFragment extends DialogFragment {

    @Override
    public void onStart() {
        super.onStart();
        // 设置dialog 的宽度为屏宽、高度为屏高的6/10、位置在屏幕底部
        Window window = getDialog().getWindow();
        window.setBackgroundDrawableResource(android.R.color.white);
        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.BOTTOM;
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        wlp.height = (int) (getContext().getResources().getDisplayMetrics().heightPixels * 0.6);
        window.setAttributes(wlp);
    }

}
