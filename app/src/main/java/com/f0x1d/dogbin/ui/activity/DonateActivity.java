package com.f0x1d.dogbin.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.f0x1d.dogbin.R;
import com.f0x1d.dogbin.billing.BillingManager;
import com.f0x1d.dogbin.ui.activity.base.BaseActivity;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;

public class DonateActivity extends BaseActivity {

    private BillingManager mBillingManager;

    private MaterialToolbar mToolbar;
    private ShapeableImageView mF0x1dIcon;
    private ShapeableImageView mTillIcon;

    private TextView mDonationStatusText;

    private ViewGroup mF0x1dDonateButtonLayout;
    private MaterialButton mF0x1dDonateButton;
    private ViewGroup mMainDonateButtonLayout;
    private MaterialButton mMainDonateButton;
    private MaterialButton mTillDonateButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donate);

        mBillingManager = BillingManager.getInstance(this);

        mToolbar = findViewById(R.id.toolbar);
        mToolbar.setTitle(R.string.donate);

        mF0x1dIcon = findViewById(R.id.f0x1d_icon);
        mTillIcon = findViewById(R.id.till_icon);

        mDonationStatusText = findViewById(R.id.donate_status_text);

        mF0x1dDonateButtonLayout = findViewById(R.id.f0x1d_donate_button_layout);
        mF0x1dDonateButton = findViewById(R.id.f0x1d_donate_button);
        mMainDonateButtonLayout = findViewById(R.id.donate_button_layout);
        mMainDonateButton = findViewById(R.id.donate_button);
        mTillDonateButton = findViewById(R.id.till_donate_button);

        mF0x1dIcon.setShapeAppearanceModel(
                mF0x1dIcon
                        .getShapeAppearanceModel()
                        .withCornerSize(20f)
        );
        mTillIcon.setShapeAppearanceModel(
                mTillIcon
                        .getShapeAppearanceModel()
                        .withCornerSize(20f)
        );

        View.OnClickListener donateClickListener = v -> mBillingManager.launchBillingFlow(this);
        mF0x1dDonateButton.setOnClickListener(donateClickListener);
        mMainDonateButton.setOnClickListener(donateClickListener);
        mTillDonateButton.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://github.com/deletescape"))));

        mBillingManager.getDonatedData().observe(this, donationStatus -> {
            switch (donationStatus) {
                case NOT_CONNECTED_BILLING:
                    mDonationStatusText.setText(R.string.donate_text_error);
                    mMainDonateButton.setEnabled(false);
                    mF0x1dDonateButton.setEnabled(false);
                    break;
                case DONATED:
                    mDonationStatusText.setText(R.string.donate_text_donated);
                    mMainDonateButton.setVisibility(View.GONE);
                    mF0x1dDonateButton.setEnabled(false);
                    break;
                case PENDING:
                    mDonationStatusText.setText(R.string.donate_text_pending);
                    mMainDonateButton.setEnabled(false);
                    mF0x1dDonateButton.setEnabled(false);
                    break;
                case NOT_DONATED:
                    mDonationStatusText.setText(R.string.donate_text);
                    mMainDonateButton.setEnabled(true);
                    mF0x1dDonateButton.setEnabled(true);
                    break;
            }
        });

        mBillingManager.getLoadedSkuDetailsData().observe(this, loaded -> {
            mF0x1dDonateButtonLayout.setVisibility(loaded ? View.VISIBLE : View.GONE);
            mMainDonateButtonLayout.setVisibility(loaded ? View.VISIBLE : View.GONE);
        });
    }
}
