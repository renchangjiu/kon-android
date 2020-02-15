package com.htt.kon.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.htt.kon.R;
import com.htt.kon.activity.LocalMusicActivity;
import com.htt.kon.adapter.list.dialog.CommonDialogAdapter;
import com.htt.kon.bean.CommonDialogItem;
import com.htt.kon.bean.Music;
import com.htt.kon.service.database.MusicDbService;
import com.htt.kon.util.JsonUtils;
import com.htt.kon.util.UiUtils;
import com.htt.kon.util.stream.Optional;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import lombok.Setter;

/**
 * @author su
 * @date 2020/02/05 17:45
 */
public class CommonDialogFragment extends BaseDialogFragment {
    private static final String B_K_FLAG = "flag";

    private static final String B_K_SINGLE_MUSIC_JSON = "musicJson";

    private static final String B_K_ABD_NAME = "abdValue";
    private static final String B_K_ABD_MUSICS_JSON = "musicsJson";


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
     * 表示打开弹出框的是哪个页面: 单曲页面
     */
    private static final String FLAG_SINGLE = "single";

    /**
     * 歌手页面
     */
    private static final String FLAG_ARTIST = "artist";

    /**
     * 专辑页面
     */
    private static final String FLAG_ALBUM = "album";

    /**
     * 文件夹页面
     */
    private static final String FLAG_DIR = "dir";


    private static final SparseArray<CommonDialogItem> FULL_ITEMS = new SparseArray<>();

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
    }

    @BindView(R.id.dc_textView)
    TextView textViewTitle;

    @BindView(R.id.dc_listView)
    ListView listView;


    @Setter
    private OnClickListener onClickListener;

    private CommonDialogFragment() {
    }

    /**
     * 单曲页面
     */
    public static CommonDialogFragment ofSingle(String musicJson) {
        CommonDialogFragment instance = new CommonDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(B_K_FLAG, FLAG_SINGLE);
        bundle.putString(B_K_SINGLE_MUSIC_JSON, musicJson);
        instance.setArguments(bundle);
        return instance;
    }

    /**
     * 歌手页面
     */
    public static CommonDialogFragment ofArtist(String artist, String musicsJson) {
        return ofAbd(FLAG_ARTIST, artist, musicsJson);
    }

    /**
     * 专辑页面
     */
    public static CommonDialogFragment ofAlbum(String album, String musicsJson) {
        return ofAbd(FLAG_ALBUM, album, musicsJson);
    }

    /**
     * 文件夹页面
     */
    public static CommonDialogFragment ofDir(String dir, String musicsJson) {
        return ofAbd(FLAG_DIR, dir, musicsJson);
    }

    private static CommonDialogFragment ofAbd(String abd, String name, String musicsJson) {
        CommonDialogFragment instance = new CommonDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(B_K_FLAG, abd);
        bundle.putString(B_K_ABD_NAME, name);
        bundle.putString(B_K_ABD_MUSICS_JSON, musicsJson);
        instance.setArguments(bundle);
        return instance;
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
        String flag = getArguments().getString(B_K_FLAG);
        switch (flag) {
            case FLAG_SINGLE:
                this.init4single();
                break;
            case FLAG_ARTIST:
                this.init4Artist();
                break;
            default:
        }
        this.listView.setOnItemClickListener((parent, view, position, id) -> {
            // 回调方法
            CommonDialogItem item = UiUtils.getListViewAdapter(this.listView, CommonDialogAdapter.class).getItem(position);
            Optional.of(this.onClickListener).ifPresent(v -> v.onClick(item));
            dismiss();
        });
    }

    private void init4single() {
        assert getArguments() != null;
        String musicJson = getArguments().getString(B_K_SINGLE_MUSIC_JSON);
        Music music = JsonUtils.json2Bean(musicJson, Music.class);
        assert music != null;

        String format = getString(R.string.cdf_dialog_title_single);
        this.textViewTitle.setText(String.format(format, music.getTitle()));
        List<CommonDialogItem> items = new ArrayList<>();
        items.add(FULL_ITEMS.get(TAG_PLAY_NEXT).setName(getString(R.string.cdf_play_next)).setData(music));
        items.add(FULL_ITEMS.get(TAG_COLLECT).setName(getString(R.string.cdf_collect)).setData(music));
        items.add(FULL_ITEMS.get(TAG_ARTIST).setName(String.format(getString(R.string.cdf_artist), music.getArtist())).setData(music));
        items.add(FULL_ITEMS.get(TAG_ALBUM).setName(String.format(getString(R.string.cdf_album), music.getAlbum())).setData(music));
        items.add(FULL_ITEMS.get(TAG_DELETE).setName(getString(R.string.cdf_delete)).setData(music));
        this.listView.setAdapter(new CommonDialogAdapter(items));
    }

    private void init4Artist() {
        assert getArguments() != null;
        String artist = getArguments().getString(B_K_ABD_NAME);
        String musicsJson = getArguments().getString(B_K_ABD_MUSICS_JSON);
        List<Music> musics = JsonUtils.json2List(musicsJson, Music.class);

        String format = getString(R.string.cdf_dialog_title_artist);
        this.textViewTitle.setText(String.format(format, artist));
        List<CommonDialogItem> items = new ArrayList<>();
        items.add(FULL_ITEMS.get(TAG_PLAY_NEXT).setName(getString(R.string.cdf_play_next)).setData(musics));
        items.add(FULL_ITEMS.get(TAG_COLLECT).setName(getString(R.string.cdf_collect)).setData(musics));
        items.add(FULL_ITEMS.get(TAG_DELETE).setName(getString(R.string.cdf_delete)).setData(musics));
        this.listView.setAdapter(new CommonDialogAdapter(items));
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
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
    }

    @Override
    public void onStop() {
        super.onStop();
    }


    public interface OnClickListener {
        void onClick(CommonDialogItem item);
    }
}
