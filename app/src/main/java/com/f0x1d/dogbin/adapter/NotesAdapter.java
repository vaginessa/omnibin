package com.f0x1d.dogbin.adapter;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.appcompat.widget.PopupMenu;
import com.f0x1d.dmsdk.model.UserDocument;
import com.f0x1d.dogbin.R;
import com.f0x1d.dogbin.adapter.base.BaseAdapter;
import com.f0x1d.dogbin.adapter.base.BaseViewHolder;
import com.google.android.material.card.MaterialCardView;

public class NotesAdapter extends BaseAdapter<UserDocument, NotesAdapter.NoteViewHolder> {

    private final OnNoteClickedListener mOnNoteClickedListener;

    public NotesAdapter(OnNoteClickedListener onNoteClickedListener) {
        this.mOnNoteClickedListener = onNoteClickedListener;
    }

    @Override
    protected NoteViewHolder createHolder(ViewGroup parent, LayoutInflater layoutInflater) {
        return new NoteViewHolder(layoutInflater.inflate(R.layout.item_note, parent, false));
    }

    public interface OnNoteClickedListener {
        void clicked(UserDocument userDocument);
        void delete(UserDocument userDocument);
        void copyUrl(UserDocument userDocument);
        boolean isDeletable(UserDocument userDocument);
    }

    class NoteViewHolder extends BaseViewHolder<UserDocument> {

        private MaterialCardView mContentCard;
        private TextView mURLText;
        private TextView mTimeText;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);

            this.mContentCard = itemView.findViewById(R.id.content_card);
            this.mURLText = itemView.findViewById(R.id.url_text);
            this.mTimeText = itemView.findViewById(R.id.time_text);

            PopupMenu popupMenu = new PopupMenu(mContentCard.getContext(), itemView);
            popupMenu.inflate(R.menu.item_long_click_menu);
            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.open_item:
                        openDocument();
                        return true;

                    case R.id.copy_link_item:
                        mOnNoteClickedListener.copyUrl(mElements.get(getAdapterPosition()));
                        return true;

                    case R.id.delete_item:
                        mOnNoteClickedListener.delete(mElements.get(getAdapterPosition()));
                        return true;

                    default:
                        return false;
                }
            });

            mContentCard.setOnClickListener(v -> openDocument());
            mContentCard.setOnLongClickListener(v -> {
                MenuPopupHelper menuPopupHelper = new MenuPopupHelper(mContentCard.getContext(), checkDeletable(popupMenu), itemView);
                menuPopupHelper.setForceShowIcon(true);
                menuPopupHelper.show();
                return true;
            });
        }

        public void bindTo(UserDocument userDocument) {
            mURLText.setText(userDocument.getSlug());
            mTimeText.setText(userDocument.getTime());
        }

        private MenuBuilder checkDeletable(PopupMenu popupMenu) {
            Menu menu = popupMenu.getMenu();
            menu.findItem(R.id.delete_item).setVisible(mOnNoteClickedListener.isDeletable(mElements.get(getAdapterPosition())));
            return (MenuBuilder) menu;
        }

        private void openDocument() {
            mOnNoteClickedListener.clicked(mElements.get(getAdapterPosition()));
        }
    }
}
