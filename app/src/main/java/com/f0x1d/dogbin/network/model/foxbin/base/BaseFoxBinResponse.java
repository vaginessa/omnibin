package com.f0x1d.dogbin.network.model.foxbin.base;

import com.google.gson.annotations.SerializedName;

public class BaseFoxBinResponse {

    @SerializedName("code")
    private int code;

    @SerializedName("ok")
    private boolean ok;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public boolean isOk() {
        return ok;
    }

    public void setOk(boolean ok) {
        this.ok = ok;
    }
}
