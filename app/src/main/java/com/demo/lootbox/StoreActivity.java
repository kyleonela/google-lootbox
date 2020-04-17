package com.demo.lootbox;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.demo.lootbox.adapters.StoreRecyclerViewAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StoreActivity extends AppCompatActivity implements PurchasesUpdatedListener {

    Button purchaseButton;
    RecyclerView storeRecyclerView;

    /* Setup Firebase Auth and Firestore */
    FirebaseAuth fbAuth;
    FirebaseFirestore firestore;

    /* Setup billing client so we can connect to google store */
    private BillingClient billingClient;

    AcknowledgePurchaseResponseListener acknowledgePurchaseResponseListener;

    String sku, itemName, itemDesc, itemPrice;
    SkuDetails skuDetails;
    String initialNames[] = {"Random Item","Random Item","Random Item","Random Item","Random Item","Random Item"};
    int images[] = {R.drawable.baseline_email_black_18dp,
            R.drawable.baseline_email_black_18dp,
            R.drawable.baseline_email_black_18dp,
            R.drawable.baseline_email_black_18dp,
            R.drawable.baseline_email_black_18dp,
            R.drawable.baseline_email_black_18dp};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);

        /* Getting current instance of database from Firebase and Firestore */
        fbAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        purchaseButton = findViewById(R.id.purchaseButton);
        storeRecyclerView = findViewById(R.id.storeRecyclerView);

        StoreRecyclerViewAdapter adapter = new StoreRecyclerViewAdapter(this, initialNames, images);
        storeRecyclerView.setAdapter(adapter);
        storeRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        establishConnection();

        purchaseButton.setOnClickListener(x -> {
            purchaseItem();
        });
    }

    /* Connect to Google Play */
    public void establishConnection() {
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

        queryInfo();
    }

    public void queryInfo() {
        // Test Items
        List<String> itemsList = new ArrayList<>();
        itemsList.add("android.test.purchased");

        if(billingClient.isReady()){
            SkuDetailsParams skuDetailsParams = SkuDetailsParams.newBuilder()
                    .setSkusList(itemsList).setType(BillingClient.SkuType.INAPP).build();
            billingClient.querySkuDetailsAsync(skuDetailsParams,
                    new SkuDetailsResponseListener(){
                        @Override
                        public void onSkuDetailsResponse(BillingResult billingResult, List<SkuDetails> list) {
                            //process the result
                            if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && list != null){
                                for (SkuDetails skuDetail : list) {
                                    skuDetails = skuDetail;
                                    sku = skuDetail.getSku();
                                    itemPrice = skuDetail.getPrice();
                                    itemName = skuDetail.getTitle();
                                    itemDesc = skuDetail.getDescription();
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

    /* Query for in-app product details */
    public void purchaseItem() {
        BillingFlowParams bfp = BillingFlowParams.newBuilder()
                .setSkuDetails(skuDetails)
                .build();
        billingClient.launchBillingFlow(StoreActivity.this, bfp);
    }

    @Override
    public void onPurchasesUpdated(BillingResult billingResult, @Nullable List<Purchase> purchases) {
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK
                && purchases != null) {
            //for every purchase
            for (Purchase purchase : purchases) {
                handlePurchase(purchase);
            }
        } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
            // Handle an error caused by a user cancelling the purchase flow.
            Toast.makeText(StoreActivity.this, "You have cancelled the purchase.", Toast.LENGTH_SHORT).show();
        } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
            Toast.makeText(StoreActivity.this, "Item already owned!", Toast.LENGTH_SHORT).show();
            purchaseButton.setEnabled(false); //disable the button
        }
        else {
            // Handle any other error codes.
            Toast.makeText(StoreActivity.this, "Error: " + billingResult.getResponseCode() + billingResult.getDebugMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void handlePurchase(Purchase purchase) {
        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
            Toast.makeText(StoreActivity.this, "You have purchased an item: " + itemName, Toast.LENGTH_SHORT).show();
            purchaseButton.setEnabled(false);
            //TODO: if it works, create a new item in the store, then write firebase code here to store it!

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

}
