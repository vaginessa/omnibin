package com.f0x1d.dogbin.network.service.pastebin;

import com.f0x1d.dmsdk.BinService;
import com.f0x1d.dmsdk.module.AuthModule;
import com.f0x1d.dogbin.App;
import com.f0x1d.dogbin.BuildConfig;
import com.f0x1d.dogbin.network.retrofit.pastebin.PasteBinApi;

import okhttp3.MultipartBody;

public class PasteBinAuth extends AuthModule {

    public PasteBinAuth(BinService binService) {
        super(binService);
    }

    @Override
    public String getUsername() throws Exception {
        return "pastebin";
    }

    @Override
    public void login(String username, String password) throws Exception {
        String token = PasteBinApi.getInstance().getService().login(
                new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("api_dev_key", BuildConfig.PASTEBIN_API_KEY)
                        .addFormDataPart("api_user_name", username)
                        .addFormDataPart("api_user_password", password)
                        .build()
        ).execute().body();

        if (!PasteBinService.isResponseOk(token))
            throw new Exception(token);

        App.getPreferencesUtil().setPastebinToken(token);
    }

    @Override
    public void register(String username, String password) throws Exception {
        throw new Exception("Registration is not supported");
    }

    @Override
    public boolean loggedIn() {
        return App.getPreferencesUtil().getPastebinToken() != null;
    }

    @Override
    public void logout() {
        App.getPreferencesUtil().setPastebinToken(null);
    }
}
