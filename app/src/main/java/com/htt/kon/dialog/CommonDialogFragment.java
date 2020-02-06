package com.htt.kon.dialog;

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
import com.htt.kon.adapter.list.dialog.CommonDialogAdapter;
import com.htt.kon.bean.CommonDialogItem;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author su
 * @date 2020/02/05 17:45
 */
public class CommonDialogFragment extends DialogFragment {
    private static final String BUNDLE_KEY_MUSIC_ID = "musicId";

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
     * 表示打开弹出框的是哪个页面: 单曲页面
     */
    private static final String FLAG_SINGLE = "single";

    /**
     * 歌手页面 & 专辑页面 & 文件夹页面
     */
    private static final String FLAG_ABD = "abd";

    private String flag;

    private static List<CommonDialogItem> FULL_ITEMS = new ArrayList<>();

    // 初始化item 列表
    static {
        FULL_ITEMS.add(new CommonDialogItem(TAG_PLAY_NEXT, "下一首播放", R.drawable.common_dialog_play_next, null));
        FULL_ITEMS.add(new CommonDialogItem(TAG_COLLECT, "收藏到歌单", R.drawable.common_dialog_collect2music_list, null));
        FULL_ITEMS.add(new CommonDialogItem(TAG_COMMENT, "评论(999)", R.drawable.common_dialog_comment, null));
        FULL_ITEMS.add(new CommonDialogItem(TAG_SHARE, "分享", R.drawable.common_dialog_share, null));
        FULL_ITEMS.add(new CommonDialogItem(TAG_UPLOAD, "上传到云盘", R.drawable.common_dialog_upload, null));
        FULL_ITEMS.add(new CommonDialogItem(TAG_ARTIST, "歌手: ", R.drawable.common_dialog_artist, null));
        FULL_ITEMS.add(new CommonDialogItem(TAG_ALBUM, "专辑: ", R.drawable.common_dialog_album, null));
        FULL_ITEMS.add(new CommonDialogItem(TAG_VIDEO, "查看视频", R.drawable.common_dialog_video, null));
        FULL_ITEMS.add(new CommonDialogItem(TAG_DELETE, "删除", R.drawable.common_dialog_delete, null));
    }

    @BindView(R.id.dc_textView)
    TextView textView;

    @BindView(R.id.dc_listView)
    ListView listView;

    private Context context;

    private OnSelectListener listener;

    /**
     * 单曲页面
     */
    public static CommonDialogFragment ofSingle(int musicId) {
        CommonDialogFragment instance = new CommonDialogFragment();
        instance.flag = FLAG_SINGLE;
        Bundle bundle = new Bundle();
        bundle.putInt(BUNDLE_KEY_MUSIC_ID, musicId);
        instance.setArguments(bundle);
        return instance;
    }

    /**
     * 歌手页面 & 专辑页面 & 文件夹页面
     */
    public static CommonDialogFragment ofAbd(int musicId) {
        CommonDialogFragment instance = new CommonDialogFragment();
        instance.flag = FLAG_ABD;
        Bundle bundle = new Bundle();
        bundle.putInt(BUNDLE_KEY_MUSIC_ID, musicId);
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
        switch (this.flag) {
            case FLAG_SINGLE:
                this.init4single();
                break;
            case FLAG_ABD:
                this.init4abd();
                break;
            default:
        }
        this.listView.setOnItemClickListener((parent, view, position, id) -> {
            if (this.listener != null) {
                this.listener.onSelect(position);
            }
        });
    }

    private void init4single() {
        int[] tags = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8};
        int musicId = getArguments().getInt(BUNDLE_KEY_MUSIC_ID);
        this.listView.setAdapter(new CommonDialogAdapter(getItemsByTags(tags)));
    }

    private void init4abd() {
        int[] tags = new int[]{0, 1, 4, 8};
        int musicId = getArguments().getInt(BUNDLE_KEY_MUSIC_ID);
        this.listView.setAdapter(new CommonDialogAdapter(getItemsByTags(tags)));
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
        this.context = context;
        Bundle bundle = getArguments();
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
        // 设置dialog 的宽度为屏宽、高度为屏高的6/10、位置在屏幕底部
        Window window = getDialog().getWindow();
        window.setBackgroundDrawableResource(android.R.color.white);
        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.BOTTOM;
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        wlp.height = (int) (context.getResources().getDisplayMetrics().heightPixels * 0.6);
        window.setAttributes(wlp);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    public void setOnSelectListener(OnSelectListener listener) {
        this.listener = listener;
    }

    public interface OnSelectListener {
        void onSelect(int tag);

    }
}
