package com.f0x1d.dogbin.network.okhttp;

import com.f0x1d.dogbin.network.model.ErrorResponse;
import com.f0x1d.dogbin.utils.Utils;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;

public class NetworkUtils {

    public static RequestBody getBody(Object o) {
        return RequestBody.create(Utils.getGson().toJson(o).getBytes());
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

    public static <T extends ErrorResponse> void checkResponseForError(Response response, Class<T> clazz) throws Exception {
        ResponseBody responseBody = response.errorBody();
        if (responseBody == null)
            return;

        ErrorResponse errorResponse = Utils.getGson().fromJson(responseBody.string(), clazz);
        if (errorResponse == null) {
            throw new Exception(response.toString());
        }
        throw new Exception(errorResponse.getErrorMessage());
    }
}
