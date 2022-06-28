package com.f0x1d.dogbin.network.service.foxbin;

import com.f0x1d.dmsdk.BinService;
import com.f0x1d.dmsdk.module.AuthModule;
import com.f0x1d.dogbin.App;
import com.f0x1d.dogbin.network.model.foxbin.FoxBinErrorResponse;
import com.f0x1d.dogbin.network.model.foxbin.FoxBinLoginRegisterRequest;
import com.f0x1d.dogbin.network.model.foxbin.FoxBinLoginRegisterResponse;
import com.f0x1d.dogbin.network.okhttp.NetworkUtils;
import com.f0x1d.dogbin.network.retrofit.foxbin.FoxBinApi;
import retrofit2.Response;

public class FoxBinAuth extends AuthModule {

    public FoxBinAuth(BinService binService) {
        super(binService);
    }

    @Override
    public String getUsername() throws Exception {
        return App.getPreferencesUtil().getFoxBinUsername();
    }

    @Override
    public void login(String username, String password) throws Exception {
        FoxBinLoginRegisterRequest foxBinLoginRegisterRequest = FoxBinLoginRegisterRequest.create(username, password);
        doAuth(FoxBinApi.getInstance().getService().login(NetworkUtils.createBody(foxBinLoginRegisterRequest)).execute(), username);
    }

    @Override
    public void register(String username, String password) throws Exception {
        FoxBinLoginRegisterRequest foxBinLoginRegisterRequest = FoxBinLoginRegisterRequest.create(username, password);
        doAuth(FoxBinApi.getInstance().getService().register(NetworkUtils.createBody(foxBinLoginRegisterRequest)).execute(), username);
    }

    private void doAuth(Response<FoxBinLoginRegisterResponse> response, String username) throws Exception {
        NetworkUtils.checkResponseForError(response, FoxBinErrorResponse.class);

        FoxBinLoginRegisterResponse foxBinLoginRegisterResponse = response.body();
        App.getPreferencesUtil().setFoxBinToken(foxBinLoginRegisterResponse.getAccessToken());
        App.getPreferencesUtil().setFoxBinUsername(username);
    }

    @Override
    public boolean loggedIn() {
        return App.getPreferencesUtil().getFoxBinToken() != null;
    }

    @Override
    public void logout() {
        App.getPreferencesUtil().setFoxBinToken(null);
    }
}
