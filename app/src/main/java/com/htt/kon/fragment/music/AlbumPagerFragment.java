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
import com.htt.kon.bean.CommonDialogItem;
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
 * 本地音乐activity 下的专辑tab 页
 *
 * @author su
 * @date 2020/02/14 21:44
 */
public class AlbumPagerFragment extends BaseLocalMusicPagerFragment {


    private AlbumAdapter adapter;

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
        this.adapter = new AlbumAdapter(this.activity);
        this.listView.setAdapter(adapter);

        this.adapter.setOnOptionClickListener((CommonDialogItem item) -> {
            AlbumAdapter.ItemData itemData = JsonUtils.json2Bean(item.getData(), AlbumAdapter.ItemData.class);
            List<Music> musics = itemData.getMusics();
            switch (item.getId()) {
                case CommonDialog.TAG_PLAY_NEXT:
                    super.activity.nextPlay(musics);
                    Toast.makeText(this.activity, this.activity.getString(R.string.added_to_next_play), Toast.LENGTH_SHORT).show();
                    break;
                case CommonDialog.TAG_COLLECT:
                    // 收藏到歌单
                    MusicListDialog mlDialog = MusicListDialog.of(musics, itemData.getAlbum());
                    mlDialog.show(activity.getSupportFragmentManager(), "1");
                    break;
                default:
            }
        });

        this.listView.setOnItemClickListener((parent, view, position, id) -> {
            AlbumAdapter.ItemData item = this.adapter.getItem(position);
            Intent intent = new Intent(this.activity, MusicsActivity.class);
            intent.putExtras(MusicsActivity.putData(item.getAlbum(), item.getMusics()));
            startActivity(intent);
        });
    }


    private AlbumPagerFragment() {
    }

    public static AlbumPagerFragment of() {
        return new AlbumPagerFragment();
    }
}
