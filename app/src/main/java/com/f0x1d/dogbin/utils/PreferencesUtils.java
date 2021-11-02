package com.f0x1d.dogbin.utils;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.f0x1d.dogbin.billing.DonationStatus;
import com.pddstudio.highlightjs.HighlightJsView;

public class PreferencesUtils {

    public static final String DARK_THEME_NAME = "dark_theme";
    public static final String ACCENT_NAME = "accent";

    public static final String SUPPORT_APP_SHOWED_NAME = "support_app_dialog";
    public static final String TOASTER_SHOWED_NAME = "toaster_dialog";
    public static final String FIRST_START_NAME = "first_start";

    public static final String API_KEY_NAME = "api_key";
    public static final String USERNAME_NAME = "username";

    public static final String FOXBIN_TOKEN_NAME = "foxbin_token";
    public static final String FOXBIN_USERNAME_NAME = "foxbin_username";

    public static final String PASTEBIN_TOKEN_NAME = "pastebin_token";

    public static final String NETWORK_REDIRECT_NOTE_NAME = "note_redirect";
    public static final String DOGBIN_DOMAIN_NAME = "dogbin_domain";

    public static final String CACHE_ONLY_MY_NAME = "cache_my";

    public static final String EDITOR_TEXT_WRAP_NAME = "editor_text_wrap";

    public static final String DONATE_STATUS_NAME = "donate_status";

    public static final String SELECTED_SERVICE_NAME = "selected_service";

    private final Context mContext;
    private final SharedPreferences mDefaultPreferences;
    private final SharedPreferences mAppPreferences;

    public PreferencesUtils(Context c) {
        this.mContext = c.getApplicationContext();
        this.mDefaultPreferences = PreferenceManager.getDefaultSharedPreferences(c);
        this.mAppPreferences = c.getSharedPreferences("dogbin_settings", Context.MODE_PRIVATE);
    }

    public boolean isDarkTheme() {
        return mDefaultPreferences.getBoolean(DARK_THEME_NAME, false);
    }

    public int selectedAccent() {
        return Integer.parseInt(mDefaultPreferences.getString(ACCENT_NAME, "0"));
    }

    public boolean supportAppShowed() {
        return mAppPreferences.getBoolean(SUPPORT_APP_SHOWED_NAME, false);
    }

    public void setSupportAppShowed(boolean showed) {
        mAppPreferences.edit().putBoolean(SUPPORT_APP_SHOWED_NAME, showed).apply();
    }

    public boolean toasterShowed() {
        return mAppPreferences.getBoolean(TOASTER_SHOWED_NAME, false);
    }

    public void setToasterShowed(boolean showed) {
        mAppPreferences.edit().putBoolean(TOASTER_SHOWED_NAME, showed).apply();
    }

    public boolean isFirstStart() {
        return mAppPreferences.getBoolean(FIRST_START_NAME, true);
    }

    public void setFirstStart(boolean firstStart) {
        mAppPreferences.edit().putBoolean(FIRST_START_NAME, firstStart).apply();
    }

    public String getApiKey() {
        return mAppPreferences.getString(API_KEY_NAME, null);
    }

    public void setApiKey(String apiKey) {
        mAppPreferences.edit().putString(API_KEY_NAME, apiKey).apply();
    }

    public String getUsername() {
        return mAppPreferences.getString(USERNAME_NAME, "");
    }

    public void setUsername(String username) {
        mAppPreferences.edit().putString(USERNAME_NAME, username).apply();
    }

    public String getFoxBinToken() {
        return mAppPreferences.getString(FOXBIN_TOKEN_NAME, null);
    }

    public void setFoxBinToken(String token) {
        mAppPreferences.edit().putString(FOXBIN_TOKEN_NAME, token).apply();
    }

    public String getFoxBinUsername() {
        return mAppPreferences.getString(FOXBIN_USERNAME_NAME, "");
    }

    public void setFoxBinUsername(String username) {
        mAppPreferences.edit().putString(FOXBIN_USERNAME_NAME, username).apply();
    }

    public String getPastebinToken() {
        return mAppPreferences.getString(PASTEBIN_TOKEN_NAME, null);
    }

    public void setPastebinToken(String token) {
        mAppPreferences.edit().putString(PASTEBIN_TOKEN_NAME, token).apply();
    }

    public boolean isRedirectFromNoteEnabled() {
        return mDefaultPreferences.getBoolean(NETWORK_REDIRECT_NOTE_NAME, true);
    }

    public String getDogbinDomain() {
        return "https://del.dog/";//mDefaultPreferences.getString(DOGBIN_DOMAIN_NAME, "https://del.dog/");
    }

    public void setDogbinDomain(String domain) {
        mDefaultPreferences.edit().putString(DOGBIN_DOMAIN_NAME, domain).apply();
    }

    public boolean cacheOnlyMy() {
        return mDefaultPreferences.getBoolean(CACHE_ONLY_MY_NAME, true);
    }

    public HighlightJsView.TextWrap textWrap() {
        return HighlightJsView.TextWrap.values()[mAppPreferences.getInt(EDITOR_TEXT_WRAP_NAME, 0)];
    }

    public void setTextWrap(int ordinal) {
        mAppPreferences.edit().putInt(EDITOR_TEXT_WRAP_NAME, ordinal).apply();
    }

    public DonationStatus getDonationStatus() {
        try {
            return DonationStatus.valueOf(mAppPreferences.getString(DONATE_STATUS_NAME, DonationStatus.NOT_DONATED.name()));
        } catch (Exception e) {
            return DonationStatus.NOT_DONATED;
        }
    }

    public void setDonationStatus(DonationStatus status) {
        mAppPreferences.edit().putString(DONATE_STATUS_NAME, status.name()).apply();
    }

    public String getSelectedService() {
        return mAppPreferences.getString(SELECTED_SERVICE_NAME, BinServiceUtils.FOXBIN_SERVICE);
    }

    public void setSelectedService(String packageName) {
        mAppPreferences.edit().putString(SELECTED_SERVICE_NAME, packageName).apply();
    }

    public SharedPreferences getAppPreferences() {
        return mAppPreferences;
    }

    public SharedPreferences getDefaultPreferences() {
        return mDefaultPreferences;
    }
}
