package com.f0x1d.testmodule.module;

import com.f0x1d.dmsdk.BinService;
import com.f0x1d.dmsdk.module.AuthModule;

public class TestAuthModule extends AuthModule {

    public TestAuthModule(BinService binService) {
        super(binService);
    }

    @Override
    public String getUsername() throws Exception {
        return null;
    }

    @Override
    public void login(String username, String password) throws Exception {

    }

    @Override
    public void register(String username, String password) throws Exception {

    }

    @Override
    public boolean loggedIn() {
        return false;
    }

    @Override
    public void logout() {

    }
}
