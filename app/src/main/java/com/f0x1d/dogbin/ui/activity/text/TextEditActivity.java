package com.f0x1d.dogbin.ui.activity.text;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.f0x1d.dogbin.R;
import com.f0x1d.dogbin.ui.activity.base.BaseActivity;
import com.f0x1d.dogbin.utils.Utils;
import com.f0x1d.dogbin.viewmodel.WritingViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import de.markusressel.kodeeditor.library.view.CodeEditorLayout;

public class TextEditActivity extends BaseActivity<WritingViewModel> {

    public static final String ACTION_UPLOAD_TO_FOXBIN = "com.f0x1d.dogbin.ACTION_UPLOAD_TO_FOXBIN";

    private MaterialToolbar mToolbar;
    private EditText mSlugText;
    private CodeEditorLayout mWritebarText;
    private FloatingActionButton mDoneButton;

    @Override
    protected Class<WritingViewModel> viewModel() {
        return WritingViewModel.class;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_edit);

        mToolbar = findViewById(R.id.toolbar);
        mToolbar.setNavigationOnClickListener(v -> onBackPressed());
        ((ViewGroup.MarginLayoutParams) mToolbar.getLayoutParams()).topMargin = Utils.statusBarHeight();

        mSlugText = findViewById(R.id.slug_text);
        mWritebarText = findViewById(R.id.writebar_text);
        mDoneButton = findViewById(R.id.done_button);

        mWritebarText.setShowDivider(false);
        mToolbar.setTitle(mViewModel.isInEditingMode() ? R.string.editing_note : R.string.creating_note);

        if (mViewModel.isInEditingMode()) {
            mSlugText.setText(mViewModel.getSlug());
            mSlugText.setEnabled(false);

            mWritebarText.setText(mViewModel.getTextFromIntent());
        }

        mViewModel.getLoadingStateData().observe(this, loadingState -> {
            if (loadingState == null)
                return;

            switch (loadingState) {
                case LOADING:
                    if (!mViewModel.isInEditingMode()) mSlugText.setEnabled(false);
                    mDoneButton.setEnabled(false);
                    mWritebarText.setEditable(false);
                    break;
                case LOADED:
                    if (!mViewModel.isInEditingMode()) mSlugText.setEnabled(true);
                    mDoneButton.setEnabled(true);
                    mWritebarText.setEditable(true);
                    break;
            }
        });

        mViewModel.getResultUrlData().observe(this, resultUrl -> {
            if (resultUrl == null) {
                mSlugText.setError(getString(R.string.invalid_link));
                return;
            }

            if (!mViewModel.isInEditingMode()) {
                mWritebarText.setText("");
                mSlugText.setText("");

                if (mViewModel.isIntentToPost()) {
                    setResult(Activity.RESULT_OK, new Intent().setData(Uri.parse(resultUrl)));
                    finish();
                    return;
                }

                Intent intent = new Intent(this, TextViewerActivity.class);
                intent.setData(Uri.parse(resultUrl));
                intent.putExtra("my_note", true);
                startActivity(intent);
            } else
                setResult(Activity.RESULT_OK);

            finish();
        });

        if (mViewModel.isIntentToPost()) {
            if (mViewModel.getSlug() != null)
                mSlugText.setText(mViewModel.getSlug());

            mWritebarText.setText(mViewModel.getTextFromIntent());
            mViewModel.publish(mViewModel.getTextFromIntent(), mViewModel.getSlug() == null ? "" : mViewModel.getSlug());
        }

        mDoneButton.setOnClickListener(v ->
                mViewModel.publish(mWritebarText.getText(), mViewModel.getSlug() == null ? mSlugText.getText().toString() : mViewModel.getSlug()));
    }

    @Override
    protected ViewModelProvider.Factory buildFactory() {
        return new WritingViewModel.WritingViewModelFactory(getIntent());
    }
}
