package com.f0x1d.dogbin.network.model.foxbin;

import com.f0x1d.dogbin.network.model.foxbin.base.BaseFoxBinResponse;
import com.google.gson.annotations.SerializedName;

public class FoxBinGetNoteResponse extends BaseFoxBinResponse {

    @SerializedName("note")
    private FoxBinNote foxBinNote;

    public FoxBinNote getFoxBinNote() {
        return foxBinNote;
    }

    public void setFoxBinNote(FoxBinNote foxBinNote) {
        this.foxBinNote = foxBinNote;
    }

    public static class FoxBinNote {

        @SerializedName("slug")
        private String slug;

        @SerializedName("date")
        private long date;

        @SerializedName("editable")
        private boolean editable;

        @SerializedName("content")
        private String content;

        public String getSlug() {
            return slug;
        }

        public void setSlug(String slug) {
            this.slug = slug;
        }

        public long getDate() {
            return date;
        }

        public void setDate(long date) {
            this.date = date;
        }

        public boolean getEditable() {
            return editable;
        }

        public void setEditable(boolean editable) {
            this.editable = editable;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
}
