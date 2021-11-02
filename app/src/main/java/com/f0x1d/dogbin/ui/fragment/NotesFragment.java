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
import com.f0x1d.dogbin.utils.ItemOffsetDecoration;
import com.f0x1d.dogbin.utils.Utils;
import com.f0x1d.dogbin.viewmodel.NotesViewModel;

public class NotesFragment extends BaseFragment<NotesViewModel> {

    private SwipeRefreshLayout mRefreshLayout;
    private RecyclerView mNotesRecycler;

    private NotesAdapter mAdapter;

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
    protected Class<NotesViewModel> viewModel() {
        return NotesViewModel.class;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRefreshLayout = findViewById(R.id.refresh_layout);
        mNotesRecycler = findViewById(R.id.notes_recycler);

        Utils.applyToolbarShit(view, requireArguments().getString("folder_title"));

        mRefreshLayout.setColorSchemeColors(Utils.getColorFromAttr(requireActivity(), R.attr.colorPrimary));
        if (isNightTheme())
            mRefreshLayout.setProgressBackgroundColorSchemeColor(Utils.getColorFromAttr(requireActivity(), android.R.attr.windowBackground));

        mNotesRecycler.setLayoutManager(new StaggeredGridLayoutManager(2, RecyclerView.VERTICAL));
        mNotesRecycler.setAdapter(mAdapter = new NotesAdapter(requireActivity()));
        mNotesRecycler.addItemDecoration(new ItemOffsetDecoration(8));

        mViewModel.getLoadingStateData().observe(getViewLifecycleOwner(), loadingState -> {
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

        mViewModel.getNotesListData().observe(getViewLifecycleOwner(), notes -> {
            if (notes == null)
                return;

            mAdapter.setNotes(notes, false);
            mAdapter.notifyDataSetChanged();
        });

        mRefreshLayout.setOnRefreshListener(mViewModel::load);
    }

    @Override
    protected ViewModelProvider.Factory buildFactory() {
        return new NotesViewModel.NotesViewModelFactory(requireArguments().getString("folder_key"));
    }
}
