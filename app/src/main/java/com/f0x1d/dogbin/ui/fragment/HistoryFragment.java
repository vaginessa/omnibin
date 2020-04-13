package com.f0x1d.dogbin.ui.fragment;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.f0x1d.dogbin.App;
import com.f0x1d.dogbin.R;
import com.f0x1d.dogbin.adapter.MyNotesAdapter;
import com.f0x1d.dogbin.ui.fragment.base.BaseFragment;
import com.f0x1d.dogbin.utils.ItemOffsetDecoration;
import com.google.android.material.appbar.MaterialToolbar;

public class HistoryFragment extends BaseFragment {

    private MaterialToolbar mToolbar;
    private RecyclerView mNotesRecycler;

    private MyNotesAdapter mAdapter;

    public static HistoryFragment newInstance() {
        Bundle args = new Bundle();

        HistoryFragment fragment = new HistoryFragment();
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

        mToolbar = findViewById(R.id.toolbar);
        mNotesRecycler = findViewById(R.id.my_notes_recycler);

        mToolbar.setTitle(R.string.history);

        mNotesRecycler.setLayoutManager(new StaggeredGridLayoutManager(2, RecyclerView.VERTICAL));
        mNotesRecycler.setAdapter(mAdapter = new MyNotesAdapter(requireActivity()));
        mNotesRecycler.addItemDecoration(new ItemOffsetDecoration(requireContext(), 8));

        App.getMyDatabase().getSavedNoteDao().getAll().observe(getViewLifecycleOwner(), notes -> {
            mAdapter.setNotes(notes, true);
            mAdapter.notifyDataSetChanged();
        });
    }
}
