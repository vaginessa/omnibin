package com.f0x1d.testservice.network.parser;

import com.f0x1d.dmsdk.model.UserNote;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DarkNetParser {

    public static List<UserNote> parse(String html) throws Exception {
        List<UserNote> userNotes = new ArrayList<>();

        Pattern pattern = Pattern.compile(".+?<tr>.+?<td><img.+? \\/><a href=\"\\/(.+?)\">(.+?)<\\/a>", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(html);

        while (matcher.find())
            userNotes.add(UserNote.createNote(matcher.group(1), matcher.group(2)));

        return userNotes;
    }

}
