package com.demo.lootbox;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.demo.lootbox.adapters.RecyclerViewAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class InventoryActivity extends AppCompatActivity {

    RecyclerView recyclerView;

    /* Setup Firebase Auth and Firestore */
    FirebaseAuth fbAuth;
    FirebaseFirestore firestore;

    /* Fetch Items through firebase */
    String itemNames[] = {"Updated Apple", "New Banana", "Pear", "Pen", "Pencil", "Tree", "Grass", "Book", "Spoon", "Fork"};
    String description[] = {"Description","Description","Description","Description","Description","Description",
            "Description","Description","Description","Description"};
    int images[] = {R.drawable.baseline_email_black_18dp,
            R.drawable.baseline_email_black_18dp,
            R.drawable.baseline_email_black_18dp,
            R.drawable.baseline_email_black_18dp,
            R.drawable.baseline_email_black_18dp,
            R.drawable.baseline_email_black_18dp,
            R.drawable.baseline_email_black_18dp,
            R.drawable.baseline_email_black_18dp,
            R.drawable.baseline_email_black_18dp,
            R.drawable.baseline_email_black_18dp};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        recyclerView = findViewById(R.id.recyclerView);

        /* Getting current instance of database from Firebase and Firestore */
        fbAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        RecyclerViewAdapter adapter = new RecyclerViewAdapter(this, itemNames, description, images);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}
