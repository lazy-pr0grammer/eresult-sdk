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

    public String cookie;
    public String accept;
    public String acceptEncoding;
    private final String subPath;
    private String acceptLanguage;

    public CaptchaFactory() {
        this.subPath = "/v2/captcha";
    }

    @Override
    public Call createCall(@NotNull OkHttpClient client, @NotNull Request request) {
        return client.newCall(
                request.newBuilder()
                        .url(request.url().newBuilder().addPathSegments(subPath)
                                .addQueryParameter("t", String.valueOf(System.currentTimeMillis()))
                                .build().toString())
                        .build());
    }

    @Override
    public void enqueueCall(@NotNull Call call, @NotNull LazyHttp.Callback<byte[]> callback, @NotNull Class<byte[]> responseType) {
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
    public byte[] parseResponse(@NotNull Response response, @NotNull Class<byte[]> responseType) throws IOException {
        cookie = response.headers().values("Set-Cookie").get(0);
        if (responseType == byte[].class) {
            assert response.body() != null;
            return response.body().bytes();
        } else {
            return response.message().getBytes();
        }
    }
}
