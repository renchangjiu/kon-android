package com.htt.kon.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

import com.htt.kon.R;
import com.htt.kon.adapter.list.music.SingleAdapter;
import com.htt.kon.bean.Music;
import com.htt.kon.util.JsonUtils;
import com.htt.kon.util.UiUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author su
 * @date 2020/03/14 12:31
 */
public class MusicsActivity extends AppCompatActivity implements DataRequisiteActivity {

    private static final String B_K_TITLE = "B_K_TITLE";
    private static final String B_K_MUSICS = "B_K_MUSICS";

    @BindView(R.id.ams_toolbar)
    Toolbar toolbar;

    @BindView(R.id.ams_listView)
    ListView listView;

    private List<Music> musics;

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

        Bundle bd = getIntent().getExtras();
        assert bd != null;
        setSupportActionBar(this.toolbar);
        this.toolbar.setTitle(bd.getString(B_K_TITLE));
        UiUtils.setStatusBarColor(this);

        this.musics = JsonUtils.json2List(bd.getString(B_K_MUSICS), Music.class);
        this.init();
    }

    private void init() {
        this.toolbar.setNavigationOnClickListener(v -> {
            finish();
        });

        View view = LayoutInflater.from(this).inflate(R.layout.fragment_local_music_single, this.listView, false);
        SingleAdapter adapter = new SingleAdapter(this.musics, this);
        this.listView.setAdapter(adapter);
    }
}
