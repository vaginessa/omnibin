package com.f0x1d.dogbin.network.model.dogbin;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DogBinAuthRequest {

    @SerializedName("username")
    private String mUsername;

    @SerializedName("password")
    private String mPassword;

    @SerializedName("permissions")
    private List<String> mPermissions;

    @SerializedName("application")
    private String mApplicationName;

    public static DogBinAuthRequest create(String username, String password, List<String> permissions, String applicationName) {
        DogBinAuthRequest dogBinAuthRequest = new DogBinAuthRequest();
        dogBinAuthRequest.setUsername(username);
        dogBinAuthRequest.setPassword(password);
        dogBinAuthRequest.setPermissions(permissions);
        dogBinAuthRequest.setApplicationName(applicationName);
        return dogBinAuthRequest;
    }

    public void setUsername(String username) {
        this.mUsername = username;
    }

    public String getUsername() {
        return mUsername;
    }

    public void setPassword(String password) {
        this.mPassword = password;
    }

    public String getPassword() {
        return mPassword;
    }

    public void setPermissions(List<String> permissions) {
        this.mPermissions = permissions;
    }

    public List<String> getPermissions() {
        return mPermissions;
    }

    public void setApplicationName(String applicationName) {
        this.mApplicationName = applicationName;
    }

    public String getApplicationName() {
        return mApplicationName;
    }
}
