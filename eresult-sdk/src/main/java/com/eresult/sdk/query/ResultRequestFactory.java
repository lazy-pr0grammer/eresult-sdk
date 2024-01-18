package com.eresult.sdk.query;

import androidx.annotation.NonNull;

import com.eresult.sdk.data.type.BoardType;
import com.eresult.sdk.data.type.ExamType;
import com.eresult.sdk.query.http.LazyHttp;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Anindya Das on 1/18/24 11:39 AM
 **/
public class ResultRequestFactory implements LazyHttp.CallFactory<String> {
    private final String year;
    private final ExamType type;
    private final String subPath;
    private final String captcha;
    private final String mainCookie;
    private final BoardType boardType;
    private final String registrationId;
    private final String studentRollNumber;

    public ResultRequestFactory(String captcha, String mainCookie, String studentRollNumber, String registrationId, BoardType boardType, String year, ExamType examType) {
        this.year = year;
        this.type = examType;
        this.captcha = captcha;
        this.boardType = boardType;
        this.subPath = "/v2/getres";
        this.mainCookie = mainCookie;
        this.registrationId = registrationId;
        this.studentRollNumber = studentRollNumber;
    }

    @Override
    public Call createCall(@NonNull OkHttpClient client, @NonNull Request request) {
        return client.newCall(request.newBuilder()
                .addHeader("Cookie", mainCookie)
                .url(request.url().newBuilder()
                        .addPathSegments(subPath)
                        .addQueryParameter("exam", type.name().toLowerCase())
                        .addQueryParameter("year", year)
                        .addQueryParameter("board", boardType.name().toLowerCase())
                        .addQueryParameter("result_type", "1")
                        .addQueryParameter("roll", studentRollNumber)
                        .addQueryParameter("reg", registrationId)
                        .addQueryParameter("eiin", "")
                        .addQueryParameter("dcode", "")
                        .addQueryParameter("ccode", "")
                        .addQueryParameter("captcha", captcha)
                        .build()
                        .toString()
                )
                .build());
    }

    @Override
    public void enqueueCall(@NonNull Call call, LazyHttp.Callback<String> callback, Class<String> responseType) {
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onFailure(call, e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                callback.onResponse(call, parseResponse(response, responseType));
            }
        });
    }

    @Override
    public String parseResponse(Response response, Class<String> responseType) {
        if (responseType == String.class) {
            assert response.body() != null;
            try {
                return response.body().string();
            } catch (IOException e) {
                return e.getMessage();
            }
        } else {
            return response.message();
        }
    }
}
