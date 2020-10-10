package com.f0x1d.dogbin.network.model;

import com.google.gson.annotations.SerializedName;

public class ErrorResponse {

    @SerializedName("message")
    private String mMessage;

    public void setMessage(String message) {
        this.mMessage = message;
    }

    public String getMessage() {
        return mMessage;
    }
}
