package com.contextgenesis.perplexy.ui.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.contextgenesis.perplexy.R;
import com.contextgenesis.perplexy.databinding.DialogInappPurchasesBinding;
import com.contextgenesis.perplexy.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class BankDialog extends Dialog {

    private static final String TAG = "BankDialog";

    // Product IDs — must match what's configured in Google Play Console
    private static final String SKU_STACK  = "stack";
    private static final String SKU_PILE   = "pile";
    private static final String SKU_BAG    = "bag";
    private static final String SKU_CHEST  = "chest";
    private static final String SKU_VAULT  = "vault";

    private DialogInappPurchasesBinding binding;
    private BillingClient billingClient;
    private List<ProductDetails> productDetailsList = new ArrayList<>();

    public BankDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        binding = DialogInappPurchasesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.cardviewStackOfCoins.setOnClickListener(v -> launchPurchase(SKU_STACK));
        binding.cardviewPileOfCoins.setOnClickListener(v ->  launchPurchase(SKU_PILE));
        binding.cardviewBagOfCoins.setOnClickListener(v ->   launchPurchase(SKU_BAG));
        binding.cardviewChestOfCoins.setOnClickListener(v -> launchPurchase(SKU_CHEST));
        binding.cardviewVaultOfCoins.setOnClickListener(v -> launchPurchase(SKU_VAULT));

        setupBillingClient();
    }

    private void setupBillingClient() {
        PurchasesUpdatedListener purchasesUpdatedListener = (billingResult, purchases) -> {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK
                    && purchases != null) {
                for (Purchase purchase : purchases) {
                    handlePurchase(purchase);
                }
            } else {
                Log.w(TAG, "Purchase update: " + billingResult.getDebugMessage());
            }
        };

        billingClient = BillingClient.newBuilder(getContext())
                .setListener(purchasesUpdatedListener)
                .enablePendingPurchases()
                .build();

        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    queryProductDetails();
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                Log.w(TAG, "Billing service disconnected");
            }
        });
    }

    private void queryProductDetails() {
        List<QueryProductDetailsParams.Product> products = new ArrayList<>();
        for (String sku : new String[]{SKU_STACK, SKU_PILE, SKU_BAG, SKU_CHEST, SKU_VAULT}) {
            products.add(QueryProductDetailsParams.Product.newBuilder()
                    .setProductId(sku)
                    .setProductType(BillingClient.ProductType.INAPP)
                    .build());
        }
        QueryProductDetailsParams params = QueryProductDetailsParams.newBuilder()
                .setProductList(products)
                .build();

        billingClient.queryProductDetailsAsync(params, (billingResult, productDetailsList) -> {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                this.productDetailsList = productDetailsList;
            }
        });
    }

    private void launchPurchase(String productId) {
        ProductDetails details = findProductDetails(productId);
        if (details == null || !(getContext() instanceof Activity)) {
            Toast.makeText(getContext(), "Unable to start purchase", Toast.LENGTH_SHORT).show();
            return;
        }
        List<BillingFlowParams.ProductDetailsParams> productDetailsParamsList = new ArrayList<>();
        productDetailsParamsList.add(
                BillingFlowParams.ProductDetailsParams.newBuilder()
                        .setProductDetails(details)
                        .build());
        BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(productDetailsParamsList)
                .build();
        billingClient.launchBillingFlow((Activity) getContext(), flowParams);
    }

    private void handlePurchase(Purchase purchase) {
        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
            // Consume the purchase so it can be bought again
            ConsumeParams consumeParams = ConsumeParams.newBuilder()
                    .setPurchaseToken(purchase.getPurchaseToken())
                    .build();
            billingClient.consumeAsync(consumeParams, (billingResult, purchaseToken) -> {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    // TODO: Grant coins to the user based on which product was purchased
                    Toast.makeText(getContext(), "Coins added successfully", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private ProductDetails findProductDetails(String productId) {
        for (ProductDetails d : productDetailsList) {
            if (d.getProductId().equals(productId)) return d;
        }
        return null;
    }

    @Override
    public void dismiss() {
        if (billingClient != null && billingClient.isReady()) {
            billingClient.endConnection();
        }
        super.dismiss();
    }
}
