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
import com.f0x1d.dogbin.adapter.NotesAdapter;
import com.f0x1d.dogbin.ui.fragment.base.BaseFragment;
import com.f0x1d.dogbin.utils.ListSpacingDecoration;
import com.f0x1d.dogbin.utils.Utils;
import com.f0x1d.dogbin.viewmodel.DefaultNotesViewModel;
import com.f0x1d.dogbin.viewmodel.NotesViewModel;
import com.google.android.material.appbar.MaterialToolbar;

public class NotesFragment extends BaseFragment {

    private MaterialToolbar mToolbar;
    private RecyclerView mNotesRecycler;
    private SwipeRefreshLayout mRefreshLayout;

    private NotesAdapter mAdapter;

    private NotesViewModel mNotesViewModel;

    public static NotesFragment newInstance(String folderTitle, String folderKey, boolean defaultFolder) {
        Bundle args = new Bundle();
        args.putString("folder_title", folderTitle);
        args.putString("folder_key", folderKey);
        args.putBoolean("folder_default", defaultFolder);

        NotesFragment fragment = new NotesFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int layoutId() {
        return R.layout.fragment_notes;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments().getBoolean("folder_default"))
            mNotesViewModel = new ViewModelProvider(requireActivity()).get(DefaultNotesViewModel.class);
        else
            mNotesViewModel = new ViewModelProvider(requireActivity()).get(NotesViewModel.class);
        mNotesViewModel.load(getArguments().getString("folder_key"));

        mToolbar = findViewById(R.id.toolbar);
        mNotesRecycler = findViewById(R.id.notes_recycler);
        mRefreshLayout = findViewById(R.id.refresh_layout);

        mToolbar.setTitle(getArguments().getString("folder_title"));
        mRefreshLayout.setColorSchemeColors(Utils.getColorFromAttr(requireActivity(), R.attr.colorAccent));
        if (isNightTheme())
            mRefreshLayout.setProgressBackgroundColorSchemeColor(Utils.getColorFromAttr(requireActivity(), android.R.attr.windowBackground));

        mNotesRecycler.setLayoutManager(new StaggeredGridLayoutManager(2, RecyclerView.VERTICAL));
        mNotesRecycler.setAdapter(mAdapter = new NotesAdapter(requireActivity()));
        mNotesRecycler.addItemDecoration(new ListSpacingDecoration(8));

        mNotesViewModel.getLoadingStateData().observe(getViewLifecycleOwner(), loadingState -> {
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

        mNotesViewModel.getNotesListData().observe(getViewLifecycleOwner(), notes -> {
            if (notes == null)
                return;

            mAdapter.setNotes(notes, false);
            mAdapter.notifyDataSetChanged();
        });

        mRefreshLayout.setOnRefreshListener(mNotesViewModel::load);
    }
}
