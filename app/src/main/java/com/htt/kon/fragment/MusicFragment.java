package com.htt.kon.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.htt.kon.App;
import com.htt.kon.R;
import com.htt.kon.activity.LocalMusicActivity;
import com.htt.kon.activity.MainActivity;
import com.htt.kon.activity.MusicListActivity;
import com.htt.kon.adapter.list.LocalManagerAdapter;
import com.htt.kon.adapter.list.MusicListAdapter;
import com.htt.kon.bean.CommonDialogItem;
import com.htt.kon.bean.Music;
import com.htt.kon.bean.MusicList;

import com.htt.kon.dialog.CommonDialog;
import com.htt.kon.dialog.MusicListDialog;
import com.htt.kon.dialog.OptionDialog;
import com.htt.kon.service.database.MusicDbService;
import com.htt.kon.service.database.MusicListDbService;
import com.htt.kon.util.IdWorker;
import com.htt.kon.util.JsonUtils;
import com.htt.kon.view.ListViewSeparateLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * MainActivity 下的MusicFragment
 *
 * @author su
 * @date 2020/02/01 19:46
 */
public class MusicFragment extends Fragment {

    private Handler handler = new Handler(msg -> {
        this.adapter.updateRes(this.musicLists);
        String format = this.activity.getString(R.string.created_music_list);
        this.separateLayout.setText(String.format(format, this.musicLists.size() - 1));
        return true;
    });

    private MainActivity activity;

    private MusicListDbService musicListDbService;

    @BindView(R.id.fm_swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.fm_listViewMusicList)
    ListView listViewMusicList;

    private MusicListAdapter adapter;

    private ListViewSeparateLayout separateLayout;

    private ListView headerListView;

    private List<MusicList> musicLists;
    private LocalManagerAdapter localManagerAdapter;

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

    @Override
    public void onStart() {
        super.onStart();
        this.initData();
    }

    private void init() {
        this.musicListDbService = MusicListDbService.of(this.activity);
        View header = LayoutInflater.from(this.activity).inflate(R.layout.list_header_music_fragment, this.listViewMusicList, false);
        this.headerListView = header.findViewById(R.id.lhmf_listView);
        this.separateLayout = header.findViewById(R.id.lhmf_separateLayout);

        this.listViewMusicList.addHeaderView(header);
        this.localManagerAdapter = new LocalManagerAdapter(this.activity);
        this.headerListView.setAdapter(localManagerAdapter);
        this.adapter = new MusicListAdapter(this.activity);
        this.listViewMusicList.setAdapter(this.adapter);

        this.initListView();
        this.initHeaderView();
        this.initSeparateLayout();
        this.initSwipeRefreshLayout();
    }

    private void initListView() {
        // 解决 ListView 与 SwipeRefreshLayout 滑动冲突的问题
        this.listViewMusicList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                swipeRefreshLayout.setEnabled(firstVisibleItem == 0);
            }
        });

        this.listViewMusicList.setOnItemClickListener((parent, view, position, id) -> {
            MusicListActivity.start(this.activity, id);
        });

        this.adapter.setOnOptionClickListener(item -> {
            MusicList ml = JsonUtils.json2Bean(item.getData(), MusicList.class);
            switch (item.getId()) {
                case CommonDialog.TAG_DOWNLOAD:
                    Toast.makeText(this.activity, "敬请期待......", Toast.LENGTH_SHORT).show();
                    break;
                case CommonDialog.TAG_SHARE:
                    Toast.makeText(this.activity, "敬请期待......", Toast.LENGTH_SHORT).show();
                    break;
                case CommonDialog.TAG_EDIT_ML:
                    Toast.makeText(this.activity, "敬请期待......", Toast.LENGTH_SHORT).show();
                    break;
                case CommonDialog.TAG_DELETE:
                    OptionDialog.of(this.activity)
                            .setTitle(R.string.tip_delete_ml)
                            .setPositiveButton((child) -> {
                                this.musicListDbService.logicDelete(ml.getId(), v -> {
                                    this.initData();
                                    this.activity.runOnUiThread(() -> {
                                        Toast.makeText(this.activity, R.string.deleted, Toast.LENGTH_SHORT).show();
                                    });
                                });
                            })
                            .setNegativeButton(child -> {
                            })
                            .show();
                    break;
                default:
            }
        });
    }

    private void initHeaderView() {
        this.headerListView.setOnItemClickListener((parent, view, position, id) -> {
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
    }

    private void initSeparateLayout() {
        // 分隔布局的点击事件
        this.separateLayout.setOnClickListener(new ListViewSeparateLayout.OnClickListener() {
            @Override
            public void onCommonClick(String ad) {
                // 收起或展开歌单列表
                if (ad.equals(ListViewSeparateLayout.ARROW_DIRECTION_DOWN)) {
                    adapter.updateRes(musicLists);
                } else {
                    adapter.clearRes();
                }
            }

            @Override
            public void onSettingImageClick(View v) {
                List<CommonDialogItem> items = new ArrayList<>();

                items.add(CommonDialog.getItem(CommonDialog.TAG_MUSIC_LIST_CREATE, getString(R.string.create_new_music_list)));
                items.add(CommonDialog.getItem(CommonDialog.TAG_MUSIC_LIST_MANAGE, getString(R.string.music_list_manage)));
                items.add(CommonDialog.getItem(CommonDialog.TAG_MUSIC_LIST_RESTORE, getString(R.string.music_list_restore)));

                CommonDialog fragment = CommonDialog.of("创建的歌单", items);
                fragment.show(activity.getSupportFragmentManager(), "1");
                fragment.setOnClickListener(item -> {
                    switch (item.getId()) {
                        // 创建新歌单
                        case CommonDialog.TAG_MUSIC_LIST_CREATE:
                            OptionDialog.ofCreateMusicList(activity, null, name -> {
                                MusicList ml = new MusicList();
                                ml.setId(IdWorker.singleNextId());
                                ml.setName(name);
                                musicListDbService.insert(ml, vv -> {
                                    initData();
                                });
                                return null;
                            });
                            break;
                        case CommonDialog.TAG_MUSIC_LIST_MANAGE:
                            Toast.makeText(activity, "敬请期待", Toast.LENGTH_SHORT).show();
                            break;
                        case CommonDialog.TAG_MUSIC_LIST_RESTORE:
                            Toast.makeText(activity, "敬请期待", Toast.LENGTH_SHORT).show();
                            break;
                        default:
                    }
                });
            }
        });
    }

    private void initSwipeRefreshLayout() {
        this.swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        this.swipeRefreshLayout.setOnRefreshListener(() -> {
            // 耗时操作放入子线程
            App.getPoolExecutor().execute(() -> {
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
            });
        });
    }

    private void initData() {
        this.localManagerAdapter.updateRes();
        this.musicListDbService.list(musicLists -> {
            this.musicLists = musicLists;
            this.handler.sendEmptyMessage(0);
        });
    }

}
