package com.eresult.sdk.query;

import com.eresult.sdk.query.http.LazyHttp;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Anindya Das on 1/17/24 11:45 PM
 **/
public class CaptchaFactory implements LazyHttp.CallFactory<String> {

    private final String subPath;

    public CaptchaFactory() {
        this.subPath = "/v2/captcha?t=" + System.currentTimeMillis();
    }

    @Override
    public Call createCall(OkHttpClient client, Request request) {
        return client.newCall(
                request.newBuilder()
                        .url(request.url().newBuilder().addEncodedPathSegments(subPath).build())
                        .build());
    }

    @Override
    public void enqueueCall(Call call, LazyHttp.Callback<String> callback, Class<String> responseType) {
        call.enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                callback.onFailure(call, e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String result = parseResponse(response, responseType);
                callback.onResponse(call, result);
            }
        });
    }

    @Override
    public String parseResponse(Response response, Class<String> responseType) throws IOException {
        if (responseType == String.class && response != null) {
            assert response.body() != null;
            return response.body().string();
        } else {
            assert response != null;
            return response.message();
        }
    }
}
