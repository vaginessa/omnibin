package com.f0x1d.dogbin.network.model;

import com.google.gson.annotations.SerializedName;

public class ApiKeyResponse {

    @SerializedName("username")
    private String mUsername;

    @SerializedName("apiKey")
    private String mApiKey;

    public void setUsername(String username) {
        this.mUsername = username;
    }

    public String getUsername() {
        return mUsername;
    }

    public void setApiKey(String apiKey) {
        this.mApiKey = apiKey;
    }

    public String getApiKey() {
        return mApiKey;
    }
}
