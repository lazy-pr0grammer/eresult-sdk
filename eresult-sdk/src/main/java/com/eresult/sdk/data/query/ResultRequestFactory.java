/**
 * ResultRequestFactory class is responsible for creating HTTP requests to retrieve exam results
 * from the eboardresults.com website. It implements the LazyHttp.CallFactory interface to generate
 * and enqueue HTTP calls asynchronously.
 * <p>
 * Created by Anindya Das on 1/18/24 11:39 AM.
 */
package com.eresult.sdk.data.query;

import androidx.annotation.NonNull;

import com.eresult.sdk.data.BoardType;
import com.eresult.sdk.data.ExamType;
import com.eresult.sdk.data.ResultType;
import com.eresult.sdk.data.query.http.LazyHttp;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ResultRequestFactory implements LazyHttp.CallFactory<String> {

    private final String resultType;
    private final String year;
    private final ExamType type;
    private final String subPath;
    private final String captcha;
    private final String eiinCode;
    private final String centerCode;
    private final String mainCookie;
    private final String districtCode;
    private final BoardType boardType;
    private final String registrationId;
    private final String studentRollNumber;

    /**
     * Constructor for ResultRequestFactory.
     *
     * @param captcha           Captcha code for result retrieval.
     * @param mainCookie        Main cookie obtained during captcha request.
     * @param studentRollNumber Student's roll number.
     * @param registrationId    Student's registration ID.
     * @param boardType         Type of the educational board.
     * @param year              Exam year.
     * @param examType          Type of the exam.
     * @param resultType        Type of result you want.
     * @param centerCode        Your center code.
     * @param districtCode      District code.
     * @param eiinCode          EIIN code.
     */
    public ResultRequestFactory(
            String captcha,
            String mainCookie,
            String studentRollNumber,
            String registrationId,
            BoardType boardType,
            String year,
            ExamType examType,
            @NonNull ResultType resultType,
            String eiinCode,
            String districtCode,
            String centerCode) {
        this.year = year;
        this.type = examType;
        this.captcha = captcha;
        this.eiinCode = eiinCode;
        this.boardType = boardType;
        this.subPath = "/v2/getres";
        this.mainCookie = mainCookie;
        this.centerCode = centerCode;
        this.districtCode = districtCode;
        this.registrationId = registrationId;
        this.studentRollNumber = studentRollNumber;
        this.resultType = resultType.getResult();
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
                request
                        .newBuilder()
                        .addHeader("Cookie", mainCookie)
                        .url(
                                request
                                        .url()
                                        .newBuilder()
                                        .addPathSegments(subPath)
                                        .addQueryParameter("exam", type.name().toLowerCase())
                                        .addQueryParameter("year", year)
                                        .addQueryParameter("board", boardType.name().toLowerCase())
                                        .addQueryParameter("result_type", resultType)
                                        .addQueryParameter("roll", studentRollNumber)
                                        .addQueryParameter("reg", registrationId)
                                        .addQueryParameter("eiin", eiinCode)
                                        .addQueryParameter("dcode", districtCode)
                                        .addQueryParameter("ccode", centerCode)
                                        .addQueryParameter("captcha", captcha)
                                        .build()
                                        .toString())
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
            @NonNull Call call, LazyHttp.Callback<String> callback, Class<String> responseType) {
        call.enqueue(
                new Callback() {
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

    /**
     * Parses the HTTP response into the specified response type.
     *
     * @param response     Response instance received from the HTTP call.
     * @param responseType Class type of the expected response.
     * @return Parsed response as a String or an error message.
     */
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