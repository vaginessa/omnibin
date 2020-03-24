package com.f0x1d.dogbin.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.f0x1d.dogbin.R;
import com.f0x1d.dogbin.ui.activity.base.BaseActivity;
import com.f0x1d.dogbin.viewmodel.DogBinLoginViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

public class DogBinLoginActivity extends BaseActivity {

    private EditText mLoginText;
    private EditText mPasswordText;
    private ExtendedFloatingActionButton mLoginButton;
    private MaterialButton mSwitchStateButton;

    private DogBinLoginViewModel mDogBinLoginViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mDogBinLoginViewModel = new ViewModelProvider(this).get(DogBinLoginViewModel.class);

        mLoginText = findViewById(R.id.login_text);
        mPasswordText = findViewById(R.id.password_text);
        mLoginButton = findViewById(R.id.login_button);
        mSwitchStateButton = findViewById(R.id.switch_state_button);

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

        mDogBinLoginViewModel.getRegisteredData().observe(this, isRegistered -> {
            if (isRegistered) {
                startActivity(new Intent(DogBinLoginActivity.this, MainActivity.class));
                finish();
            } else {
                Toast.makeText(DogBinLoginActivity.this, R.string.error_during_register, Toast.LENGTH_LONG).show();
            }
        });

        View.OnClickListener loginClickListener = v -> mDogBinLoginViewModel.login(mLoginText.getText().toString(), mPasswordText.getText().toString());
        View.OnClickListener registerClickListener = v -> mDogBinLoginViewModel.register(mLoginText.getText().toString(), mPasswordText.getText().toString());

        mDogBinLoginViewModel.getIsInLoginModeData().observe(this, inLoginMode -> {
            if (inLoginMode) {
                mSwitchStateButton.setText(R.string.register);

                mLoginButton.setText(R.string.log_in);
                mLoginButton.setIconResource(R.drawable.ic_login);

                mLoginButton.setOnClickListener(loginClickListener);
            } else {
                mSwitchStateButton.setText(R.string.log_in);

                mLoginButton.setText(R.string.register);
                mLoginButton.setIconResource(R.drawable.ic_register);

                mLoginButton.setOnClickListener(registerClickListener);
            }
        });

        mSwitchStateButton.setOnClickListener(v -> mDogBinLoginViewModel.switchMode());
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(DogBinLoginActivity.this, MainActivity.class));
        finish();
    }
}
