package com.htt.kon.dialog;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.htt.kon.R;
import com.htt.kon.adapter.list.dialog.CommonDialogAdapter;
import com.htt.kon.bean.CommonDialogItem;
import com.htt.kon.util.JsonUtils;
import com.htt.kon.util.UiUtils;
import com.htt.kon.util.stream.Optional;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import lombok.Setter;

/**
 * @author su
 * @date 2020/02/05 17:45
 */
public class CommonDialog extends DialogFragment {
    private static final String B_K_TITLE = "title";
    private static final String B_K_ITEMS = "items";

    /**
     * 下一首播放
     */
    public static final int TAG_PLAY_NEXT = 0;

    /**
     * 收藏到歌单
     */
    public static final int TAG_COLLECT = 1;

    /**
     * 评论
     */
    public static final int TAG_COMMENT = 2;

    /**
     * 分享
     */
    public static final int TAG_SHARE = 3;

    /**
     * 上传到云盘
     */
    public static final int TAG_UPLOAD = 4;

    /**
     * 歌手
     */
    public static final int TAG_ARTIST = 5;

    /**
     * 专辑
     */
    public static final int TAG_ALBUM = 6;

    /**
     * 查看视频
     */
    public static final int TAG_VIDEO = 7;

    /**
     * 删除
     */
    public static final int TAG_DELETE = 8;

    /**
     * 音质升级
     */
    public static final int TAG_IMPROVE = 9;

    /**
     * 歌单创建
     */
    public static final int TAG_MUSIC_LIST_CREATE = 10;

    /**
     * 歌单管理
     */
    public static final int TAG_MUSIC_LIST_MANAGE = 11;

    /**
     * 歌单恢复
     */
    public static final int TAG_MUSIC_LIST_RESTORE = 12;


    public static final SparseArray<CommonDialogItem> FULL_ITEMS = new SparseArray<>();

    // 初始化item 列表
    static {
        FULL_ITEMS.put(TAG_PLAY_NEXT, new CommonDialogItem(TAG_PLAY_NEXT, "下一首播放", R.drawable.common_dialog_play_next, null));
        FULL_ITEMS.put(TAG_COLLECT, new CommonDialogItem(TAG_COLLECT, "收藏到歌单", R.drawable.common_dialog_collect2music_list, null));
        FULL_ITEMS.put(TAG_COMMENT, new CommonDialogItem(TAG_COMMENT, "评论(999)", R.drawable.common_dialog_comment, null));
        FULL_ITEMS.put(TAG_SHARE, new CommonDialogItem(TAG_SHARE, "分享", R.drawable.common_dialog_share, null));
        FULL_ITEMS.put(TAG_UPLOAD, new CommonDialogItem(TAG_UPLOAD, "上传到云盘", R.drawable.common_dialog_upload, null));
        FULL_ITEMS.put(TAG_ARTIST, new CommonDialogItem(TAG_ARTIST, "歌手: ", R.drawable.common_dialog_artist, null));
        FULL_ITEMS.put(TAG_ALBUM, new CommonDialogItem(TAG_ALBUM, "专辑: ", R.drawable.common_dialog_album, null));
        FULL_ITEMS.put(TAG_VIDEO, new CommonDialogItem(TAG_VIDEO, "查看视频", R.drawable.common_dialog_video, null));
        FULL_ITEMS.put(TAG_DELETE, new CommonDialogItem(TAG_DELETE, "删除", R.drawable.common_dialog_delete, null));
        FULL_ITEMS.put(TAG_IMPROVE, new CommonDialogItem(TAG_IMPROVE, "音质升级", R.drawable.common_dialog_improve, null));

        FULL_ITEMS.put(TAG_MUSIC_LIST_CREATE, new CommonDialogItem(TAG_MUSIC_LIST_CREATE, "创建新歌单", R.drawable.common_dialog_improve, null));
        FULL_ITEMS.put(TAG_MUSIC_LIST_MANAGE, new CommonDialogItem(TAG_MUSIC_LIST_MANAGE, "歌单管理", R.drawable.common_dialog_improve, null));
        FULL_ITEMS.put(TAG_MUSIC_LIST_RESTORE, new CommonDialogItem(TAG_MUSIC_LIST_RESTORE, "恢复歌单", R.drawable.common_dialog_improve, null));
    }

    @BindView(R.id.dc_textView)
    TextView textViewTitle;

    @BindView(R.id.dc_listView)
    ListView listView;


    @Setter
    private OnClickListener onClickListener;

    private CommonDialog() {
    }


    public static CommonDialog of(String title, List<CommonDialogItem> items) {
        CommonDialog of = new CommonDialog();
        Bundle bundle = new Bundle();
        bundle.putString(B_K_TITLE, title);
        bundle.putString(B_K_ITEMS, JsonUtils.bean2Json(items));
        of.setArguments(bundle);
        return of;
    }

    @Override
    public void onStart() {
        super.onStart();
        // 设置dialog: 背景透明、宽度为屏宽、位置在屏幕底部
        Window window = getDialog().getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.BOTTOM;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(params);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_common, container, false);
        ButterKnife.bind(this, view);
        this.init();

        slideToUp(view);
        return view;
    }


    private void init() {
        this.textViewTitle.setText(getArguments().getString(B_K_TITLE));
        String string = getArguments().getString(B_K_ITEMS);
        List<CommonDialogItem> items = JsonUtils.json2List(string, CommonDialogItem.class);
        this.listView.setAdapter(new CommonDialogAdapter(items));

        this.listView.setOnItemClickListener((parent, view, position, id) -> {
            // 回调方法
            CommonDialogItem item = UiUtils.getAdapter(this.listView, CommonDialogAdapter.class).getItem(position);
            Optional.of(this.onClickListener).ifPresent(v -> v.onClick(item));
            dismiss();
        });
    }

    /**
     * 向上滑动动画
     */
    public static void slideToUp(View view) {
        Animation slide = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                1.0f, Animation.RELATIVE_TO_SELF, 0.0f);

        slide.setDuration(400);
        slide.setFillAfter(true);
        slide.setFillEnabled(true);
        view.startAnimation(slide);
    }

    public interface OnClickListener {
        void onClick(CommonDialogItem item);
    }
}
