package com.f0x1d.dogbin.ui.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.f0x1d.dmsdk.model.Folder;
import com.f0x1d.dmsdk.model.UserDocument;
import com.f0x1d.dogbin.R;
import com.f0x1d.dogbin.adapter.NotesAdapter;
import com.f0x1d.dogbin.ui.activity.text.TextViewerActivity;
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

    public static NotesFragment newInstance(Folder folder) {
        Bundle args = new Bundle();
        args.putString("folder_title", folder.getTitle());
        args.putString("folder_key", folder.getKey());
        args.putBoolean("folder_available_unauthorized", folder.isAvailableUnauthorized());

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
        mRefreshLayout.setOnRefreshListener(mViewModel::load);

        mNotesRecycler.setLayoutManager(new StaggeredGridLayoutManager(2, RecyclerView.VERTICAL));
        mNotesRecycler.setAdapter(mAdapter = new NotesAdapter(new NotesAdapter.OnNoteClickedListener() {
            @Override
            public void clicked(UserDocument userDocument) {
                mViewModel.open(userDocument);
            }

            @Override
            public void delete(UserDocument userDocument) {
                mViewModel.deleteNote(userDocument);
            }

            @Override
            public void copyUrl(UserDocument userDocument) {
                mViewModel.copyUrl(userDocument);
            }

            @Override
            public boolean isDeletable(UserDocument userDocument) {
                return mViewModel.noteDeletable(userDocument);
            }
        }));
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

        mViewModel.getEventsData().observe(getViewLifecycleOwner(), event -> {
            if (event.isConsumed()) return;

            if (event.type().equals(NotesViewModel.EVENT_TYPE_OPEN_NOTE)) {
                Intent intent = new Intent(requireContext(), TextViewerActivity.class);
                intent.setData(Uri.parse(event.consume()));
                intent.putExtra("my_note", (boolean) event.argument(0));

                startActivity(intent);
            }
        });

        mViewModel.getNotesListData().observe(getViewLifecycleOwner(), notes -> mAdapter.setNotes(notes));
    }

    @Override
    protected ViewModelProvider.Factory buildFactory() {
        return new NotesViewModel.NotesViewModelFactory(
                requireArguments().getString("folder_key"),
                requireArguments().getBoolean("folder_available_unauthorized")
        );
    }
}
