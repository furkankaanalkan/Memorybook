package com.example.memorybook;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.memorybook.databinding.RecyclerRowBinding;

import java.util.ArrayList;

public class MainActAdapter extends RecyclerView.Adapter<MainActAdapter.MainActHolder> {

    ArrayList<Memory> memoryArrayList;

    public MainActAdapter (ArrayList<Memory> memoryArrayList){
        this.memoryArrayList = memoryArrayList;
    }

    public class MainActHolder extends RecyclerView.ViewHolder {

        RecyclerRowBinding binding;

        public MainActHolder (RecyclerRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }
    }

    public int getItemCount(){
        if (memoryArrayList == null){
            return 0;
        }
        return memoryArrayList.size();
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

        holder.binding.layoutRow.setText(memoryArrayList.get(position).getName());
        holder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(holder.itemView.getContext(),ExplainActivity.class);
                intent.putExtra("memoryId", memoryArrayList.get(holder.getAdapterPosition()).getId());
                intent.putExtra("info","old");
                holder.itemView.getContext().startActivity(intent);


            }
        });

    }



}
