package com.htt.kon.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.text.HtmlCompat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.htt.kon.App;
import com.htt.kon.R;
import com.htt.kon.bean.Music;
import com.htt.kon.constant.CommonConstant;
import com.htt.kon.service.database.MusicDbService;
import com.htt.kon.util.MusicFileSearcher;
import com.htt.kon.util.UiUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * @author su
 * @date 2020/02/10 21:06
 */
public class ScanMusicFinishActivity extends AppCompatActivity {

    private MusicDbService service;

    private static final int HANDLER_WHAT_FINISH = 1;

    private static final int HANDLER_WHAT_PROCESS = 2;

    private Handler handler = new Handler((msg) -> {
        if (msg.what == HANDLER_WHAT_FINISH) {
            // 扫描结束
            int count = (int) msg.obj;
            String source = String.format(getString(R.string.scan_result_count), count);
            this.textViewResult.setText(HtmlCompat.fromHtml(source, HtmlCompat.FROM_HTML_MODE_LEGACY));
            this.textViewCancelScan.setVisibility(View.GONE);
            this.textViewBack.setVisibility(View.VISIBLE);
            this.textViewLyric.setVisibility(View.VISIBLE);
            this.imageViewSearcher.setImageResource(R.drawable.scan_music_ok);
        } else if (msg.what == HANDLER_WHAT_PROCESS) {
            // 扫描进度
            String path = (String) msg.obj;
            this.textViewResult.setText(path);
        }
        return true;
    });

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.asmf_imageViewSearcher)
    ImageView imageViewSearcher;

    @BindView(R.id.asmf_textViewResult)
    TextView textViewResult;

    @BindView(R.id.asmf_textViewCancelScan)
    TextView textViewCancelScan;

    @BindView(R.id.asmf_textViewBack)
    TextView textViewBack;

    @BindView(R.id.asmf_textViewLyric)
    TextView textViewLyric;

    private Task task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_music_finish);


        ButterKnife.bind(this);
        setSupportActionBar(this.toolbar);
        UiUtils.setStatusBarColor(this);
        this.init();

        this.initTask();
    }

    private void initTask() {
        this.task = new Task();
        App.getPoolExecutor().execute(task);
    }

    private void init() {
        this.service = MusicDbService.of(this);
        this.toolbar.setNavigationOnClickListener(v -> {
            finish();
        });

        this.textViewCancelScan.setOnClickListener(v -> {
            this.task.stopFlag = true;
            startActivity(new Intent(this, ScanMusicActivity.class));
            Toast.makeText(this, "已停止", Toast.LENGTH_SHORT).show();
        });
        this.textViewBack.setOnClickListener(v -> {
            startActivity(new Intent(this, LocalMusicActivity.class));
            finish();
        });

        this.textViewLyric.setOnClickListener(v -> {
            Toast.makeText(this, "敬请期待...", Toast.LENGTH_SHORT).show();
        });

        this.initAnim();
    }

    /**
     * TODO: 动画效果
     */
    private void initAnim() {
    }


    private class Task implements Runnable {

        private volatile boolean stopFlag = false;

        @Override
        public void run() {
            // int count = 5;
            // for (int i = 0; i < count; i++) {
            //     LogUtils.e(System.currentTimeMillis());
            //     ThreadUtil.sleep(1000);
            // }

            List<String> paths = MusicFileSearcher.search(ScanMusicFinishActivity.this);
            List<Music> oldMusics = service.list(CommonConstant.MID_LOCAL_MUSIC);
            List<String> newPaths = new ArrayList<>();
            for (String path : paths) {
                if (!this.contains(path, oldMusics)) {
                    newPaths.add(path);
                }
            }
            for (String path : newPaths) {
                if (stopFlag) {
                    break;
                }
                if (service.insert(path, CommonConstant.MID_LOCAL_MUSIC)) {
                    this.sendProcessMsg(path);
                }
            }
            if (!stopFlag) {
                Message msg = Message.obtain();
                msg.what = HANDLER_WHAT_FINISH;
                msg.obj = paths.size();
                handler.sendMessage(msg);
            }
        }

        private void sendProcessMsg(String path) {
            Message msg = Message.obtain();
            msg.what = HANDLER_WHAT_PROCESS;
            msg.obj = path;
            handler.sendMessage(msg);
        }


        private boolean contains(String path, List<Music> container) {
            for (Music music : container) {
                if (path.equals(music.getPath())) {
                    return true;
                }
            }
            return false;
        }
    }

}
