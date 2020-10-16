package com.f0x1d.dogbin.network.parser;

import com.f0x1d.dmsdk.model.UserDocument;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DarkNetParser {

    public static List<UserDocument> parse(String html) throws Exception {
        List<UserDocument> userDocuments = new ArrayList<>();

        Pattern pattern = Pattern.compile(".+?<tr>.+?<td><span.+?</span><a href=\"/(.+?)\">(.+?)</a>", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(html);

        while (matcher.find())
            userDocuments.add(UserDocument.createDocument(matcher.group(1), matcher.group(2)));

        return userDocuments;
    }

}
