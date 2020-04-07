package com.htt.kon.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.htt.kon.R;
import com.htt.kon.util.UiUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author su
 * @date 2020/02/10 18:07
 */
public class ScanMusicActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.asm_textViewScanFull)
    TextView textViewScanFull;

    @BindView(R.id.asm_textViewScanCustom)
    TextView textViewScanCustom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_music);

        ButterKnife.bind(this);
        setSupportActionBar(this.toolbar);
        UiUtils.setStatusBarColor(this);
        this.init();
    }

    private void init() {
        this.toolbar.setNavigationOnClickListener(v -> {
            finish();
        });

        this.textViewScanFull.setOnClickListener(v -> {
            startActivity(new Intent(this, ScanMusicFinishActivity.class));
            finish();
        });

        this.textViewScanCustom.setOnClickListener(v -> {
            Toast.makeText(this, "敬请期待...", Toast.LENGTH_SHORT).show();
        });

    }
}
