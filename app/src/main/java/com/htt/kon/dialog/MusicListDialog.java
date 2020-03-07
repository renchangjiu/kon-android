package com.htt.kon.dialog;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.htt.kon.R;
import com.htt.kon.activity.LocalMusicActivity;
import com.htt.kon.adapter.list.MusicListAdapter;
import com.htt.kon.adapter.list.dialog.MusicListDialogAdapter;
import com.htt.kon.service.database.MusicListDbService;

import org.apache.commons.io.filefilter.FalseFileFilter;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 歌单列表弹出框
 *
 * @author su
 * @date 2020/03/06 20:12
 */
public class MusicListDialog extends DialogFragment {

    private LocalMusicActivity activity;

    private MusicListDbService service;


    @BindView(R.id.dml_listView)
    ListView listView;

    private MusicListDialogAdapter adapter;


    private MusicListDialog() {
    }

    public static MusicListDialog of() {
        return new MusicListDialog();
    }

    @Override
    public void onStart() {
        super.onStart();
        // 设置dialog: 背景透明、宽度为屏宽的95%
        Window window = getDialog().getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = (int) (getContext().getResources().getDisplayMetrics().widthPixels * 0.95);
        params.height = (int) (getContext().getResources().getDisplayMetrics().heightPixels * 0.65);
        window.setAttributes(params);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.activity = (LocalMusicActivity) context;
        this.service = MusicListDbService.of(context);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_music_list, container, false);
        ButterKnife.bind(this, view);

        this.init();
        return view;
    }


    private void init() {
        this.adapter = new MusicListDialogAdapter(this.activity);
        View header = LayoutInflater.from(this.activity).inflate(R.layout.list_header_ml_dialog, this.listView, false);
        this.listView.addHeaderView(header);
        this.listView.setAdapter(adapter);
    }
}
