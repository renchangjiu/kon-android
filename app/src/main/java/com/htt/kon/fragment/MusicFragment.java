package com.htt.kon.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.htt.kon.dialog.CommonDialogFragment;
import com.htt.kon.dialog.OptionDialog;
import com.htt.kon.service.database.MusicDbService;
import com.htt.kon.util.LogUtils;
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
                List<CommonDialogItem> items = new ArrayList<>();
                items.add(new CommonDialogItem(CommonDialogFragment.TAG_MUSIC_LIST_CREATE, "创建新歌单", R.drawable.common_dialog_play_next, null));
                items.add(new CommonDialogItem(CommonDialogFragment.TAG_MUSIC_LIST_MANAGE, "歌单管理", R.drawable.common_dialog_play_next, null));
                items.add(new CommonDialogItem(CommonDialogFragment.TAG_MUSIC_LIST_RESTORE, "恢复歌单", R.drawable.common_dialog_play_next, null));
                CommonDialogFragment fragment = CommonDialogFragment.of("创建的歌单", items);
                fragment.show(activity.getSupportFragmentManager(), "1");
                fragment.setOnClickListener(item -> {
                    switch (item.getId()) {
                        case CommonDialogFragment.TAG_MUSIC_LIST_CREATE:
                            OptionDialog of = OptionDialog.of(activity)
                                    .setChild(LayoutInflater.from(activity).inflate(R.layout.dialog_child_create_music_list, null))
                                    .setTitle(getString(R.string.create_music_list))
                                    .disabled(DialogInterface.BUTTON_POSITIVE)
                                    .setPositiveButton(getString(R.string.submit), (child) -> {
                                        EditText et = child.findViewById(R.id.dccml_editText);
                                        String s = et.getText().toString();
                                        LogUtils.e();
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
                            break;
                        case CommonDialogFragment.TAG_MUSIC_LIST_MANAGE:
                            break;
                        case CommonDialogFragment.TAG_MUSIC_LIST_RESTORE:
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
}
