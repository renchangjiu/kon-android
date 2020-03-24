package com.htt.kon.util;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;
import android.widget.HeaderViewListAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.core.content.ContextCompat;

import com.htt.kon.R;

import java.lang.reflect.Method;

/**
 * @author su
 * @date 2020/02/03 17:58
 */
public class UiUtils {
    /**
     * 使菜单同时显示图标和文字
     */
    public static void showMenuIconAndTitleTogether(Menu menu) {
        if (menu != null) {
            if (menu.getClass().equals(MenuBuilder.class)) {
                try {
                    Method method = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                    method.setAccessible(true);
                    method.invoke(menu, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * 设置状态栏颜色
     */
    public static void setStatusBarColor(Activity activity) {
        setStatusBarColor(activity, R.color.colorPrimary);
    }

    /**
     * 设置状态栏颜色
     */
    public static void setStatusBarColor(Activity activity, int colorId) {
        Window window = activity.getWindow();
        //取消设置透明状态栏,使 ContentView 内容不再覆盖状态栏
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //需要设置这个 flag 才能调用 setStatusBarColor 来设置状态栏颜色
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        //设置状态栏颜色
        int color = ContextCompat.getColor(activity, colorId);
        window.setStatusBarColor(color);
    }

    /**
     * 获取 ListView 的 adapter
     *
     * @param listView listView
     * @param t        Adapter.class
     */
    public static <T> T getAdapter(ListView listView, Class<T> t) {
        ListAdapter adapter = listView.getAdapter();
        if (adapter instanceof HeaderViewListAdapter) {
            HeaderViewListAdapter ad = (HeaderViewListAdapter) listView.getAdapter();
            return (T) ad.getWrappedAdapter();
        } else {
            return (T) adapter;
        }
    }

    /**
     * 设置 TextView 的跑马灯效果(使用xml设置可能无效)
     */
    public static void setTextMarquee(@NonNull TextView... textViews) {
        for (TextView tv : textViews) {
            tv.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            tv.setSingleLine(true);
            tv.setSelected(true);
            tv.setFocusable(true);
            tv.setFocusableInTouchMode(true);
        }
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
}
