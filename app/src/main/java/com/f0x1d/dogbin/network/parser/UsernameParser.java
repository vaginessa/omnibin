package com.f0x1d.dogbin.network.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UsernameParser {

    public static String parse(String meContent) {
        Pattern usernamePattern = Pattern.compile("<div class=\"center-inside\">.+?<div class=\"card\">.+?<h1>(.+?)<\\/h1>", Pattern.DOTALL);
        Matcher usernameMatcher = usernamePattern.matcher(meContent);
        if (usernameMatcher.find()) {
            return usernameMatcher.group(1);
        }
        return null;
    }
}
