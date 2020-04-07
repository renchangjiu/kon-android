package com.htt.kon.activity;

import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.htt.kon.R;
import com.htt.kon.adapter.list.MusicsCheckedAdapter;
import com.htt.kon.bean.Music;
import com.htt.kon.dialog.MusicListDialog;
import com.htt.kon.dialog.OptionDialog;
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
public class MusicsCheckActivity extends BaseActivity implements DataRequisiteActivity {

    private static List<Music> dataContainer;

    private Handler handler = new Handler((msg) -> {
        this.adapter.clearChecked();
        this.adapter.updateRes(this.musics);
        this.setStatusBarTitle();
        Toast.makeText(this, R.string.deleted, Toast.LENGTH_SHORT).show();
        if (this.musics.isEmpty()) {
            View footer = LayoutInflater.from(this).inflate(R.layout.list_footer_music_check, this.listView, false);
            this.listView.addFooterView(footer);
        }
        return true;
    });

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.amc_textView)
    TextView textView;

    @BindView(R.id.amc_listView)
    ListView listView;

    private MusicsCheckedAdapter adapter;

    private List<Music> musics;


    public static void start(Activity source, List<Music> musics) {
        Intent intent = new Intent(source, MusicsCheckActivity.class);
        dataContainer = musics;
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
        this.musics = dataContainer;
        dataContainer = null;
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
        List<Music> checked = this.adapter.getChecked();
        if (checked.isEmpty()) {
            Toast.makeText(this, getString(R.string.please_select_music), Toast.LENGTH_SHORT).show();
            return;
        }
        switch (view.getId()) {
            case R.id.amc_playNext:
                super.nextPlay(checked);
                super.hidePlayBar();
                break;
            case R.id.amc_add2music_list:
                MusicListDialog mlDialog = MusicListDialog.of(checked, checked.get(0).getTitle());
                mlDialog.show(super.getSupportFragmentManager(), "1");
                break;
            case R.id.amc_share:
                Toast.makeText(this, "敬请期待...", Toast.LENGTH_SHORT).show();
                break;
            case R.id.amc_delete:
                OptionDialog.ofDeleteMusic(this, checked, () -> {
                    this.musics.removeIf(checked::contains);
                    this.handler.sendEmptyMessage(0);
                    return null;
                });
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
