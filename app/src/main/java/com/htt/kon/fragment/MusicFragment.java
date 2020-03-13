package com.htt.kon.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.EditText;
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
import com.htt.kon.adapter.list.LocalManagerAdapter;
import com.htt.kon.adapter.list.MusicListAdapter;
import com.htt.kon.bean.CommonDialogItem;
import com.htt.kon.bean.MusicList;

import com.htt.kon.dialog.CommonDialog;
import com.htt.kon.dialog.OptionDialog;
import com.htt.kon.service.database.MusicListDbService;
import com.htt.kon.util.IdWorker;
import com.htt.kon.util.TextWatcherWrapper;
import com.htt.kon.view.ListViewSeparateLayout;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

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

    private MusicListDbService musicListDbService;

    @BindView(R.id.fm_swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.fm_listViewMusicList)
    ListView listViewMusicList;

    private MusicListAdapter adapter;

    private ListViewSeparateLayout separateLayout;

    private LocalManagerAdapter localManagerAdapter;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.activity = (MainActivity) context;
    }

    @Override
    public void onStart() {
        super.onStart();
        this.localManagerAdapter.updateRes();
        this.adapter.updateRes();
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
        this.musicListDbService = MusicListDbService.of(this.activity);

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
        View header = LayoutInflater.from(this.activity).inflate(R.layout.list_header_music_fragment, this.listViewMusicList, false);
        ListView headerListView = header.findViewById(R.id.lhmf_listView);
        this.separateLayout = header.findViewById(R.id.lhmf_separateLayout);

        this.listViewMusicList.addHeaderView(header);

        this.localManagerAdapter = new LocalManagerAdapter(this.activity);
        this.adapter = new MusicListAdapter(this.activity);
        headerListView.setAdapter(this.localManagerAdapter);
        this.listViewMusicList.setAdapter(this.adapter);

        headerListView.setOnItemClickListener((parent, view, position, id) -> {
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
        this.separateLayout.setOnClickListener(new ListViewSeparateLayout.OnClickListener() {
            @Override
            public void onCommonClick(String ad) {
                // 收起或展开歌单列表
                if (ad.equals(ListViewSeparateLayout.ARROW_DIRECTION_DOWN)) {
                    adapter.updateRes();
                } else {
                    adapter.clearRes();
                }
            }

            @Override
            public void onSettingImageClick(View v) {
                List<CommonDialogItem> items = new ArrayList<>();

                items.add(CommonDialog.FULL_ITEMS.get(CommonDialog.TAG_MUSIC_LIST_CREATE).setName(getString(R.string.create_new_music_list)));
                items.add(CommonDialog.FULL_ITEMS.get(CommonDialog.TAG_MUSIC_LIST_MANAGE).setName(getString(R.string.music_list_manage)));
                items.add(CommonDialog.FULL_ITEMS.get(CommonDialog.TAG_MUSIC_LIST_RESTORE).setName(getString(R.string.music_list_restore)));

                CommonDialog fragment = CommonDialog.of("创建的歌单", items);
                fragment.show(activity.getSupportFragmentManager(), "1");
                fragment.setOnClickListener(item -> {
                    switch (item.getId()) {
                        case CommonDialog.TAG_MUSIC_LIST_CREATE:
                            OptionDialog.ofCreateMusicList(activity, null, name -> {
                                MusicList ml = new MusicList();
                                ml.setId(IdWorker.singleNextId());
                                ml.setName(name);
                                musicListDbService.insert(ml, vv -> {
                                    // 刷新页面
                                    updateInterface();
                                    adapter.updateRes();
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
        this.updateInterface();
    }


    // private void caseCreateMusicList() {
    //     OptionDialog of = OptionDialog.of(activity)
    //             .setChild(LayoutInflater.from(activity).inflate(R.layout.dialog_child_create_music_list, null))
    //             .setTitle(getString(R.string.create_music_list))
    //             .disabled(DialogInterface.BUTTON_POSITIVE)
    //             .setPositiveButton(getString(R.string.submit), (child) -> {
    //                 EditText et = child.findViewById(R.id.dccml_editText);
    //                 String name = et.getText().toString();
    //
    //                 MusicList ml = new MusicList();
    //                 ml.setId(IdWorker.singleNextId());
    //                 ml.setName(name);
    //                 musicListDbService.insert(ml, v -> {
    //                     // 刷新页面
    //                     updateInterface();
    //                     this.adapter.updateRes();
    //                 });
    //             })
    //             .setNegativeButton(child -> {
    //             })
    //             .end();
    //     EditText et = of.getChild().findViewById(R.id.dccml_editText);
    //
    //     et.addTextChangedListener(new TextWatcherWrapper() {
    //         @Override
    //         public void afterTextChanged(Editable s) {
    //             String str = s.toString();
    //             if (StringUtils.isNotEmpty(str)) {
    //                 of.enabled(DialogInterface.BUTTON_POSITIVE);
    //             } else {
    //                 of.disabled(DialogInterface.BUTTON_POSITIVE);
    //             }
    //         }
    //     });
    //     of.show();
    // }

    private void updateInterface() {
        this.musicListDbService.list(v -> {
            int count = v.size() - 1;
            this.activity.runOnUiThread(() -> {
                String format = this.activity.getString(R.string.created_music_list);
                this.separateLayout.setText(String.format(format, count));
            });
        });
    }
}
