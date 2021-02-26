package com.f0x1d.dogbin.network.model.foxbin;

import com.f0x1d.dogbin.network.model.foxbin.base.BaseFoxBinResponse;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FoxBinGetNotesResponse extends BaseFoxBinResponse {

    @SerializedName("notes")
    private List<FoxBinGetNoteResponse.FoxBinNote> notes;

    public List<FoxBinGetNoteResponse.FoxBinNote> getNotes() {
        return notes;
    }

    public void setNotes(List<FoxBinGetNoteResponse.FoxBinNote> notes) {
        this.notes = notes;
    }
}
