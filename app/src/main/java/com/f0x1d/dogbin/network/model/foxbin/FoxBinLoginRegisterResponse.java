package com.f0x1d.dogbin.network.model.foxbin;

import com.f0x1d.dogbin.network.model.foxbin.base.BaseFoxBinResponse;
import com.google.gson.annotations.SerializedName;

public class FoxBinLoginRegisterResponse extends BaseFoxBinResponse {

    @SerializedName("accessToken")
    private String accessToken;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
