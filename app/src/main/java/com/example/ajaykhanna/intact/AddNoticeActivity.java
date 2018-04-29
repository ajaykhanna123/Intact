package com.example.ajaykhanna.intact;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class AddNoticeActivity extends AppCompatActivity  {
    private Toolbar mToolbar;
    private BottomNavigationView bottomNavigationView;
    private Uri imageMainUri;
    public ImageView imgPost;
    private LinearLayout addimageLayout;
    AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_notice);

        mToolbar = findViewById(R.id.addNoticeToolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Add Notice");

        addimageLayout=findViewById(R.id.addImageLinearLayout);
        imgPost=new ImageView(AddNoticeActivity.this);

        bottomNavigationView = findViewById(R.id.bottomNavAddNotice);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int id = item.getItemId();

                switch (id) {
                    case R.id.navCamera:
                        goToCmera();

                        return true;
                    case R.id.navFile:
                        return true;
                }
                return false;
            }
        });
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(0);
        menuItem.setChecked(true);

        //dialog box for image

         builder=new AlertDialog.Builder(this);




    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.nav_add_notice, menu);
        return true;
    }

    public void goToCmera() {
        //if  sdk more than M then ask the permissoins ..otherwise just pick the image
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(AddNoticeActivity.this, Manifest.permission.CAMERA) != PackageManager
                    .PERMISSION_GRANTED) {
                Toast.makeText(AddNoticeActivity.this, "Permission denied", Toast.LENGTH_LONG)
                        .show();
                ActivityCompat.requestPermissions(AddNoticeActivity.this, new String[]
                                {Manifest.permission.CAMERA}
                        , 2);

            } else {
                bringImagePicker();
            }
        } else {
            bringImagePicker();
        }
    }

    private void bringImagePicker() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)

                .start(AddNoticeActivity.this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageMainUri = result.getUri();
                imgPost=new ImageView(AddNoticeActivity.this);
                imgPost.setImageURI(imageMainUri);
                letsSetLayoutParamsForImageView(imgPost);
                addimageLayout.addView(imgPost);
                imgPost.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {

                        builder.setMessage("Do you want to remove the Image")

                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        imgPost.setImageResource(0);
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                })

                        .show();
                        

                        return true;
                    }
                });

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }



    public void letsSetLayoutParamsForImageView(ImageView imageView){
        imageView.setLayoutParams( new LinearLayout.LayoutParams(500,500));
    }




}
