package com.badmanners.okhttp;

import com.franmontiel.persistentcookiejar.ClearableCookieJar;
import com.franmontiel.persistentcookiejar.cache.CookieCache;


public interface ModifiableCookieJar extends ClearableCookieJar {

    CookieCache getCookieCache();

    void persistCookieCache();
}
