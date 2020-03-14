package com.htt.kon.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.htt.kon.R;

/**
 * @author su
 * @date 2020/03/14 12:31
 */
public class MusicsActivity extends AppCompatActivity implements DataRequisiteActivity {


    public static Bundle putData() {
        throw new RuntimeException();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        DataRequisiteActivity.putData();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_musics);
    }
}
