package com.example.factokuploadimagestofirebasecloud;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DownloadImage extends AppCompatActivity {

    private RecyclerView recyclerView;

    private RecyclerAdapterImages recyclerAdapterImages;
    private ArrayList<UserModel> mArrayList;
    private FirebaseFirestore mFirebaseFirestore;
    private DocumentReference documentReference;
    private static final String TAG = "Main Activity";

    private Map<String, Object> note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_recycler_view);

        mFirebaseFirestore = FirebaseFirestore.getInstance();   //image storing fire store option

        mArrayList = new ArrayList<UserModel>();
        getIdForWidget();
        downloadRecycler();

        recyclerView.setLayoutManager(new GridLayoutManager(DownloadImage.this, 2, GridLayoutManager.VERTICAL, false));

    }

    private void downloadRecycler() {

        mFirebaseFirestore.collection("User Data").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                if (queryDocumentSnapshots == null) {
                    Log.e(TAG, "Listen failed!");
                    return;
                } else {

                    Toast.makeText(DownloadImage.this, "Here we are ", Toast.LENGTH_SHORT).show();

                    // Log.d(TAG, queryDocumentSnapshots.getString("Image URL")+"\n");

                    mArrayList.clear();

                    //List<UserModel> userModels = queryDocumentSnapshots.toObjects(UserModel.class);
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        UserModel userModel = documentSnapshot.toObject(UserModel.class);
                        mArrayList.add(userModel);
                    }
                    recyclerAdapterImages = new RecyclerAdapterImages(DownloadImage.this, mArrayList);
                    recyclerView.setAdapter(recyclerAdapterImages);
                    recyclerAdapterImages.notifyDataSetChanged();
                }
            }
        });
    }

    private void getIdForWidget() {
        recyclerView = findViewById(R.id.relative_layout_image_recycler_main);
    }
}
