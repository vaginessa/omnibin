package com.f0x1d.dogbin.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import com.f0x1d.dogbin.R;
import com.f0x1d.dogbin.billing.BillingManager;
import com.f0x1d.dogbin.ui.activity.base.BaseActivity;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.imageview.ShapeableImageView;

public class DonateActivity extends BaseActivity<AndroidViewModel> {

    private BillingManager mBillingManager;

    private MaterialToolbar mToolbar;
    private ShapeableImageView mF0x1dIcon;
    private MaterialCardView mDonateCard;
    private TextView mDonationStatusText;
    private ShapeableImageView mCatIcon;
    private MaterialButton mDonateButton;
    private MaterialButton mF0x1dDonateButton;

    @Override
    protected Class<AndroidViewModel> viewModel() {
        return null;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donate);

        mBillingManager = BillingManager.getInstance(this);

        mToolbar = findViewById(R.id.toolbar);
        mToolbar.setTitle(R.string.donate);
        mToolbar.setNavigationOnClickListener(v -> onBackPressed());

        mF0x1dIcon = findViewById(R.id.f0x1d_icon);

        mDonateCard = findViewById(R.id.donate_card);
        mDonationStatusText = findViewById(R.id.donate_status_text);
        mCatIcon = findViewById(R.id.cat_icon);
        mDonateButton = findViewById(R.id.donate_button);

        mF0x1dDonateButton = findViewById(R.id.f0x1d_donate_button);

        mF0x1dIcon.setShapeAppearanceModel(
                mF0x1dIcon
                        .getShapeAppearanceModel()
                        .withCornerSize(20f)
        );

        mF0x1dDonateButton.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://github.com/F0x1d"))));
        mDonateButton.setOnClickListener(v -> mBillingManager.launchBillingFlow(this));

        mBillingManager.getDonatedData().observe(this, donationStatus -> {
            switch (donationStatus) {
                case NOT_CONNECTED_BILLING:
                    mCatIcon.setImageResource(R.drawable.ic_cat_sad);
                    mDonationStatusText.setText(R.string.donate_text_error);
                    mDonateButton.setEnabled(false);
                    break;
                case DONATED:
                    mCatIcon.setImageResource(R.drawable.ic_cat_happy);
                    mDonationStatusText.setText(R.string.donate_text_donated);
                    mDonateButton.setVisibility(View.GONE);
                    break;
                case PENDING:
                    mCatIcon.setImageResource(R.drawable.ic_cat_happy);
                    mDonationStatusText.setText(R.string.donate_text_pending);
                    mDonateButton.setEnabled(false);
                    break;
                case NOT_DONATED:
                    mCatIcon.setImageResource(R.drawable.ic_cat_sad);
                    mDonationStatusText.setText(R.string.donate_text);
                    mDonateButton.setEnabled(true);
                    break;
            }
        });

        mBillingManager.getLoadedSkuDetailsData().observe(this, loaded -> mDonateCard.setVisibility(loaded ? View.VISIBLE : View.INVISIBLE));
    }
}
