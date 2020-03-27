package com.f0x1d.dogbin.ui.fragment.settings;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.f0x1d.dogbin.App;
import com.f0x1d.dogbin.R;
import com.f0x1d.dogbin.network.retrofit.DogBinApi;
import com.f0x1d.dogbin.ui.activity.DogBinLoginActivity;
import com.f0x1d.dogbin.ui.activity.MainActivity;
import com.f0x1d.dogbin.utils.PreferencesUtils;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class SettingsFragment extends PreferenceFragmentCompat {

    private SwitchPreferenceCompat mDarkModePreference;

    private Preference mLoginPreference;

    private SwitchPreferenceCompat mProxySwitch;
    private Preference mProxyPreference;

    private Preference mClearCachePreference;

    private Executor mExecutor = Executors.newSingleThreadExecutor();

    public static SettingsFragment newInstance() {
        Bundle args = new Bundle();

        SettingsFragment fragment = new SettingsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.settings);

        mDarkModePreference = findPreference(PreferencesUtils.DARK_THEME_NAME);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            mDarkModePreference.setEnabled(false);
            mDarkModePreference.setSummary(R.string.dark_theme_q);
        }
        mDarkModePreference.setOnPreferenceChangeListener((preference, newValue) -> {
            requireActivity().recreate();
            return true;
        });

        mLoginPreference = findPreference("log_in");
        if (DogBinApi.getInstance().getCookieJar().isDoggyClientCookieSaved())
            mLoginPreference.setTitle(R.string.log_out);
        mLoginPreference.setOnPreferenceClickListener(preference -> {
            if (DogBinApi.getInstance().getCookieJar().isDoggyClientCookieSaved()) {
                DogBinApi.getInstance().getCookieJar().clear();

                startActivity(new Intent(requireActivity(), MainActivity.class));
                requireActivity().finish();
            } else {
                startActivity(new Intent(requireActivity(), DogBinLoginActivity.class));
                requireActivity().finish();
            }
            return false;
        });

        mProxySwitch = findPreference(PreferencesUtils.PROXY_NAME);
        mProxySwitch.setOnPreferenceChangeListener((preference, newValue) -> {
            if (App.getPrefsUtil().getProxyHost() == null) {
                Toast.makeText(requireContext(), R.string.you_need_to_setup_proxy, Toast.LENGTH_SHORT).show();
                return false;
            }
            return true;
        });

        mProxyPreference = findPreference("proxy");
        mProxyPreference.setOnPreferenceClickListener(preference -> {
            showProxyDialog();
            return false;
        });

        mClearCachePreference = findPreference("cache_nuke");
        mClearCachePreference.setOnPreferenceClickListener(preference -> {
            mExecutor.execute(() -> App.getMyDatabase().getSavedNoteDao().nukeTable());
            return false;
        });
    }

    private void showProxyDialog() {
        View v = getLayoutInflater().inflate(R.layout.dialog_proxy, null);

        EditText hostText = v.findViewById(R.id.host_text);
        EditText portText = v.findViewById(R.id.port_text);
        EditText loginText = v.findViewById(R.id.login_text);
        EditText passwordText = v.findViewById(R.id.password_text);
        MaterialCheckBox authBox = v.findViewById(R.id.auth_box);

        hostText.setText(App.getPrefsUtil().getProxyHost());
        portText.setText(String.valueOf(App.getPrefsUtil().getProxyPort()));
        loginText.setText(App.getPrefsUtil().getProxyLogin());
        passwordText.setText(App.getPrefsUtil().getProxyPassword());

        TextInputLayout loginTextLayout = v.findViewById(R.id.login_text_layout);
        TextInputLayout passwordTextLayout = v.findViewById(R.id.password_text_layout);

        authBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            loginTextLayout.setEnabled(isChecked);
            passwordTextLayout.setEnabled(isChecked);
        });

        if (!loginText.getText().toString().isEmpty() && !passwordText.getText().toString().isEmpty())
            authBox.setChecked(true);

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.socks_proxy)
                .setView(v)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    App.getPrefsUtil().saveProxy(
                            hostText.getText().toString(),
                            Integer.parseInt(portText.getText().toString()),
                            loginText.getText().toString(),
                            passwordText.getText().toString()
                    );

                    mProxySwitch.setChecked(true);
                })
                .show();
    }
}
