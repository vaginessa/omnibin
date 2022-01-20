package com.f0x1d.dogbin.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.widget.TextViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.f0x1d.dmsdk.model.Folder;
import com.f0x1d.dogbin.R;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.List;

public class FoldersAdapter extends RecyclerView.Adapter<FoldersAdapter.FolderViewHolder> {

    private List<Folder> mFolders = new ArrayList<>();

    private Context mContext;
    private FolderClickListener mFolderClickListener;

    public FoldersAdapter(Context context, FolderClickListener folderClickListener) {
        this.mContext = context;
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
        mFolders.clear();
        mFolders.addAll(folders);
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