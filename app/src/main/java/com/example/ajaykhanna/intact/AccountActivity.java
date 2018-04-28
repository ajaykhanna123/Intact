package com.example.ajaykhanna.intact;

import android.*;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import de.hdodenhof.circleimageview.CircleImageView;

public class AccountActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private Toolbar mToolbar;
    private Spinner spinnerCourse;
    private EditText edtName;
    private EditText edtRollNo;
    private EditText edtEmail;
    private EditText edtPhnNo;
    private ProgressBar progressSave;//circlar progress bar
    private boolean ischanged =false;
    private FirebaseAuth firebaseAuth;
    private String userId;
    private StorageReference mStorageRef;
    private Uri imageMainUri=null;
    private RelativeLayout layoutAcc1;
    private Button btnSaveSettings;
    private CircleImageView imgUser;


    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        //Toolbar name ="Account"
        mToolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.profile_toobar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//parent layout is mainActivity

        //firrbase initiation
        firebaseAuth=FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
       // progressSave=(ProgressBar)findViewById(R.id.progressSave);
        userId=firebaseAuth.getCurrentUser().getUid();
        firebaseFirestore=FirebaseFirestore.getInstance();


        //initise all the fields

        edtName=findViewById(R.id.edtAccName);
        edtEmail=findViewById(R.id.edtAccEmail);
        edtPhnNo=findViewById(R.id.edtAccPhnNo);
        edtRollNo=findViewById(R.id.edtAccRollNo);
        btnSaveSettings=findViewById(R.id.btnSaveSettings);
        progressSave=findViewById(R.id.progressSave);//it is firstly set to gone
        imgUser=findViewById(R.id.imgUser);


        //spinner for course
        spinnerCourse=findViewById(R.id.spinnerCourse);
        spinnerCourse.setOnItemSelectedListener(this);
        //spinner course items

        List<String> categoriesCourse= new ArrayList<String>();
        categoriesCourse.add("B.Tech(Mechanical)");
        categoriesCourse.add("B.Tech(Computer Science)");
        categoriesCourse.add("B.Tech(Electrical)");
        categoriesCourse.add("B.Tech(Civil)");
        categoriesCourse.add("M.Tech(Computer Science)");
        //creating adapter for spinnerCourse
        final ArrayAdapter<String> courseAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,
                categoriesCourse);
        //setting dropDown style for spinner with radio button

        courseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerCourse.setAdapter(courseAdapter);


        //add image to firebase





        //image permissions
        imgUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if  sdk more than M then ask the permissoins ..otherwise just pick the image
                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                    if(ContextCompat.checkSelfPermission(AccountActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager
                            .PERMISSION_GRANTED){
                        Toast.makeText(AccountActivity.this,"Permission denied",Toast.LENGTH_LONG)
                                .show();
                        ActivityCompat.requestPermissions(AccountActivity.this,new String[]
                                        {Manifest.permission.READ_EXTERNAL_STORAGE}
                                ,2);

                    }else
                    {
                        bringImagePicker();
                    }
                }else
                {
                    bringImagePicker();
                }
            }
        });
        progressSave.setVisibility(View.VISIBLE);
        btnSaveSettings.setEnabled(false);
        firebaseFirestore.collection("Users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful())
                {
                    if(task.getResult().exists())
                    {
                        Toast.makeText(AccountActivity.this,"data retrieved",Toast.LENGTH_LONG).show();
                        String name=task.getResult().getString("name");
                        String image=task.getResult().getString("image");
                        String rollNo=task.getResult().getString("rollNo");
                        String email=task.getResult().getString("email");
                        String phnNo=task.getResult().getString("PhnNo");
                        String course=task.getResult().getString("course");

                        edtName.setText(name);
                        edtEmail.setText(email);
                        edtPhnNo.setText(phnNo);
                        edtRollNo.setText(rollNo);
                        if (course != null) {
                            int spinnerPosition = courseAdapter.getPosition(course);
                            spinnerCourse.setSelection(spinnerPosition);
                        }

                        imageMainUri=Uri.parse(image);
                        RequestOptions placeHolderRequest=new RequestOptions();
                        placeHolderRequest.placeholder(R.drawable.default_image);

                        Glide.with(AccountActivity.this).setDefaultRequestOptions(placeHolderRequest).load(image)
                                .into(imgUser);
                    }
                    else{

                        Toast.makeText(AccountActivity.this,"data not retreived",Toast.LENGTH_LONG).show();
                    }
                }
                else{
                    String errorMessage=task.getException().toString();
                    Toast.makeText(AccountActivity.this,"Firestore error"+errorMessage,Toast.LENGTH_LONG).show();

                }
                progressSave.setVisibility(View.INVISIBLE);
                btnSaveSettings.setEnabled(true);
            }
        });

        /*if(TextUtils.isEmpty(accCourse) &&  TextUtils.isEmpty(accName) && TextUtils.isEmpty(accRollNo) &&
                TextUtils.isEmpty(accPhnNo) &&  TextUtils.isEmpty(accEmail))
        {
            btnSaveSettings.setEnabled(false);
            btnSaveSettings.getBackground().setAlpha(2);
        }*/
        btnSaveSettings.setEnabled(true);
        btnSaveSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String accName=edtName.getText().toString();
                final String accRollNo=edtRollNo.getText().toString();
                final String accPhnNo=edtPhnNo.getText().toString();
                final String accEmail=edtEmail.getText().toString();
                final String accCourse=spinnerCourse.getSelectedItem().toString();


                if (!TextUtils.isEmpty(accName) && !TextUtils.isEmpty(accRollNo) && !TextUtils.isEmpty(accCourse) ) {
                    if (ischanged) {
                        btnSaveSettings.setEnabled(true);
                        progressSave.setVisibility(View.VISIBLE);

                        StorageReference imagePath = mStorageRef.child("profile_images").child(userId + ".jpg");
                        imagePath.putFile(imageMainUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if (task.isSuccessful()) {
                                    storeFirestore(task, accName,accEmail,accRollNo,accPhnNo,accCourse);

                                } else {
                                    String errorMessage = task.getException().toString();
                                    Toast.makeText(AccountActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                                }
                                progressSave.setVisibility(View.INVISIBLE);
                            }
                        });
                    }else
                    {
                        storeFirestore(null, accName,accEmail,accRollNo,accPhnNo,accCourse);
                    }
                }

            }
        });



    }

    private void storeFirestore(@NonNull Task<UploadTask.TaskSnapshot> task,String username,String userEmail,String
                                userRollNo,String userPhnNo,String userCourse) {
        Uri uri_Download;
        if(task!=null)
        {
            uri_Download=task.getResult().getDownloadUrl();
        }else
        {
            uri_Download=imageMainUri;
        }

        Map<String,String> usermap=new HashMap<>();
        usermap.put("name",username);
        usermap.put("image",uri_Download.toString());
        usermap.put("email",userEmail);
        usermap.put("rollNo",userRollNo);
        usermap.put("PhnNo",userPhnNo);
        usermap.put("course",userCourse);
        firebaseFirestore.collection("Users").document(userId).set(usermap).addOnCompleteListener(
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(AccountActivity.this,"Data is uploaded",Toast.LENGTH_LONG
                            ).show();
                            Intent mainIntent=new Intent(AccountActivity.this,MainActivity.class);
                            startActivity(mainIntent);
                            finish();

                        }else{
                            String errorMessage=task.getException().toString();
                            Toast.makeText(AccountActivity.this,"Firestore error"+errorMessage,Toast.LENGTH_LONG).show();
                        }
                        progressSave.setVisibility(View.INVISIBLE);
                    }
                }
        );
    }





    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        String item = parent.getItemAtPosition(position).toString();

        // Showing selected spinner item
        //Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
    }
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }

    private void bringImagePicker() {
                CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)

                .start(AccountActivity.this);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageMainUri= result.getUri();
                imgUser.setImageURI(imageMainUri);

                ischanged=true;
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }




}
