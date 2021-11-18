package com.f0x1d.dogbin.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;

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
import com.f0x1d.dogbin.utils.fragments.FragmentNavigator;
import com.f0x1d.dogbin.utils.fragments.MyFragmentBuilder;
import com.f0x1d.dogbin.viewmodel.MainViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.shape.MaterialShapeDrawable;

public class MainActivity extends BaseActivity<MainViewModel> {

    private FragmentNavigator mFragmentNavigator;

    private FloatingActionButton mPublishButton;
    private BottomNavigationView mBottomNavigation;

    @Override
    protected Class<MainViewModel> viewModel() {
        return MainViewModel.class;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFragmentNavigator = new FragmentNavigator(getSupportFragmentManager(), R.id.main_container_view, new MyFragmentBuilder());

        mPublishButton = findViewById(R.id.publish_button);
        mBottomNavigation = findViewById(R.id.bottom_navigation);

        mViewModel.getEventsData().observe(this, event -> {
            if (event.isConsumed()) return;

            if (event.type().equals(MainViewModel.EVENT_VIEW_TEXT)) {
                Pair<Uri, Boolean> data = event.consume();

                if (data.second) { // not the best solution ofc, but i had to think about it a lot of time before
                    finish();
                    startActivity(new Intent(this, MainActivity.class));
                }

                startActivity(new Intent(this, TextViewerActivity.class).setData(data.first));
            } else if (event.type().equals(MainViewModel.EVENT_TOASTER_DIALOG)) {
                new MaterialAlertDialogBuilder(this)
                        .setCancelable(false)
                        .setTitle(R.string.vtosters)
                        .setMessage(R.string.toaster_dialog)
                        .setPositiveButton(android.R.string.ok, (dialog, which) -> App.getPreferencesUtil().setToasterShowed(event.consume()))
                        .show();
            }
        });
        if (savedInstanceState == null)
            mViewModel.processIntent(getIntent(), false);

        mViewModel.getPublishButtonVisibleData().observe(this, isVisible -> mPublishButton.setVisibility(isVisible ? View.VISIBLE : View.GONE));

        setupBottomNavigation(savedInstanceState);
        mPublishButton.setOnClickListener(v -> startActivity(new Intent(this, TextEditActivity.class)));
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mViewModel.processIntent(intent, true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        BillingManager.getInstance(this).loadPurchases();
    }

    private void setupBottomNavigation(Bundle savedInstanceState) {
        mBottomNavigation.setOnItemSelectedListener(item -> {
            mViewModel.setCurrentTab(item.getItemId());
            mPublishButton.setVisibility(item.getItemId() == R.id.settings_navigation ? View.GONE : View.VISIBLE);

            Folder defaultFolderData = mViewModel.getDefaultFolderData().getValue();

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
        getWindow().setNavigationBarColor(((MaterialShapeDrawable) mBottomNavigation.getBackground()).getResolvedTintColor());

        mViewModel.getDefaultFolderData().observe(this, defaultFolderData -> {
            if (savedInstanceState == null) {
                mBottomNavigation.setSelectedItemId(R.id.default_folder_navigation);
            }

            MenuItem defaultFolderItem = mBottomNavigation.getMenu().findItem(R.id.default_folder_navigation);
            defaultFolderItem.setTitle(defaultFolderData.getTitle());
            defaultFolderItem.setIcon(defaultFolderData.getIcon());

            mBottomNavigation.setVisibility(View.VISIBLE);
        });

        mViewModel.getLoggedInData().observe(this, loggedIn ->
                mBottomNavigation.getMenu().findItem(R.id.login_navigation).setVisible(!loggedIn));
        mViewModel.getShowFoldersItemData().observe(this, showFolders ->
                mBottomNavigation.getMenu().findItem(R.id.folders_navigation).setVisible(showFolders));
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
