package com.htt.kon.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.htt.kon.R;
import com.htt.kon.activity.LocalMusicActivity;
import com.htt.kon.activity.MainActivity;
import com.htt.kon.adapter.list.LocalManagerAdapter;
import com.htt.kon.service.database.MusicDbService;
import com.htt.kon.view.ListViewSeparateLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * MainActivity 下的MusicFragment
 *
 * @author su
 * @date 2020/02/01 19:46
 */
public class MusicFragment extends Fragment {

    private MainActivity activity;

    private MusicDbService musicDbService;


    @BindView(R.id.fm_swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;


    @BindView(R.id.fm_listViewManager)
    ListView listView;

    @BindView(R.id.fm_listViewSeparateLayout)
    ListViewSeparateLayout listViewSeparateLayout;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.activity = (MainActivity) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_music, container, false);
        ButterKnife.bind(this, view);

        this.init();
        return view;
    }

    private void init() {
        this.musicDbService = MusicDbService.of(this.activity);
        this.listView.setAdapter(new LocalManagerAdapter(this.activity));
        this.listView.setOnItemClickListener((parent, view, position, id) -> {
            switch (position) {
                case 0:
                    startActivity(new Intent(activity, LocalMusicActivity.class));
                    break;
                case 1:
                    Toast.makeText(activity, "敬请期待...", Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    Toast.makeText(activity, "敬请期待...", Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    Toast.makeText(activity, "敬请期待...", Toast.LENGTH_SHORT).show();
                    break;
                case 4:
                    Toast.makeText(activity, "敬请期待...", Toast.LENGTH_SHORT).show();
                    break;
                default:
            }
        });

        // 分隔布局的点击事件
        this.listViewSeparateLayout.setOnClickListener(new ListViewSeparateLayout.OnClickListener() {
            @Override
            public void onCommonClick(View v) {
            }

            @Override
            public void onSettingImageClick(View v) {
            }
        });

        this.swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        this.swipeRefreshLayout.setOnRefreshListener(() -> {
            // 耗时操作放入子线程
            new Thread(() -> {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                this.activity.runOnUiThread(() -> {
                    Toast.makeText(activity, "刷新结束", Toast.LENGTH_SHORT).show();
                });
                // 结束刷新事件
                this.swipeRefreshLayout.setRefreshing(false);
            }).start();
        });
    }
}
