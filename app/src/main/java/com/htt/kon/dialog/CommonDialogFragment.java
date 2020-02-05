package com.htt.kon.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.htt.kon.R;
import com.htt.kon.activity.PlayBarActivity;
import com.htt.kon.adapter.list.dialog.CommonDialogAdapter;
import com.htt.kon.bean.CommonDialogItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author su
 * @date 2020/02/05 17:45
 */
public class CommonDialogFragment extends DialogFragment {
    private static final String BUNDLE_KEY_MUSIC_ID = "musicId";
    private static final String BUNDLE_KEY_TAGS = "tags";

    public static final int TAG_PLAY_NEXT = 0;
    public static final int TAG_COLLECT = 1;
    public static final int TAG_COMMENT = 2;
    public static final int TAG_SHARE = 3;
    public static final int TAG_UPLOAD = 4;
    public static final int TAG_ARTIST = 5;
    public static final int TAG_ALBUM = 6;
    public static final int TAG_VIDEO = 7;
    public static final int TAG_DELETE = 8;


    /**
     * 本地音乐 -> 单曲页面弹出框 tag 数组
     */
    public static final int[] TAGS_SINGLE = new int[]{0, 1, 2, 3, 4, 5, 6, 7,8};

    /**
     * 本地音乐 -> 歌手页面弹出框 tag 数组
     */
    public static final int[] TAGS_ARTIST = new int[]{0, 1, 4, 8};

    /**
     * 本地音乐 -> 专辑页面弹出框 tag 数组
     */
    public static final int[] TAGS_ALBUM = new int[]{0, 1, 4, 8};

    /**
     * 本地音乐 -> 文件夹页面弹出框 tag 数组
     */
    public static final int[] TAGS_DIR = new int[]{0, 1, 4, 8};


    private static List<CommonDialogItem> FULL_ITEMS = new ArrayList<>();

    // 初始化item 列表
    static {
        CommonDialogItem item1 = new CommonDialogItem().setId(TAG_PLAY_NEXT).setImageId(R.drawable.common_dialog_play_next).setName("下一首播放");
        CommonDialogItem item2 = new CommonDialogItem().setId(TAG_COLLECT).setImageId(R.drawable.common_dialog_collect2music_list).setName("收藏到歌单");
        CommonDialogItem item3 = new CommonDialogItem().setId(TAG_COMMENT).setImageId(R.drawable.common_dialog_comment).setName("评论(999)");
        CommonDialogItem item4 = new CommonDialogItem().setId(TAG_SHARE).setImageId(R.drawable.common_dialog_share).setName("分享");
        CommonDialogItem item5 = new CommonDialogItem().setId(TAG_UPLOAD).setImageId(R.drawable.common_dialog_upload).setName("上传到云盘");
        CommonDialogItem item6 = new CommonDialogItem().setId(TAG_ARTIST).setImageId(R.drawable.common_dialog_artist).setName("歌手");
        CommonDialogItem item7 = new CommonDialogItem().setId(TAG_ALBUM).setImageId(R.drawable.common_dialog_album).setName("专辑: ");
        CommonDialogItem item8 = new CommonDialogItem().setId(TAG_VIDEO).setImageId(R.drawable.common_dialog_video).setName("查看视频");
        CommonDialogItem item9 = new CommonDialogItem().setId(TAG_DELETE).setImageId(R.drawable.common_dialog_delete).setName("删除");
        FULL_ITEMS.add(item1);
        FULL_ITEMS.add(item2);
        FULL_ITEMS.add(item3);
        FULL_ITEMS.add(item4);
        FULL_ITEMS.add(item5);
        FULL_ITEMS.add(item6);
        FULL_ITEMS.add(item7);
        FULL_ITEMS.add(item8);
        FULL_ITEMS.add(item9);
    }

    @BindView(R.id.dc_textView)
    TextView textView;

    @BindView(R.id.dc_listView)
    ListView listView;

    private int[] tags;

    private OnSelectListener listener;

    public static CommonDialogFragment of(int musicId, int[] tags) {
        CommonDialogFragment instance = new CommonDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(BUNDLE_KEY_MUSIC_ID, musicId);
        bundle.putIntArray(BUNDLE_KEY_TAGS, tags);
        instance.setArguments(bundle);
        return instance;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_common, container, false);
        ButterKnife.bind(this, view);

        this.init();
        return view;
    }

    private void init() {
        if (this.tags == TAGS_SINGLE) {
            int musicId = getArguments().getInt(BUNDLE_KEY_MUSIC_ID);
            this.listView.setAdapter(new CommonDialogAdapter(getItemsByTags(tags)));
        } else {

        }

    }

    private List<CommonDialogItem> getItemsByTags(int[] tags) {
        List<CommonDialogItem> items = new ArrayList<>();
        for (int tag : tags) {
            // 索引与tag 是一样的
            CommonDialogItem item = FULL_ITEMS.get(tag);
            items.add(item);
        }
        return items;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Bundle bundle = getArguments();
        this.tags = bundle.getIntArray(BUNDLE_KEY_TAGS);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        // 设置宽度为屏宽、位置在屏幕底部
        Window window = getDialog().getWindow();
        window.setBackgroundDrawableResource(android.R.color.white);
        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.BOTTOM;
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        wlp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(wlp);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    public void setSelectListener(OnSelectListener listener) {
        this.listener = listener;
    }

    public interface OnSelectListener {
        void onPositiveClick(DialogFragment dialog);

        void onNegativeClick(DialogFragment dialog);
    }
}
