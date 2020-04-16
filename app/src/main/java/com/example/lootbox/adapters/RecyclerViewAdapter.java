package com.example.lootbox.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lootbox.R;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {

    String itemName[], description[];
    int images[];
    Context context;

    public RecyclerViewAdapter(Context context, String itemName[], String description[], int images[]){
        this.context = context;
        this.itemName = itemName;
        this.description = description;
        this.images = images;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater li = LayoutInflater.from(context);
        View v = li.inflate(R.layout.inventory_row, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.inventoryImage.setImageResource(images[position]);
        holder.inventoryItemNameText.setText(itemName[position]);
        holder.inventoryItemDescriptionText.setText(description[position]);
    }

    @Override
    public int getItemCount() {
        return images.length;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView inventoryItemNameText, inventoryItemDescriptionText;
        ImageView inventoryImage;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            inventoryImage = itemView.findViewById(R.id.inventoryImage);
            inventoryItemNameText = itemView.findViewById(R.id.inventoryItemNameText);
            inventoryItemDescriptionText = itemView.findViewById(R.id.inventoryItemDescriptionText);
        }
    }
}
