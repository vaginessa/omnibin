package com.f0x1d.dogbin.ui.fragment.settings;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;
import androidx.preference.EditTextPreference;
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

public class SettingsFragment extends PreferenceFragmentCompat {

    private Preference mDonatePreference;

    private SwitchPreferenceCompat mDarkModePreference;
    private ListPreference mAccentPreference;

    private Preference mUsernamePreference;
    private Preference mLoginPreference;

    private Preference mServicePreference;
    private boolean mAskedServiceDialog = false;

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

        boolean loggedIn = BinServiceUtils.getCurrentActiveService().loggedIn();

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
            if (BinServiceUtils.getCurrentActiveService().loggedIn()) {
                BinServiceUtils.getCurrentActiveService().logout();

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
            mAskedServiceDialog = true;
            BinServiceUtils.refreshInstalledServices();
            return false;
        });

        mClearCachePreference = findPreference("cache_nuke");
        mClearCachePreference.setOnPreferenceClickListener(preference -> {
            Utils.getExecutor().execute(() -> App.getMyDatabase().getDogbinSavedNoteDao().nukeTable());
            return false;
        });

        mSettingsViewModel.getUsernameData().observe(this, username -> {
            if (username == null)
                return;

            mUsernamePreference.setTitle(username);
        });

        BinServiceUtils.getInstalledServicesData().observe(this, services -> {
            if (!mAskedServiceDialog)
                return;

            mAskedServiceDialog = false;

            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle(R.string.select_service)
                    .setSingleChoiceItems(Utils.getInstalledServices(services), Utils.getSelectedService(services), (dialog, which) -> {
                        switch (which) {
                            case 0:
                                App.getPreferencesUtil().setSelectedService(BinServiceUtils.PASTEBIN_SERVICE);
                                break;
                            case 1:
                                App.getPreferencesUtil().setSelectedService(BinServiceUtils.FOXBIN_SERVICE);
                                break;
                            default:
                                App.getPreferencesUtil().setSelectedService(services.get(which - BinServiceUtils.IMPLEMENTED_SERVICES.length).packageName);
                        }
                        BinServiceUtils.refreshCurrentService();

                        requireActivity().finish();
                        startActivity(new Intent(requireContext(), MainActivity.class));
                    })
                    .show();
        });
    }
}
