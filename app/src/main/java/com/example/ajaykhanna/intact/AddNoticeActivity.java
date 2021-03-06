package com.example.ajaykhanna.intact;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.provider.SyncStateContract;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import id.zelory.compressor.Compressor;

public class AddNoticeActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private BottomNavigationView bottomNavigationView;
    private Uri imageMainUri;
    public ImageView imgPost;
    private LinearLayout addimageLayout;
    AlertDialog.Builder builder;
    final static int PICK_PDF_CODE = 2342;

    //the firebase objects for storage and database
    private StorageReference storageReference;
    private FirebaseFirestore firebaseFirestore;
    Uri finalPdf1;

    private FirebaseAuth firebaseAuth;
    private String current_user_id;
    private Bitmap compressedImageFile;
    TextView pdfView;
    Uri pdf;

    //declare all fields
    EditText edtNoticeTitle;
    EditText edtNoticeDesc;
    String downloadPdf;


    private ProgressBar newPostProgressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_notice);

        mToolbar = findViewById(R.id.addNoticeToolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Add Notice");


        //initialse all fields
        newPostProgressBar=findViewById(R.id.addNoticeProgress);

        edtNoticeTitle = findViewById(R.id.edtNoticeTitle);
        edtNoticeDesc = findViewById(R.id.edtNoticeDesc);


        addimageLayout = findViewById(R.id.addImageLinearLayout);
        imgPost = new ImageView(AddNoticeActivity.this);
        pdfView = new TextView(AddNoticeActivity.this);

        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        current_user_id = firebaseAuth.getCurrentUser().getUid();


        bottomNavigationView = findViewById(R.id.bottomNavAddNotice);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        //choose wich file is used while makig a notice
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int id = item.getItemId();

                switch (id) {
                    case R.id.navCamera:
                        goToCmera();
                        return true;
                    case R.id.navFile:
                        getPDF();
                        return true;
                }
                return false;
            }
        });
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(0);
        menuItem.setChecked(true);

        //dialog box for image

        builder = new AlertDialog.Builder(this);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.save_add_notice, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        int id = item.getItemId();

        switch (id) {
            case R.id.saveNotice:
                saveAllDataToFirestore();
                return true;
        }
        return true;
    }

    private void saveAllDataToFirestore() {

        final String noticeTitle = edtNoticeTitle.getText().toString();
        final String noticeDesc = edtNoticeDesc.getText().toString();

        if (!TextUtils.isEmpty(noticeTitle)) {
            newPostProgressBar.setVisibility(View.VISIBLE);

            final String randomName = UUID.randomUUID().toString();
            StorageReference filePath = storageReference.child("post_images").child(randomName + ".jpg");

            StorageReference pdfPath = storageReference.child("post_pdf").child(randomName + ".pdf");


            if (imageMainUri != null && pdf != null) {

                pdfPath.putFile(pdf).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        downloadPdf = task.getResult().getDownloadUrl().toString();

                        if (task.isSuccessful()) {
                            Toast.makeText(AddNoticeActivity.this, "pdf file upload", Toast.LENGTH_LONG)
                                    .show();

                        }
                    }
                })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                String message = e.getMessage().toString();
                                Toast.makeText(AddNoticeActivity.this, message, Toast.LENGTH_LONG).show();

                            }
                        });


                filePath.putFile(imageMainUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull final Task<UploadTask.TaskSnapshot> task) {
                        final String downloadUri = task.getResult().getDownloadUrl().toString();

                        if (task.isSuccessful()) {
                            File newImageFile = new File(imageMainUri.getPath());
                            try {
                                compressedImageFile = new Compressor(AddNoticeActivity.this)
                                        .setMaxHeight(100)
                                        .setMaxWidth(100)
                                        .setQuality(2)
                                        .compressToBitmap(newImageFile);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                            byte[] thumbData = baos.toByteArray();
                            UploadTask uploadTask = storageReference.child("post_images/thumbs").child(randomName + ".jpg")
                                    .putBytes(thumbData);


                            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                    String downloadThumbUri = taskSnapshot.getDownloadUrl().toString();


                                    Map<String, Object> postMap = new HashMap<>();
                                    postMap.put("image_thumb", downloadThumbUri);
                                    postMap.put("image_url", downloadUri);
                                    postMap.put("desc", noticeDesc);
                                    postMap.put("title", noticeTitle);
                                    postMap.put("user_id", current_user_id);
                                    postMap.put("timeStamp", FieldValue.serverTimestamp());
                                    postMap.put("pdf_file", downloadPdf);


                                    firebaseFirestore.collection("Posts").add(postMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentReference> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(AddNoticeActivity.this, "This post was added", Toast.LENGTH_LONG)
                                                        .show();
                                                Intent mainIntent = new Intent(AddNoticeActivity.this, MainActivity.class);
                                                startActivity(mainIntent);
                                                finish();

                                            } else {
                                                Toast.makeText(AddNoticeActivity.this, "this post was not added"
                                                        , Toast.LENGTH_LONG).show();

                                            }

                                        }
                                    });

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    String message = e.getMessage().toString();
                                    Toast.makeText(AddNoticeActivity.this, message, Toast.LENGTH_LONG).show();

                                }
                            });
                            newPostProgressBar.setVisibility(View.INVISIBLE);


                        } else {
                            addimageLayout.removeView(newPostProgressBar);
                        }
                    }
                });
            } else if (pdf != null && imageMainUri == null) {


                UploadTask uploadTask = storageReference.child("post_pdf").child(randomName + ".jpg")
                        .putFile(pdf);


                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        String downloadPdf = taskSnapshot.getDownloadUrl().toString();


                        Map<String, Object> postMap = new HashMap<>();
                        postMap.put("image_thumb", null);
                        postMap.put("image_url", null);
                        postMap.put("desc", noticeDesc);
                        postMap.put("title", noticeTitle);
                        postMap.put("user_id", current_user_id);
                        postMap.put("timeStamp", FieldValue.serverTimestamp());
                        postMap.put("pdf_file", downloadPdf);


                        firebaseFirestore.collection("Posts").add(postMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(AddNoticeActivity.this, "This post was added", Toast.LENGTH_LONG)
                                            .show();
                                    Intent mainIntent = new Intent(AddNoticeActivity.this, MainActivity.class);
                                    startActivity(mainIntent);
                                    finish();

                                } else {
                                    Toast.makeText(AddNoticeActivity.this, "this post was not added"
                                            , Toast.LENGTH_LONG).show();

                                }

                            }
                        });

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        String message = e.getMessage().toString();
                        Toast.makeText(AddNoticeActivity.this, message, Toast.LENGTH_LONG).show();

                    }
                });
                newPostProgressBar.setVisibility(View.INVISIBLE);


            } else if (pdf == null && imageMainUri != null) {

                filePath.putFile(imageMainUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull final Task<UploadTask.TaskSnapshot> task) {
                        final String downloadUri = task.getResult().getDownloadUrl().toString();

                        if (task.isSuccessful()) {
                            File newImageFile = new File(imageMainUri.getPath());
                            try {
                                compressedImageFile = new Compressor(AddNoticeActivity.this)
                                        .setMaxHeight(100)
                                        .setMaxWidth(100)
                                        .setQuality(2)
                                        .compressToBitmap(newImageFile);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                            byte[] thumbData = baos.toByteArray();
                            UploadTask uploadTask = storageReference.child("post_images/thumbs").child(randomName + ".jpg")
                                    .putBytes(thumbData);


                            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                    String downloadThumbUri = taskSnapshot.getDownloadUrl().toString();


                                    Map<String, Object> postMap = new HashMap<>();
                                    postMap.put("image_thumb", downloadThumbUri);
                                    postMap.put("image_url", downloadUri);
                                    postMap.put("desc", noticeDesc);
                                    postMap.put("title", noticeTitle);
                                    postMap.put("user_id", current_user_id);
                                    postMap.put("timeStamp", FieldValue.serverTimestamp());
                                    postMap.put("pdf_file", null);


                                    firebaseFirestore.collection("Posts").add(postMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentReference> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(AddNoticeActivity.this, "This post was added", Toast.LENGTH_LONG)
                                                        .show();
                                                Intent mainIntent = new Intent(AddNoticeActivity.this, MainActivity.class);
                                                startActivity(mainIntent);
                                                finish();

                                            } else {
                                                Toast.makeText(AddNoticeActivity.this, "this post was not added"
                                                        , Toast.LENGTH_LONG).show();

                                            }

                                        }
                                    });

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    String message = e.getMessage().toString();
                                    Toast.makeText(AddNoticeActivity.this, message, Toast.LENGTH_LONG).show();

                                }
                            });


                        } else {
                            addimageLayout.removeView(newPostProgressBar);
                        }
                    }
                });
                newPostProgressBar.setVisibility(View.INVISIBLE);

            } else {

                Map<String, Object> postMap = new HashMap<>();
                postMap.put("image_thumb", null);
                postMap.put("image_url", null);
                postMap.put("desc", noticeDesc);
                postMap.put("title", noticeTitle);
                postMap.put("user_id", current_user_id);
                postMap.put("timeStamp", FieldValue.serverTimestamp());
                postMap.put("pdf_file", null);

                firebaseFirestore.collection("Posts").add(postMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(AddNoticeActivity.this, "This post was added", Toast.LENGTH_LONG)
                                    .show();
                            Intent mainIntent = new Intent(AddNoticeActivity.this, MainActivity.class);
                            startActivity(mainIntent);
                            finish();

                        } else {
                            Toast.makeText(AddNoticeActivity.this, "this post was not added"
                                    , Toast.LENGTH_LONG).show();

                        }

                    }
                });
                newPostProgressBar.setVisibility(View.INVISIBLE);


            }


        } else {
            Toast.makeText(AddNoticeActivity.this, "Title of the post is must", Toast.LENGTH_LONG).show();
        }

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
                imgPost = new ImageView(AddNoticeActivity.this);
                imgPost.setImageURI(imageMainUri);
                letsSetLayoutParamsForImageView(imgPost);
                addimageLayout.removeView(imgPost);
                addimageLayout.addView(imgPost);
                imgPost.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {

                        builder.setMessage("Do you want to remove the Image")

                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        imgPost.setImageResource(0);
                                        imageMainUri = null;
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

        if (requestCode == PICK_PDF_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            //if a file is selected

            if (data.getData() != null) {
                //uploading the file


                addimageLayout.removeView(pdfView);
                pdf = data.getData();
                pdfView.setText(pdf.toString());
                pdfView.setText("file.pdf");
                addimageLayout.addView(pdfView);

                finalPdf1 = pdf;
                pdfView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent pdfOpenintent = new Intent(Intent.ACTION_VIEW);
                        pdfOpenintent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        pdfOpenintent.setDataAndType(finalPdf1, "application/pdf");
                        try {
                            startActivity(pdfOpenintent);
                        } catch (ActivityNotFoundException e) {

                        }

                    }
                });
                pdfView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {

                        builder.setMessage("Do you want to remove the file")

                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        addimageLayout.removeView(pdfView);
                                        pdf = null;
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


            } else {
                Toast.makeText(this, "No file chosen", Toast.LENGTH_SHORT).show();
            }
        }
    }


    public void letsSetLayoutParamsForImageView(ImageView imageView) {
        imageView.setLayoutParams(new LinearLayout.LayoutParams(500, 500));
        imageView.setAdjustViewBounds(true);
    }

    private void getPDF() {
        //for greater than lolipop versions we need the permissions asked on runtime
        //so if the permission is not available user will go to the screen to allow storage permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.parse("package:" + getPackageName()));
            startActivity(intent);
            return;
        }
        //creating an intent for file chooser
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Pdf file"), PICK_PDF_CODE);
    }


}
