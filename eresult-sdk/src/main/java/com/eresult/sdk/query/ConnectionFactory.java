package com.eresult.sdk.query;

import com.eresult.sdk.query.http.LazyHttp;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Anindya Das on 1/17/24 11:29 PM
 **/
public class ConnectionFactory<T> implements LazyHttp.CallFactory<T> {

    private final String subPath;

    public ConnectionFactory() {
        this.subPath = "/en/ebr.app/home/";
    }

    @Override
    public Call createCall(@NotNull OkHttpClient client, @NotNull Request request) {
        return client.newCall(
                request.newBuilder()
                        .url(request.url().newBuilder().addEncodedPathSegments(subPath).build())
                        .build());
    }

    @Override
    public void enqueueCall(@NotNull Call call, LazyHttp.Callback<T> callback, Class<T> responseType) {
        call.enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                callback.onFailure(call, e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                T result = parseResponse(response, responseType);
                callback.onResponse(call, result);
            }
        });
    }

    @Override
    public T parseResponse(Response response, Class<T> responseType) throws IOException {
        if (responseType == String.class && response != null) {
            assert response.body() != null;
            return (T) response.body().string();
        } else return (T) response;
    }
}