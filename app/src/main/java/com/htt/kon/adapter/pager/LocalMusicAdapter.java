package com.htt.kon.adapter.pager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.htt.kon.fragment.music.BaseLocalMusicPagerFragment;

import java.util.List;

/**
 * @author su
 * @date 2020/02/03 20:57
 */
public class LocalMusicAdapter extends FragmentPagerAdapter {

    private List<BaseLocalMusicPagerFragment> fragments;
    private List<String> titles;

    public LocalMusicAdapter(@NonNull FragmentManager fm, List<BaseLocalMusicPagerFragment> fragments, List<String> titles) {
        super(fm, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.fragments = fragments;
        this.titles = titles;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return this.fragments.get(position);
    }

    @Override
    public int getCount() {
        return this.fragments.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return this.titles.get(position);
    }
}
