package com.f0x1d.dogbin.ui.activity.text;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.f0x1d.dogbin.App;
import com.f0x1d.dogbin.R;
import com.f0x1d.dogbin.db.entity.MyNote;
import com.f0x1d.dogbin.ui.activity.base.BaseActivity;
import com.f0x1d.dogbin.viewmodel.WritingViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import de.markusressel.kodeeditor.library.view.CodeEditorLayout;

public class DogBinTextEditActivity extends BaseActivity {

    public static final String ACTION_UPLOAD_TO_DOGBIN = "com.f0x1d.dogbin.ACTION_UPLOAD_TO_DOGBIN";

    private EditText mSlugText;
    private CodeEditorLayout mWritebarText;
    private FloatingActionButton mDoneButton;

    private WritingViewModel mWritingViewModel;
    private Executor mExecutor = Executors.newSingleThreadExecutor();

    private boolean mEditingMode;
    private String mSlug;
    private String mTextFromIntent;
    private boolean mIntentToPost = false;
    private boolean mIntentToCopy = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_edit);

        mEditingMode = getIntent().getBooleanExtra("edit", false);
        mSlug = getIntent().getStringExtra("slug");
        mTextFromIntent = getIntent().getStringExtra(Intent.EXTRA_TEXT);
        mIntentToCopy = getIntent().getBooleanExtra("copy", true);
        mIntentToPost = !mEditingMode && mTextFromIntent != null && getIntent().getAction() != null &&
                (getIntent().getAction().equals(ACTION_UPLOAD_TO_DOGBIN) || getIntent().getAction().equals(Intent.ACTION_SEND));

        mWritingViewModel = new ViewModelProvider(this).get(WritingViewModel.class);

        mDoneButton = findViewById(R.id.done_button);
        mWritebarText = findViewById(R.id.writebar_text);
        mSlugText = findViewById(R.id.slug_text);

        mWritebarText.setShowDivider(false);

        if (mEditingMode) {
            mSlugText.setText(mSlug);
            mSlugText.setEnabled(false);

            mWritebarText.setText(mTextFromIntent);
        }

        mWritingViewModel.getLoadingState().observe(this, loadingState -> {
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

        mWritingViewModel.getDocumentResponse().observe(this, document -> {
            if (document == null) {
                mSlugText.setError(getString(R.string.invalid_link));
                return;
            }

            if (!mEditingMode) {
                mWritebarText.setText("");
                mSlugText.setText("");

                String delDogUrl = "https://del.dog/" + document.slug;

                if (mIntentToCopy) {
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText(getString(R.string.app_name), delDogUrl);
                    clipboard.setPrimaryClip(clip);

                    Toast.makeText(this, getString(R.string.copied_to_clipboard, delDogUrl), Toast.LENGTH_SHORT).show();
                }

                mExecutor.execute(() -> addRecordToDB(document.slug));

                if (mIntentToPost) {
                    setResult(Activity.RESULT_OK, new Intent().setData(Uri.parse(delDogUrl)));
                    finish();
                    return;
                }

                Intent intent = new Intent(this, DogBinTextViewerActivity.class);
                intent.setData(Uri.parse(delDogUrl));
                startActivity(intent);
            } else {
                setResult(Activity.RESULT_OK);
                finish();
            }
        });

        if (mIntentToPost) {
            if (mSlug != null)
                mSlugText.setText(mSlug);

            mWritebarText.setText(mTextFromIntent);
            mWritingViewModel.publish(mTextFromIntent, mSlug == null ? "" : mSlug);
        }

        mDoneButton.setOnClickListener(v ->
                mWritingViewModel.publish(mWritebarText.getText(), mSlug == null ? mSlugText.getText().toString() : mSlug));
    }

    private void addRecordToDB(String slug) {
        MyNote myNote = new MyNote();
        myNote.slug = slug;

        Date date = new Date(System.currentTimeMillis());
        DateFormat formatter = new SimpleDateFormat("dd.MM.yy HH:mm", Locale.getDefault());
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));

        myNote.time = formatter.format(date);

        App.getMyDatabase().myNoteDao().insert(myNote);
    }
}
