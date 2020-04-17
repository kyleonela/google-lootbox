package com.demo.lootbox;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
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



    AcknowledgePurchaseResponseListener acknowledgePurchaseResponseListener;

    void handlePurchase(Purchase purchase) {
        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
            // Grant entitlement to the user.
            // Acknowledge the purchase if it hasn't already been acknowledged.
            if (!purchase.isAcknowledged()) {
                AcknowledgePurchaseParams acknowledgePurchaseParams =
                        AcknowledgePurchaseParams.newBuilder()
                                .setPurchaseToken(purchase.getPurchaseToken())
                                .build();
                billingClient.acknowledgePurchase(acknowledgePurchaseParams, acknowledgePurchaseResponseListener);
            }
        }
    }



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

        billingClient = BillingClient.newBuilder(this)
                .setListener(this)
                .enablePendingPurchases()
                .build();
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
        List<String> itemsList = new ArrayList<>();
        itemsList.add("android.test.purchased");
        itemsList.add("apple_test");
//                Arrays.asList(
//                        "Sandal", "Fork", "Spoon", "Couch", "Credit Card", "Nail Clipper",
//                        "Bed", "Paper", "Headphones", "Book", "Outlet", "Tree", "Grass", "Apple",
//                        "Banana", "Pear", "Pen", "Pencil")
        Toast.makeText(StoreActivity.this, "buying" + itemsList, Toast.LENGTH_SHORT).show();


        if(billingClient.isReady()){
            SkuDetailsParams skuDetailsParams = SkuDetailsParams.newBuilder()
                    .setSkusList(itemsList).setType(BillingClient.SkuType.INAPP).build();
            billingClient.querySkuDetailsAsync(skuDetailsParams,
                    new SkuDetailsResponseListener(){
                @Override
                public void onSkuDetailsResponse(BillingResult billingResult, List<SkuDetails> list) {
                    //process the result
                    BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                            .setSkuDetails(list.get(0))
                            .build();
                    billingClient.launchBillingFlow(StoreActivity.this, flowParams);

                    if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && list != null){
                        Toast.makeText(StoreActivity.this, "buying", Toast.LENGTH_SHORT).show();

                        for (SkuDetails skuDetails : list) {
                            String sku = skuDetails.getSku();
                            Toast.makeText(StoreActivity.this, "buying"+sku, Toast.LENGTH_SHORT).show();
                            if ("apple_test".equals(sku)) {
                                Toast.makeText(StoreActivity.this, "buying", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        Toast.makeText(StoreActivity.this, "Error: Cannot query product", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(StoreActivity.this, "Error: The billing client is not ready!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPurchasesUpdated(BillingResult billingResult, @Nullable List<Purchase> purchases) {
        Toast.makeText(StoreActivity.this, "Purchased Item: " + purchases.size(), Toast.LENGTH_SHORT).show();
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK
                && purchases != null) {
            for (Purchase purchase : purchases) {
                handlePurchase(purchase);
            }

    } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
        // Handle an error caused by a user cancelling the purchase flow.
    } else {
        // Handle any other error codes.
    }
    }



}
