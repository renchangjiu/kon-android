package com.htt.kon.activity;

import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.htt.kon.R;
import com.htt.kon.adapter.list.MusicsCheckedAdapter;
import com.htt.kon.bean.Music;
import com.htt.kon.util.JsonUtils;
import com.htt.kon.util.UiUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author su
 * @date 2020/03/17 19:59
 */
public class MusicsCheckedActivity extends BaseActivity implements DataRequisiteActivity {
    private static final String B_K_MUSICS = "B_K_MUSICS";

    @BindView(R.id.amc_toolbar)
    Toolbar toolbar;

    @BindView(R.id.amc_textView)
    TextView textView;

    @BindView(R.id.amc_listView)
    ListView listView;
    private MusicsCheckedAdapter adapter;
    private List<Music> musics;


    public static Bundle putData(List<Music> musics) {
        Bundle bd = new Bundle();
        bd.putString(B_K_MUSICS, JsonUtils.bean2Json(musics));
        return bd;
    }

    @Override
    protected void onStart() {
        super.onStart();
        super.hidePlayBar();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_musics_checked);
        ButterKnife.bind(this);
        setSupportActionBar(this.toolbar);
        UiUtils.setStatusBarColor(this);

        this.init();
    }

    private void init() {
        this.toolbar.setNavigationOnClickListener(v -> {
            finish();
        });
        Bundle bd = getIntent().getExtras();
        assert bd != null;
        this.musics = JsonUtils.json2List(bd.getString(B_K_MUSICS), Music.class);
        this.adapter = new MusicsCheckedAdapter(musics, this);
        this.listView.setAdapter(adapter);
        this.listView.setOnItemClickListener((parent, view, position, id) -> {
            this.adapter.checkedIf(position);
            this.setStatusBarTitle();
            this.whenCheck();
        });
        this.setStatusBarTitle();
    }

    @OnClick({R.id.amc_textView})
    public void click(View view) {
        this.adapter.checkedAll();
        this.setStatusBarTitle();
        this.whenCheck();
    }

    private void setStatusBarTitle() {
        String format = super.getString(R.string.item_selected_count);
        this.toolbar.setTitle(String.format(format, this.adapter.getCheckedCount()));
    }

    private void whenCheck() {
        if (this.adapter.getCheckedCount() == musics.size()) {
            this.textView.setText(getString(R.string.unselect_all));
        } else {
            this.textView.setText(getString(R.string.select_all));
        }
    }
}
