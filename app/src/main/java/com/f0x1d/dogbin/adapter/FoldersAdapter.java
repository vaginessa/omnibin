package com.f0x1d.dogbin.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import com.f0x1d.dmsdk.model.Folder;
import com.f0x1d.dogbin.R;
import com.f0x1d.dogbin.adapter.base.BaseAdapter;
import com.f0x1d.dogbin.adapter.base.BaseViewHolder;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;

public class FoldersAdapter extends BaseAdapter<Folder, FoldersAdapter.FolderViewHolder> {

    private FolderClickListener mFolderClickListener;

    public FoldersAdapter(FolderClickListener folderClickListener) {
        this.mFolderClickListener = folderClickListener;
    }

    @Override
    protected FolderViewHolder createHolder(ViewGroup parent, LayoutInflater layoutInflater) {
        return new FolderViewHolder(layoutInflater.inflate(R.layout.item_folder, parent, false));
    }

    public interface FolderClickListener {
        void onClicked(Folder folder);
    }

    class FolderViewHolder extends BaseViewHolder<Folder> {

        private MaterialCardView mContentCard;
        private MaterialTextView mFolderTitleText;

        public FolderViewHolder(@NonNull View itemView) {
            super(itemView);

            mContentCard = itemView.findViewById(R.id.content_card);
            mFolderTitleText = itemView.findViewById(R.id.folder_title_text);

            mContentCard.setOnClickListener(v -> mFolderClickListener.onClicked(mElements.get(getAdapterPosition())));
        }

        public void bindTo(Folder folder) {
            mFolderTitleText.setText(folder.getTitle());
            mFolderTitleText.setCompoundDrawablesWithIntrinsicBounds(folder.getIcon(), null, null, null);
        }
    }
}