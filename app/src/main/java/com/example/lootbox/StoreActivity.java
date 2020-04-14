package com.example.lootbox;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StoreActivity extends AppCompatActivity implements PurchasesUpdatedListener {

    Button purchaseButton;

    /* Setup Firebase Auth and Firestore */
    FirebaseAuth fbAuth;
    FirebaseFirestore firestore;

    /* Setup billing client so we can connect to google store */
    private BillingClient billingClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);

        /* Getting current instance of database from Firebase and Firestore */
        fbAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        purchaseButton = findViewById(R.id.purchaseButton);

        establishConnection();

        purchaseButton.setOnClickListener(x -> {
            purchaseItem();
        });
    }

    /* Connect to Google Play */
    private void establishConnection() {
        billingClient = BillingClient.newBuilder(this).setListener(this).build();
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    Toast.makeText(StoreActivity.this, "Billing store successfully connected", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(StoreActivity.this, "Cannot connect to store: " + billingResult.getResponseCode() , Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                Toast.makeText(StoreActivity.this, "You have disconnected from the Billing service", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /* Query for in-app product details */
    private void purchaseItem() {
        // Test Items
        List<String> itemsList = new ArrayList<>(
                Arrays.asList(
                        "Sandal", "Fork", "Spoon", "Couch", "Credit Card", "Nail Clipper",
                        "Bed", "Paper", "Headphones", "Book", "Outlet", "Tree", "Grass", "Apple",
                        "Banana", "Pear", "Pen", "Pencil")
        );

        if(billingClient.isReady()){
            SkuDetailsParams.Builder sdp = SkuDetailsParams.newBuilder();
            sdp.setSkusList(itemsList).setType(BillingClient.SkuType.INAPP);
            billingClient.querySkuDetailsAsync(sdp.build(), new SkuDetailsResponseListener() {
                @Override
                public void onSkuDetailsResponse(BillingResult billingResult, List<SkuDetails> list) {
                    //process the result
                    if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK){
                        //todo: implement purchases? make sure u make a new doc, similar to register activity!
                    } else {
                        Toast.makeText(StoreActivity.this, "Error: Cannot query product", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            //todo: else...
        }
    }

    @Override
    public void onPurchasesUpdated(BillingResult billingResult, @Nullable List<Purchase> list) {
        Toast.makeText(StoreActivity.this, "Purchased Item: " + list.size(), Toast.LENGTH_SHORT).show();
    }
}
