package com.example.ajaykhanna.intact;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class DiscussionActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discussion);

        mToolbar=(Toolbar)findViewById(R.id.DiscussionToolbar);


        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Discussion Forum");

        BottomNavigationView bottomNavigationView =(BottomNavigationView)findViewById(R.id.bottomNavBar);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        Menu menu=bottomNavigationView.getMenu();
        MenuItem menuItem=menu.getItem(1);
        menuItem.setChecked(true);



    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId=item.getItemId();
        switch (itemId)
        {
            case R.id.navGeneralNotice:
                Intent noticeIntent=new Intent(DiscussionActivity.this,GeneralNoticeActivity.class);
                startActivity(noticeIntent);
                noticeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                finish();
                break;
            case R.id.navStudyMaterial:
                Intent studyMaterial=new Intent(DiscussionActivity.this,StudyMaterialActivity.class);
                startActivity(studyMaterial);
                studyMaterial.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                finish();
                break;
            case R.id.navTeacherInfo:
                Intent teacherInfo=new Intent(DiscussionActivity.this,TeacherInfoActivity.class);
                startActivity(teacherInfo);
                teacherInfo.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                finish();
                break;
            case R.id.navDiscussionForum:
                Intent discussionIntent=new Intent(DiscussionActivity.this,DiscussionActivity.class);
                startActivity(discussionIntent);
                discussionIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                finish();
                break;

        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }
}
