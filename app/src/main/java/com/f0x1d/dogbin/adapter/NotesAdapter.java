package com.f0x1d.dogbin.adapter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.f0x1d.dogbin.R;
import com.f0x1d.dogbin.db.entity.SavedNote;
import com.f0x1d.dogbin.ui.activity.text.TextViewerActivity;
import com.f0x1d.dogbin.utils.BinServiceUtils;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteViewHolder> {

    private List<SavedNote> mSavedNotes = new ArrayList<>();

    private Context mContext;

    public NotesAdapter(Context context) {
        this.mContext = context;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NoteViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_note, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        holder.bindTo(mSavedNotes.get(position));
    }

    @Override
    public int getItemCount() {
        return mSavedNotes.size();
    }

    public void setNotes(List<SavedNote> savedNotes, boolean toStart) {
        mSavedNotes.clear();
        if (toStart) {
            for (SavedNote savedNote : savedNotes) {
                mSavedNotes.add(0, savedNote);
            }
        } else
            mSavedNotes.addAll(savedNotes);
    }

    class NoteViewHolder extends RecyclerView.ViewHolder {

        private MaterialCardView mContentCard;
        private TextView mURLText;
        private TextView mTimeText;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);

            this.mContentCard = itemView.findViewById(R.id.content_card);
            this.mURLText = itemView.findViewById(R.id.url_text);
            this.mTimeText = itemView.findViewById(R.id.time_text);

            mContentCard.setOnClickListener(v -> {
                Intent intent = new Intent(mContext, TextViewerActivity.class);
                intent.setData(Uri.parse(BinServiceUtils.getCurrentActiveService().getDomain() + mSavedNotes.get(getAdapterPosition()).getSlug()));
                intent.putExtra("my_note", true);

                mContext.startActivity(intent);
            });
            mContentCard.setOnLongClickListener(v -> {
                String delDogUrl = BinServiceUtils.getCurrentActiveService().getDomain() + mSavedNotes.get(getAdapterPosition()).getSlug();

                ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText(mContext.getString(R.string.app_name), delDogUrl);
                clipboard.setPrimaryClip(clip);

                Toast.makeText(mContext, mContext.getString(R.string.copied_to_clipboard, delDogUrl), Toast.LENGTH_SHORT).show();
                return true;
            });
        }

        public void bindTo(SavedNote savedNote) {
            mURLText.setText(savedNote.getSlug());
            mTimeText.setText(savedNote.getTime());
        }
    }
}
