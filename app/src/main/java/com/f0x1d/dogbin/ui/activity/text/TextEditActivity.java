package com.f0x1d.dogbin.ui.activity.text;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import com.f0x1d.dmsdk.BinService;
import com.f0x1d.dogbin.App;
import com.f0x1d.dogbin.R;
import com.f0x1d.dogbin.ui.activity.base.BaseActivity;
import com.f0x1d.dogbin.utils.AndroidUtils;
import com.f0x1d.dogbin.utils.Event;
import com.f0x1d.dogbin.viewmodel.WritingViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import de.markusressel.kodeeditor.library.view.CodeEditorLayout;

public class TextEditActivity extends BaseActivity<WritingViewModel> {

    public static final String ACTION_UPLOAD_TO_FOXBIN = "com.f0x1d.dogbin.ACTION_UPLOAD_TO_FOXBIN";

    private MaterialToolbar mToolbar;
    private EditText mSlugText;
    private View mWritebarText;
    private FloatingActionButton mDoneButton;

    @Override
    protected Class<WritingViewModel> viewModel() {
        return WritingViewModel.class;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int layout;
        switch (App.getPreferencesUtil().textInputType()) {
            case 0:
            default:
                layout = R.layout.activity_text_edit_edittext;
                break;

            case 1:
                layout = R.layout.activity_text_edit_kodeview;
                break;
        }
        setContentView(layout);

        mToolbar = findViewById(R.id.toolbar);
        mToolbar.setNavigationOnClickListener(v -> onBackPressed());

        mSlugText = findViewById(R.id.slug_text);
        mWritebarText = findViewById(R.id.writebar_text);
        mDoneButton = findViewById(R.id.done_button);

        if (mWritebarText instanceof CodeEditorLayout)
            ((CodeEditorLayout) mWritebarText).setShowDivider(false);

        mToolbar.setTitle(mViewModel.isInEditingMode() ? R.string.editing_note : R.string.creating_note);

        if (mViewModel.getSlug() != null)
            mSlugText.setText(mViewModel.getSlug());
        if (mViewModel.isInEditingMode())
            mSlugText.setEnabled(false);
        if (mViewModel.isIntentToPost() || mViewModel.isInEditingMode())
            setText(getIntent().getStringExtra(Intent.EXTRA_TEXT));

        mDoneButton.setOnClickListener(v -> mViewModel.sendDialogEvent());

        mViewModel.getLoadingStateData().observe(this, loadingState -> {
            switch (loadingState) {
                case LOADING:
                    if (!mViewModel.isInEditingMode()) mSlugText.setEnabled(false);
                    mDoneButton.setEnabled(false);
                    setEditable(false);
                    break;
                case LOADED:
                    if (!mViewModel.isInEditingMode()) mSlugText.setEnabled(true);
                    mDoneButton.setEnabled(true);
                    setEditable(true);
                    break;
            }
        });

        mViewModel.getEventsData().observe(this, event -> {
            if (event.isConsumed()) return;

            switch (event.type()) {
                case WritingViewModel.EVENT_TYPE_POSTED:
                    posted(event.consume());
                    break;

                case WritingViewModel.EVENT_TYPE_SHOW_POSTING_DIALOG:
                    publishDialog(event.argument(0), event, getText(), mSlugText.getText().toString());
                    break;
            }
        });
    }

    private void posted(String resultUrl) {
        if (resultUrl == null) {
            mSlugText.setError(getString(R.string.invalid_link));
            return;
        }

        if (!mViewModel.isInEditingMode()) {
            setText("");
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
    }

    private void publishDialog(BinService binService, Event event, String text, String slug) {
        View dialogView = binService.ui().buildSettingsDialog(mViewModel.isInEditingMode(), getTheme());
        if (dialogView == null) {
            mViewModel.publish(text, slug, null);
            return;
        }

        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.settings)
                .setView(dialogView)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    mViewModel.publish(
                            text,
                            slug,
                            binService.ui().collectDataFromDialog(dialogView, mViewModel.isInEditingMode())
                    );
                    event.consume();
                })
                .show();
    }

    @Override
    protected ViewModelProvider.Factory buildFactory() {
        return AndroidUtils.buildViewModelFactory(() -> new WritingViewModel(getApplication(), getIntent()));
    }

    private String getText() {
        if (mWritebarText instanceof CodeEditorLayout) {
            return ((CodeEditorLayout) mWritebarText).getText();
        } else if (mWritebarText instanceof EditText) {
            return ((EditText) mWritebarText).getText().toString();
        }
        return null;
    }

    private void setEditable(boolean editable) {
        if (mWritebarText instanceof CodeEditorLayout) {
            ((CodeEditorLayout) mWritebarText).setEditable(editable);
        } else if (mWritebarText instanceof EditText) {
            ((EditText) mWritebarText).setEnabled(editable);
        }
    }

    private void setText(String text) {
        if (mWritebarText instanceof CodeEditorLayout) {
            ((CodeEditorLayout) mWritebarText).setText(text);
        } else if (mWritebarText instanceof EditText) {
            ((EditText) mWritebarText).setText(text);
        }
    }
}
