package com.htt.kon.fragment.music;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.htt.kon.App;
import com.htt.kon.R;
import com.htt.kon.activity.LocalMusicActivity;
import com.htt.kon.adapter.list.LocalManagerAdapter;
import com.htt.kon.adapter.list.LocalMusicArtistAdapter;
import com.htt.kon.adapter.list.LocalMusicSingleAdapter;
import com.htt.kon.bean.Music;
import com.htt.kon.broadcast.MusicPlayStateBroadcastReceiver;
import com.htt.kon.constant.MidConstant;
import com.htt.kon.dialog.CommonDialogFragment;
import com.htt.kon.service.Playlist;
import com.htt.kon.service.database.MusicDbService;
import com.htt.kon.util.LogUtils;
import com.htt.kon.util.UiUtils;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 本地音乐activity 下的歌手tab 页
 *
 * @author su
 * @date 2020/02/14 21:44
 */
public class LocalMusicArtistPagerFragment extends Fragment {

    @BindView(R.id.flma_listView)
    ListView listView;

    private MusicDbService musicDbService;

    private LocalMusicActivity activity;

    private MusicPlayStateBroadcastReceiver receiver;

    private Playlist playlist;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.activity = (LocalMusicActivity) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_local_music_artist, container, false);
        ButterKnife.bind(this, view);

        this.musicDbService = MusicDbService.of(this.activity);
        this.init();

        this.receiver = MusicPlayStateBroadcastReceiver.register(this.activity);
        this.receiver.setOnReceiveBroadcastListener(v -> {
            switch (v) {
                case MusicPlayStateBroadcastReceiver.FLAG_PLAY:
                case MusicPlayStateBroadcastReceiver.FLAG_CLEAR:
                    UiUtils.getListViewAdapter(this.listView, LocalMusicArtistAdapter.class).notifyDataSetChanged();
                    break;
                default:
            }
        });
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MusicPlayStateBroadcastReceiver.unregister(this.activity, this.receiver);
    }

    private void init() {
        this.playlist = App.getApp().getPlaylist();
        this.initListView();
    }

    private void initListView() {
        new Thread(() -> {
            List<Music> list = this.musicDbService.list(MidConstant.MID_LOCAL_MUSIC);
            // 按歌手分类
            Map<String, List<Music>> map = new HashMap<>();
            for (Music music : list) {
                String artist = music.getArtist();
                // 若姓名以 / 分隔, 则视为两人
                String[] split = artist.split("/");
                for (String s : split) {
                    if (StringUtils.isEmpty(s)) {
                        continue;
                    }
                    if (map.containsKey(s)) {
                        map.get(s).add(music);
                    } else {
                        List<Music> r = new LinkedList<>();
                        r.add(music);
                        map.put(s, r);
                    }
                }
            }
            // 封装参数
            List<LocalMusicArtistAdapter.ItemData> res = new ArrayList<>();
            Set<Map.Entry<String, List<Music>>> entries = map.entrySet();
            for (Map.Entry<String, List<Music>> entry : entries) {
                LocalMusicArtistAdapter.ItemData item = new LocalMusicArtistAdapter.ItemData();
                item.setArtist(entry.getKey());
                item.setMusics(entry.getValue());
                res.add(item);
            }

            this.activity.runOnUiThread(() -> {
                LocalMusicArtistAdapter adapter = new LocalMusicArtistAdapter(res, this.activity);
                this.listView.setAdapter(adapter);

                adapter.setOnOptionClickListener(item -> {
                    List<Music> musics = (List<Music>) item.getData();
                    switch (item.getId()) {
                        case CommonDialogFragment.TAG_PLAY_NEXT:
                            this.activity.nextPlay(musics);
                            Toast.makeText(this.activity, this.activity.getString(R.string.added_to_next_play), Toast.LENGTH_SHORT).show();
                            break;
                        default:
                    }
                    LogUtils.e(item);
                });
            });
        }).start();
    }


    private LocalMusicArtistPagerFragment() {
    }

    public static LocalMusicArtistPagerFragment of() {
        return new LocalMusicArtistPagerFragment();
    }
}
