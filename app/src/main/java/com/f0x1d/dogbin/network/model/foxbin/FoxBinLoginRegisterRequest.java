package com.f0x1d.dogbin.network.model.foxbin;

import com.google.gson.annotations.SerializedName;

public class FoxBinLoginRegisterRequest {

    @SerializedName("username")
    private String username;

    @SerializedName("password")
    private String password;

    public static FoxBinLoginRegisterRequest create(String username, String password) {
        FoxBinLoginRegisterRequest request = new FoxBinLoginRegisterRequest();
        request.setUsername(username);
        request.setPassword(password);
        return request;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
