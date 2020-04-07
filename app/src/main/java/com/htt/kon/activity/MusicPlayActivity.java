package com.htt.kon.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.htt.kon.R;
import com.htt.kon.bean.Music;
import com.htt.kon.broadcast.BaseReceiver;
import com.htt.kon.broadcast.PlayStateChangeReceiver;
import com.htt.kon.constant.FragmentTagConstant;
import com.htt.kon.dialog.PlayListDialog;
import com.htt.kon.service.MusicService;
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

    private Music curMusic;

    public static void start(Activity source) {
        source.startActivity(new Intent(source, MusicsActivity.class));
    }

    @Override
    protected void onStart() {
        super.onStart();
        this.initData();
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
        // MusicService msService = super.msService;
        this.toolbar.setNavigationOnClickListener(v -> {
            finish();
        });
        // 监听播放广播
        PlayStateChangeReceiver receiver = new PlayStateChangeReceiver();
        BaseReceiver.registerLocal(this, receiver, PlayStateChangeReceiver.ACTION);
        receiver.setOnReceiveListener(flag -> {
            switch (flag) {
                case PLAY:
                case CLEAR:
                case REMOVE:
                    this.initData();
                    break;
                default:
            }
        });
        // this.curMusic = super.playlist.getCurMusic();
        this.initData();
    }

    private void initData() {
        this.toolbar.setTitle(this.curMusic.getTitle());
        this.seekBar.setMax(this.curMusic.getDuration());
        // if (super.msService != null) {
        //     this.updateSeekBar();
        // }
        // this.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
        //     @Override
        //     public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        //         if (fromUser) {
        //             msService.seekTo(progress);
        //         }
        //     }
        //
        //     @Override
        //     public void onStartTrackingTouch(SeekBar seekBar) {
        //     }
        //
        //     @Override
        //     public void onStopTrackingTouch(SeekBar seekBar) {
        //     }
        // });
    }

    public void updateSeekBar() {
        // this.seekBar.setProgress(super.msService.getCurrentPosition());
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
                break;
            case R.id.amp_ivPlay:
                break;
            case R.id.amp_ivNext:
                break;
            case R.id.amp_ivPlaylist:
                // PlayListDialog.of()
                //         .setOnClickListener(new BaseActivity.PlayListDialogFragmentOnClickListener())
                //         .show(getSupportFragmentManager(), FragmentTagConstant.PLAYLIST_FRAGMENT);
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
}
