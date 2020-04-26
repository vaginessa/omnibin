package com.f0x1d.dogbin.network.parser;

import android.widget.Toast;

import com.f0x1d.dogbin.App;
import com.f0x1d.dogbin.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UsernameParser {

    public static String parse(String meContent) {
        try {
            Pattern usernamePattern = Pattern.compile("<div class=\"center-inside\">.+?<div class=\"card\">.+?<h1>(.+?)<\\/h1>", Pattern.DOTALL);
            Matcher usernameMatcher = usernamePattern.matcher(meContent);
            if (usernameMatcher.find()) {
                return usernameMatcher.group(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(App.getInstance(), App.getInstance().getString(R.string.error, e.getLocalizedMessage()), Toast.LENGTH_SHORT).show();
        }

        return "";
    }
}
