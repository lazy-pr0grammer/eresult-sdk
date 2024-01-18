package com.eresult.sdk.query;

import androidx.annotation.NonNull;

import com.eresult.sdk.query.http.LazyHttp;

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
    private final String subPath;

    public CaptchaFactory() {
        this.subPath = "/v2/captcha";
    }

    @Override
    public Call createCall(@NonNull OkHttpClient client, @NonNull Request request) {
        return client.newCall(
                request.newBuilder()
                        .url(request.url().newBuilder().addPathSegments(subPath)
                                .addQueryParameter("t", String.valueOf(System.currentTimeMillis()))
                                .build().toString())
                        .build());
    }

    @Override
    public void enqueueCall(@NonNull Call call, @NonNull LazyHttp.Callback<byte[]> callback, @NonNull Class<byte[]> responseType) {
        call.enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onFailure(call, e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                callback.onResponse(call, parseResponse(response, responseType));
            }
        });
    }

    @Override
    public byte[] parseResponse(@NonNull Response response, @NonNull Class<byte[]> responseType) throws IOException {
        cookie = response.headers().values("Set-Cookie").get(0);
        if (responseType == byte[].class) {
            assert response.body() != null;
            return response.body().bytes();
        } else {
            return response.message().getBytes();
        }
    }
}
