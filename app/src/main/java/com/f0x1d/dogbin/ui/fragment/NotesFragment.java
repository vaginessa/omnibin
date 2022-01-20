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
import com.google.android.material.appbar.MaterialToolbar;

public class NotesFragment extends BaseFragment<NotesViewModel> {

    private MaterialToolbar mToolbar;
    private SwipeRefreshLayout mRefreshLayout;
    private RecyclerView mNotesRecycler;

    private NotesAdapter mAdapter;

    public static NotesFragment newInstance(String folderTitle, String folderKey) {
        Bundle args = new Bundle();
        args.putString("folder_title", folderTitle);
        args.putString("folder_key", folderKey);

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

        mToolbar = findViewById(R.id.toolbar);
        mRefreshLayout = findViewById(R.id.refresh_layout);
        mNotesRecycler = findViewById(R.id.notes_recycler);

        mToolbar.setTitle(requireArguments().getString("folder_title"));
        if (getParentFragmentManager().getBackStackEntryCount() > 0) {
            mToolbar.setNavigationIcon(R.drawable.ic_arrow_back);
            mToolbar.setNavigationOnClickListener(v -> getParentFragmentManager().popBackStack());
        }

        mRefreshLayout.setColorSchemeColors(Utils.getColorFromAttr(requireActivity(), R.attr.colorPrimary));
        if (isNightTheme())
            mRefreshLayout.setProgressBackgroundColorSchemeColor(Utils.getColorFromAttr(requireActivity(), android.R.attr.windowBackground));

        mNotesRecycler.setLayoutManager(new StaggeredGridLayoutManager(2, RecyclerView.VERTICAL));
        mNotesRecycler.setAdapter(mAdapter = new NotesAdapter(requireActivity(), userDocument -> mViewModel.deleteNote(userDocument)));
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
        });

        mRefreshLayout.setOnRefreshListener(mViewModel::load);
    }

    @Override
    protected ViewModelProvider.Factory buildFactory() {
        return new NotesViewModel.NotesViewModelFactory(requireArguments().getString("folder_key"));
    }
}
