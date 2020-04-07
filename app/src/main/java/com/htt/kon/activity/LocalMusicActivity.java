package com.htt.kon.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.tabs.TabLayout;
import com.htt.kon.R;
import com.htt.kon.adapter.pager.LocalMusicAdapter;
import com.htt.kon.fragment.music.AlbumPagerFragment;
import com.htt.kon.fragment.music.ArtistPagerFragment;
import com.htt.kon.fragment.music.BaseLocalMusicPagerFragment;
import com.htt.kon.fragment.music.DirPagerFragment;
import com.htt.kon.fragment.music.SinglePagerFragment;
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
public class LocalMusicActivity extends BaseActivity {

    @BindView(R.id.toolbar)
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
        List<BaseLocalMusicPagerFragment> fragments = new ArrayList<>();
        fragments.add(SinglePagerFragment.of());
        fragments.add(ArtistPagerFragment.of());
        fragments.add(AlbumPagerFragment.of());
        fragments.add(DirPagerFragment.of());
        List<String> titles = new ArrayList<>();
        titles.add("单曲");
        titles.add("歌手");
        titles.add("专辑");
        titles.add("文件夹");
        this.viewPager.setAdapter(new LocalMusicAdapter(getSupportFragmentManager(), fragments, titles));
        this.tabLayout.setupWithViewPager(this.viewPager);
        this.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                fragments.get(position).initData();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
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
                startActivity(new Intent(this, ScanMusicActivity.class));
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtils.e();
    }
}
