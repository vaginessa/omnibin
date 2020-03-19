package com.f0x1d.dogbin.ui.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.f0x1d.dogbin.R;
import com.f0x1d.dogbin.adapter.MyNotesAdapter;
import com.f0x1d.dogbin.ui.fragment.base.BaseFragment;
import com.f0x1d.dogbin.viewmodel.MyNotesViewModel;
import com.google.android.material.appbar.MaterialToolbar;

public class MyNotesFragment extends BaseFragment {

    private MaterialToolbar mToolbar;
    private RecyclerView mNotesRecycler;
    private ProgressBar mLoadingProgress;

    private MyNotesAdapter mAdapter;

    private MyNotesViewModel mMyNotesViewModel;

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

        mMyNotesViewModel = new ViewModelProvider(requireActivity()).get(MyNotesViewModel.class);
        mMyNotesViewModel.load();

        mToolbar = findViewById(R.id.toolbar);
        mNotesRecycler = findViewById(R.id.my_notes_recycler);
        mLoadingProgress = findViewById(R.id.loading_progress);

        mToolbar.setTitle(R.string.my_notes);
        mToolbar.inflateMenu(R.menu.my_notes_menu);
        mToolbar.getMenu().getItem(0).setOnMenuItemClickListener(item -> {
            mMyNotesViewModel.load();
            return true;
        });

        mNotesRecycler.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false));
        mNotesRecycler.setAdapter(mAdapter = new MyNotesAdapter(requireActivity()));

        mMyNotesViewModel.getLoadingStateData().observe(getViewLifecycleOwner(), loadingState -> {
            if (loadingState == null)
                return;

            switch (loadingState) {
                case LOADING:
                    mLoadingProgress.setVisibility(View.VISIBLE);
                    mNotesRecycler.setVisibility(View.GONE);
                    break;
                case LOADED:
                    mLoadingProgress.setVisibility(View.GONE);
                    mNotesRecycler.setVisibility(View.VISIBLE);
                    break;
            }
        });

        mMyNotesViewModel.getMyNotesListData().observe(getViewLifecycleOwner(), notes -> {
            if (notes == null)
                return;

            mAdapter.setNotes(notes, false);
            mAdapter.notifyDataSetChanged();
        });
    }
}
