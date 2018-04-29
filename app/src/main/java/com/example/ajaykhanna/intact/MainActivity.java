package com.example.ajaykhanna.intact;

import android.app.Fragment;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenu;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.lang.reflect.Field;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth mAuth;
    private android.support.v7.widget.Toolbar mToolbar;


    //fragments
    public NoticeFragment noticeFragment;
    public StudyFragment studyFragment;
    public DiscussionFragment discussionFragment;
    public TeacherFragment teacherFragment;
    public NotificationFragment notificationFragment;

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle mToogle;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        //Toolbar name ="intact"
        mToolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.menu_app_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Intact");

        //drawer menu layout
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerlayout);
        mToogle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(mToogle);
        mToogle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


        mAuth = FirebaseAuth.getInstance();

        //Bottom navigation view
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavBar);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);

        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(0);
        menuItem.setChecked(true);


        //initialise all fragments
        noticeFragment = new NoticeFragment();
        teacherFragment = new TeacherFragment();
        discussionFragment = new DiscussionFragment();
        studyFragment = new StudyFragment();
        notificationFragment = new NotificationFragment();

        replaceFragment(noticeFragment);//app starts from dis fragment

        navigationView = (NavigationView) findViewById(R.id.navigationView);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id=item.getItemId();
                switch(id)
                {
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


        navigationView.setNavigationItemSelectedListener(this);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(mToogle.onOptionsItemSelected(item)){
            return true;
        }

        return super.onOptionsItemSelected(item);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {

        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {


    int id=item.getItemId();
    switch (id)
    {
        case R.id.navAccount:
            Intent accIntent=new Intent(MainActivity.this,AccountActivity.class);
            startActivity(accIntent);

            return true;
        case R.id.navLogOut:
            FirebaseAuth.getInstance().signOut();
            sendToLogin();
            return true;
    }
        return false;
    }



}
