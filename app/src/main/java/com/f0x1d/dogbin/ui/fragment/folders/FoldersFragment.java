package com.f0x1d.dogbin.ui.fragment.folders;

import android.os.Bundle;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.f0x1d.dogbin.R;
import com.f0x1d.dogbin.adapter.FoldersAdapter;
import com.f0x1d.dogbin.ui.fragment.NotesFragment;
import com.f0x1d.dogbin.ui.fragment.base.BaseFragment;
import com.f0x1d.dogbin.utils.Utils;
import com.f0x1d.dogbin.viewmodel.FoldersViewModel;
import com.google.android.material.appbar.MaterialToolbar;

public class FoldersFragment extends BaseFragment<FoldersViewModel> {

    private MaterialToolbar mToolbar;
    private RecyclerView mFoldersRecycler;
    private SwipeRefreshLayout mRefreshLayout;

    private FoldersAdapter mAdapter;

    public static FoldersFragment newInstance() {
        Bundle args = new Bundle();

        FoldersFragment fragment = new FoldersFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int layoutId() {
        return R.layout.fragment_folders;
    }

    @Override
    protected Class<FoldersViewModel> viewModel() {
        return FoldersViewModel.class;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mToolbar = findViewById(R.id.toolbar);
        mFoldersRecycler = findViewById(R.id.folders_recycler);
        mRefreshLayout = findViewById(R.id.refresh_layout);

        mToolbar.setTitle(R.string.folders);

        mRefreshLayout.setColorSchemeColors(Utils.getColorFromAttr(requireActivity(), R.attr.colorPrimary));
        if (isNightTheme())
            mRefreshLayout.setProgressBackgroundColorSchemeColor(Utils.getColorFromAttr(requireActivity(), android.R.attr.windowBackground));
        mRefreshLayout.setOnRefreshListener(mViewModel::load);

        mFoldersRecycler.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false));
        mFoldersRecycler.setAdapter(mAdapter = new FoldersAdapter(folder ->
                ((FoldersWrapperFragment) getParentFragment()).getFragmentNavigator().switchTo(NotesFragment.newInstance(folder), folder.getKey() + "_notes", true)));

        mViewModel.getLoadingStateData().observe(getViewLifecycleOwner(), loadingState -> {
            if (loadingState == null)
                return;

            switch (loadingState) {
                case LOADING:
                    mRefreshLayout.setRefreshing(true);
                    mFoldersRecycler.setVisibility(View.GONE);
                    break;

                case LOADED:
                    mRefreshLayout.setRefreshing(false);
                    mFoldersRecycler.setVisibility(View.VISIBLE);
                    break;
            }
        });

        mViewModel.getEventsData().observe(getViewLifecycleOwner(), event -> {
            if (event.isConsumed()) return;

            if (event.type().equals(FoldersViewModel.EVENT_TYPE_CLEAR_BACKSTACK)) {
                event.consume();
                ((FoldersWrapperFragment) getParentFragment()).getFragmentNavigator().popBackStack();
            }
        });

        mViewModel.getFoldersData().observe(getViewLifecycleOwner(), folders -> mAdapter.setFolders(folders));
    }
}
