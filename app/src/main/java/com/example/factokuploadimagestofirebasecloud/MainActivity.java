package com.example.factokuploadimagestofirebasecloud;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //------Widgets------//
    private Button button_choose, button_upload, button_download;
    private EditText editText_post;
    private ImageView imageView;
    private Uri imageUri;
    private TextView textView_progress;
    private ProgressBar mProgressbar;
    private RecyclerView recyclerView;
    //------Variables-----//
    private FirebaseFirestore mFirebaseFirestore;
    private StorageReference mStorageReference;

    private Map<String, String> mMapUploadData;
    private static final int PICK_VIDEO_REQUEST = 1;
    private static final int PICK_IMAGE_REQUEST = 20;
    private static final String TAG = "Main Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getIdForAllWidgets();

        mFirebaseFirestore = FirebaseFirestore.getInstance();   //real time database
        mStorageReference = FirebaseStorage.getInstance().getReference("Uploads");   //image storing fire store option

        mMapUploadData = new HashMap<>();

        button_upload.setOnClickListener(this);
        button_download.setOnClickListener(this);
        button_choose.setOnClickListener(this);
    }

    @SuppressLint("WrongViewCast")
    private void getIdForAllWidgets() {
        button_choose = findViewById(R.id.button_main_choose);
        button_download = findViewById(R.id.button_main_download);
        button_upload = findViewById(R.id.button_main_Upload);
        editText_post = findViewById(R.id.edit_text_main_activity);
        imageView = findViewById(R.id.image_view_main_activity);
        mProgressbar = findViewById(R.id.progress_bar_main_activity);
        textView_progress = findViewById(R.id.text_view_progress_main_activity);
    }

    @Override
    public void onClick(View view) {
        if (view == button_choose) {
            getImageFromGalleryOrCamera();
        } else if (view == button_download) {
            Intent intent = new Intent(MainActivity.this, DownloadImage.class);
            startActivity(intent);
        } else if (view == button_upload) {
            uploadFile();
        }
    }

    private void getImageFromGalleryOrCamera() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Toast.makeText(this, "lol", Toast.LENGTH_SHORT).show();
//        if (requestCode == PICK_IMAGE_REQUEST && requestCode == RESULT_OK && data != null && data.getData() != null) {
        imageUri = data.getData();

        Toast.makeText(this, "url : " + imageUri, Toast.LENGTH_SHORT).show();
        Picasso.with(MainActivity.this).
                load(imageUri).
                centerCrop().
                fit().
                into(imageView);

//        }
//        else{
//            Toast.makeText(this, "Image loading problem ", Toast.LENGTH_SHORT).show();
//        }
    }

    private String getFileExtension(Uri imageUri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(
                cr.getType(imageUri)
        );
    }

    private void uploadFile() {
        if (imageUri != null) {
            mProgressbar.setVisibility(View.VISIBLE);
            StorageReference storageReference = mStorageReference.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));
            storageReference.putFile(imageUri).
                    addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mProgressbar.setProgress(0);
                                    textView_progress.setText("0");
                                }
                            }, 500);
                            uploadDataToFirebase(taskSnapshot);
                            Toast.makeText(MainActivity.this, "Upload Successful", Toast.LENGTH_SHORT).show();
                        }
                    }).
                    addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this, "Uploading Fault occurred " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }).
                    addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            mProgressbar.setProgress((int) progress);
                            textView_progress.setText(String.valueOf(progress));
                        }
                    });
        } else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadDataToFirebase(UploadTask.TaskSnapshot taskSnapshot) {

        String postName = editText_post.getText().toString();
        Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
        while (!urlTask.isSuccessful()) ;
        Uri downloadUrl = urlTask.getResult();


        UserModel userModel=new UserModel(String.valueOf(downloadUrl),postName);
//        mMapUploadData.put("Image URL ", String.valueOf(downloadUrl));
//        mMapUploadData.put("Post Name ", postName);

        mFirebaseFirestore.collection("User Data").document(String.valueOf(System.currentTimeMillis())).set(userModel).
                addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(MainActivity.this, "URL Upload Successful", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, "URL Upload Failure", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
