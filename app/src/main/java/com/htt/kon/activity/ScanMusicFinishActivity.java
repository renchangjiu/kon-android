package com.htt.kon.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;
import android.widget.Toast;

import com.htt.kon.R;
import com.htt.kon.bean.Mp3Metadata;
import com.htt.kon.bean.Music;
import com.htt.kon.dao.AppDatabase;
import com.htt.kon.dao.MusicDao;
import com.htt.kon.util.IdWorker;
import com.htt.kon.util.LogUtils;
import com.htt.kon.util.MusicFileMetadataParser;
import com.htt.kon.util.MusicFileSearcher;
import com.htt.kon.util.StorageUtils;
import com.htt.kon.util.UiUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author su
 * @date 2020/02/10 21:06
 */
public class ScanMusicFinishActivity extends AppCompatActivity {

    private MusicDao musicDao;

    private Handler handler = new Handler((msg) -> {
        int count = (int) msg.obj;
        this.textViewResult.setText("共扫描到" + count + "首歌曲");

        return true;
    });

    @BindView(R.id.asmf_toolbar)
    Toolbar toolbar;

    @BindView(R.id.asmf_textViewResult)
    TextView textViewResult;

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
        this.musicDao = AppDatabase.of(this).musicDao();
        this.toolbar.setNavigationOnClickListener(v -> {
            finish();
        });

        this.textViewLyric.setOnClickListener(v -> {
            Toast.makeText(this, "敬请期待...", Toast.LENGTH_SHORT).show();
        });
    }


    private class Task implements Runnable {
        private boolean stopFlag = true;

        @Override
        public void run() {
            String sdcardRootPath = StorageUtils.getSdcardRootPath(ScanMusicFinishActivity.this);
            String externalRootPath = StorageUtils.getExternalRootPath(ScanMusicFinishActivity.this);

            List<String> list = MusicFileSearcher.search(sdcardRootPath);
            list.addAll(MusicFileSearcher.search(externalRootPath));

            try {
                this.covert2music(list);
            } catch (Exception e) {
                LogUtils.e(e);
            }
        }

        private void covert2music(List<String> list) throws Exception {
            List<Music> musics = new ArrayList<>();
            String root = ScanMusicFinishActivity.this.getExternalFilesDir(null).getAbsolutePath();
            File dir = new File(root + "/image/");
            if (!dir.exists()) {
                dir.mkdir();
            }
            for (String path : list) {
                Mp3Metadata metadata = MusicFileMetadataParser.parse(path);
                Music music = new Music();
                music.setId(IdWorker.singleNextId());
                music.setMid(0L);
                music.setPath(path);
                music.setSize(new File(path).length());
                if (metadata.getImage() != null) {
                    File imageFile = new File(dir.getAbsolutePath() + "/" + music.getId() + ".png");
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
                music.setCreateTime(System.currentTimeMillis());
                music.setDelFlag(2);
                musics.add(music);
            }
            this.save2db(musics);
        }

        /**
         * TODO: 已存在的不插入到db
         */
        private void save2db(List<Music> musics) {
            for (Music music : musics) {
                musicDao.insert(music);
                LogUtils.e(music);
            }
            Message msg = Message.obtain();
            msg.what = 1;
            msg.obj = musics.size();
            handler.sendMessage(msg);
        }
    }

}
