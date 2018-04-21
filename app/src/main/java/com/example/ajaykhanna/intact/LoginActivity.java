package com.example.ajaykhanna.intact;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    //firebase
    private FirebaseAuth mAuth;

    private String mCustomToken;
    //buttons
    private TextInputLayout edtLoginUserName;
    private TextInputLayout edtLoginPassword;
    private Button btnLogin;
    private ProgressDialog mLoginProgress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mToolbar=(Toolbar)findViewById(R.id.loginToolbar);
       setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Intact");
        mAuth = FirebaseAuth.getInstance();

        //buttons
        edtLoginUserName=(TextInputLayout)findViewById(R.id.edtStdId);
        edtLoginPassword=(TextInputLayout)findViewById(R.id.edtPassword);
        btnLogin=(Button)findViewById(R.id.btnLogin);

        mLoginProgress=new ProgressDialog(LoginActivity.this);
        //when user log in
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName=edtLoginUserName.getEditText().getText().toString();
                String password=edtLoginPassword.getEditText().getText().toString();
                if(!TextUtils.isEmpty(userName) || !TextUtils.isEmpty(password))
                {
                    loginUser(userName,password);
                    mLoginProgress.setTitle("Logging In");
                    mLoginProgress.setMessage("Please wait while we check your credentials");
                    mLoginProgress.setCanceledOnTouchOutside(false);
                    mLoginProgress.show();
                }

            }
        });
    }

    void loginUser(String uId,String pass)
    {
        mAuth.signInWithEmailAndPassword(uId,pass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                mLoginProgress.dismiss();
              if(task.isSuccessful())
              {
                  Intent mainIntent=new Intent(LoginActivity.this,MainActivity.class);
                  startActivity(mainIntent);
                  mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                  finish();
              }
              else
              {
                  mLoginProgress.hide();
                  Toast.makeText(LoginActivity.this,"Authentication failed",Toast.LENGTH_LONG).show();
              }
            }
        });

    }



}
