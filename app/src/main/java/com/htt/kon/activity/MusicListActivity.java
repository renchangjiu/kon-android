package com.htt.kon.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.htt.kon.R;
import com.htt.kon.adapter.list.MusicsAdapter;
import com.htt.kon.bean.Music;
import com.htt.kon.bean.MusicList;
import com.htt.kon.broadcast.BaseReceiver;
import com.htt.kon.broadcast.PlayStateChangeReceiver;
import com.htt.kon.service.database.MusicDbService;
import com.htt.kon.service.database.MusicListDbService;
import com.htt.kon.util.LogUtils;
import com.htt.kon.util.MMCQ;
import com.htt.kon.util.UiUtils;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

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
                this.toolbar.setTitle(this.musicList.getName());
                break;
            case WHAT_INIT_MUSICS:
                String format = super.getString(R.string.local_music_count);
                this.tvCount.setText(String.format(format, this.musics.size()));
                if (!this.musics.isEmpty()) {
                    // 歌单封面暂时使用歌曲的封面
                    Bitmap bitmap;
                    if (StringUtils.isNotEmpty(this.musics.get(0).getImage())) {
                        bitmap = BitmapFactory.decodeFile(this.musics.get(0).getImage());
                    } else {
                        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.list_item_ml_def);
                    }
                    this.ivCover.setImageBitmap(bitmap);
                    try {
                        // 从封面中提取出主色调
                        List<int[]> result = MMCQ.compute(bitmap, 5);
                        int[] dominantColor = result.get(0);
                        int rgb = Color.rgb(dominantColor[0], dominantColor[1], dominantColor[2]);
                        this.relativeLayout.setBackgroundColor(rgb);
                        this.toolbar.setBackgroundColor(rgb);
                        getWindow().setStatusBarColor(rgb);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
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
    private RelativeLayout relativeLayout;

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
        UiUtils.setStatusBarColor(this, R.color.transparent);

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
        this.initListView();

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

        // 监听播放广播
        PlayStateChangeReceiver receiver = new PlayStateChangeReceiver();
        BaseReceiver.registerLocal(this, receiver, PlayStateChangeReceiver.ACTION);
        receiver.setOnReceiveListener(flag -> {
            switch (flag) {
                case PLAY:
                case CLEAR:
                case REMOVE:
                    this.adapter.notifyDataSetChanged();
                    break;
                default:
            }
        });
    }

    private void initHeaderView() {
        ivCover = headerView.findViewById(R.id.lhm_ivCover);
        tvMusicListName = headerView.findViewById(R.id.lhm_tvMusicListName);
        relativeLayout = headerView.findViewById(R.id.lhm_relativeLayout);
        TextView tvCollect = headerView.findViewById(R.id.lhm_tvCollect);
        TextView tvComment = headerView.findViewById(R.id.lhm_tvComment);
        TextView tvShare = headerView.findViewById(R.id.lhm_tvShare);
        TextView tvDownload = headerView.findViewById(R.id.lhm_tvDownload);
        tvCount = headerView.findViewById(R.id.lhm_tvCount);
        LinearLayout llPlayAll = headerView.findViewById(R.id.lhm_llPlayAll);
        TextView tvMultipleChoice = headerView.findViewById(R.id.lhm_tvMultipleChoice);

        // 播放全部
        llPlayAll.setOnClickListener(v -> {
            super.replacePlaylist(this.musics, 0);
            this.adapter.notifyDataSetChanged();
        });

        tvMultipleChoice.setOnClickListener(v -> {
            MusicsCheckedActivity.start(this, this.adapter.getRes());
        });

        tvCollect.setOnClickListener(v -> {
        });
    }

    private void initListView() {
        this.listView.setOnItemClickListener((parent, view, position, id) -> {
            super.replacePlaylist(this.musics, position - 1);
            this.adapter.notifyDataSetChanged();
        });

        this.listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (musicList == null) {
                    return;
                }
                if (firstVisibleItem == 0) {
                    toolbar.setTitle(R.string.music_list);
                } else {
                    toolbar.setTitle(musicList.getName());
                }
            }
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
