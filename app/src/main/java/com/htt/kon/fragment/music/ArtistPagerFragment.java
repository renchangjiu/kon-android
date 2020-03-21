package com.htt.kon.fragment.music;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.htt.kon.App;
import com.htt.kon.R;
import com.htt.kon.activity.MusicsActivity;
import com.htt.kon.adapter.list.music.AlbumAdapter;
import com.htt.kon.adapter.list.music.ArtistAdapter;
import com.htt.kon.bean.Music;

import com.htt.kon.constant.CommonConstant;
import com.htt.kon.dialog.CommonDialog;
import com.htt.kon.dialog.MusicListDialog;
import com.htt.kon.util.JsonUtils;
import com.htt.kon.util.LogUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 本地音乐activity 下的歌手tab 页
 *
 * @author su
 * @date 2020/02/14 21:44
 */
public class ArtistPagerFragment extends BaseLocalMusicPagerFragment {

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
        ArtistAdapter adapter = new ArtistAdapter(this.activity);
        this.listView.setAdapter(adapter);

        adapter.setOnOptionClickListener(item -> {
            ArtistAdapter.ItemData itemData = JsonUtils.json2Bean(item.getData(), ArtistAdapter.ItemData.class);
            List<Music> musics = itemData.getMusics();
            switch (item.getId()) {
                case CommonDialog.TAG_PLAY_NEXT:
                    super.activity.nextPlay(musics);
                    Toast.makeText(this.activity, this.activity.getString(R.string.added_to_next_play), Toast.LENGTH_SHORT).show();
                    break;
                case CommonDialog.TAG_COLLECT:
                    // 收藏到歌单
                    MusicListDialog mlDialog = MusicListDialog.of(musics, itemData.getArtist());
                    mlDialog.show(activity.getSupportFragmentManager(), "1");
                    break;
                default:
            }
            LogUtils.e(item);
        });

        this.listView.setOnItemClickListener((parent, view, position, id) -> {
            ArtistAdapter.ItemData item = adapter.getItem(position);
            MusicsActivity.start(this.activity, item.getArtist(), item.getMusics());
        });
    }


    private ArtistPagerFragment() {
    }

    public static ArtistPagerFragment of() {
        return new ArtistPagerFragment();
    }
}
