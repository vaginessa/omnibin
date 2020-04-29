package com.f0x1d.dogbin.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Build;
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

    private int[] mColors = new int[4];

    private Context mContext;
    private FolderClickListener mFolderClickListener;

    public FoldersAdapter(Context context, FolderClickListener folderClickListener) {
        this.mContext = context;
        this.mFolderClickListener = folderClickListener;

        mColors[0] = mContext.getResources().getColor(R.color.pinkAccent);
        mColors[1] = mContext.getResources().getColor(R.color.limeAccent);
        mColors[2] = mContext.getResources().getColor(R.color.pixelAccent);
        mColors[3] = mContext.getResources().getColor(R.color.goldAccent);
    }

    @NonNull
    @Override
    public FolderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FolderViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_folder, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull FolderViewHolder holder, int position) {
        holder.bindTo(mFolders.get(position));
        holder.applyColor(mColors[position % mColors.length]);
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

        public void applyColor(int color) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
                return;

            mFolderTitleText.setTextColor(color);
            mFolderTitleText.setCompoundDrawableTintList(ColorStateList.valueOf(color));
            mContentCard.setStrokeColor(color);
        }
    }
}