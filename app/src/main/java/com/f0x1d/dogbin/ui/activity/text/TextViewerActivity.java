package com.f0x1d.dogbin.ui.activity.text;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import androidx.annotation.Nullable;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.lifecycle.ViewModelProvider;
import com.f0x1d.dogbin.App;
import com.f0x1d.dogbin.R;
import com.f0x1d.dogbin.billing.BillingManager;
import com.f0x1d.dogbin.billing.DonationStatus;
import com.f0x1d.dogbin.ui.activity.DonateActivity;
import com.f0x1d.dogbin.ui.activity.base.BaseActivity;
import com.f0x1d.dogbin.ui.view.CoolCodeView;
import com.f0x1d.dogbin.utils.AndroidUtils;
import com.f0x1d.dogbin.viewmodel.TextViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class TextViewerActivity extends BaseActivity<TextViewModel> {

    public static final String ACTION_TEXT_VIEW = "com.f0x1d.dogbin.ACTION_TEXT_VIEW";

    private MaterialToolbar mToolbar;
    private CoolCodeView mTextCodeView;
    private ProgressBar mLoadingProgress;

    @Override
    protected Class<TextViewModel> viewModel() {
        return TextViewModel.class;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_view);

        mToolbar = findViewById(R.id.toolbar);
        mToolbar.setNavigationOnClickListener(v -> onBackPressed());
        mTextCodeView = findViewById(R.id.text_code_view);
        mLoadingProgress = findViewById(R.id.loading_progress);

        mTextCodeView.setWrapText(App.getPreferencesUtil().textWrap());

        setupToolbar();

        mViewModel.getLoadingStateData().observe(this, loadingState -> {
            switch (loadingState) {
                case LOADING:
                    mLoadingProgress.setVisibility(View.VISIBLE);
                    mTextCodeView.setVisibility(View.GONE);
                    break;
                case LOADED:
                    mLoadingProgress.setVisibility(View.GONE);
                    mTextCodeView.setVisibility(View.VISIBLE);
                    break;
            }
        });

        mViewModel.getTextData().observe(this, text -> mTextCodeView.setText(text));
        mViewModel.getIsEditableData().observe(this, isEditable -> mToolbar.getMenu().findItem(R.id.edit_item).setVisible(isEditable));

        mViewModel.getEventsData().observe(this, event -> {
            if (event.isConsumed()) return;

            if (event.type().equals(TextViewModel.EVENT_TYPE_REDIRECT)) {
                new CustomTabsIntent.Builder().build().launchUrl(this, Uri.parse(event.consume()));
                finish();
            }
        });

        supportApp();
    }

    private void setupToolbar() {
        mViewModel.getSlugData().observe(this, slug -> mToolbar.setTitle(slug));

        mToolbar.inflateMenu(R.menu.text_viewer_menu);

        MenuItem editItem = mToolbar.getMenu().findItem(R.id.edit_item);
        editItem.setOnMenuItemClickListener(item -> {
            startActivityForResult(new Intent(TextViewerActivity.this, TextEditActivity.class)
                    .putExtra("slug", mViewModel.getSlugData().getValue())
                    .putExtra(Intent.EXTRA_TEXT, mViewModel.getTextData().getValue())
                    .putExtra("edit", true), 1);
            return true;
        });
        editItem.setVisible(false);

        MenuItem textWrapItem = mToolbar.getMenu().findItem(R.id.text_wrap_item);
        textWrapItem.setOnMenuItemClickListener(item -> {
            boolean checked = item.isChecked();

            mTextCodeView.setWrapText(!checked);
            App.getPreferencesUtil().setTextWrap(!checked);

            item.setChecked(!checked);
            return true;
        });
        textWrapItem.setChecked(App.getPreferencesUtil().textWrap());
    }

    private void supportApp() {
        if (!App.getPreferencesUtil().supportAppShowed() && BillingManager.getInstance(this).getDonatedData().getValue() != DonationStatus.DONATED) {
            new MaterialAlertDialogBuilder(this)
                    .setCancelable(false)
                    .setTitle(R.string.hey)
                    .setMessage(R.string.donate_text)
                    .setPositiveButton(R.string.donate, (dialog, which) -> {
                        startActivity(new Intent(TextViewerActivity.this, DonateActivity.class));
                        App.getPreferencesUtil().setSupportAppShowed(true);
                    })
                    .setNeutralButton(android.R.string.cancel, (dialog, which) -> {
                        dialog.cancel();
                        App.getPreferencesUtil().setSupportAppShowed(true);
                    })
                    .show();
        }
    }

    @Override
    protected ViewModelProvider.Factory buildFactory() {
        return AndroidUtils.buildViewModelFactory(() -> new TextViewModel(getApplication(), getIntent()));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            mViewModel.load();
        }
    }
}
