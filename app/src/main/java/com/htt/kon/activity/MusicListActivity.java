package com.htt.kon.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

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
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.htt.kon.R;
import com.htt.kon.adapter.list.MusicsAdapter;
import com.htt.kon.adapter.list.music.ItemData;
import com.htt.kon.bean.Music;
import com.htt.kon.bean.MusicList;
import com.htt.kon.broadcast.BaseReceiver;
import com.htt.kon.broadcast.PlayStateChangeReceiver;
import com.htt.kon.dialog.CommonDialog;
import com.htt.kon.dialog.MusicListDialog;
import com.htt.kon.dialog.OptionDialog;
import com.htt.kon.service.database.MusicDbService;
import com.htt.kon.service.database.MusicListDbService;
import com.htt.kon.util.JsonUtils;
import com.htt.kon.util.LogUtils;
import com.htt.kon.util.MMCQ;
import com.htt.kon.util.UiUtils;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
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
    private static final String B_K_MUSIC_LIST_ID = "B_K_MUSIC_LIST_ID";

    private final int WHAT_INIT_MUSIC_LIST = 1;
    private final int WHAT_INIT_MUSICS = 2;

    private Handler handler = new Handler((msg) -> {
        switch (msg.what) {
            case WHAT_INIT_MUSIC_LIST:
                this.tvMusicListName.setText(this.musicList.getName());
                this.toolbar.setTitle(this.musicList.getName());
                break;
            case WHAT_INIT_MUSICS:
                this.adapter.updateRes(this.musics);
                if (this.musics.isEmpty()) {
                    this.llPlayAll.setVisibility(View.GONE);
                    View footerView = LayoutInflater.from(this).inflate(R.layout.list_footer_ml, this.listView, false);
                    this.listView.addFooterView(footerView);
                    break;
                }
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


    @BindView(R.id.toolbar)
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
    private LinearLayout llPlayAll;
    private long mlId;
    private MusicListDbService musicListDbService;
    private MusicDbService musicDbService;

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
        UiUtils.setStatusBarColor(this, R.color.ml_bg_def);

        this.init();
    }

    private void init() {
        this.mlId = getIntent().getLongExtra(B_K_MUSIC_LIST_ID, -1);
        this.toolbar.setNavigationOnClickListener(v -> {
            finish();
        });

        // 初始化界面
        this.headerView = LayoutInflater.from(this).inflate(R.layout.list_header_ml, this.listView, false);
        this.initHeaderView();
        this.listView.addHeaderView(headerView);
        this.adapter = new MusicsAdapter(this);
        this.listView.setAdapter(this.adapter);
        this.initListView();

        musicDbService = MusicDbService.of(this);
        musicListDbService = MusicListDbService.of(this);
        this.initData();

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

    private void initData() {
        // 初始化数据
        musicListDbService.getById(mlId, musicList -> {
            Message msg = Message.obtain();
            msg.what = WHAT_INIT_MUSIC_LIST;
            this.musicList = musicList;
            handler.sendMessage(msg);
        });
        musicDbService.list(mlId, musics -> {
            Message msg = Message.obtain();
            msg.what = WHAT_INIT_MUSICS;
            this.musics = musics;
            handler.sendMessage(msg);
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
        llPlayAll = headerView.findViewById(R.id.lhm_llPlayAll);
        TextView tvMultipleChoice = headerView.findViewById(R.id.lhm_tvMultipleChoice);

        // 播放全部
        llPlayAll.setOnClickListener(v -> {
            super.replacePlaylist(this.musics, 0);
            this.adapter.notifyDataSetChanged();
        });

        tvMultipleChoice.setOnClickListener(v -> {
            MusicsCheckActivity.start(this, this.adapter.getRes());
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

        this.adapter.setOnOptionClickListener(item -> {
            Music music = JsonUtils.json2Bean(item.getData(), Music.class);
            switch (item.getId()) {
                // 下一首播放
                case CommonDialog.TAG_PLAY_NEXT:
                    super.nextPlay(music);
                    Toast.makeText(this, getString(R.string.added_to_next_play), Toast.LENGTH_SHORT).show();
                    break;
                // 收藏到歌单
                case CommonDialog.TAG_COLLECT:
                    MusicListDialog.of(music, music.getTitle()).show(getSupportFragmentManager(), "1");
                    break;
                // 删除
                case CommonDialog.TAG_DELETE:
                    OptionDialog.ofDeleteMusic(this, music, () -> {
                        this.initData();
                        this.runOnUiThread(() -> {
                            Toast.makeText(this, R.string.deleted, Toast.LENGTH_SHORT).show();
                        });
                        return null;
                    });
                    break;
                default:
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
