package com.htt.kon.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.text.HtmlCompat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.htt.kon.R;
import com.htt.kon.bean.Mp3Metadata;
import com.htt.kon.bean.Music;
import com.htt.kon.constant.MidConstant;
import com.htt.kon.service.database.MusicDbService;
import com.htt.kon.util.IdWorker;
import com.htt.kon.util.LogUtils;
import com.htt.kon.util.MusicFileMetadataParser;
import com.htt.kon.util.MusicFileSearcher;
import com.htt.kon.util.UiUtils;
import com.htt.kon.util.stream.Optional;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author su
 * @date 2020/02/10 21:06
 */
public class ScanMusicFinishActivity extends AppCompatActivity {

    private MusicDbService musicDbService;

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

    @BindView(R.id.asmf_toolbar)
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
        new Thread(task).start();
    }

    private void init() {
        this.musicDbService = MusicDbService.of(this);
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
            List<Music> oldMusics = musicDbService.list(MidConstant.MID_LOCAL_MUSIC);
            List<String> newPaths = new ArrayList<>();
            for (String path : paths) {
                if (!this.contains(path, oldMusics)) {
                    newPaths.add(path);
                }
            }
            String root = ScanMusicFinishActivity.this.getExternalFilesDir(null).getAbsolutePath();
            File dir = new File(root + "/image/");
            if (!dir.exists()) {
                dir.mkdir();
            }
            for (String path : newPaths) {
                if (stopFlag) {
                    break;
                }
                Optional<Music> opt = this.convert2music(path, dir.getAbsolutePath());
                if (opt.isPresent()) {
                    Music music = opt.get();
                    musicDbService.insert(music);
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

        /**
         * 提取出音乐标签数据, 转换成 Music 对象
         */
        private Optional<Music> convert2music(String path, String imageRootPath) {
            try {
                Mp3Metadata metadata = MusicFileMetadataParser.parse(path);
                Music music = new Music();
                music.setId(IdWorker.singleNextId());
                music.setMid(MidConstant.MID_LOCAL_MUSIC);
                music.setPath(path);
                music.setSize(new File(path).length());
                if (metadata.getImage() != null) {
                    File imageFile = new File(imageRootPath + "/" + music.getId() + ".png");
                    FileOutputStream out = new FileOutputStream(imageFile);
                    out.write(metadata.getImage());
                    out.flush();
                    out.close();
                    music.setImage(imageFile.getAbsolutePath());
                }
                music.setTitle(metadata.getTitle());
                music.setArtist(metadata.getArtist());
                music.setAlbum(metadata.getAlbum());
                music.setDuration(metadata.getDuration());
                music.setBitRate(metadata.getBitRate());
                music.setCreateTime(System.currentTimeMillis());
                music.setDelFlag(2);

                if (StringUtils.isEmpty(music.getTitle())) {
                    return Optional.of(null);
                }
                return Optional.of(music);
            } catch (IOException e) {
                LogUtils.e(e);
                return Optional.of(null);
            }
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
