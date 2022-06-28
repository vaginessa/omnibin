package com.f0x1d.dogbin.utils;

import java.util.Date;

public class TimeUtils {

    public static String currentTimeToString() {
        return new Date(System.currentTimeMillis()).toLocaleString();
    }
}
