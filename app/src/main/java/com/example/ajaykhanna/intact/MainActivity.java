package com.example.ajaykhanna.intact;

import android.app.Fragment;
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

public class MainActivity extends AppCompatActivity  {

    private FirebaseAuth mAuth;
    private android.support.v7.widget.Toolbar mToolbar;

    //fragments
    public NoticeFragment noticeFragment;
    public StudyFragment studyFragment;
    public DiscussionFragment discussionFragment;
    public TeacherFragment teacherFragment;
    public NotificationFragment notificationFragment;

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

        Menu menu=bottomNavigationView.getMenu();
        MenuItem menuItem=menu.getItem(0);
        menuItem.setChecked(true);


        //initialise all fragments
        noticeFragment=new NoticeFragment();
        teacherFragment=new TeacherFragment();
        discussionFragment=new DiscussionFragment();
        studyFragment=new StudyFragment();
        notificationFragment=new NotificationFragment();

        replaceFragment(noticeFragment);//app starts from dis fragment

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navGeneralNotice:
                        replaceFragment(noticeFragment);
                        return true;
                    case R.id.navDiscussionForum:
                        replaceFragment(discussionFragment);
                        return true;
                    case R.id.navStudyMaterial:
                        replaceFragment(studyFragment);
                        return true;
                    case R.id.navTeacherInfo:
                        replaceFragment(teacherFragment);
                        return true;
                    case R.id.navNotifications:
                        replaceFragment(notificationFragment);
                        return true;


                }
                return false;
            }
        });

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
    private void replaceFragment(android.support.v4.app.Fragment fragment)
    {
        android.support.v4.app.FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.mainContainer,fragment);
        fragmentTransaction.commit();
    }




}
