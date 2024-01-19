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

import com.eresult.sdk.data.BoardType;
import com.eresult.sdk.data.ExamType;
import com.eresult.sdk.data.ResultType;
import com.eresult.sdk.data.query.CaptchaFactory;
import com.eresult.sdk.data.query.ResultRequestFactory;
import com.eresult.sdk.data.query.http.LazyHttp;

import java.io.IOException;
import java.util.concurrent.Executor;

import okhttp3.Call;

/**
 * EResult class is responsible for handling result requests and captcha retrieval.
 * It utilizes LazyHttp for making asynchronous HTTP requests.
 */
public class EResult {
    private String year;
    private String eiinCode;
    private ExamType examType;
    private String mainCookie;
    private String centerCode;
    private BoardType boardType;
    private String districtCode;
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
            ExamType examType, String eiinCode, String centerCode, String districtCode) {
        this.year = year;
        this.eiinCode = eiinCode;
        this.examType = examType;
        this.boardType = boardType;
        this.resultType = resultType;
        this.centerCode = centerCode;
        this.districtCode = districtCode;
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
     * @param captcha  Captcha code for result retrieval.
     * @param callback Callback to handle the response or failure.
     */
    public void requestResult(String captcha, ResultCallback<String> callback) {
        lazyHttp.queryAsync(
                new ResultRequestFactory(
                        captcha, mainCookie, studentRollNumber, registrationId, boardType, year, examType, resultType, eiinCode, districtCode, centerCode),
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
        private String eiinCode;
        private ResultType type;
        private ExamType examType;
        private String centerCode;
        private String districtCode;
        private BoardType boardType;
        private String registrationId;
        private String studentRollNumber;

        // Setter methods for Builder parameters.
        public Builder setYear(String year) {
            this.year = year;
            return this;
        }

        public Builder setEiinCode(String eiinCode) {
            this.eiinCode = eiinCode;
            return this;
        }

        public Builder setCenterCode(String centerCode) {
            this.centerCode = centerCode;
            return this;
        }

        public Builder setDistrictCode(String districtCode) {
            this.districtCode = districtCode;
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
        public EResult build() throws IllegalAccessException {
            if (type == null)
                throw new IllegalAccessException("You can not access results without setting its type!");

            if (type == ResultType.BOARD)
                throw new IllegalAccessException("Board based results is not available yet!");

            if (type == ResultType.CENTER) {
                if (year == null || centerCode == null || districtCode == null || examType == null || boardType == null)
                    throw new NullPointerException("Needed parameters need to be set for a center based request!");
            }

            if (type == ResultType.DISTRICT) {
                if (year == null || districtCode == null || examType == null || boardType == null)
                    throw new NullPointerException("Needed parameters need to be set for district based request!");
            }

            if (type == ResultType.INSTITUTION) {
                if (year == null || examType == null || boardType == null || eiinCode == null)
                    throw new NullPointerException("All parameters need to be set for an institution based request!");
            }

            if (type == ResultType.INDIVIDUAL) {
                if (year == null || studentRollNumber == null || registrationId == null || examType == null || boardType == null)
                    throw new NullPointerException("All parameters need to be set for an individual request!");
            }

            return new EResult(year, type, registrationId, studentRollNumber, boardType, examType, eiinCode, centerCode, districtCode);
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