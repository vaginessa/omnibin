package com.f0x1d.dogbin.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.f0x1d.dogbin.R;
import com.f0x1d.dogbin.ui.activity.base.BaseActivity;
import com.f0x1d.dogbin.viewmodel.LoginViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

public class LoginActivity extends BaseActivity {

    private EditText mLoginText;
    private EditText mPasswordText;
    private ExtendedFloatingActionButton mLoginButton;
    private MaterialButton mSwitchStateButton;

    private LoginViewModel mLoginViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mLoginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        mLoginText = findViewById(R.id.login_text);
        mPasswordText = findViewById(R.id.password_text);
        mLoginButton = findViewById(R.id.login_button);
        mSwitchStateButton = findViewById(R.id.switch_state_button);

        mLoginViewModel.getLoadingStateData().observe(this, loadingState -> {
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

        mLoginViewModel.getLoggedInData().observe(this, loggedIn -> {
            if (loggedIn) {
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            }
        });

        mLoginViewModel.getRegisteredData().observe(this, isRegistered -> {
            if (isRegistered) {
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            }
        });

        View.OnClickListener loginClickListener = v -> mLoginViewModel.login(mLoginText.getText().toString(), mPasswordText.getText().toString());
        View.OnClickListener registerClickListener = v -> mLoginViewModel.register(mLoginText.getText().toString(), mPasswordText.getText().toString());

        mLoginViewModel.getIsInLoginModeData().observe(this, inLoginMode -> {
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

        mSwitchStateButton.setOnClickListener(v -> mLoginViewModel.switchMode());
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
    }
}
