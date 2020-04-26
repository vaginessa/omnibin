package com.f0x1d.dogbin.network.parser;

import android.widget.Toast;

import com.f0x1d.dogbin.App;
import com.f0x1d.dogbin.R;
import com.f0x1d.dogbin.db.entity.SavedNote;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyNotesParser {

    public static List<SavedNote> parse(String meContent) {
        try {
            List<SavedNote> myNotes = new ArrayList<>();

            Pattern patternNotesList = Pattern.compile("<h2>Documents</h2>(.+?)<h2>", Pattern.DOTALL);
            Matcher matcherNotesList = patternNotesList.matcher(meContent);
            if (matcherNotesList.find()) {
                String notesList = matcherNotesList.group(1).replace("<h2>Documents</h2>", "").replace("<h2>", "");

                Pattern notePattern = Pattern.compile("<a .+?>(.+?)</a> (.+?)</p>", Pattern.DOTALL);
                Matcher noteMatcher = notePattern.matcher(notesList);

                while (noteMatcher.find())
                    myNotes.add(SavedNote.createNote("", noteMatcher.group(1), noteMatcher.group(2).replace("(", "").replace(")", "")));
            }

            return myNotes;
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(App.getInstance(), App.getInstance().getString(R.string.error, e.getLocalizedMessage()), Toast.LENGTH_SHORT).show();

            return Collections.emptyList();
        }
    }
}
