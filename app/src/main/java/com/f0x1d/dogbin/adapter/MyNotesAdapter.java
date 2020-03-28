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
import com.f0x1d.dogbin.db.entity.MyNote;
import com.f0x1d.dogbin.ui.activity.text.DogBinTextViewerActivity;

import java.util.ArrayList;
import java.util.List;

public class MyNotesAdapter extends RecyclerView.Adapter<MyNotesAdapter.MyNoteViewHolder> {

    private List<MyNote> mMyNotes = new ArrayList<>();

    private Context mContext;

    public MyNotesAdapter(Context context) {
        this.mContext = context;
    }

    @NonNull
    @Override
    public MyNoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyNoteViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_note, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyNoteViewHolder holder, int position) {
        holder.bindTo(mMyNotes.get(position));
    }

    @Override
    public int getItemCount() {
        return mMyNotes.size();
    }

    public void setNotes(List<MyNote> myNotes, boolean toStart) {
        mMyNotes.clear();
        if (toStart) {
            for (MyNote myNote : myNotes) {
                mMyNotes.add(0, myNote);
            }
        } else
            mMyNotes.addAll(myNotes);
    }

    public class MyNoteViewHolder extends RecyclerView.ViewHolder {

        TextView mURLText;
        TextView mTimeText;

        public MyNoteViewHolder(@NonNull View itemView) {
            super(itemView);

            this.mURLText = itemView.findViewById(R.id.url_text);
            this.mTimeText = itemView.findViewById(R.id.time_text);

            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(mContext, DogBinTextViewerActivity.class);
                intent.setData(Uri.parse("https://del.dog/" + mMyNotes.get(getAdapterPosition()).getSlug()));
                intent.putExtra("my_note", true);

                mContext.startActivity(intent);
            });
            itemView.setOnLongClickListener(v -> {
                String delDogUrl = "https://del.dog/" + mMyNotes.get(getAdapterPosition()).getSlug();

                ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText(mContext.getString(R.string.app_name), delDogUrl);
                clipboard.setPrimaryClip(clip);

                Toast.makeText(mContext, mContext.getString(R.string.copied_to_clipboard, delDogUrl), Toast.LENGTH_SHORT).show();
                return true;
            });
        }

        public void bindTo(MyNote myNote) {
            mURLText.setText(myNote.getSlug());
            mTimeText.setText(myNote.getTime());
        }
    }
}
