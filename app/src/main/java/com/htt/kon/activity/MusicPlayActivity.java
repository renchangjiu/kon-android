package com.htt.kon.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.htt.kon.App;
import com.htt.kon.R;
import com.htt.kon.bean.Music;
import com.htt.kon.broadcast.BaseReceiver;
import com.htt.kon.broadcast.PlayStateChangeReceiver;
import com.htt.kon.constant.FragmentTagConstant;
import com.htt.kon.dialog.PlayListDialog;
import com.htt.kon.service.MusicService;
import com.htt.kon.service.Playlist;
import com.htt.kon.util.LogUtils;
import com.htt.kon.util.UiUtils;

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

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.amp_tvCurrPos)
    TextView tvCurrPos;

    @BindView(R.id.amp_tvDuration)
    TextView tvDuration;

    @BindView(R.id.amp_seekBar)
    SeekBar seekBar;

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
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
            }
        };
        bindService(new Intent(this, MusicService.class), conn, Context.BIND_AUTO_CREATE);

        this.playlist = App.getPlaylist();

        // 监听播放广播
        this.receiver = PlayStateChangeReceiver.registerLocal(this, flag -> this.initData());
    }

    private void initData() {
        this.curMusic = this.playlist.getCurMusic();
        this.toolbar.setTitle(this.curMusic.getTitle());
        this.seekBar.setMax(this.curMusic.getDuration());
        this.updateSeekBar();
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

    public void updateSeekBar() {
        this.seekBar.setProgress(this.msService.getCurrentPosition());
        handler.sendEmptyMessageDelayed(UPDATE_PROGRESS, 500);
    }

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
            case R.id.amp_ivPlayMode:
                break;
            case R.id.amp_ivPrev:
                this.msService.prev();
                break;
            case R.id.amp_ivPlay:
                this.msService.playOrPause();
                break;
            case R.id.amp_ivNext:
                this.msService.next();
                break;
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
