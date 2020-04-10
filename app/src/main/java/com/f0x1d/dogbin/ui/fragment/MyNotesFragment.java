package com.f0x1d.dogbin.ui.fragment;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.f0x1d.dogbin.R;
import com.f0x1d.dogbin.adapter.MyNotesAdapter;
import com.f0x1d.dogbin.ui.fragment.base.BaseFragment;
import com.f0x1d.dogbin.viewmodel.DogBinMyNotesViewModel;
import com.google.android.material.appbar.MaterialToolbar;

public class MyNotesFragment extends BaseFragment {

    private MaterialToolbar mToolbar;
    private RecyclerView mNotesRecycler;
    private SwipeRefreshLayout mRefreshLayout;

    private MyNotesAdapter mAdapter;

    private DogBinMyNotesViewModel mDogBinMyNotesViewModel;

    public static MyNotesFragment newInstance() {
        Bundle args = new Bundle();

        MyNotesFragment fragment = new MyNotesFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int layoutId() {
        return R.layout.fragment_my_notes;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mDogBinMyNotesViewModel = new ViewModelProvider(requireActivity()).get(DogBinMyNotesViewModel.class);
        mDogBinMyNotesViewModel.load();

        mToolbar = findViewById(R.id.toolbar);
        mNotesRecycler = findViewById(R.id.my_notes_recycler);
        mRefreshLayout = findViewById(R.id.refresh_layout);

        mToolbar.setTitle(R.string.my_notes);
        mRefreshLayout.setColorSchemeResources(isNightTheme() ? R.color.dogBinAccent : R.color.pixelAccent);
        if (isNightTheme())
            mRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.dogBinBackground);

        mNotesRecycler.setLayoutManager(new StaggeredGridLayoutManager(2, RecyclerView.VERTICAL));
        mNotesRecycler.setAdapter(mAdapter = new MyNotesAdapter(requireActivity()));

        mDogBinMyNotesViewModel.getLoadingStateData().observe(getViewLifecycleOwner(), loadingState -> {
            if (loadingState == null)
                return;

            switch (loadingState) {
                case LOADING:
                    mRefreshLayout.setRefreshing(true);
                    mNotesRecycler.setVisibility(View.GONE);
                    break;
                case LOADED:
                    mRefreshLayout.setRefreshing(false);
                    mNotesRecycler.setVisibility(View.VISIBLE);
                    break;
            }
        });

        mDogBinMyNotesViewModel.getMyNotesListData().observe(getViewLifecycleOwner(), notes -> {
            if (notes == null)
                return;

            mAdapter.setNotes(notes, false);
            mAdapter.notifyDataSetChanged();
        });

        mRefreshLayout.setOnRefreshListener(mDogBinMyNotesViewModel::load);
    }
}
