package com.eresult.sdk;

import android.util.Log;

import com.eresult.sdk.data.Result;
import com.eresult.sdk.data.type.ResultType;
import com.eresult.sdk.query.http.LazyHttp;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Anindya Das on 1/17/24 6:30 AM
 **/
public class EResult {
    private ResultType resultType;
    private String registrationId;
    private String studentRollNumber;

    private LazyHttp lazyHttp;

    private EResult(ResultType resultType) {
        this.resultType = resultType;
        this.lazyHttp = new LazyHttp.Builder()
                .baseUrl("https://eboardresults.com/en/ebr.app/home/")
                .build();
    }

    private EResult(ResultType resultType, String registrationId, String studentRollNumber) {
        this.resultType = resultType;
        this.registrationId = registrationId;
        this.studentRollNumber = studentRollNumber;
        this.lazyHttp = new LazyHttp.Builder()
                .baseUrl("https://eboardresults.com/en/ebr.app/home/")
                .build();
    }

    public void query() {
        lazyHttp.queryAsync(new LazyHttp.MyCallFactory(), Response.class, new okhttp3.Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Log.d("EResult", Objects.requireNonNull(response.body()).string());
            }
        });
    }

    public static class Builder {
        private ResultType type;

        public Builder setResultType(ResultType type) {
            this.type = type;
            return this;
        }

        public EResult build() {
            if (type == null) {
                throw new NullPointerException("Result type needs to be specified");
            }
            return new EResult(type);
        }

    }

    public static interface Callback<T, V> {
        void onReceived(T result);

        void onQueryFailed(V error);
    }
}
