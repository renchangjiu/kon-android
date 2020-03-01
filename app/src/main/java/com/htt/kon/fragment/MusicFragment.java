package com.htt.kon.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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
import com.htt.kon.bean.CommonDialogItem;
import com.htt.kon.bean.MusicList;
import com.htt.kon.constant.MidConstant;
import com.htt.kon.dialog.CommonDialogFragment;
import com.htt.kon.dialog.OptionDialog;
import com.htt.kon.service.database.Callback;
import com.htt.kon.service.database.MusicDbService;
import com.htt.kon.service.database.MusicListDbService;
import com.htt.kon.util.IdWorker;
import com.htt.kon.util.LogUtils;
import com.htt.kon.util.TextWatcherWrapper;
import com.htt.kon.util.requests.Requests;
import com.htt.kon.view.ListViewSeparateLayout;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Response;

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
        this.musicListDbService = MusicListDbService.of(this.activity);

        this.updateInterface();

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
                List<CommonDialogItem> items = new ArrayList<>();

                items.add(CommonDialogFragment.FULL_ITEMS.get(CommonDialogFragment.TAG_MUSIC_LIST_CREATE).setName(getString(R.string.create_new_music_list)));
                items.add(CommonDialogFragment.FULL_ITEMS.get(CommonDialogFragment.TAG_MUSIC_LIST_MANAGE).setName(getString(R.string.music_list_manage)));
                items.add(CommonDialogFragment.FULL_ITEMS.get(CommonDialogFragment.TAG_MUSIC_LIST_RESTORE).setName(getString(R.string.music_list_restore)));

                CommonDialogFragment fragment = CommonDialogFragment.of("创建的歌单", items);
                fragment.show(activity.getSupportFragmentManager(), "1");
                fragment.setOnClickListener(item -> {
                    switch (item.getId()) {
                        case CommonDialogFragment.TAG_MUSIC_LIST_CREATE:
                            caseCreateMusicList();
                            break;
                        case CommonDialogFragment.TAG_MUSIC_LIST_MANAGE:
                            Toast.makeText(activity, "敬请期待", Toast.LENGTH_SHORT).show();
                            break;
                        case CommonDialogFragment.TAG_MUSIC_LIST_RESTORE:
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


    private void caseCreateMusicList() {
        OptionDialog of = OptionDialog.of(activity)
                .setChild(LayoutInflater.from(activity).inflate(R.layout.dialog_child_create_music_list, null))
                .setTitle(getString(R.string.create_music_list))
                .disabled(DialogInterface.BUTTON_POSITIVE)
                .setPositiveButton(getString(R.string.submit), (child) -> {
                    EditText et = child.findViewById(R.id.dccml_editText);
                    String name = et.getText().toString();

                    MusicList ml = new MusicList();
                    ml.setId(IdWorker.singleNextId());
                    ml.setName(name);
                    ml.setCreateTime(System.currentTimeMillis());
                    ml.setPlayCount(0);
                    ml.setDelFlag(2);
                    musicListDbService.insert(ml, v -> {
                        // 刷新页面
                        updateInterface();

                    });
                })
                .setNegativeButton(child -> {
                })
                .end();
        EditText et = of.getChild().findViewById(R.id.dccml_editText);
        et.addTextChangedListener(new TextWatcherWrapper() {
            @Override
            public void afterTextChanged(Editable s) {
                String str = s.toString();
                if (StringUtils.isNotEmpty(str)) {
                    of.enabled(DialogInterface.BUTTON_POSITIVE);
                } else {
                    of.disabled(DialogInterface.BUTTON_POSITIVE);
                }
            }
        });
        of.show();
    }

    private void updateInterface() {
        this.musicListDbService.list(v -> {
            int count = v.size() - 1;
            this.activity.runOnUiThread(() -> {
                String format = this.activity.getString(R.string.created_music_list);
                this.listViewSeparateLayout.setText(String.format(format, count));
            });
        });
    }

}
