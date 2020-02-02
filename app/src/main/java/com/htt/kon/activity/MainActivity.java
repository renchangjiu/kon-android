package com.htt.kon.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.htt.kon.R;
import com.htt.kon.fragment.MusicFragment;
import com.htt.kon.fragment.DiscoverFragment;
import com.htt.kon.fragment.FriendsFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author su
 * @date 2020/01/31 21:20
 */
public class MainActivity extends AppCompatActivity {

    @BindView(R.id.am_drawerLayout)
    DrawerLayout drawerLayout;

    @BindView(R.id.am_toolbar)
    Toolbar toolbar;

    @BindView(R.id.am_viewPager)
    ViewPager viewPager;

    @BindView(R.id.am_imageViewMusic)
    ImageView imageViewMusic;

    @BindView(R.id.am_imageViewDiscover)
    ImageView imageViewDiscover;

    @BindView(R.id.am_imageViewFriends)
    ImageView imageViewFriends;

    private List<Fragment> fragments;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        this.toolbar.setTitle("");
        setSupportActionBar(this.toolbar);
        setStatusBarColor();

        this.initActivity();
    }

    private void initActivity() {
        this.imageViewMusic.setSelected(true);
        fragments = new ArrayList<>();
        fragments.add(new MusicFragment());
        fragments.add(new DiscoverFragment());
        fragments.add(new FriendsFragment());
        this.viewPager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager(), fragments));

        this.viewPager.addOnPageChangeListener(new MyPageChangeListener());
        this.toolbar.setNavigationOnClickListener(v -> {
            drawerLayout.openDrawer(GravityCompat.START);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @OnClick({R.id.am_imageViewMusic, R.id.am_imageViewDiscover, R.id.am_imageViewFriends})
    void click(View view) {
        switch (view.getId()) {
            case R.id.am_imageViewMusic:
                this.viewPager.setCurrentItem(0);
                break;
            case R.id.am_imageViewDiscover:
                this.viewPager.setCurrentItem(1);
                break;
            case R.id.am_imageViewFriends:
                this.viewPager.setCurrentItem(2);
                break;
            default:
        }
    }

    private class MyFragmentPagerAdapter extends FragmentPagerAdapter {
        private List<Fragment> fragments;

        public MyFragmentPagerAdapter(@NonNull FragmentManager fm, List<Fragment> fragments) {
            super(fm, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
            this.fragments = fragments;
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
    }


    class MyPageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            switch (position) {
                case 0:
                    imageViewMusic.setSelected(true);
                    imageViewDiscover.setSelected(false);
                    imageViewFriends.setSelected(false);
                    break;
                case 1:
                    imageViewMusic.setSelected(false);
                    imageViewDiscover.setSelected(true);
                    imageViewFriends.setSelected(false);
                    break;
                case 2:
                    imageViewMusic.setSelected(false);
                    imageViewDiscover.setSelected(false);
                    imageViewFriends.setSelected(true);
                    break;
                default:
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    /**
     * 设置状态栏颜色
     */
    private void setStatusBarColor() {
        Window window = getWindow();
        //取消设置透明状态栏,使 ContentView 内容不再覆盖状态栏
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //需要设置这个 flag 才能调用 setStatusBarColor 来设置状态栏颜色
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        //设置状态栏颜色
        int color = ContextCompat.getColor(this, R.color.colorPrimary);
        window.setStatusBarColor(color);
    }
}