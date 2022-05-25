package com.f0x1d.dogbin.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.f0x1d.dmsdk.model.Folder;
import com.f0x1d.dogbin.R;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.List;

public class FoldersAdapter extends RecyclerView.Adapter<FoldersAdapter.FolderViewHolder> {

    private List<Folder> mFolders = new ArrayList<>();

    private FolderClickListener mFolderClickListener;

    public FoldersAdapter(FolderClickListener folderClickListener) {
        this.mFolderClickListener = folderClickListener;
    }

    @NonNull
    @Override
    public FolderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FolderViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_folder, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull FolderViewHolder holder, int position) {
        holder.bindTo(mFolders.get(position));
    }

    @Override
    public int getItemCount() {
        return mFolders.size();
    }

    public void setFolders(List<Folder> folders) {
        mFolders = folders;
        notifyDataSetChanged();
    }

    public interface FolderClickListener {
        void onClicked(Folder folder);
    }

    class FolderViewHolder extends RecyclerView.ViewHolder {

        private MaterialCardView mContentCard;
        private MaterialTextView mFolderTitleText;

        public FolderViewHolder(@NonNull View itemView) {
            super(itemView);

            mContentCard = itemView.findViewById(R.id.content_card);
            mFolderTitleText = itemView.findViewById(R.id.folder_title_text);

            mContentCard.setOnClickListener(v -> mFolderClickListener.onClicked(mFolders.get(getAdapterPosition())));
        }

        public void bindTo(Folder folder) {
            mFolderTitleText.setText(folder.getTitle());
            mFolderTitleText.setCompoundDrawablesWithIntrinsicBounds(folder.getIcon(), null, null, null);
        }
    }
}