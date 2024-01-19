/**
 * CaptchaFactory class is responsible for creating HTTP requests to retrieve captcha images
 * from the eboardresults.com website. It implements the LazyHttp.CallFactory interface to generate
 * and enqueue HTTP calls asynchronously.
 * <p>
 * Created by Anindya Das on 1/17/24 11:45 PM.
 */
package com.eresult.sdk.data.query;

import androidx.annotation.NonNull;

import com.eresult.sdk.data.query.http.LazyHttp;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CaptchaFactory implements LazyHttp.CallFactory<byte[]> {

    // Variable to store the cookie received in the response header.
    public String cookie;

    // Subpath for captcha image retrieval.
    private final String subPath;

    /**
     * Constructor for CaptchaFactory.
     */
    public CaptchaFactory() {
        this.subPath = "/v2/captcha";
    }

    /**
     * Creates an HTTP call using the provided OkHttpClient and Request.
     *
     * @param client  OkHttpClient instance.
     * @param request Request instance for the HTTP call.
     * @return Call instance for the HTTP request.
     */
    @Override
    public Call createCall(@NonNull OkHttpClient client, @NonNull Request request) {
        return client.newCall(
                request.newBuilder()
                        .url(request.url().newBuilder().addPathSegments(subPath)
                                .addQueryParameter("t", String.valueOf(System.currentTimeMillis()))
                                .build().toString())
                        .build());
    }

    /**
     * Enqueues the HTTP call for asynchronous execution and sets up callbacks for success or failure.
     *
     * @param call         Call instance representing the HTTP request.
     * @param callback     Callback to handle the response or failure.
     * @param responseType Class type of the expected response.
     */
    @Override
    public void enqueueCall(
            @NonNull Call call, @NonNull LazyHttp.Callback<byte[]> callback, @NonNull Class<byte[]> responseType) {
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

    /**
     * Parses the HTTP response into the specified response type (byte[] in this case).
     *
     * @param response     Response instance received from the HTTP call.
     * @param responseType Class type of the expected response.
     * @return Captcha image as a byte array or an error message.
     */
    @Override
    public byte[] parseResponse(@NonNull Response response, @NonNull Class<byte[]> responseType) throws IOException {
        // Extract and store the cookie from the response header.
        cookie = response.headers().values("Set-Cookie").get(0);

        if (responseType == byte[].class) {
            assert response.body() != null;
            return response.body().bytes();
        } else {
            return response.message().getBytes();
        }
    }
}