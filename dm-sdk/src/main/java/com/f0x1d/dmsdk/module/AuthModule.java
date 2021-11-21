package com.f0x1d.dmsdk.module;

import androidx.annotation.Keep;

import com.f0x1d.dmsdk.BinService;
import com.f0x1d.dmsdk.module.base.BaseModule;

@Keep
public abstract class AuthModule extends BaseModule {

    public AuthModule(BinService binService) {
        super(binService);
    }

    /**
     * Called only when user is logged in
     * @return user's username
     * @throws Exception
     */
    public abstract String getUsername() throws Exception;

    /**
     * Login button clicked
     * @param username
     * @param password
     * @throws Exception
     */
    public abstract void login(String username, String password) throws Exception;

    /**
     * Register button clicked
     * @param username
     * @param password
     * @throws Exception
     */
    public abstract void register(String username, String password) throws Exception;

    /**
     * @return true if user is logged in, false if not
     */
    public abstract boolean loggedIn();

    /**
     * Log out of account
     */
    public abstract void logout();
}
