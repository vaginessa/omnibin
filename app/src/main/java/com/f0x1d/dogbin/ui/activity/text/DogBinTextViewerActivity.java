package com.f0x1d.dogbin.ui.activity.text;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.lifecycle.ViewModelProvider;

import com.f0x1d.dogbin.App;
import com.f0x1d.dogbin.R;
import com.f0x1d.dogbin.ui.activity.base.BaseActivity;
import com.f0x1d.dogbin.viewmodel.DogBinTextViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.pddstudio.highlightjs.HighlightJsView;
import com.pddstudio.highlightjs.models.Theme;

public class DogBinTextViewerActivity extends BaseActivity {

    private DogBinTextViewModel mDogBinTextViewModel;

    private MaterialToolbar mToolbar;
    private HighlightJsView mTextCodeView;
    private ProgressBar mLoadingProgress;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_view);

        mDogBinTextViewModel = new ViewModelProvider(this).get(DogBinTextViewModel.class);
        mDogBinTextViewModel.load(getIntent().getData().toString().split("/")[3]);
        mDogBinTextViewModel.loadEditable(mDogBinTextViewModel.getSlug());

        mToolbar = findViewById(R.id.toolbar);
        mTextCodeView = findViewById(R.id.text_code_view);
        mLoadingProgress = findViewById(R.id.loading_progress);

        mToolbar.setTitle(mDogBinTextViewModel.getSlug());

        mTextCodeView.setShowLineNumbers(true);
        mTextCodeView.setZoomSupportEnabled(true);
        mTextCodeView.setTheme(isNightTheme() ? Theme.DOGBIN_NIGHT_THEME : Theme.GOOGLECODE);
        if (isNightTheme())
            mTextCodeView.setBackgroundColor(getResources().getColor(R.color.dogBinBackground));

        mTextCodeView.setOnContentHighlightedListener(mDogBinTextViewModel);

        mDogBinTextViewModel.getLoadingStateData().observe(this, loadingState -> {
            if (loadingState == null)
                return;

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

        mDogBinTextViewModel.getTextData().observe(this, text -> {
            if (text == null)
                return;

            mTextCodeView.setSource(text);
        });

        mDogBinTextViewModel.getIsRedirectData().observe(this, redirectURL -> {
            if (redirectURL == null)
                return;

            new CustomTabsIntent.Builder().build().launchUrl(this, Uri.parse(redirectURL));
            finish();
        });

        mDogBinTextViewModel.getIsEditableData().observe(this, isEditable -> {
            if (isEditable) {
                mToolbar.inflateMenu(R.menu.edit_menu);
                mToolbar.getMenu().getItem(0).setOnMenuItemClickListener(item -> {
                    startActivityForResult(new Intent(DogBinTextViewerActivity.this, DogBinTextEditActivity.class)
                            .putExtra("slug", mDogBinTextViewModel.getSlug())
                            .putExtra(Intent.EXTRA_TEXT, mDogBinTextViewModel.getTextData().getValue())
                            .putExtra("edit", true), 1);
                    return false;
                });
            }
        });

        App.getMyDatabase().getSavedNoteDao().getBySlug(mDogBinTextViewModel.getSlug()).observe(this, savedNote -> {
            if (savedNote == null)
                return;

            mTextCodeView.setSource(savedNote.getContent());
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            mDogBinTextViewModel.load(mDogBinTextViewModel.getSlug());
        }
    }
}
