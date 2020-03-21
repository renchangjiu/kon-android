package com.htt.kon.activity;

import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.htt.kon.R;
import com.htt.kon.adapter.list.MusicsCheckedAdapter;
import com.htt.kon.bean.Music;
import com.htt.kon.dialog.MusicListDialog;
import com.htt.kon.util.JsonUtils;
import com.htt.kon.util.UiUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author su
 * @date 2020/03/17 19:59
 */
public class MusicsCheckedActivity extends BaseActivity implements DataRequisiteActivity {
    private static final String B_K_MUSICS = "B_K_MUSICS";

    @BindView(R.id.amc_toolbar)
    Toolbar toolbar;

    @BindView(R.id.amc_textView)
    TextView textView;

    @BindView(R.id.amc_listView)
    ListView listView;
    private MusicsCheckedAdapter adapter;
    private List<Music> musics;


    public static void start(Activity source, List<Music> musics) {
        Intent intent = new Intent(source, MusicsCheckedActivity.class);
        Bundle bd = new Bundle();
        bd.putString(B_K_MUSICS, JsonUtils.bean2Json(musics));
        intent.putExtras(bd);
        source.startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        super.hidePlayBar();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_musics_checked);
        ButterKnife.bind(this);
        setSupportActionBar(this.toolbar);
        UiUtils.setStatusBarColor(this);
        this.init();
    }

    private void init() {
        this.toolbar.setNavigationOnClickListener(v -> {
            finish();
        });
        Bundle bd = getIntent().getExtras();
        assert bd != null;
        this.musics = JsonUtils.json2List(bd.getString(B_K_MUSICS), Music.class);
        this.adapter = new MusicsCheckedAdapter(musics, this);
        this.listView.setAdapter(adapter);
        this.listView.setOnItemClickListener((parent, view, position, id) -> {
            this.adapter.checkedIf(position);
            this.setStatusBarTitle();
            this.whenCheck();
        });
        this.setStatusBarTitle();
    }

    /**
     * 全选 & 取消全选
     */
    @OnClick({R.id.amc_textView})
    public void click1(View view) {
        this.adapter.checkedAll();
        this.setStatusBarTitle();
        this.whenCheck();
    }

    /**
     * 下一首播放 & 加入歌单 & 上传到云盘 & 删除
     */
    @OnClick({R.id.amc_playNext, R.id.amc_add2music_list, R.id.amc_share, R.id.amc_delete})
    public void click2(View view) {
        List<Music> musics = this.adapter.getChecked();
        if (musics.isEmpty()) {
            Toast.makeText(this, getString(R.string.please_select_music), Toast.LENGTH_SHORT).show();
            return;
        }
        switch (view.getId()) {
            case R.id.amc_playNext:
                super.nextPlay(musics);
                super.hidePlayBar();
                break;
            case R.id.amc_add2music_list:
                MusicListDialog mlDialog = MusicListDialog.of(musics, musics.get(0).getTitle());
                mlDialog.show(super.getSupportFragmentManager(), "1");
                break;
            case R.id.amc_share:
                Toast.makeText(this, "敬请期待...", Toast.LENGTH_SHORT).show();
                break;
            case R.id.amc_delete:
                // TODO: 删除
                break;
            default:
        }
    }


    private void setStatusBarTitle() {
        String format = super.getString(R.string.item_selected_count);
        this.toolbar.setTitle(String.format(format, this.adapter.getCheckedCount()));
    }

    private void whenCheck() {
        if (this.adapter.getCheckedCount() == musics.size()) {
            this.textView.setText(getString(R.string.unselect_all));
        } else {
            this.textView.setText(getString(R.string.select_all));
        }
    }
}
