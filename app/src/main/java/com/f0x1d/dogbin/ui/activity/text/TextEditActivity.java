package com.f0x1d.dogbin.ui.activity.text;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.f0x1d.dogbin.R;
import com.f0x1d.dogbin.ui.activity.base.BaseActivity;
import com.f0x1d.dogbin.utils.BinServiceUtils;
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

    private boolean mEditingMode;
    private String mSlug;
    private String mTextFromIntent;
    private boolean mIntentToPost = false;
    private boolean mIntentToCopy = true;

    @Override
    protected Class<WritingViewModel> viewModel() {
        return WritingViewModel.class;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_edit);

        mEditingMode = getIntent().getBooleanExtra("edit", false);
        mSlug = getIntent().getStringExtra("slug");
        mTextFromIntent = getIntent().getStringExtra(Intent.EXTRA_TEXT);
        mIntentToCopy = getIntent().getBooleanExtra("copy", true);
        mIntentToPost = !mEditingMode && mTextFromIntent != null && getIntent().getAction() != null &&
                (getIntent().getAction().equals(ACTION_UPLOAD_TO_FOXBIN) || getIntent().getAction().equals(Intent.ACTION_SEND));

        mToolbar = findViewById(R.id.toolbar);
        ((ViewGroup.MarginLayoutParams) mToolbar.getLayoutParams()).topMargin = Utils.statusBarHeight();

        mSlugText = findViewById(R.id.slug_text);
        mWritebarText = findViewById(R.id.writebar_text);
        mDoneButton = findViewById(R.id.done_button);

        mWritebarText.setShowDivider(false);
        mToolbar.setTitle(mEditingMode ? R.string.editing_note : R.string.creating_note);

        if (mEditingMode) {
            mSlugText.setText(mSlug);
            mSlugText.setEnabled(false);

            mWritebarText.setText(mTextFromIntent);
        }

        mViewModel.getLoadingStateData().observe(this, loadingState -> {
            if (loadingState == null)
                return;

            switch (loadingState) {
                case LOADING:
                    if (!mEditingMode) mSlugText.setEnabled(false);
                    mDoneButton.setEnabled(false);
                    mWritebarText.setEditable(false);
                    break;
                case LOADED:
                    if (!mEditingMode) mSlugText.setEnabled(true);
                    mDoneButton.setEnabled(true);
                    mWritebarText.setEditable(true);
                    break;
            }
        });

        mViewModel.getResultSlugData().observe(this, resultSlug -> {
            if (resultSlug == null) {
                mSlugText.setError(getString(R.string.invalid_link));
                return;
            }

            if (!mEditingMode) {
                mWritebarText.setText("");
                mSlugText.setText("");

                String resultUrl = BinServiceUtils.getCurrentActiveService().getDomain() + resultSlug;

                if (mIntentToCopy) {
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText(getString(R.string.app_name), resultUrl);
                    clipboard.setPrimaryClip(clip);

                    Toast.makeText(this, getString(R.string.copied_to_clipboard, resultUrl), Toast.LENGTH_SHORT).show();
                }

                if (mIntentToPost) {
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

        if (mIntentToPost) {
            if (mSlug != null)
                mSlugText.setText(mSlug);

            mWritebarText.setText(mTextFromIntent);
            mViewModel.publish(mTextFromIntent, mSlug == null ? "" : mSlug);
        }

        mDoneButton.setOnClickListener(v ->
                mViewModel.publish(mWritebarText.getText(), mSlug == null ? mSlugText.getText().toString() : mSlug));
    }

    @Override
    protected ViewModelProvider.Factory buildFactory() {
        return new WritingViewModel.WritingViewModelFactory(getIntent().getBooleanExtra("edit", false));
    }
}
