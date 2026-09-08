package com.example.memorybook;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.memorybook.databinding.RecyclerRowBinding;

import java.util.ArrayList;

public class MainActAdapter extends RecyclerView.Adapter<MainActAdapter.MainActHolder> {

    ArrayList<String> deneme;

    public MainActAdapter (ArrayList<String> deneme){
        this.deneme = deneme;
    }

    public class MainActHolder extends RecyclerView.ViewHolder {

        RecyclerRowBinding binding;

        public MainActHolder (RecyclerRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }
    }

    public int getItemCount(){
        if (deneme == null){
            return 0;
        }
        return deneme.size();
    }

    @NonNull
    @Override
    public MainActAdapter.MainActHolder onCreateViewHolder(ViewGroup parent , int viewType){
        RecyclerRowBinding recyclerRowBinding = RecyclerRowBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new MainActAdapter.MainActHolder(recyclerRowBinding);
    }

    @NonNull
    @Override
    public void  onBindViewHolder(MainActAdapter.MainActHolder holder , int position){

        holder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                int adapterPosition = holder.getAdapterPosition();


            }
        });

    }



}
