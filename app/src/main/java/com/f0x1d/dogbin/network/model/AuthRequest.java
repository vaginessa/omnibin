package com.f0x1d.dogbin.network.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AuthRequest {

    @SerializedName("username")
    private String mUsername;

    @SerializedName("password")
    private String mPassword;

    @SerializedName("permissions")
    private List<String> mPermissions;

    @SerializedName("application")
    private String mApplicationName;

    public static AuthRequest create(String username, String password, List<String> permissions, String applicationName) {
        AuthRequest authRequest = new AuthRequest();
        authRequest.setUsername(username);
        authRequest.setPassword(password);
        authRequest.setPermissions(permissions);
        authRequest.setApplicationName(applicationName);
        return authRequest;
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
