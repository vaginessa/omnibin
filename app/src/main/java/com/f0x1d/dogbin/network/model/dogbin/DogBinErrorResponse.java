package com.f0x1d.dogbin.network.model.dogbin;

import com.f0x1d.dogbin.network.model.ErrorResponse;
import com.google.gson.annotations.SerializedName;

public class DogBinErrorResponse implements ErrorResponse {

    @SerializedName("message")
    private String mMessage;

    public void setMessage(String message) {
        this.mMessage = message;
    }

    public String getMessage() {
        return mMessage;
    }

    @Override
    public String getErrorMessage() {
        return getMessage();
    }
}
