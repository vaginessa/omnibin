package com.f0x1d.dogbin.ui.fragment.settings;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.Build;
import android.os.Bundle;

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
import com.f0x1d.dogbin.utils.BinServiceUtils;
import com.f0x1d.dogbin.utils.PreferencesUtils;
import com.f0x1d.dogbin.utils.Utils;
import com.f0x1d.dogbin.viewmodel.SettingsViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;

public class SettingsFragment extends PreferenceFragmentCompat {

    private Preference mDonatePreference;

    private SwitchPreferenceCompat mDarkModePreference;
    private ListPreference mAccentPreference;

    private Preference mUsernamePreference;
    private Preference mLoginPreference;

    private Preference mServicePreference;

    private Preference mClearCachePreference;

    private SettingsViewModel mSettingsViewModel;

    public static SettingsFragment newInstance() {
        Bundle args = new Bundle();

        SettingsFragment fragment = new SettingsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        mSettingsViewModel = new ViewModelProvider(this).get(SettingsViewModel.class);

        boolean loggedIn = BinServiceUtils.getCurrentActiveService().auth().loggedIn();

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

        mUsernamePreference = findPreference("username");
        if (!loggedIn)
            mUsernamePreference.setVisible(false);

        mLoginPreference = findPreference("log_in");
        if (loggedIn)
            mLoginPreference.setTitle(R.string.log_out);
        mLoginPreference.setOnPreferenceClickListener(preference -> {
            if (BinServiceUtils.getCurrentActiveService().auth().loggedIn()) {
                BinServiceUtils.getCurrentActiveService().auth().logout();

                requireActivity().finish();
                startActivity(new Intent(requireActivity(), MainActivity.class));
            } else {
                requireActivity().finish();
                startActivity(new Intent(requireActivity(), LoginActivity.class));
            }
            return false;
        });

        mServicePreference = findPreference("select_service");
        mServicePreference.setOnPreferenceClickListener(preference -> {
            List<ApplicationInfo> services = BinServiceUtils.getInstalledServicesData().getValue();

            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle(R.string.select_service)
                    .setSingleChoiceItems(Utils.getInstalledServices(services), Utils.getSelectedService(services), (dialog, which) ->
                            Utils.switchService(which, services, requireActivity()))
                    .show();
            return false;
        });

        mClearCachePreference = findPreference("cache_nuke");
        mClearCachePreference.setOnPreferenceClickListener(preference -> {
            Utils.getExecutor().execute(() -> {
                App.getMyDatabase().getDogbinSavedNoteDao().nukeTable();
                App.getMyDatabase().getPastebinSavedNoteDao().nukeTable();
                App.getMyDatabase().getFoxBinSavedNoteDao().nukeTable();
            });
            return false;
        });

        mSettingsViewModel.getUsernameData().observe(this, username -> {
            if (username == null)
                return;

            mUsernamePreference.setTitle(username);
        });
    }
}
