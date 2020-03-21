package com.f0x1d.dogbin.network.okhttp.badmanners;

import androidx.annotation.NonNull;

import com.franmontiel.persistentcookiejar.cache.CookieCache;
import com.franmontiel.persistentcookiejar.persistence.CookiePersistor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.HttpUrl;


public class ModifiablePersistentCookieJar implements ModifiableCookieJar {

    public static final String DOGGY_CLIENT_COOKIE_NAME = "doggie_session";

    private CookieCache cache;
    private CookiePersistor persistor;

    public ModifiablePersistentCookieJar(CookieCache cache, CookiePersistor persistor) {
        this.cache = cache;
        this.persistor = persistor;

        this.cache.addAll(persistor.loadAll());
    }

    private static List<Cookie> filterPersistentCookies(List<Cookie> cookies) {
        List<Cookie> persistentCookies = new ArrayList<>();

        for (Cookie cookie : cookies) {
            if (cookie.persistent()) {
                persistentCookies.add(cookie);
            }
        }
        return persistentCookies;
    }

    private static boolean isCookieExpired(Cookie cookie) {
        return cookie.expiresAt() < System.currentTimeMillis();
    }

    @Override
    synchronized public void saveFromResponse(@NonNull HttpUrl url, @NonNull List<Cookie> cookies) {
        cache.addAll(cookies);
        persistor.saveAll(filterPersistentCookies(cookies));
    }

    @NonNull
    @Override
    synchronized public List<Cookie> loadForRequest(@NonNull HttpUrl url) {
        List<Cookie> cookiesToRemove = new ArrayList<>();
        List<Cookie> validCookies = new ArrayList<>();

        for (Iterator<Cookie> it = cache.iterator(); it.hasNext(); ) {
            Cookie currentCookie = it.next();

            if (isCookieExpired(currentCookie)) {
                cookiesToRemove.add(currentCookie);
                it.remove();

            } else if (currentCookie.matches(url)) {
                validCookies.add(currentCookie);
            }
        }

        persistor.removeAll(cookiesToRemove);

        return validCookies;
    }

    @Override
    synchronized public void clearSession() {
        cache.clear();
        cache.addAll(persistor.loadAll());
    }

    @Override
    synchronized public void clear() {
        cache.clear();
        persistor.clear();
    }

    @Override
    public CookieCache getCookieCache() {
        return cache;
    }

    public boolean isDoggyClientCookieSaved() {
        for (Cookie cookie : getSavedCookies()) {
            if (cookie.name().equals(DOGGY_CLIENT_COOKIE_NAME))
                return true;
        }
        return false;
    }

    public List<Cookie> getSavedCookies() {
        List<Cookie> savedCookies = new ArrayList<>();
        for (Cookie cookie : cache) {
            savedCookies.add(cookie);
        }

        return savedCookies;
    }

    @Override
    synchronized public void persistCookieCache() {
        persistor.clear();

        List<Cookie> cacheList = new ArrayList<>();
        for (Cookie cookie : cache)
            cacheList.add(cookie);

        persistor.saveAll(cacheList);
    }
}
