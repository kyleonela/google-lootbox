package com.demo.lootbox.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.demo.lootbox.R;

public class StoreRecyclerViewAdapter extends RecyclerView.Adapter<StoreRecyclerViewAdapter.MyViewHolder>{

    String itemName[], description[];
    int images[];
    Context context;

    public StoreRecyclerViewAdapter(Context context, String itemName[], int images[]){
        this.context = context;
        this.itemName = itemName;
        this.images = images;
    }

    @NonNull
    @Override
    public StoreRecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater li = LayoutInflater.from(context);
        View v = li.inflate(R.layout.store_row, parent, false);
        return new StoreRecyclerViewAdapter.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull StoreRecyclerViewAdapter.MyViewHolder holder, int position) {
        holder.questionMarkImage.setImageResource(images[position]);
        holder.storeRandomItemNameText.setText(itemName[position]);
    }

    @Override
    public int getItemCount() {
        return images.length;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView storeRandomItemNameText;
        ImageView questionMarkImage;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            questionMarkImage = itemView.findViewById(R.id.questionMarkImage);
            storeRandomItemNameText = itemView.findViewById(R.id.storeRandomItemNameText);
        }
    }
}
