package com.example.ajaykhanna.intact;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toolbar;

public class NoticePostActivity extends AppCompatActivity {
    private android.support.v7.widget.Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_post);

        mToolbar=(android.support.v7.widget.Toolbar)findViewById(R.id.addNoticeToolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Post");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
