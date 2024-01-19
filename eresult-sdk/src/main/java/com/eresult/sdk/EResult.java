/**
 * EResult class is part of the eboardresults.com SDK, which allows users to request exam results
 * and captcha images from the eboardresults.com website.
 * <p>
 * Created by Anindya Das on 1/17/24 6:30 AM.
 */
package com.eresult.sdk;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;

import com.eresult.sdk.data.type.BoardType;
import com.eresult.sdk.data.type.ExamType;
import com.eresult.sdk.data.type.ResultType;
import com.eresult.sdk.query.CaptchaFactory;
import com.eresult.sdk.query.ResultRequestFactory;
import com.eresult.sdk.query.http.LazyHttp;

import java.io.IOException;
import java.util.concurrent.Executor;

import okhttp3.Call;

/**
 * EResult class is responsible for handling result requests and captcha retrieval.
 * It utilizes LazyHttp for making asynchronous HTTP requests.
 */
public class EResult {
    private String year;
    private ExamType examType;
    private String mainCookie;
    private BoardType boardType;
    private String registrationId;
    private final LazyHttp lazyHttp;
    private String studentRollNumber;
    private final ResultType resultType;

    // Private constructor for creating an EResult instance with a specified ResultType.
    private EResult(ResultType resultType) {
        this.resultType = resultType;
        this.lazyHttp = new LazyHttp.Builder().baseUrl("https://eboardresults.com").build();
    }

    // Private constructor for creating a fully initialized EResult instance.
    private EResult(
            String year,
            ResultType resultType,
            String registrationId,
            String studentRollNumber,
            BoardType boardType,
            ExamType examType) {
        this.year = year;
        this.examType = examType;
        this.boardType = boardType;
        this.resultType = resultType;
        this.registrationId = registrationId;
        this.studentRollNumber = studentRollNumber;
        this.lazyHttp = new LazyHttp.Builder().baseUrl("https://eboardresults.com").build();
    }

    /**
     * Requests a captcha image asynchronously.
     *
     * @param callback Callback to handle the response or failure.
     */
    public void requestCaptcha(ResultCallback<Bitmap> callback) {
        CaptchaFactory factory = new CaptchaFactory();
        lazyHttp.queryAsync(
                factory,
                byte[].class,
                new LazyHttp.Callback<byte[]>() {
                    @Override
                    public void onResponse(Call call, byte[] response) {
                        try {
                            mainCookie = factory.cookie;
                            new LExecutor()
                                    .execute(
                                            () ->
                                                    callback.onResponse(
                                                            BitmapFactory.decodeByteArray(response, 0, response.length)));
                        } catch (Exception e) {
                            callback.onFailure(e.getMessage());
                        }
                    }

                    @Override
                    public void onFailure(Call call, IOException e) {
                        callback.onFailure(e.getMessage());
                    }
                });
    }

    /**
     * Requests exam results asynchronously using the provided captcha.
     *
     * @param captcha   Captcha code for result retrieval.
     * @param callback  Callback to handle the response or failure.
     */
    public void requestResult(String captcha, ResultCallback<String> callback) {
        lazyHttp.queryAsync(
                new ResultRequestFactory(
                        captcha, mainCookie, studentRollNumber, registrationId, boardType, year, examType),
                String.class,
                new LazyHttp.Callback<String>() {
                    @Override
                    public void onResponse(Call call, String response) {
                        new LExecutor().execute(() -> callback.onResponse(response));
                    }

                    @Override
                    public void onFailure(Call call, IOException e) {
                        callback.onFailure(e.getMessage());
                    }
                });
    }

    /**
     * Builder class for creating an EResult instance with specified parameters.
     */
    public static class Builder {
        private String year;
        private ResultType type;
        private ExamType examType;
        private BoardType boardType;
        private String registrationId;
        private String studentRollNumber;

        // Setter methods for Builder parameters.
        public Builder setYear(String year) {
            this.year = year;
            return this;
        }

        public Builder setResultType(ResultType type) {
            this.type = type;
            return this;
        }

        public Builder setExamType(ExamType examType) {
            this.examType = examType;
            return this;
        }

        public Builder setBoardType(BoardType boardType) {
            this.boardType = boardType;
            return this;
        }

        public Builder setRegistrationId(String registrationId) {
            this.registrationId = registrationId;
            return this;
        }

        public Builder setStudentRollNumber(String studentRollNumber) {
            this.studentRollNumber = studentRollNumber;
            return this;
        }

        /**
         * Builds and returns an EResult instance with the specified parameters.
         *
         * @return Fully initialized EResult instance.
         * @throws NullPointerException if any required parameter is not set.
         */
        public EResult build() {
            if (type == null || year == null || examType == null || boardType == null
                    || registrationId == null || studentRollNumber == null) {
                throw new NullPointerException("All parameters must be set!");
            }
            return new EResult(year, type, registrationId, studentRollNumber, boardType, examType);
        }
    }

    /**
     * Callback interface for handling asynchronous responses or failures.
     *
     * @param <T> Type of the response data.
     */
    public interface ResultCallback<T> {
        void onResponse(T result);

        void onFailure(String result);
    }

    /**
     * Executor class for running tasks on the main thread.
     */
    private static class LExecutor implements Executor {
        private final Handler handler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(Runnable command) {
            handler.post(command);
        }
    }
}