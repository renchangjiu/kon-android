package com.htt.kon.activity;

import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.htt.kon.R;
import com.htt.kon.adapter.list.music.SingleAdapter;
import com.htt.kon.bean.Music;
import com.htt.kon.broadcast.BaseReceiver;
import com.htt.kon.broadcast.PlayStateChangeReceiver;
import com.htt.kon.util.JsonUtils;
import com.htt.kon.util.LogUtils;
import com.htt.kon.util.UiUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author su
 * @date 2020/03/14 12:31
 */
public class MusicsActivity extends BaseActivity implements DataRequisiteActivity {

    private static final String B_K_TITLE = "B_K_TITLE";
    private static final String B_K_MUSICS = "B_K_MUSICS";

    @BindView(R.id.ams_toolbar)
    Toolbar toolbar;

    @BindView(R.id.ams_listView)
    ListView listView;

    private List<Music> musics;

    private SingleAdapter adapter;

    public static Bundle putData(String title, List<Music> musics) {
        Bundle bd = new Bundle();
        bd.putString(B_K_TITLE, title);
        bd.putString(B_K_MUSICS, JsonUtils.bean2Json(musics));
        return bd;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_musics);
        ButterKnife.bind(this);
        setSupportActionBar(this.toolbar);
        UiUtils.setStatusBarColor(this);

        Bundle bd = getIntent().getExtras();
        assert bd != null;

        this.toolbar.setTitle(bd.getString(B_K_TITLE));
        this.musics = JsonUtils.json2List(bd.getString(B_K_MUSICS), Music.class);
        this.init();
    }

    private void init() {
        this.toolbar.setNavigationOnClickListener(v -> {
            finish();
        });
        this.adapter = new SingleAdapter(this.musics, this);
        this.listView.setAdapter(adapter);
        View headerView = LayoutInflater.from(this).inflate(R.layout.list_header_single, this.listView, false);
        TextView textViewCount = headerView.findViewById(R.id.lhs_textViewCount);
        TextView textViewMultipleChoice = headerView.findViewById(R.id.lhs_textViewMultipleChoice);
        String format = super.getString(R.string.local_music_count);
        textViewCount.setText(String.format(format, musics.size()));

        textViewMultipleChoice.setOnClickListener(v -> {
            Intent intent = new Intent(this, MusicsCheckedActivity.class);
            intent.putExtras(MusicsCheckedActivity.putData(this.adapter.getRes()));
            startActivity(intent);
        });
        this.listView.addHeaderView(headerView);

        this.listView.setOnItemClickListener((parent, view, position, id) -> {
            setPlaylist(position - 1);
        });

        // 播放全部
        headerView.setOnClickListener(v -> {
            setPlaylist(0);
        });

        // 监听播放广播
        PlayStateChangeReceiver receiver = new PlayStateChangeReceiver();
        BaseReceiver.registerLocal(this, receiver, PlayStateChangeReceiver.ACTION);
        receiver.setOnReceiveListener(flag -> {
            switch (flag) {
                case PLAY:
                case CLEAR:
                case REMOVE:
                    this.adapter.notifyDataSetChanged();
                    break;
                default:
            }
        });
    }

    /**
     * 更新播放列表
     *
     * @param index 在新的播放列表中的index
     */
    private void setPlaylist(int index) {
        super.replacePlaylist(this.musics, index);
        this.adapter.notifyDataSetChanged();
    }

}
