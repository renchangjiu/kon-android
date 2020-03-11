package com.htt.kon.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.htt.kon.R;
import com.htt.kon.activity.LocalMusicActivity;
import com.htt.kon.adapter.list.dialog.MusicListDialogAdapter;
import com.htt.kon.bean.Music;
import com.htt.kon.bean.MusicList;
import com.htt.kon.service.database.MusicDbService;
import com.htt.kon.service.database.MusicListDbService;
import com.htt.kon.util.IdWorker;
import com.htt.kon.util.JsonUtils;
import com.htt.kon.util.LogUtils;
import com.htt.kon.util.TextWatcherWrapper;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

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

    private MusicDbService musicDbService;

    private MusicListDbService musicListDbService;

    @BindView(R.id.dml_listView)
    ListView listView;

    private static final String B_K_DEF_ML_NAME = "defaultMusicListName";
    private static final String B_K_MUSIC = "MUSIC";
    private static final String B_K_MUSICS = "MUSICS";

    private MusicListDialog() {
    }

    /**
     * of
     *
     * @param defMusicListName 若选择"新建歌单"选项, 则其为新建歌单的默认名称
     */
    public static MusicListDialog of(@NonNull Music music, String defMusicListName) {
        MusicListDialog of = new MusicListDialog();
        Bundle bd = new Bundle();
        bd.putString(B_K_MUSIC, JsonUtils.bean2Json(music));
        bd.putString(B_K_DEF_ML_NAME, defMusicListName);
        of.setArguments(bd);
        return of;
    }

    /**
     * of
     *
     * @param defMusicListName 若选择"新建歌单"选项, 则其为新建歌单的默认名称
     */
    public static MusicListDialog of(@NonNull List<Music> musics, String defMusicListName) {
        MusicListDialog of = new MusicListDialog();
        Bundle bd = new Bundle();
        bd.putString(B_K_MUSICS, JsonUtils.bean2Json(musics));
        bd.putString(B_K_DEF_ML_NAME, defMusicListName);
        of.setArguments(bd);
        return of;
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
        this.musicDbService = MusicDbService.of(context);
        this.musicListDbService = MusicListDbService.of(context);
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
        MusicListDialogAdapter adapter = new MusicListDialogAdapter(this.activity);
        View header = LayoutInflater.from(this.activity).inflate(R.layout.list_header_ml_dialog, this.listView, false);
        this.listView.addHeaderView(header);
        this.listView.setAdapter(adapter);

        this.listView.setOnItemClickListener((parent, view, position, id) -> {
            List<Music> musics = this.getMusics();
            if (id == -1) {
                OptionDialog.ofCreateMusicList(activity, getArguments().getString(B_K_DEF_ML_NAME), name -> {
                    MusicList ml = new MusicList();
                    ml.setId(IdWorker.singleNextId());
                    ml.setName(name);
                    musicListDbService.insert(ml, v -> {
                        for (Music music : musics) {
                            music.setId(null);
                            music.setMid(v.getId());
                        }
                        this.musicDbService.insert(musics, null, null);
                        this.dismiss();
                        activity.runOnUiThread(() -> {
                            Toast.makeText(this.activity, this.activity.getString(R.string.collected_to_ml), Toast.LENGTH_SHORT).show();
                        });
                    });
                    return null;
                });
            } else {
                for (Music music : musics) {
                    music.setId(null);
                    music.setMid(id);
                }
                this.musicDbService.insert(musics, id, null);
                this.dismiss();
                Toast.makeText(this.activity, this.activity.getString(R.string.collected_to_ml), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 从参数中获取歌曲列表
     */
    private List<Music> getMusics() {
        List<Music> musics = new ArrayList<>();
        Bundle bd = getArguments();
        assert bd != null;
        String string = bd.getString(B_K_MUSIC);
        if (StringUtils.isNotEmpty(string)) {
            musics.add(JsonUtils.json2Bean(string, Music.class));
        } else {
            String strings = bd.getString(B_K_MUSICS);
            musics = JsonUtils.json2List(strings, Music.class);
        }
        return musics;
    }

}
