package com.htt.kon.fragment.music;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.htt.kon.R;
import com.htt.kon.activity.LocalMusicActivity;
import com.htt.kon.activity.ScanMusicFinishActivity;
import com.htt.kon.adapter.list.music.ItemData;
import com.htt.kon.adapter.list.music.LocalMusicFragmentAdapter;
import com.htt.kon.bean.CommonDialogItem;
import com.htt.kon.bean.Music;
import com.htt.kon.broadcast.BaseReceiver;
import com.htt.kon.broadcast.PlayStateChangeReceiver;
import com.htt.kon.constant.CommonConstant;
import com.htt.kon.dialog.CommonDialog;
import com.htt.kon.dialog.MusicListDialog;
import com.htt.kon.dialog.OptionDialog;
import com.htt.kon.service.database.MusicDbService;
import com.htt.kon.util.JsonUtils;
import com.htt.kon.util.UiUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author su
 * @date 2020/02/16 14:00
 */
public abstract class BaseLocalMusicPagerFragment extends Fragment {
    private Handler handler = new Handler((msg) -> {
        this.adapter.updateRes(this.musics);
        if (this.musics.isEmpty()) {
            if (this.listView.getFooterViewsCount() == 0) {
                this.listView.addFooterView(this.footer);
            }
        } else {
            this.listView.removeFooterView(this.footer);
        }
        return true;
    });

    protected ListView listView;

    protected LocalMusicActivity activity;

    /**
     * 本地音乐集合
     */
    protected List<Music> musics;

    private MusicDbService musicDbService;

    private PlayStateChangeReceiver receiver;

    private LocalMusicFragmentAdapter adapter;

    private View footer;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.activity = (LocalMusicActivity) context;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_local_music, container, false);
        this.listView = view.findViewById(R.id.flm_listView);

        this.init();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        this.initData();
        this.adapter = UiUtils.getAdapter(this.listView, LocalMusicFragmentAdapter.class);
        assert adapter != null;

        // 事件处理
        adapter.setOnOptionClickListener((CommonDialogItem item) -> {
            ItemData itemData = JsonUtils.json2Bean(item.getData(), ItemData.class);
            List<Music> musics = itemData.getMusics();
            String title = itemData.getTitle();
            switch (item.getId()) {
                case CommonDialog.TAG_PLAY_NEXT:
                    this.activity.nextPlay(musics);
                    Toast.makeText(this.activity, this.activity.getString(R.string.added_to_next_play), Toast.LENGTH_SHORT).show();
                    break;
                case CommonDialog.TAG_COLLECT:
                    // 收藏到歌单
                    MusicListDialog mlDialog = MusicListDialog.of(musics, title);
                    mlDialog.show(activity.getSupportFragmentManager(), "1");
                    break;
                case CommonDialog.TAG_DELETE:
                    OptionDialog.ofDeleteMusic(this.activity, musics, () -> {
                        this.initData();
                        this.activity.runOnUiThread(() -> {
                            Toast.makeText(this.activity, R.string.deleted, Toast.LENGTH_SHORT).show();
                        });
                        return null;
                    });
                    break;
                default:
            }
        });
    }

    private void init() {
        footer = LayoutInflater.from(this.activity).inflate(R.layout.list_footer_sabd, this.listView, false);
        footer.findViewById(R.id.lfs_textView).setOnClickListener(v -> {
            startActivity(new Intent(this.activity, ScanMusicFinishActivity.class));
        });
        this.listView.addFooterView(footer);

        this.receiver = new PlayStateChangeReceiver();
        BaseReceiver.registerLocal(this.activity, this.receiver, PlayStateChangeReceiver.ACTION);
        this.receiver.setOnReceiveListener(this::onReceiveBroadcast);
        this.musicDbService = MusicDbService.of(this.activity);
    }

    /**
     * 从数据库中初始化数据
     */
    public void initData() {
        this.musicDbService.list(CommonConstant.MID_LOCAL_MUSIC, musics -> {
            this.musics = musics;
            this.handler.sendEmptyMessage(0);
            this.dataInitialized();
        });
    }

    /**
     * 当数据被初始化后调用, 一般用于子类更新界面
     * 注:
     * 1. 该方法会在子线程中执行
     */
    public void dataInitialized() {

    }

    /**
     * 当接收到广播时调用
     *
     * @param flag MusicPlayStateBroadcastReceiver.FLAG_PLAY or MusicPlayStateBroadcastReceiver.FLAG_CLEAR, etc
     */
    private void onReceiveBroadcast(PlayStateChangeReceiver.Flag flag) {
        switch (flag) {
            case PLAY:
            case CLEAR:
            case REMOVE:
                UiUtils.getAdapter(this.listView, BaseAdapter.class).notifyDataSetChanged();
                break;
            default:
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        BaseReceiver.unregisterLocal(this.activity, this.receiver);
    }

}
