package com.contextgenesis.perplexy.ui.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.cardview.widget.CardView;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

import com.contextgenesis.perplexy.R;
import com.contextgenesis.perplexy.billingUtils.IabHelper;
import com.contextgenesis.perplexy.billingUtils.IabResult;
import com.contextgenesis.perplexy.billingUtils.Inventory;
import com.contextgenesis.perplexy.billingUtils.Purchase;
import com.contextgenesis.perplexy.ui.NumberLineActivity;
import com.contextgenesis.perplexy.utils.Constants;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BankDialog extends Dialog {

    @Bind(R.id.cardview_stack_of_coins)
    CardView stack;

    IabHelper mHelper;
    Context context;

    @OnClick(R.id.cardview_stack_of_coins)
    public void onClick_stack() {
        init("stack");
    }

    @OnClick(R.id.cardview_pile_of_coins)
    public void onClick_pile() {

    }

    @OnClick(R.id.cardview_bag_of_coins)
    public void onClick_bag() {

    }

    @OnClick(R.id.cardview_chest_of_coins)
    public void onClick_chest() {

    }

    @OnClick(R.id.cardview_vault_of_coins)
    public void onClick_vault() {

    }

    public BankDialog(Context context) {
        super(context);
        this.context = context;
    }

    public void init(final String type) {
        String base64EncodedPublicKey = Constants.base64EncodedPublicKey;
        mHelper = new IabHelper(context, base64EncodedPublicKey);

        final IabHelper.QueryInventoryFinishedListener
                mQueryFinishedListener = new IabHelper.QueryInventoryFinishedListener() {
            public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
                if (result.isFailure()) {
                    // handle error
                    return;
                }
//                if (inventory.getSkuDetails("stack") == null)
//                    Log.wtf("Query Inventory", "Not Found");
                else {
                    String type_price =
                            inventory.getSkuDetails(type).getPrice();
                    System.out.println("TYPE PRICE: " + type_price);
                }
            }
        };

        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    // Oh no, there was a problem.
                    Log.d("APP BILLING", "Problem setting up In-app Billing: " + result);
                }
                // Hooray, IAB is fully set up!
//                List itemList = new ArrayList();
//                itemList.add("stack");
//
//                try {
//                    mHelper.queryInventoryAsync(true, itemList, itemList, mQueryFinishedListener);
//                } catch (IabHelper.IabAsyncInProgressException e) {
//                    e.printStackTrace();
//                }

                makePurchase(type);
            }
        });
    }

    public void makePurchase(final String type) {
        Toast.makeText(getContext(), "Purchasing " + type + " of coins..", Toast.LENGTH_LONG).show();
        // Consumer
        final IabHelper.OnConsumeFinishedListener mConsumeFinishedListener =
                new IabHelper.OnConsumeFinishedListener() {
                    public void onConsumeFinished(Purchase purchase, IabResult result) {
                        if (result.isSuccess()) {
                            // TODO: add coins
                            switch (type) {
                                case "stack": {
                                    Toast.makeText(getContext(), "Coins added successfully", Toast.LENGTH_LONG).show();
                                    break;
                                }
                                case "pile": {
                                    Toast.makeText(getContext(), "Coins added successfully", Toast.LENGTH_LONG).show();
                                    break;
                                }
                                case "bag": {
                                    Toast.makeText(getContext(), "Coins added successfully", Toast.LENGTH_LONG).show();
                                    break;
                                }
                                case "chest": {
                                    Toast.makeText(getContext(), "Coins added successfully", Toast.LENGTH_LONG).show();
                                    break;
                                }
                                case "vault": {
                                    Toast.makeText(getContext(), "Coins added successfully", Toast.LENGTH_LONG).show();
                                    break;
                                }
                            }
                            // provision the in-app purchase to the user
                            // (for example, credit 50 gold coins to player's character)
                        } else {
                            // handle error
                        }
                    }
                };


        IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener
                = new IabHelper.OnIabPurchaseFinishedListener() {
            public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
                if (result.isFailure()) {
                    Log.d("Purchase Failed", "Error purchasing: " + result);
                    return;
                } else if (purchase.getSku().equals(type)) {
                    // consume the coins and update the UI
                    try {
                        mHelper.consumeAsync(purchase,
                                mConsumeFinishedListener);
                    } catch (IabHelper.IabAsyncInProgressException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        try {
            // For tracking online
            int requestCode = 0;
            switch (type) {
                case "stack": {
                    requestCode = 10001;
                    break;
                }
                case "pile": {
                    requestCode = 20001;
                    break;
                }
                case "bag": {
                    requestCode = 30001;
                    break;
                }
                case "chest": {
                    requestCode = 40001;
                    break;
                }
                case "vault": {
                    requestCode = 50001;
                    break;
                }
            }
            mHelper.launchPurchaseFlow((Activity) context, type, requestCode,
                    mPurchaseFinishedListener, type + " of coins");
        } catch (IabHelper.IabAsyncInProgressException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_inapp_purchases);
        ButterKnife.bind(this);

        this.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                if (mHelper != null) try {
                    mHelper.dispose();
                } catch (IabHelper.IabAsyncInProgressException e) {
                    e.printStackTrace();
                }
                mHelper = null;
            }
        });
    }

}