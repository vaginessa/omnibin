package com.f0x1d.dogbin.utils.fragments;

import androidx.annotation.IdRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class FragmentNavigator {

    private FragmentManager mFragmentManager;
    @IdRes
    private int mContainerView;
    private FragmentBuilder mFragmentBuilder;

    public FragmentNavigator(FragmentManager fragmentManager, int containerView, FragmentBuilder fragmentBuilder) {
        this.mFragmentManager = fragmentManager;
        this.mContainerView = containerView;
        this.mFragmentBuilder = fragmentBuilder;
    }

    public void switchTo(String tag) {
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out, android.R.animator.fade_in, android.R.animator.fade_out);

        Fragment currentFragment = getCurrentFragment();
        if (currentFragment != null)
            fragmentTransaction.hide(currentFragment);

        Fragment targetFragment = mFragmentManager.findFragmentByTag(tag);
        if (targetFragment == null)
            fragmentTransaction.add(mContainerView, mFragmentBuilder.getFragment(tag), tag);
        else
            fragmentTransaction.show(targetFragment);

        fragmentTransaction.commitNow();
    }

    public void switchTo(Fragment fragment, String tag, boolean backStack) {
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out, android.R.animator.fade_in, android.R.animator.fade_out);

        Fragment currentFragment = getCurrentFragment();
        if (currentFragment != null)
            fragmentTransaction.hide(currentFragment);

        Fragment targetFragment = mFragmentManager.findFragmentByTag(tag);
        if (targetFragment == null)
            fragmentTransaction.add(mContainerView, fragment, tag);
        else
            fragmentTransaction.show(targetFragment);

        if (backStack)
            fragmentTransaction.addToBackStack(null);

        fragmentTransaction.commit();
    }

    public Fragment getCurrentFragment() {
        for (Fragment fragment : mFragmentManager.getFragments()) {
            if (fragment != null && fragment.isVisible())
                return fragment;
        }
        return null;
    }

    public void popBackStack() {
        mFragmentManager.popBackStack();
    }

    public interface FragmentBuilder {
        Fragment getFragment(String tag);
    }
}
