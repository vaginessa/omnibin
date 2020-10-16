package com.f0x1d.dogbin.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.fragment.app.Fragment;

import com.f0x1d.dmsdk.model.Folder;
import com.f0x1d.dogbin.App;
import com.f0x1d.dogbin.R;
import com.f0x1d.dogbin.billing.BillingManager;
import com.f0x1d.dogbin.ui.activity.base.BaseActivity;
import com.f0x1d.dogbin.ui.activity.text.TextEditActivity;
import com.f0x1d.dogbin.ui.activity.text.TextViewerActivity;
import com.f0x1d.dogbin.ui.fragment.NotesFragment;
import com.f0x1d.dogbin.ui.fragment.folders.FoldersWrapperFragment;
import com.f0x1d.dogbin.utils.BinServiceUtils;
import com.f0x1d.dogbin.utils.fragments.FragmentNavigator;
import com.f0x1d.dogbin.utils.fragments.MyFragmentBuilder;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

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

        if (savedInstanceState == null) processIntent(getIntent(), false);

        setupBottomNavigation(savedInstanceState);
        mPublishButton.setOnClickListener(v -> startActivity(new Intent(this, TextEditActivity.class)));
    }

    @Override
    protected void onStart() {
        super.onStart();
        BillingManager.getInstance(this).loadPurchases();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        processIntent(intent, true);
    }

    private void processIntent(Intent intent, boolean recreate) {
        if (intent.getAction() != null && intent.getAction().equals(TextViewerActivity.ACTION_TEXT_VIEW)) {
            String modulePackageName = intent.getStringExtra("module_package_name");
            BinServiceUtils.getBinServiceForPackageName(modulePackageName == null ? BinServiceUtils.DOGBIN_SERVICE : modulePackageName);

            if (recreate) {
                finish();
                startActivity(new Intent(this, MainActivity.class));
            }

            startActivity(new Intent(this, TextViewerActivity.class).setData(Uri.parse(intent.getStringExtra("url"))));
        } else if (intent.getData() != null) {
            App.getPreferencesUtil().setSelectedService(BinServiceUtils.getInbuiltServiceForUrl(intent.getData().toString()));
            BinServiceUtils.refreshCurrentService();

            if (recreate) {
                finish();
                startActivity(new Intent(this, MainActivity.class));
            }

            startActivity(new Intent(this, TextViewerActivity.class).setData(intent.getData()));
        }
    }

    private void setupBottomNavigation(Bundle savedInstanceState) {
        Folder defaultFolderData = BinServiceUtils.getCurrentActiveService().getDefaultFolder();

        MenuItem defaultFolderItem = mBottomNavigation.getMenu().findItem(R.id.default_folder_navigation);
        defaultFolderItem.setTitle(defaultFolderData.getTitle());
        defaultFolderItem.setIcon(defaultFolderData.getIcon());

        if (BinServiceUtils.getCurrentActiveService().loggedIn())
            mBottomNavigation.getMenu().findItem(R.id.login_navigation).setVisible(false);
        if (!BinServiceUtils.getCurrentActiveService().showFoldersItem())
            mBottomNavigation.getMenu().findItem(R.id.folders_navigation).setVisible(false);

        mBottomNavigation.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.settings_navigation:
                    mFragmentNavigator.switchTo("settings");
                    return true;
                case R.id.login_navigation:
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                    return true;
                case R.id.default_folder_navigation:
                    mFragmentNavigator.switchTo(NotesFragment.newInstance(defaultFolderData.getTitle(), defaultFolderData.getKey(), true),
                            defaultFolderData.getKey() + "_notes", false);
                    return true;
                case R.id.folders_navigation:
                    mFragmentNavigator.switchTo("folders");
                    return true;

                default:
                    return false;
            }
        });

        if (savedInstanceState == null)
            mBottomNavigation.setSelectedItemId(R.id.default_folder_navigation);
    }

    @Override
    public void onBackPressed() {
        Fragment currentFragment = mFragmentNavigator.getCurrentFragment();
        if (currentFragment instanceof FoldersWrapperFragment) {
            if (!((FoldersWrapperFragment) currentFragment).onBackPressed())
                return;
        }
        super.onBackPressed();
    }
}
