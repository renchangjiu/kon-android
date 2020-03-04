package com.htt.kon.fragment.music;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.htt.kon.R;
import com.htt.kon.adapter.list.music.AlbumAdapter;
import com.htt.kon.adapter.list.music.DirAdapter;
import com.htt.kon.bean.CommonDialogItem;
import com.htt.kon.bean.Music;
import com.htt.kon.constant.CommonConstant;

import com.htt.kon.dialog.CommonDialogFragment;
import com.htt.kon.util.JsonUtils;
import com.htt.kon.util.LogUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 本地音乐activity 下的文件夹tab 页
 *
 * @author su
 * @date 2020/02/14 21:44
 */
public class DirPagerFragment extends BaseLocalMusicPagerFragment {


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        this.init();
        return view;
    }


    private void init() {
        this.initListView();
    }

    private void initListView() {
        new Thread(() -> {
            List<Music> list = super.musicDbService.list(CommonConstant.MID_LOCAL_MUSIC);
            // 按文件夹分类
            Map<String, List<Music>> map = super.musicDbService.listGroupByDir(list);

            // 封装参数
            List<DirAdapter.ItemData> res = new ArrayList<>();
            Set<Map.Entry<String, List<Music>>> entries = map.entrySet();
            for (Map.Entry<String, List<Music>> entry : entries) {
                DirAdapter.ItemData item = new DirAdapter.ItemData();
                item.setPath(entry.getKey());
                item.setMusics(entry.getValue());
                res.add(item);
            }

            this.activity.runOnUiThread(() -> {
                DirAdapter adapter = new DirAdapter(res, this.activity);
                this.listView.setAdapter(adapter);

                adapter.setOnOptionClickListener((CommonDialogItem item) -> {
                    AlbumAdapter.ItemData itemData = JsonUtils.json2Bean(item.getData(), AlbumAdapter.ItemData.class);
                    List<Music> musics = itemData.getMusics();
                    switch (item.getId()) {
                        case CommonDialogFragment.TAG_PLAY_NEXT:
                            super.activity.nextPlay(musics);
                            Toast.makeText(this.activity, this.activity.getString(R.string.added_to_next_play), Toast.LENGTH_SHORT).show();
                            break;
                        default:
                    }
                    LogUtils.e(item);
                });
            });
        }).start();
    }


    private DirPagerFragment() {
    }

    public static DirPagerFragment of() {
        return new DirPagerFragment();
    }
}
