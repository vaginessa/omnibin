package com.f0x1d.dogbin.network.model.foxbin;

import com.f0x1d.dogbin.network.model.foxbin.base.BaseFoxBinResponse;
import com.google.gson.annotations.SerializedName;

public class FoxBinCreatedDocumentResponse extends BaseFoxBinResponse {

    @SerializedName("slug")
    private String slug;

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }
}
