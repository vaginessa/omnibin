package com.f0x1d.dogbin.ui.fragment.settings;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;
import com.f0x1d.dogbin.App;
import com.f0x1d.dogbin.R;
import com.f0x1d.dogbin.billing.BillingManager;
import com.f0x1d.dogbin.ui.activity.DonateActivity;
import com.f0x1d.dogbin.ui.activity.LoginActivity;
import com.f0x1d.dogbin.ui.activity.MainActivity;
import com.f0x1d.dogbin.utils.PreferencesUtils;
import com.f0x1d.dogbin.utils.ThreadingUtils;
import com.f0x1d.dogbin.utils.services.BinServiceUtils;
import com.f0x1d.dogbin.utils.services.ServicesUtils;
import com.f0x1d.dogbin.viewmodel.SettingsViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;

public class SettingsFragment extends PreferenceFragmentCompat {

    private SettingsViewModel mSettingsViewModel;

    private Preference mDonatePreference;

    private SwitchPreferenceCompat mDarkModePreference;
    private ListPreference mAccentPreference;
    private Preference mTextInputTypePreference;

    private Preference mUsernamePreference;
    private Preference mLoginPreference;

    private Preference mServicePreference;

    private Preference mClearCachePreference;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        mSettingsViewModel = new ViewModelProvider(this).get(SettingsViewModel.class);

        addPreferencesFromResource(R.xml.settings);

        mDonatePreference = findPreference("donate");
        mDonatePreference.setOnPreferenceClickListener(preference -> {
            startActivity(new Intent(requireContext(), DonateActivity.class));
            return false;
        });

        mDarkModePreference = findPreference(PreferencesUtils.DARK_THEME_NAME);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            mDarkModePreference.setEnabled(false);
            mDarkModePreference.setSummary(R.string.dark_theme_q);
        }
        mDarkModePreference.setOnPreferenceChangeListener((preference, newValue) -> {
            requireActivity().recreate();
            return true;
        });

        mAccentPreference = findPreference(PreferencesUtils.ACCENT_NAME);
        BillingManager.getInstance(requireContext()).getDonatedData().observe(this, donationStatus -> {
            switch (donationStatus) {
                case NOT_CONNECTED_BILLING:
                case NOT_DONATED:
                case PENDING:
                    mAccentPreference.setSummary(R.string.donate_text_theme_lock);
                    mAccentPreference.setEnabled(false);
                    break;
                case DONATED:
                    mAccentPreference.setSummary("");
                    mAccentPreference.setEnabled(true);
                    break;
            }
        });
        mAccentPreference.setOnPreferenceChangeListener((preference, newValue) -> {
            requireActivity().recreate();
            return true;
        });

        mTextInputTypePreference = findPreference("text_input_type");
        mTextInputTypePreference.setOnPreferenceClickListener(preference -> {
            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle(R.string.text_input_type)
                    .setSingleChoiceItems(new CharSequence[]{"EditText", "KodeView"}, App.getPreferencesUtil().textInputType(), (dialog, which) -> {
                        App.getPreferencesUtil().setTextInputType(which);
                        dialog.cancel();
                    })
                    .setPositiveButton(android.R.string.cancel, null)
                    .show();
            return false;
        });

        mUsernamePreference = findPreference("username");

        mLoginPreference = findPreference("log_in");
        mLoginPreference.setOnPreferenceClickListener(preference -> {
            if (mSettingsViewModel.getLoggedInData().getValue() == Boolean.TRUE) {
                mSettingsViewModel.logout();

                requireActivity().finish();
                startActivity(new Intent(requireActivity(), MainActivity.class));
            } else {
                requireActivity().finish();
                startActivity(new Intent(requireActivity(), LoginActivity.class));
            }
            return true;
        });

        mServicePreference = findPreference("select_service");
        mServicePreference.setOnPreferenceClickListener(preference -> {
            List<ApplicationInfo> services = BinServiceUtils.getInstalledServices();

            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle(R.string.select_service)
                    .setSingleChoiceItems(ServicesUtils.getInstalledServices(services), ServicesUtils.getSelectedService(services), (dialog, which) -> {
                        ServicesUtils.switchService(which, services);
                        dialog.dismiss();
                    })
                    .setPositiveButton(android.R.string.cancel, null)
                    .show();
            return true;
        });

        mClearCachePreference = findPreference("cache_nuke");
        mClearCachePreference.setOnPreferenceClickListener(preference -> {
            ThreadingUtils.getExecutor().execute(() -> {
                App.getMyDatabase().getPastebinSavedNoteDao().nukeTable();
                App.getMyDatabase().getFoxBinSavedNoteDao().nukeTable();
            });
            return true;
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mSettingsViewModel.getLoggedInData().observe(getViewLifecycleOwner(), loggedIn -> {
            mLoginPreference.setTitle(loggedIn ? R.string.log_out : R.string.log_in);
            mUsernamePreference.setVisible(loggedIn);
        });

        mSettingsViewModel.getUsernameData().observe(getViewLifecycleOwner(), username -> mUsernamePreference.setTitle(username));

        BinServiceUtils.getInstanceData().observe(getViewLifecycleOwner(), service -> mServicePreference.setSummary(service.getServiceFullName()));
    }
}
