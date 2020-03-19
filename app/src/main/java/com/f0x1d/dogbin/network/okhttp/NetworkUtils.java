package com.f0x1d.dogbin.network.okhttp;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class NetworkUtils {

    public static RequestBody getAuthBody(String username, String password) {
        return new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("username", username)
                .addFormDataPart("password", password)
                .build();
    }

    public static RequestBody getDocumentBody(String data, String slug) {
        if (slug.isEmpty())
            return RequestBody.create(MediaType.get("text/plain"), data);
        else {
            return new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("data", data)
                    .addFormDataPart("slug", slug)
                    .build();
        }
    }
}
