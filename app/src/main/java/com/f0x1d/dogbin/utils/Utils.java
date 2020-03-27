package com.f0x1d.dogbin.utils;

import com.f0x1d.dogbin.db.entity.MyNote;
import com.f0x1d.dogbin.db.entity.SavedNote;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class Utils {

    public static String currentTimeToString() {
        Date date = new Date(System.currentTimeMillis());
        DateFormat formatter = new SimpleDateFormat("dd.MM.yy HH:mm", Locale.getDefault());
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));

        return formatter.format(date);
    }

    public static List<MyNote> savedNotesToMyNotes(List<SavedNote> savedNotes) {
        List<MyNote> myNotes = new ArrayList<>();

        for (SavedNote savedNote : savedNotes) {
            myNotes.add(0, MyNote.createNote(savedNote.getSlug(), savedNote.getTime()));
        }

        return myNotes;
    }
}
