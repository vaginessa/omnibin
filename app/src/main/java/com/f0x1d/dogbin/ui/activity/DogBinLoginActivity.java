package com.f0x1d.dogbin.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.f0x1d.dogbin.R;
import com.f0x1d.dogbin.ui.activity.base.BaseActivity;
import com.f0x1d.dogbin.viewmodel.DogBinLoginViewModel;
import com.google.android.material.button.MaterialButton;

public class DogBinLoginActivity extends BaseActivity {

    private EditText mLoginText;
    private EditText mPasswordText;
    private MaterialButton mLoginButton;
    private MaterialButton mRegisterButton;

    private DogBinLoginViewModel mDogBinLoginViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mDogBinLoginViewModel = new ViewModelProvider(this).get(DogBinLoginViewModel.class);

        mLoginText = findViewById(R.id.login_text);
        mPasswordText = findViewById(R.id.password_text);
        mLoginButton = findViewById(R.id.login_button);
        mRegisterButton = findViewById(R.id.register_button);

        mDogBinLoginViewModel.getLoadingStateData().observe(this, loadingState -> {
            if (loadingState == null)
                return;

            switch (loadingState) {
                case LOADING:
                    mLoginText.setEnabled(false);
                    mPasswordText.setEnabled(false);
                    mLoginButton.setEnabled(false);
                    break;
                case LOADED:
                    mLoginText.setEnabled(true);
                    mPasswordText.setEnabled(true);
                    mLoginButton.setEnabled(true);
                    break;
            }
        });

        mDogBinLoginViewModel.getLoggedInData().observe(this, loggedIn -> {
            if (loggedIn) {
                startActivity(new Intent(DogBinLoginActivity.this, MainActivity.class));
                finish();
            } else {
                Toast.makeText(DogBinLoginActivity.this, R.string.invalid_login_or_password, Toast.LENGTH_SHORT).show();
            }
        });

        mLoginButton.setOnClickListener(v -> mDogBinLoginViewModel.login(mLoginText.getText().toString(), mPasswordText.getText().toString()));
        mRegisterButton.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://del.dog/register"))));
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(DogBinLoginActivity.this, MainActivity.class));
        finish();
    }
}
