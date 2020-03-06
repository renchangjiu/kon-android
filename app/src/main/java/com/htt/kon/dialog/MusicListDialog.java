package com.htt.kon.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.htt.kon.App;
import com.htt.kon.R;
import com.htt.kon.service.database.MusicListDbService;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 歌单列表弹出框
 *
 * @author su
 * @date 2020/03/06 20:12
 */
public class MusicListDialog extends DialogFragment {

    private Context context;
    private MusicListDbService service;


    @BindView(R.id.dml_listView)
    ListView listView;


    private MusicListDialog() {
    }

    public static MusicListDialog of() {
        MusicListDialog of = new MusicListDialog();

        return of;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
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

    }
}
