package com.demo.lootbox;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.demo.lootbox.adapters.RecyclerViewAdapter;
import com.google.android.gms.common.util.ArrayUtils;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

        displayUserDetails(fbAuth, firestore);
    }

    private void displayUserDetails(FirebaseAuth auth, FirebaseFirestore fStore){
        String userId = auth.getCurrentUser().getUid();
        CollectionReference colRef = fStore.collection("items").document(userId).collection("itemsList");

        colRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot querySnapshot) {
                ArrayList<String> newItemsList = new ArrayList<String>();
                ArrayList<String> newDescList = new ArrayList<String>();
                for (QueryDocumentSnapshot qds : querySnapshot){
                    String iName = qds.get("itemName").toString();
                    String iDesc = qds.get("itemPrice").toString();
                    newItemsList.add(iName);
                    newDescList.add(iDesc);
                    Collections.addAll(newItemsList, itemNames);
                    Collections.addAll(newDescList, description);
                }
                String iArray[] = newItemsList.toArray(new String[newItemsList.size()]);
                String dArray[] = newDescList.toArray(new String[newDescList.size()]);
                setupView(iArray, dArray);
            }
        });
    }

    private void setupView(String[] iArray, String[] dArray) {
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(this, iArray, dArray, images);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}
