package com.example.ajaykhanna.intact;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenu;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.lang.reflect.Field;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth mAuth;
    private android.support.v7.widget.Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar=(android.support.v7.widget.Toolbar)findViewById(R.id.menu_app_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Intact");


        mAuth=FirebaseAuth.getInstance();

        //Bottom navigation view
        BottomNavigationView bottomNavigationView =(BottomNavigationView)findViewById(R.id.bottomNavBar);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        Menu menu=bottomNavigationView.getMenu();
        MenuItem menuItem=menu.getItem(0);
        menuItem.setChecked(true);

    }
    @Override
    protected void onStart() {
        super.onStart();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
         super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
         super.onOptionsItemSelected(item);
        if(item.getItemId()==R.id.menu_logOut)
        {
            FirebaseAuth.getInstance().signOut();
            sendToLogin();
        }

        return true;

    }
    public void sendToLogin()
    {
        Intent loginIntent=new Intent(MainActivity.this,LoginActivity.class);
        startActivity(loginIntent);
        loginIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        finish();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item)
    {
        int itemId=item.getItemId();
        switch (itemId)
        {
            case R.id.navGeneralNotice:
                Intent noticeIntent=new Intent(MainActivity.this,GeneralNoticeActivity.class);
                startActivity(noticeIntent);
                noticeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                finish();
                break;
            case R.id.navStudyMaterial:
                Intent studyMaterial=new Intent(MainActivity.this,StudyMaterialActivity.class);
                startActivity(studyMaterial);
                studyMaterial.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                finish();
                break;
            case R.id.navTeacherInfo:
                Intent teacherInfo=new Intent(MainActivity.this,TeacherInfoActivity.class);
                startActivity(teacherInfo);
                teacherInfo.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                finish();
                break;
            case R.id.navDiscussionForum:
                Intent discussionIntent=new Intent(MainActivity.this,DiscussionActivity.class);
                startActivity(discussionIntent);
                discussionIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                finish();
                break;

        }


        return false;

    }


}
