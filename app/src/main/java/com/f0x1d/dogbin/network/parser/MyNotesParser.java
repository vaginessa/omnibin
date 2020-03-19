package com.f0x1d.dogbin.network.parser;

import com.f0x1d.dogbin.db.entity.MyNote;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyNotesParser {

    public static List<MyNote> parse(String meContent) {
        List<MyNote> myNotes = new ArrayList<>();

        Pattern patternNotesList = Pattern.compile("<h2>Documents</h2>(.+?)<h2>", Pattern.DOTALL);
        Matcher matcherNotesList = patternNotesList.matcher(meContent);
        if (matcherNotesList.find()) {
            String notesList = matcherNotesList.group(1).replace("<h2>Documents</h2>", "").replace("<h2>", "");

            Pattern notePattern = Pattern.compile("<a .+?>(.+?)</a> (.+?)</p>", Pattern.DOTALL);
            Matcher noteMatcher = notePattern.matcher(notesList);

            while (noteMatcher.find()) {
                MyNote myNote = new MyNote();
                myNote.slug = noteMatcher.group(1);
                myNote.time = noteMatcher.group(2).replace("(", "").replace(")", "");

                myNotes.add(myNote);
            }
        }

        return myNotes;
    }
}
