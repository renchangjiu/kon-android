package com.htt.kon.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.htt.kon.R;
import com.htt.kon.bean.Music;
import com.htt.kon.util.JsonUtils;

import java.util.List;

/**
 * @author su
 * @date 2020/03/17 19:59
 */
public class MusicsCheckedActivity extends BaseActivity implements DataRequisiteActivity {
    private static final String B_K_MUSICS = "B_K_MUSICS";

    public static Bundle putData(List<Music> musics) {
        Bundle bd = new Bundle();
        bd.putString(B_K_MUSICS, JsonUtils.bean2Json(musics));
        return bd;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_musics_checked);
        super.hidePlayBar();
    }
}
