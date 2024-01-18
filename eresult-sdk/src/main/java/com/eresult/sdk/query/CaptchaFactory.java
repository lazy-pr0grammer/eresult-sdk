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
public class CaptchaFactory implements LazyHttp.CallFactory<byte[]> {

    private final String subPath;

    public CaptchaFactory() {
        this.subPath = "/v2/captcha";
    }

    @Override
    public Call createCall(OkHttpClient client, Request request) {
        return client.newCall(
                request.newBuilder()
                        .url(request.url().newBuilder().addPathSegments(subPath)
                                .addQueryParameter("t", String.valueOf(System.currentTimeMillis()))
                                .build().toString())
                        .build());
    }

    @Override
    public void enqueueCall(Call call, LazyHttp.Callback<byte[]> callback, Class<byte[]> responseType) {
        call.enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                callback.onFailure(call, e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                callback.onResponse(call, parseResponse(response, responseType));
            }
        });
    }

    @Override
    public byte[] parseResponse(Response response, Class<byte[]> responseType) throws IOException {
        if (responseType == byte[].class && response != null) {
            assert response.body() != null;
            return response.body().bytes();
        } else {
            assert response != null;
            return response.message().getBytes();
        }
    }
}
