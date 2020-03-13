package com.htt.kon.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.htt.kon.App;
import com.htt.kon.R;
import com.htt.kon.util.PermissionHelper;

import org.jetbrains.annotations.NotNull;

/**
 * @author su
 */
public class SplashActivity extends AppCompatActivity {
    private PermissionHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 设置全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // 移除标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);

        this.helper = new PermissionHelper(this);
        this.helper.setOnApplyPermissionListener(this::runApp);
        if (Build.VERSION.SDK_INT < 23 || helper.isAllRequestedPermissionGranted()) {
            // 如果系统版本低于23，或权限全部申请了，就直接跑应用逻辑
            runApp();
        } else {
            // 如果还有权限未申请，而且系统版本大于23，执行申请权限逻辑
            helper.applyPermissions();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        helper.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void runApp() {
        App.getPoolExecutor().execute(() -> {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
    }
}
