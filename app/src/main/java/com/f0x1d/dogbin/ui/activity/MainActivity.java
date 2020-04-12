package com.f0x1d.dogbin.ui.activity;

import android.content.Intent;
import android.os.Bundle;

import com.f0x1d.dogbin.App;
import com.f0x1d.dogbin.R;
import com.f0x1d.dogbin.billing.BillingManager;
import com.f0x1d.dogbin.network.retrofit.DogBinApi;
import com.f0x1d.dogbin.ui.activity.base.BaseActivity;
import com.f0x1d.dogbin.ui.activity.text.DogBinTextEditActivity;
import com.f0x1d.dogbin.utils.fragments.FragmentNavigator;
import com.f0x1d.dogbin.utils.fragments.MyFragmentBuilder;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Locale;

public class MainActivity extends BaseActivity {

    private FragmentNavigator mFragmentNavigator;

    private FloatingActionButton mPublishButton;
    private BottomNavigationView mBottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFragmentNavigator = new FragmentNavigator(getSupportFragmentManager(), R.id.main_container_view, new MyFragmentBuilder());

        mPublishButton = findViewById(R.id.publish_button);
        mBottomNavigation = findViewById(R.id.bottom_navigation);

        mBottomNavigation.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.settings_navigation:
                    mFragmentNavigator.switchTo("settings");
                    return true;
                case R.id.login_navigation:
                    startActivity(new Intent(MainActivity.this, DogBinLoginActivity.class));
                    finish();
                    return true;
                case R.id.my_notes_navigation:
                    mFragmentNavigator.switchTo("my_notes");
                    return true;
                case R.id.history_navigation:
                    mFragmentNavigator.switchTo("history");
                    return true;

                default:
                    return false;
            }
        });

        mPublishButton.setOnClickListener(v -> startActivity(new Intent(this, DogBinTextEditActivity.class)));

        if (DogBinApi.getInstance().getCookieJar().isDoggyClientCookieSaved()) {
            mBottomNavigation.getMenu().findItem(R.id.login_navigation).setVisible(false);
            mBottomNavigation.getMenu().findItem(R.id.history_navigation).setVisible(false);

            if (savedInstanceState == null)
                mBottomNavigation.setSelectedItemId(R.id.my_notes_navigation);
        } else {
            mBottomNavigation.getMenu().findItem(R.id.my_notes_navigation).setVisible(false);

            if (savedInstanceState == null)
                mBottomNavigation.setSelectedItemId(R.id.history_navigation);
        }

        checkUserIsFromRussia();
    }

    @Override
    protected void onStart() {
        super.onStart();
        BillingManager.getInstance(this).loadPurchases();
    }

    private void checkUserIsFromRussia() {
        if (App.getPrefsUtil().isFirstStart() && Locale.getDefault().getLanguage().startsWith("ru")) {
            new MaterialAlertDialogBuilder(this)
                    .setTitle(R.string.app_name)
                    .setMessage(R.string.dogbin_block_in_russia)
                    .setCancelable(false)
                    .setPositiveButton(android.R.string.ok, (dialog, which) -> App.getPrefsUtil().setFirstStart(false))
                    .show();
        }
    }
}
