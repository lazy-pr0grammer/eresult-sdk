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

    public String cookie;
    private final String subPath;
    private final String userAgent;
    private final String secChUaSt;
    private final String cacheControl;
    private final String secFetchDest;
    private final String secFetchSite;
    private final String secFetchUser;
    private final String secFetchMode;
    private final String acceptEncoding;
    private final String acceptLanguage;
    private final String secChUaMobileSt;
    private final String secChuPlatformSt;

    public ConnectionFactory() {
        this.secFetchUser = "?1";
        this.secFetchSite = "none";
        this.secChUaMobileSt = "?0";
        this.secFetchDest = "document";
        this.secFetchMode = "navigate";
        this.cacheControl = "max-age=0";
        this.subPath = "/en/ebr.app/home/";
        this.secChuPlatformSt = "\"Linux\"";
        this.acceptEncoding = "gzip, deflate";
        this.acceptLanguage = "en-US,en;q=0.9";
        this.secChUaSt = "\"Chromium\";v=\"116\", \"Not)A;Brand\";v=\"24\", \"Google Chrome\";v=\"116\"";
        this.userAgent = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/116.0.0.0 Safari/537.36";
    }

    @Override
    public Call createCall(@NotNull OkHttpClient client, @NotNull Request request) {
        int insecureRequests = 1;
        return client.newCall(
                request.newBuilder()
                        .addHeader("Sec-Ch-Ua-Mobile", secChUaMobileSt)
                        .addHeader("Sec-Ch-Ua-Platform", secChuPlatformSt)
                        .addHeader("Sec-Fetch-Dest", secFetchDest)
                        .addHeader("Sec-Fetch-Mode", secFetchMode)
                        .addHeader("Sec-Fetch-Site", secFetchSite)
                        .addHeader("Sec-Fetch-User", secFetchUser)
                        .addHeader("Accept-Language", acceptLanguage)
                        .addHeader("Accept-Encoding", acceptEncoding)
                        .addHeader("Cache-Control", cacheControl)
                        .addHeader("User-Agent", userAgent)
                        .addHeader("Sec-Ch-Ua", secChUaSt)
                        .addHeader("Upgrade-Insecure-Requests", String.valueOf(insecureRequests))
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
                callback.onResponse(call, parseResponse(response, responseType));
            }
        });
    }

    @Override
    public T parseResponse(Response response, Class<T> responseType) throws IOException {
        cookie = response.header("Cookie");
        if (responseType == String.class && response != null) {
            assert response.body() != null;
            return (T) response.body().string();
        } else return (T) response;
    }
}