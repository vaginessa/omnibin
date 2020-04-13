package com.f0x1d.dogbin.billing;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.f0x1d.dogbin.App;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BillingManager implements PurchasesUpdatedListener, BillingClientStateListener {

    public static final String SKU_DONATION_NAME = "donation";

    private static BillingManager sBillingManager;

    private Context mContext;
    private BillingClient mBillingClient;

    private MutableLiveData<DonationStatus> mDonatedData = new MutableLiveData<>(App.getPrefsUtil().getDonationStatus());

    private MutableLiveData<Boolean> mLoadedSkuDetailsData = new MutableLiveData<>(false);
    private SkuDetails mSkuDetailsProduct;

    private BillingManager(Context c) {
        mContext = c.getApplicationContext();

        if (GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(mContext) != ConnectionResult.SUCCESS) {
            mDonatedData.setValue(DonationStatus.NOT_CONNECTED_BILLING);
            return;
        }

        mBillingClient = BillingClient.newBuilder(c)
                .enablePendingPurchases()
                .setListener(this)
                .build();
        mBillingClient.startConnection(this);
    }

    public static BillingManager getInstance(Context c) {
        synchronized (BillingManager.class) {
            return sBillingManager == null ? sBillingManager = new BillingManager(c) : sBillingManager;
        }
    }

    public void loadPurchases() {
        Purchase.PurchasesResult purchasesResult = mBillingClient.queryPurchases(BillingClient.SkuType.INAPP);
        if (purchasesResult.getBillingResult().getResponseCode() != BillingClient.BillingResponseCode.OK)
            return;

        handlePurchases(purchasesResult.getPurchasesList());
    }

    @Override
    public void onPurchasesUpdated(BillingResult billingResult, @Nullable List<Purchase> list) {
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.SERVICE_DISCONNECTED) {
            mBillingClient.startConnection(this);
        } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK ||
                billingResult.getResponseCode() == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
            loadPurchases();
        }
    }

    private void handlePurchases(Collection<Purchase> purchases) {
        boolean hasDonationPurchase = false;

        for (Purchase purchase : purchases) {
            if (purchase.getPurchaseState() == Purchase.PurchaseState.UNSPECIFIED_STATE) {
                continue;
            }

            if (purchase.getSku().equals(SKU_DONATION_NAME)) {
                hasDonationPurchase = true;

                if (purchase.getPurchaseState() == Purchase.PurchaseState.PENDING) {
                    setDonationStatus(DonationStatus.PENDING);
                } else if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
                    setDonationStatus(DonationStatus.DONATED);

                    if (!purchase.isAcknowledged()) {
                        mBillingClient.acknowledgePurchase(AcknowledgePurchaseParams.newBuilder().setPurchaseToken(purchase.getPurchaseToken()).build(),
                                billingResult -> Log.d("dogbin", "Acknowledged purchase: " + billingResult.getResponseCode()));
                    }
                }
            }
        }

        if (!hasDonationPurchase)
            setDonationStatus(DonationStatus.NOT_DONATED);
    }

    public void launchBillingFlow(Activity activity) {
        if (mSkuDetailsProduct == null)
            return;

        BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                .setSkuDetails(mSkuDetailsProduct)
                .build();

        mBillingClient.launchBillingFlow(activity, billingFlowParams);
    }

    private void loadProducts() {
        List<String> skuList = new ArrayList<>();
        skuList.add(SKU_DONATION_NAME);

        SkuDetailsParams params = SkuDetailsParams.newBuilder()
                .setSkusList(skuList)
                .setType(BillingClient.SkuType.INAPP)
                .build();

        mBillingClient.querySkuDetailsAsync(params, (billingResult, skuDetailsList) -> {
            if (billingResult.getResponseCode() != BillingClient.BillingResponseCode.OK) {
                mLoadedSkuDetailsData.setValue(false);
                return;
            }

            mLoadedSkuDetailsData.setValue(true);
            mSkuDetailsProduct = skuDetailsList.get(0);
        });
    }

    @Override
    public void onBillingSetupFinished(BillingResult billingResult) {
        if (billingResult.getResponseCode() != BillingClient.BillingResponseCode.OK) {
            mDonatedData.setValue(DonationStatus.NOT_CONNECTED_BILLING);
            return;
        }

        loadProducts();
        loadPurchases();
    }

    @Override
    public void onBillingServiceDisconnected() {
        mDonatedData.setValue(DonationStatus.NOT_CONNECTED_BILLING);
        mBillingClient.startConnection(this);
    }

    private void setDonationStatus(DonationStatus status) {
        App.getPrefsUtil().setDonationStatus(status);

        mDonatedData.setValue(status);
    }

    public LiveData<DonationStatus> getDonatedData() {
        return mDonatedData;
    }

    public LiveData<Boolean> getLoadedSkuDetailsData() {
        return mLoadedSkuDetailsData;
    }
}
