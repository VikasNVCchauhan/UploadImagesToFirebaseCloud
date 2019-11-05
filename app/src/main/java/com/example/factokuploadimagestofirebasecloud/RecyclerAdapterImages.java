package com.example.factokuploadimagestofirebasecloud;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.firestore.auth.User;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;
import java.sql.Types;
import java.util.ArrayList;

public class RecyclerAdapterImages extends RecyclerView.Adapter<RecyclerAdapterImages.RecyclerImageHolder> {

    private ArrayList<UserModel> arrayList;
    private Context context;
    private View view;

    public RecyclerAdapterImages(Context context, ArrayList<UserModel> arrayList) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerImageHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        view = LayoutInflater.from(context).inflate(R.layout.recycle_item_image, null);
        return new RecyclerImageHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerImageHolder recyclerImageHolder, int i) {

          Picasso.with(context).load(arrayList.get(i).getImageURL()).centerCrop().fit().into(recyclerImageHolder.imageView);
          recyclerImageHolder.textView.setText(arrayList.get(i).getText_Post());
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    class RecyclerImageHolder extends RecyclerView.ViewHolder {

        public ImageView imageView;
        public TextView textView;

        public RecyclerImageHolder(@NonNull View itemView) {
            super(itemView);

            getIdForAllWidget(itemView);
        }

        private void getIdForAllWidget(View itemView) {
            imageView = itemView.findViewById(R.id.image_view_recycler_item_image);
            textView = itemView.findViewById(R.id.text_view_recycler_item_image);
        }
    }


}
