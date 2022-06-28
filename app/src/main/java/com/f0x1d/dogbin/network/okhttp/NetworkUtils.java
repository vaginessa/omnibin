package com.f0x1d.dogbin.network.okhttp;

import com.f0x1d.dogbin.network.model.ErrorResponse;
import com.google.gson.Gson;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;

public class NetworkUtils {

    private static Gson sGson = new Gson();

    public static RequestBody createBody(Object o) {
        return RequestBody.create(sGson.toJson(o).getBytes());
    }

    public static <T extends ErrorResponse> void checkResponseForError(Response<?> response, Class<T> clazz) throws Exception {
        ResponseBody responseBody = response.errorBody();
        if (responseBody == null)
            return;

        ErrorResponse errorResponse = sGson.fromJson(responseBody.string(), clazz);
        if (errorResponse == null) {
            throw new Exception(response.toString());
        }
        throw new Exception(errorResponse.getErrorMessage());
    }
}
