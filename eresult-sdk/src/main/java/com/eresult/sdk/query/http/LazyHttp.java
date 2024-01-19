/**
 * LazyHttp class provides a simplified interface for making synchronous and asynchronous HTTP requests.
 * It encapsulates OkHttp functionality and allows the creation of HTTP calls, synchronous queries, and
 * asynchronous queries with callbacks.
 * <p>
 * Created by Anindya Das on 1/17/24 11:20 AM.
 */
package com.eresult.sdk.query.http;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LazyHttp {

    // Base URL for the HTTP requests.
    private final HttpUrl httpUrl;

    // OkHttpClient instance for making HTTP requests.
    private final OkHttpClient client;

    /**
     * Private constructor for LazyHttp.
     *
     * @param builder Builder instance to build LazyHttp with required parameters.
     */
    private LazyHttp(@NonNull Builder builder) {
        this.client = builder.client;
        this.httpUrl = HttpUrl.parse(builder.baseUrl);
    }

    /**
     * Builder class for constructing LazyHttp instances.
     */
    public static class Builder {
        private String baseUrl;
        private final OkHttpClient client = new OkHttpClient();

        /**
         * Sets the base URL for HTTP requests.
         *
         * @param baseUrl Base URL for HTTP requests.
         * @return Builder instance.
         */
        public Builder baseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        /**
         * Builds and returns a LazyHttp instance with the specified parameters.
         *
         * @return Fully initialized LazyHttp instance.
         * @throws IllegalArgumentException if the base URL is not set.
         */
        public LazyHttp build() {
            if (baseUrl == null || baseUrl.isEmpty()) {
                throw new IllegalArgumentException("Base url must be set!");
            }
            return new LazyHttp(this);
        }
    }

    /**
     * Makes a synchronous HTTP query using the provided CallFactory and response type.
     *
     * @param callFactory  CallFactory instance responsible for creating HTTP calls.
     * @param responseType Class type of the expected response.
     * @param <T>          Type parameter representing the expected response type.
     * @return The parsed response.
     */
    public <T> T query(@NonNull CallFactory<T> callFactory, Class<T> responseType) {
        Request request = new Request.Builder().url(httpUrl).build();
        Call call = callFactory.createCall(client, request);

        try {
            Response response = call.execute();
            return callFactory.parseResponse(response, responseType);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Makes an asynchronous HTTP query using the provided CallFactory, response type, and callback.
     *
     * @param callFactory  CallFactory instance responsible for creating HTTP calls.
     * @param responseType Class type of the expected response.
     * @param callback     Callback to handle the asynchronous response or failure.
     * @param <T>          Type parameter representing the expected response type.
     */
    public <T> void queryAsync(@NonNull CallFactory<T> callFactory, Class<T> responseType, Callback<T> callback) {
        Request request = new Request.Builder().url(httpUrl).build();
        callFactory.enqueueCall(callFactory.createCall(client, request), callback, responseType);
    }

    /**
     * Callback interface for handling asynchronous HTTP responses or failures.
     *
     * @param <T> Type parameter representing the expected response type.
     */
    public interface Callback<T> {
        void onResponse(Call call, T response);

        void onFailure(Call call, IOException e);
    }

    /**
     * CallFactory interface for creating, enqueueing, and parsing HTTP calls.
     *
     * @param <T> Type parameter representing the expected response type.
     */
    public interface CallFactory<T> {
        Call createCall(OkHttpClient client, Request request);

        void enqueueCall(Call call, Callback<T> callback, Class<T> responseType);

        T parseResponse(Response response, Class<T> responseType) throws IOException;
    }

    /**
     * Result class represents the result of an HTTP query, containing the response and cookie information.
     *
     * @param <T> Type parameter representing the expected response type.
     */
    public static class Result<T> {
        public T response;
        public String cookie;

        /**
         * Constructor for Result class.
         *
         * @param response Parsed response from the HTTP query.
         * @param cookie   Cookie information obtained from the response header.
         */
        public Result(T response, String cookie) {
            this.cookie = cookie;
            this.response = response;
        }
    }
}