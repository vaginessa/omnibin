package com.f0x1d.dogbin.utils;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.f0x1d.dogbin.billing.DonationStatus;
import com.pddstudio.highlightjs.HighlightJsView;

public class PreferencesUtils {

    public static final String DARK_THEME_NAME = "dark_theme";
    public static final String GOLD_THEME_NAME = "gold_theme";

    public static final String FIRST_START_NAME = "first_start";

    public static final String PROXY_NAME = "proxy_enable";
    public static final String PROXY_HOST_NAME = "proxy_host";
    public static final String PROXY_PORT_NAME = "proxy_port";
    public static final String PROXY_LOGIN_NAME = "proxy_login";
    public static final String PROXY_PASSWORD_NAME = "proxy_password";
    public static final String NETWORK_REDIRECT_NOTE_NAME = "note_redirect";

    public static final String CACHE_ONLY_MY_NAME = "cache_my";
    public static final String CACHE_AUTO_CLEAR_NAME = "auto_cache_clear";

    public static final String EDITOR_TEXT_WRAP_NAME = "editor_text_wrap";

    public static final String DONATE_STATUS_NAME = "donate_status";

    private Context mContext;
    private SharedPreferences mDefaultPreferences;
    private SharedPreferences mAppPreferences;

    public PreferencesUtils(Context c) {
        this.mContext = c.getApplicationContext();
        this.mDefaultPreferences = PreferenceManager.getDefaultSharedPreferences(c);
        this.mAppPreferences = c.getSharedPreferences("dogbin_settings", Context.MODE_PRIVATE);
    }

    public boolean isDarkTheme() {
        return mDefaultPreferences.getBoolean(DARK_THEME_NAME, false);
    }

    public boolean isGoldTheme() {
        return mDefaultPreferences.getBoolean(GOLD_THEME_NAME, false);
    }

    public boolean isFirstStart() {
        return mAppPreferences.getBoolean(FIRST_START_NAME, true);
    }

    public void setFirstStart(boolean firstStart) {
        mAppPreferences.edit().putBoolean(FIRST_START_NAME, firstStart).apply();
    }

    public String getProxyHost() {
        return mAppPreferences.getString(PROXY_HOST_NAME, null);
    }

    public int getProxyPort() {
        return mAppPreferences.getInt(PROXY_PORT_NAME, 0);
    }

    public String getProxyLogin() {
        return mAppPreferences.getString(PROXY_LOGIN_NAME, null);
    }

    public String getProxyPassword() {
        return mAppPreferences.getString(PROXY_PASSWORD_NAME, null);
    }

    public boolean isAuthForProxyRequired() {
        return getProxyLogin() != null && getProxyPassword() != null;
    }

    public void saveProxy(String host, int port, String login, String password) {
        mAppPreferences.edit()
                .putString(PROXY_HOST_NAME, host)
                .putInt(PROXY_PORT_NAME, port)
                .putString(PROXY_LOGIN_NAME, login.isEmpty() ? null : login)
                .putString(PROXY_PASSWORD_NAME, password.isEmpty() ? null : password)
                .apply();
    }

    public boolean isProxyEnabled() {
        return mDefaultPreferences.getBoolean(PROXY_NAME, false);
    }

    public void setProxyEnabled(boolean value) {
        mDefaultPreferences.edit().putBoolean(PROXY_NAME, value).apply();
    }

    public boolean isRedirectFromNoteEnabled() {
        return mDefaultPreferences.getBoolean(NETWORK_REDIRECT_NOTE_NAME, true);
    }

    public boolean cacheOnlyMy() {
        return mDefaultPreferences.getBoolean(CACHE_ONLY_MY_NAME, true);
    }

    public boolean autoClearCache() {
        return mDefaultPreferences.getBoolean(CACHE_AUTO_CLEAR_NAME, true);
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

    public SharedPreferences getAppPreferences() {
        return mAppPreferences;
    }

    public SharedPreferences getDefaultPreferences() {
        return mDefaultPreferences;
    }
}
