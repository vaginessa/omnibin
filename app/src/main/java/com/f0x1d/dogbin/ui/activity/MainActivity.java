package com.f0x1d.dogbin.ui.activity;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
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
import com.f0x1d.dogbin.utils.TextDrawable;
import com.f0x1d.dogbin.utils.Utils;
import com.f0x1d.dogbin.utils.fragments.FragmentNavigator;
import com.f0x1d.dogbin.utils.fragments.MyFragmentBuilder;
import com.f0x1d.dogbin.viewmodel.MainViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.shape.MaterialShapeDrawable;

import java.util.List;

public class MainActivity extends BaseActivity<MainViewModel> {

    private FragmentNavigator mFragmentNavigator;

    private FloatingActionButton mPublishButton;
    private BottomNavigationView mBottomNavigation;

    private TextDrawable mBinDrawable;

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
                startActivity(new Intent(this, TextViewerActivity.class).setData(event.consume()));
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
            mViewModel.processIntent(getIntent());

        mViewModel.getPublishButtonVisibleData().observe(this, isVisible -> mPublishButton.setVisibility(isVisible ? View.VISIBLE : View.GONE));

        setupBottomNavigation(savedInstanceState);
        mPublishButton.setOnClickListener(v -> startActivity(new Intent(this, TextEditActivity.class)));
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mViewModel.processIntent(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        BinServiceUtils.refreshInstalledServices();
        BillingManager.getInstance(this).loadPurchases();
    }

    private void setupBottomNavigation(Bundle savedInstanceState) {
        mBottomNavigation.setOnItemSelectedListener(item -> {
            mViewModel.setCurrentTab(item.getItemId());

            Folder defaultFolderData = mViewModel.getNavigationData().getValue().defaultFolder;

            swapDrawableCheckedState(item.getItemId() == R.id.settings_navigation); // shit for my custom drawable

            switch (item.getItemId()) {
                case R.id.settings_navigation:
                    mFragmentNavigator.switchTo("settings");
                    return true;
                case R.id.login_navigation:
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                    return true;
                case R.id.default_folder_navigation:
                    mFragmentNavigator.switchTo(NotesFragment.newInstance(defaultFolderData), defaultFolderData.getKey() + "_notes", false);
                    return true;
                case R.id.folders_navigation:
                    mFragmentNavigator.switchTo("folders");
                    return true;

                default:
                    return false;
            }
        });
        getWindow().setNavigationBarColor(((MaterialShapeDrawable) mBottomNavigation.getBackground()).getResolvedTintColor());

        mViewModel.getNavigationData().observe(this, data -> {
            mBottomNavigation.getMenu().findItem(R.id.login_navigation).setVisible(!data.loggedIn);
            mBottomNavigation.getMenu().findItem(R.id.folders_navigation).setVisible(data.showFolders);

            mBinDrawable = new TextDrawable(data.shortName);
            mBottomNavigation
                    .getMenu()
                    .findItem(R.id.settings_navigation)
                    .setIcon(mBinDrawable);

            MenuItem defaultFolderItem = mBottomNavigation.getMenu().findItem(R.id.default_folder_navigation);
            defaultFolderItem.setTitle(data.defaultFolder.getTitle());
            defaultFolderItem.setIcon(data.defaultFolder.getIcon());

            mBottomNavigation.setVisibility(View.VISIBLE);

            if (mFragmentNavigator.getCurrentFragment() instanceof NotesFragment) {
                mFragmentNavigator.switchTo(NotesFragment.newInstance(data.defaultFolder), data.defaultFolder.getKey() + "_notes", false, false);
            }

            if (savedInstanceState == null && mFragmentNavigator.getCurrentFragment() == null) {
                mBottomNavigation.setSelectedItemId(R.id.default_folder_navigation);
            }
        });

        ViewGroup navigationMenuView = (ViewGroup) mBottomNavigation.getChildAt(0);
        for (int i = 0; i < navigationMenuView.getChildCount(); i++) {
            BottomNavigationItemView bottomNavigationItemView = (BottomNavigationItemView) navigationMenuView.getChildAt(i);

            bottomNavigationItemView.setOnLongClickListener(v -> {
                if (v.getId() != R.id.settings_navigation)
                    return false;

                openPopup(v);
                return true;
            });
        }
    }

    private void swapDrawableCheckedState(boolean checked) {
        if (mBinDrawable == null) return;

        int[] states = mBinDrawable.getState();
        if (states.length == 0)
            states = new int[] { -android.R.attr.state_checked };

        for (int i = 0; i < states.length; i++) {
            int state = states[i];

            if (Math.abs(state) == android.R.attr.state_checked)
                states[i] = checked ? android.R.attr.state_checked : -android.R.attr.state_checked;
        }

        mBinDrawable.setState(states);
    }

    private void openPopup(View v) {
        MenuBuilder menuBuilder = new MenuBuilder(v.getContext());

        List<ApplicationInfo> services = BinServiceUtils.getInstalledServices();
        String[] installedServices = Utils.getInstalledServices(services);

        for (int i = 0; i < installedServices.length; i++) {
            String installedService = installedServices[i];

            int finalI = i;
            menuBuilder.add(installedService).setOnMenuItemClickListener(item -> {
                Utils.switchService(finalI, services);
                return true;
            });
        }

        new MenuPopupHelper(this, menuBuilder, v).show();
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
