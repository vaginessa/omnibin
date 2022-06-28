package com.f0x1d.dogbin.utils.services;

import com.f0x1d.dmsdk.model.UserDocument;
import com.f0x1d.dogbin.db.entity.FoxBinSavedNote;
import com.f0x1d.dogbin.db.entity.PastebinSavedNote;

import java.util.ArrayList;
import java.util.List;

public class ServicesConvertors {

    public static List<UserDocument> toUserNotesPastebin(List<PastebinSavedNote> savedNotes, boolean myNotes) {
        List<UserDocument> userDocuments = new ArrayList<>();

        for (PastebinSavedNote savedNote : savedNotes) {
            userDocuments.add(UserDocument.createDocument(savedNote.getSlug(), savedNote.getTime(), myNotes));
        }

        return userDocuments;
    }

    public static List<UserDocument> toUserNotesFoxBin(List<FoxBinSavedNote> savedNotes, boolean myNotes) {
        List<UserDocument> userDocuments = new ArrayList<>();

        for (FoxBinSavedNote savedNote : savedNotes) {
            userDocuments.add(UserDocument.createDocument(savedNote.getSlug(), savedNote.getTime(), myNotes));
        }

        return userDocuments;
    }
}
