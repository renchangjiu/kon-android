package com.htt.kon.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.htt.kon.R;
import com.htt.kon.adapter.list.MusicsAdapter;
import com.htt.kon.bean.Music;
import com.htt.kon.bean.MusicList;
import com.htt.kon.service.database.MusicDbService;
import com.htt.kon.service.database.MusicListDbService;
import com.htt.kon.util.LogUtils;
import com.htt.kon.util.UiUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * 歌单详情页面
 *
 * @author su
 * @date 2020/03/21 21:04
 */
public class MusicListActivity extends BaseActivity implements DataRequisiteActivity {
    private final int WHAT_INIT_MUSIC_LIST = 1;
    private final int WHAT_INIT_MUSICS = 2;

    private Handler handler = new Handler((msg) -> {
        switch (msg.what) {
            case WHAT_INIT_MUSIC_LIST:
                this.tvMusicListName.setText(this.musicList.getName());
                break;
            case WHAT_INIT_MUSICS:
                String format = super.getString(R.string.local_music_count);
                this.tvCount.setText(String.format(format, this.musics.size()));
                if (!this.musics.isEmpty()) {
                    // 歌单封面暂时使用歌曲的封面
                    this.ivCover.setImageBitmap(BitmapFactory.decodeFile(this.musics.get(0).getImage()));
                }
                break;
            default:
        }
        return true;
    });

    private static final String B_K_MUSIC_LIST_ID = "B_K_MUSIC_LIST_ID";

    @BindView(R.id.aml_toolbar)
    Toolbar toolbar;

    @BindView(R.id.aml_listView)
    ListView listView;
    private MusicsAdapter adapter;

    private View headerView;

    /**
     * 歌单信息
     */
    private MusicList musicList;

    /**
     * 歌单下歌曲集合
     */
    private List<Music> musics;

    private TextView tvMusicListName;

    private TextView tvCount;
    private ImageView ivCover;

    /**
     * @param musicListId 歌单id
     */
    public static void start(Activity source, long musicListId) {
        Intent intent = new Intent(source, MusicListActivity.class);
        intent.putExtra(B_K_MUSIC_LIST_ID, musicListId);
        source.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_list);
        ButterKnife.bind(this);

        setSupportActionBar(this.toolbar);
        UiUtils.setStatusBarColor(this, R.color.grey5);

        this.init();
    }

    private void init() {
        long mlId = getIntent().getLongExtra(B_K_MUSIC_LIST_ID, -1);
        this.toolbar.setNavigationOnClickListener(v -> {
            finish();
        });

        // 初始化界面
        this.headerView = LayoutInflater.from(this).inflate(R.layout.list_header_ml, this.listView, false);
        this.initHeaderView();
        this.listView.addHeaderView(headerView);
        this.adapter = new MusicsAdapter(this, mlId);
        this.listView.setAdapter(this.adapter);

        // 初始化数据
        MusicDbService musicDbService = MusicDbService.of(this);
        MusicListDbService musicListDbService = MusicListDbService.of(this);
        musicListDbService.getById(mlId, musicList -> {
            Message msg = Message.obtain();
            msg.what = WHAT_INIT_MUSIC_LIST;
            handler.sendMessage(msg);
            this.musicList = musicList;
        });
        musicDbService.list(mlId, musics -> {
            Message msg = Message.obtain();
            msg.what = WHAT_INIT_MUSICS;
            handler.sendMessage(msg);
            this.musics = musics;
        });
    }

    private void initHeaderView() {
        ivCover = headerView.findViewById(R.id.lhm_ivCover);
        tvMusicListName = headerView.findViewById(R.id.lhm_tvMusicListName);
        TextView tvCollect = headerView.findViewById(R.id.lhm_tvCollect);
        TextView tvComment = headerView.findViewById(R.id.lhm_tvComment);
        TextView tvShare = headerView.findViewById(R.id.lhm_tvShare);
        TextView tvDownload = headerView.findViewById(R.id.lhm_tvDownload);
        tvCount = headerView.findViewById(R.id.lhm_tvCount);
        LinearLayout llPlayAll = headerView.findViewById(R.id.lhm_llPlayAll);
        TextView tvMultipleChoice = headerView.findViewById(R.id.lhm_tvMultipleChoice);

        // 播放全部
        llPlayAll.setOnClickListener(v -> {
            LogUtils.e(v);
            //TODO
            // super.replacePlaylist(this.musics, 0);
            // this.adapter.notifyDataSetChanged();
        });

        tvMultipleChoice.setOnClickListener(v -> {
            MusicsCheckedActivity.start(this, this.adapter.getRes());
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mml_search:
                LogUtils.e(1);
                break;
            case R.id.mml_auto_download:
                LogUtils.e(2);
                break;
            case R.id.mml_edit:
                LogUtils.e(3);
                break;
            case R.id.mml_sort:
                LogUtils.e(4);
                break;
            case R.id.mml_delete:
                LogUtils.e(5);
                break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_music_list, menu);
        return true;
    }

    /**
     * 使菜单同时显示图标和文字
     */
    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        UiUtils.showMenuIconAndTitleTogether(menu);
        return super.onMenuOpened(featureId, menu);
    }
}
