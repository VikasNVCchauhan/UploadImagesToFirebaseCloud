package com.example.factokuploadimagestofirebasecloud;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

public class RecyclerAdapterVideos extends RecyclerView.Adapter<RecyclerAdapterVideos.RecyclerVideoHolder> {

    @NonNull
    @Override
    public RecyclerVideoHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerVideoHolder recyclerVideoHolder, int i) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class RecyclerVideoHolder extends RecyclerView.ViewHolder {
        public RecyclerVideoHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
