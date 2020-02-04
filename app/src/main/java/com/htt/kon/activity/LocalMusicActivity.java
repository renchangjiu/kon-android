package com.htt.kon.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.tabs.TabLayout;
import com.htt.kon.R;
import com.htt.kon.adapter.pager.LocalMusicAdapter;
import com.htt.kon.fragment.LocalMusicPagerFragment;
import com.htt.kon.util.LogUtils;
import com.htt.kon.util.UiUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author su
 * @date 2020/02/03 17:50
 */
public class LocalMusicActivity extends PlayBarActivity {

    @BindView(R.id.alm_toolbar)
    Toolbar toolbar;

    @BindView(R.id.alm_tabLayout)
    TabLayout tabLayout;

    @BindView(R.id.alm_viewPager)
    ViewPager viewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_music);
        ButterKnife.bind(this);

        setSupportActionBar(this.toolbar);
        UiUtils.setStatusBarColor(this);
        this.init();
    }

    private void init() {
        this.toolbar.setNavigationOnClickListener(v -> {
            finish();
        });
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(LocalMusicPagerFragment.getInstance(LocalMusicPagerFragment.FLAG_SINGLE));
        fragments.add(LocalMusicPagerFragment.getInstance(LocalMusicPagerFragment.FLAG_ARTIST));
        fragments.add(LocalMusicPagerFragment.getInstance(LocalMusicPagerFragment.FLAG_ALBUM));
        fragments.add(LocalMusicPagerFragment.getInstance(LocalMusicPagerFragment.FLAG_DIR));
        List<String> titles = new ArrayList<>();
        titles.add("单曲");
        titles.add("歌手");
        titles.add("专辑");
        titles.add("文件夹");
        this.viewPager.setAdapter(new LocalMusicAdapter(getSupportFragmentManager(), fragments, titles));
        this.tabLayout.setupWithViewPager(this.viewPager);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_local_music, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mlm_menu_search:
                LogUtils.e(1);
                break;
            case R.id.mlm_scan_local_music:
                LogUtils.e(2);
                break;
            case R.id.mlm_select_sort_way:
                LogUtils.e(3);
                break;
            case R.id.mlm_get_cover_lyrics:
                LogUtils.e(4);
                break;
            case R.id.mlm_improve_tone_quality:
                LogUtils.e(5);
                break;
            default:
        }
        return super.onOptionsItemSelected(item);
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