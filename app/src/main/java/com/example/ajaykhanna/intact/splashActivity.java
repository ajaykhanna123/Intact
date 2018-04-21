package com.example.ajaykhanna.intact;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class splashActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private final int SPLASH_TIME=2000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mAuth=FirebaseAuth.getInstance();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                FirebaseUser currentUser=mAuth.getCurrentUser();
                if(currentUser==null)
                {
                    Intent loginIntent=new Intent(splashActivity.this,LoginActivity.class);
                    startActivity(loginIntent);
                    loginIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    finish();
                }
                else
                {
                    Intent mainIntent=new Intent(splashActivity.this,MainActivity.class);
                    mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(mainIntent);
                    finish();
                }

            }
        },SPLASH_TIME);


    }
}
