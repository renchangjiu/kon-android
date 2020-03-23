package com.htt.kon.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.htt.kon.R;
import com.htt.kon.util.LogUtils;
import com.htt.kon.util.UiUtils;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * 歌单详情页面
 *
 * @author su
 * @date 2020/03/21 21:04
 */
public class MusicListActivity extends BaseActivity implements DataRequisiteActivity {

    private static final String B_K_MUSIC_LIST_ID = "B_K_MUSIC_LIST_ID";


    @BindView(R.id.aml_toolbar)
    Toolbar toolbar;

    @BindView(R.id.aml_listView)
    ListView listView;

    /**
     * @param musicListId 歌单id
     */
    public static void start(Activity source, long musicListId) {
        Intent intent = new Intent(source, MusicListActivity.class);
        intent.putExtra(B_K_MUSIC_LIST_ID, musicListId);
        source.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_list);
        ButterKnife.bind(this);

        setSupportActionBar(this.toolbar);
        UiUtils.setStatusBarColor(this);

        this.init();
    }

    private void init() {
        this.toolbar.setNavigationOnClickListener(v -> {
            finish();
        });
        View headerView = LayoutInflater.from(this).inflate(R.layout.list_header_ml, this.listView, false);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mml_search:
                LogUtils.e(1);
                break;
            case R.id.mml_auto_download:
                LogUtils.e(2);
                break;
            case R.id.mml_edit:
                LogUtils.e(3);
                break;
            case R.id.mml_sort:
                LogUtils.e(4);
                break;
            case R.id.mml_delete:
                LogUtils.e(5);
                break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_music_list, menu);
        return true;
    }

    /**
     * 使菜单同时显示图标和文字
     */
    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        UiUtils.showMenuIconAndTitleTogether(menu);
        return super.onMenuOpened(featureId, menu);
    }
}
