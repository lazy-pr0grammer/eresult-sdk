package com.eresult.sdk;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.eresult.sdk.data.type.ResultType;
import com.eresult.sdk.query.CaptchaFactory;
import com.eresult.sdk.query.ConnectionFactory;
import com.eresult.sdk.query.http.LazyHttp;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Anindya Das on 1/17/24 6:30 AM
 **/
public class EResult {
    private String registrationId;
    private String studentRollNumber;
    private final LazyHttp lazyHttp;
    private final ResultType resultType;


    private EResult(ResultType resultType) {
        this.resultType = resultType;
        this.lazyHttp = new LazyHttp.Builder()
                .baseUrl("https://eboardresults.com")
                .build();
    }

    private EResult(ResultType resultType, String registrationId, String studentRollNumber) {
        this.resultType = resultType;
        this.registrationId = registrationId;
        this.studentRollNumber = studentRollNumber;
        this.lazyHttp = new LazyHttp.Builder()
                .baseUrl("https://eboardresults.com")
                .build();
    }

    public void query() {
        lazyHttp.queryAsync(new ConnectionFactory<>(), String.class, new LazyHttp.Callback<String>() {
            @Override
            public void onResponse(Call call, String response) {
                Log.d("EResult", response);
            }

            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("EResult", Objects.requireNonNull(e.getMessage()));
            }
        });
    }

    public void requestCaptcha(CaptchaCallback callback) {
        lazyHttp.queryAsync(new CaptchaFactory(), byte[].class, new LazyHttp.Callback<byte[]>() {
            @Override
            public void onResponse(Call call, byte[] response) {
                try {
                    new LExecutor().execute(() ->
                            callback.decodedBitmap(BitmapFactory.decodeByteArray(response, 0, response.length)));
                } catch (Exception e) {
                    callback.decodingFailure(e.getMessage());
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                callback.decodingFailure(e.getMessage());
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

    public interface CaptchaCallback {
        void decodedBitmap(Bitmap bitmap);

        void decodingFailure(String message);
    }

    private static class LExecutor implements Executor {
        private final Handler handler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(Runnable command) {
            handler.post(command);
        }
    }
}
