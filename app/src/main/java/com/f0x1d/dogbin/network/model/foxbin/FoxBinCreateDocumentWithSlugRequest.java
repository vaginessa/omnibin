package com.f0x1d.dogbin.network.model.foxbin;

import com.google.gson.annotations.SerializedName;

public class FoxBinCreateDocumentWithSlugRequest extends FoxBinCreateDocumentRequest {

    @SerializedName("slug")
    private String slug;

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }
}
