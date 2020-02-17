package com.htt.kon.fragment.music;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.htt.kon.R;
import com.htt.kon.adapter.list.LocalMusicAlbumAdapter;
import com.htt.kon.bean.CommonDialogItem;
import com.htt.kon.bean.Music;
import com.htt.kon.broadcast.MusicPlayStateBroadcastReceiver;
import com.htt.kon.constant.MidConstant;
import com.htt.kon.dialog.CommonDialogFragment;
import com.htt.kon.util.LogUtils;
import com.htt.kon.util.UiUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 本地音乐activity 下的专辑tab 页
 *
 * @author su
 * @date 2020/02/14 21:44
 */
public class AlbumPagerFragment extends BaseLocalMusicPagerFragment {


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
            List<Music> list = super.musicDbService.list(MidConstant.MID_LOCAL_MUSIC);
            // 按歌手分类
            Map<String, List<Music>> map = super.musicDbService.listGroupByAlbum(list);

            // 封装参数
            List<LocalMusicAlbumAdapter.ItemData> res = new ArrayList<>();
            Set<Map.Entry<String, List<Music>>> entries = map.entrySet();
            for (Map.Entry<String, List<Music>> entry : entries) {
                LocalMusicAlbumAdapter.ItemData item = new LocalMusicAlbumAdapter.ItemData();
                item.setArtist(entry.getValue().get(0).getArtist());
                item.setAlbum(entry.getKey());
                item.setMusics(entry.getValue());
                res.add(item);
            }

            this.activity.runOnUiThread(() -> {
                LocalMusicAlbumAdapter adapter = new LocalMusicAlbumAdapter(res, this.activity);
                this.listView.setAdapter(adapter);

                adapter.setOnOptionClickListener((CommonDialogItem item) -> {
                    List<Music> musics = (List<Music>) item.getData();
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


    private AlbumPagerFragment() {
    }

    public static AlbumPagerFragment of() {
        return new AlbumPagerFragment();
    }
}
