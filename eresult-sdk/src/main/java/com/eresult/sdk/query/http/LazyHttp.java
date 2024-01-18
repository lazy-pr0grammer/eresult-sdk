package com.eresult.sdk.query.http;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Anindya Das on 1/17/24 11:20 AM
 **/
public class LazyHttp {

    private final HttpUrl httpUrl;
    private final OkHttpClient client;

    private LazyHttp(@NotNull Builder builder) {
        this.client = builder.client;
        this.httpUrl = HttpUrl.parse(builder.baseUrl);
    }

    public static class Builder {
        private String baseUrl;
        private final OkHttpClient client = new OkHttpClient();

        public Builder baseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        public LazyHttp build() {
            if (baseUrl == null || baseUrl.isEmpty()) {
                throw new IllegalArgumentException("Base url must be set!");
            }
            return new LazyHttp(this);
        }
    }

    public <T> T query(@NotNull CallFactory<T> callFactory, Class<T> responseType) {
        Request request = new Request.Builder().url(httpUrl).build();
        Call call = callFactory.createCall(client, request);

        try {
            Response response = call.execute();
            return callFactory.parseResponse(response, responseType);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public <T> void queryAsync(@NotNull CallFactory<T> callFactory, Class<T> responseType, Callback<T> callback) {
        Request request = new Request.Builder().url(httpUrl).build();
        callFactory.enqueueCall(callFactory.createCall(client, request), callback, responseType);
    }

    public interface Callback<T> {
        void onResponse(Call call, T response);

        void onFailure(Call call, IOException e);
    }

    public interface CallFactory<T> {
        Call createCall(OkHttpClient client, Request request);

        void enqueueCall(Call call, Callback<T> callback, Class<T> responseType);

        T parseResponse(Response response, Class<T> responseType) throws IOException;
    }

    /**
     * Under close inspection
     * Please do not touch this class until you think you can make it better
     */
    public static class Result<T> {
        public T response;
        public String cookie;

        public Result(T response, String cookie) {
            this.cookie = cookie;
            this.response = response;
        }
    }
}
