package com.f0x1d.dogbin.network.model.foxbin;

import com.f0x1d.dogbin.network.model.ErrorResponse;
import com.f0x1d.dogbin.network.model.foxbin.base.BaseFoxBinResponse;
import com.google.gson.annotations.SerializedName;

public class FoxBinErrorResponse extends BaseFoxBinResponse implements ErrorResponse {

    @SerializedName("error")
    private String error;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    @Override
    public String getErrorMessage() {
        return getError();
    }
}
