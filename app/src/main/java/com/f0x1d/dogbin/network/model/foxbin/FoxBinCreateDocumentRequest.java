package com.f0x1d.dogbin.network.model.foxbin;

import com.google.gson.annotations.SerializedName;

public class FoxBinCreateDocumentRequest {

    @SerializedName("content")
    private String content;

    @SerializedName("accessToken")
    private String accessToken;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
