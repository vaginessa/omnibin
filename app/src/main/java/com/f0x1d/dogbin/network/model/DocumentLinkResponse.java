package com.f0x1d.dogbin.network.model;

import com.squareup.moshi.Json;

public class DocumentLinkResponse {

    @Json(name = "isUrl")
    public boolean isUrl;

    // Slug is a note's url
    @Json(name = "key")
    public String slug;
}
