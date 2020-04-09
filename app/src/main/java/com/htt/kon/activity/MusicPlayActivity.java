package com.htt.kon.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.htt.kon.App;
import com.htt.kon.R;
import com.htt.kon.bean.Music;
import com.htt.kon.broadcast.BaseReceiver;
import com.htt.kon.broadcast.PlayStateChangeReceiver;
import com.htt.kon.constant.FragmentTagConstant;
import com.htt.kon.dialog.PlayListDialog;
import com.htt.kon.service.MusicService;
import com.htt.kon.service.Playlist;
import com.htt.kon.util.CommonUtils;
import com.htt.kon.util.LogUtils;
import com.htt.kon.util.MMCQ;
import com.htt.kon.util.UiUtils;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 音乐播放页
 *
 * @author su
 * @date 2020/02/26 21:39
 */
public class MusicPlayActivity extends AppCompatActivity {

    private static final int UPDATE_PROGRESS = 1;

    private Handler handler = new Handler(msg -> {
        switch (msg.what) {
            case UPDATE_PROGRESS:
                updateSeekBar();
                break;
            default:
        }
        return true;
    });

    @BindView(R.id.amp_llRoot)
    LinearLayout linearLayout;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.amp_tvCurrPos)
    TextView tvCurrPos;

    @BindView(R.id.amp_tvDuration)
    TextView tvDuration;

    @BindView(R.id.amp_seekBar)
    SeekBar seekBar;

    @BindView(R.id.amp_ivPlayMode)
    ImageView ivPlayMode;

    @BindView(R.id.amp_ivPlay)
    ImageView ivPlay;

    private MusicService msService;

    private Playlist playlist;

    private Music curMusic;

    private PlayStateChangeReceiver receiver;

    private ServiceConnection conn;

    public static void start(Activity source) {
        source.startActivity(new Intent(source, MusicsActivity.class));
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_play);
        ButterKnife.bind(this);

        setSupportActionBar(this.toolbar);
        UiUtils.setStatusBarColor(this, R.color.grey5);
        this.init();
    }


    private void init() {
        this.toolbar.setNavigationOnClickListener(v -> {
            finish();
        });

        this.conn = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                msService = ((MusicService.MusicBinder) service).getMusicService();
                initData();
                ivPlay.setImageResource(msService.isPlaying() ? R.drawable.selector_mp_play : R.drawable.selector_mp_pause);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
            }
        };
        bindService(new Intent(this, MusicService.class), conn, Context.BIND_AUTO_CREATE);

        this.playlist = App.getPlaylist();

        // 监听播放广播
        this.receiver = PlayStateChangeReceiver.registerLocal(this, flag -> {
            switch (flag) {
                case PLAY:
                case PAUSE:
                    this.initData();
                    break;
                case PREPARED:
                    // 防止切换上下首歌时, 按钮图标闪变
                    this.ivPlay.setImageResource(this.msService.isPlaying() ? R.drawable.selector_mp_play : R.drawable.selector_mp_pause);
                    break;
                case CLEAR:
                    finish();
                    break;
                case REMOVE:
                    break;
                default:
            }
        });
    }

    private void initData() {
        this.curMusic = this.playlist.getCurMusic();
        this.toolbar.setTitle(this.curMusic.getTitle());
        this.tvDuration.setText(CommonUtils.formatTime(this.msService.getDuration() / 1000));
        this.seekBar.setMax(this.curMusic.getDuration());
        this.updateSeekBar();
        this.setBackground();
        this.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    msService.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    private void updateSeekBar() {
        this.seekBar.setProgress(this.msService.getCurrentPosition());
        this.tvCurrPos.setText(CommonUtils.formatTime(this.msService.getCurrentPosition() / 1000));
        handler.sendEmptyMessageDelayed(UPDATE_PROGRESS, 500);
    }

    private void setBackground() {
        int color = ContextCompat.getColor(this, R.color.grey5);
        // 从封面中提取出主色调
        // FIXME: 有bug
        if (StringUtils.isNotEmpty(this.curMusic.getImage())) {
            Bitmap bitmap = BitmapFactory.decodeFile(this.curMusic.getImage());
            try {
                List<int[]> result = MMCQ.compute(bitmap, 5);
                int[] dominantColor = result.get(0);
                color = Color.rgb(dominantColor[0], dominantColor[1], dominantColor[2]);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        getWindow().setStatusBarColor(color);
        this.toolbar.setBackgroundColor(color);
        this.linearLayout.setBackgroundColor(color);
    }


    /**
     * TODO
     */
    @OnClick({R.id.amp_ivLove, R.id.amp_ivDownload, R.id.amp_ivComment, R.id.amp_ivOption})
    public void clickMiddle(View view) {
        switch (view.getId()) {
            case R.id.amp_ivLove:
                break;
            case R.id.amp_ivDownload:
                break;
            case R.id.amp_ivComment:
                break;
            case R.id.amp_ivOption:
                break;
            default:
        }
    }

    @OnClick({R.id.amp_ivPlayMode, R.id.amp_ivPrev, R.id.amp_ivPlay, R.id.amp_ivNext, R.id.amp_ivPlaylist})
    public void clickBottom(View view) {
        switch (view.getId()) {
            // 切换播放模式
            case R.id.amp_ivPlayMode:
                this.msService.setMode();
                switch (playlist.getMode()) {
                    case Playlist.MODE_LOOP:
                        this.ivPlayMode.setImageResource(R.drawable.selector_mp_loop);
                        break;
                    case Playlist.MODE_RANDOM:
                        this.ivPlayMode.setImageResource(R.drawable.selector_mp_random);
                        break;
                    default:
                        this.ivPlayMode.setImageResource(R.drawable.selector_mp_single_loop);
                        break;
                }
                Toast.makeText(this, Playlist.getModeByValue(playlist.getMode(), this).getLabel(), Toast.LENGTH_SHORT).show();
                break;
            // 上一首
            case R.id.amp_ivPrev:
                this.msService.prev();
                break;
            // 播放 or 暂停
            case R.id.amp_ivPlay:
                this.msService.playOrPause();
                this.ivPlay.setImageResource(this.msService.isPlaying() ? R.drawable.selector_mp_play : R.drawable.selector_mp_pause);
                break;
            // 下一首
            case R.id.amp_ivNext:
                this.msService.next();
                break;
            // 播放列表
            case R.id.amp_ivPlaylist:
                PlayListDialog.of(this.msService).show(getSupportFragmentManager(), FragmentTagConstant.PLAYLIST_FRAGMENT);
                break;
            default:
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_music_play, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mmp_share:
                LogUtils.e(1);
                break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(this.conn);
        BaseReceiver.unregisterLocal(this, this.receiver);
    }
}
