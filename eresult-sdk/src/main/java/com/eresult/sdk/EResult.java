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

import androidx.annotation.NonNull;

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
    private final LazyHttp lazyHttp;
    private final ResultType resultType;
    private String year;
    private String eiinCode;
    private ExamType examType;
    private String mainCookie;
    private String centerCode;
    private BoardType boardType;
    private String districtCode;
    private String registrationId;
    private String studentRollNumber;

    // Private constructor for creating an EResult instance with a specified ResultType.
    private EResult(ResultType resultType) {
        this.resultType = resultType;
        this.lazyHttp = new LazyHttp.Builder().baseUrl("https://eboardresults.com").build();
    }

    // Private constructor for creating a fully initialized EResult instance.

    /**
     * Constructs an instance of EResult with the specified parameters.
     *
     * @param year              The academic year for the result.
     * @param resultType        The type of result (e.g., CENTER, DISTRICT, INSTITUTION, INDIVIDUAL).
     * @param registrationId    The registration ID for the result.
     * @param studentRollNumber The roll number of the student for individual results.
     * @param boardType         The type of educational board (e.g., Dhaka, Chittagong, etc.).
     * @param examType          The type of examination (e.g., SSC, HSC).
     * @param eiinCode          The Educational Institution Identification Number (EIIN) code for institution results.
     * @param centerCode        The examination center code for center results.
     * @param districtCode      The district code for district results.
     */
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
     * Throws a NullPointerException if any of the provided objects is null.
     *
     * @param objects The objects to check for null values.
     * @throws NullPointerException If any of the provided objects is null.
     */
    private static void throwNullPointerException(Object... objects) {
        for (Object obj : objects) {
            if (obj == null) {
                throw new NullPointerException("All parameters need to be set for an individual request!");
            }
        }
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
     * Callback interface for handling asynchronous responses or failures.
     *
     * @param <T> Type of the response data.
     */
    public interface ResultCallback<T> {
        void onResponse(T result);

        void onFailure(String result);
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

        /**
         * Sets the academic year for the result.
         *
         * @param year The academic year to set.
         * @return The Builder instance for method chaining.
         */
        @NonNull
        public Builder setYear(@NonNull String year) {
            this.year = year;
            return this;
        }

        /**
         * Sets the Educational Institution Identification Number (EIIN) code.
         *
         * @param eiinCode The EIIN code to set.
         * @return The Builder instance for method chaining.
         */
        @NonNull
        public Builder setEiinCode(@NonNull String eiinCode) {
            this.eiinCode = eiinCode;
            return this;
        }

        /**
         * Sets the examination center code.
         *
         * @param centerCode The center code to set.
         * @return The Builder instance for method chaining.
         */
        @NonNull
        public Builder setCenterCode(@NonNull String centerCode) {
            this.centerCode = centerCode;
            return this;
        }

        /**
         * Sets the district code for the result.
         *
         * @param districtCode The district code to set.
         * @return The Builder instance for method chaining.
         */
        @NonNull
        public Builder setDistrictCode(@NonNull String districtCode) {
            this.districtCode = districtCode;
            return this;
        }

        /**
         * Sets the result type for the result.
         *
         * @param type The result type to set.
         * @return The Builder instance for method chaining.
         */
        @NonNull
        public Builder setResultType(@NonNull ResultType type) {
            this.type = type;
            return this;
        }

        /**
         * Sets the exam type for the result.
         *
         * @param examType The exam type to set.
         * @return The Builder instance for method chaining.
         */
        @NonNull
        public Builder setExamType(@NonNull ExamType examType) {
            this.examType = examType;
            return this;
        }

        /**
         * Sets the board type for the result.
         *
         * @param boardType The board type to set.
         * @return The Builder instance for method chaining.
         */
        @NonNull
        public Builder setBoardType(@NonNull BoardType boardType) {
            this.boardType = boardType;
            return this;
        }


        /**
         * Sets the registration ID for the result.
         *
         * @param registrationId The registration ID to set.
         * @return The Builder instance for method chaining.
         */
        @NonNull
        public Builder setRegistrationId(@NonNull String registrationId) {
            this.registrationId = registrationId;
            return this;
        }

        /**
         * Sets the student roll number for the result.
         *
         * @param studentRollNumber The student roll number to set.
         * @return The Builder instance for method chaining.
         */
        @NonNull
        public Builder setStudentRollNumber(@NonNull String studentRollNumber) {
            this.studentRollNumber = studentRollNumber;
            return this;
        }


        /**
         * Builds and returns an EResult instance with the specified parameters.
         *
         * @return Fully initialized EResult instance.
         * @throws NullPointerException or NullPointerException if any required parameter is not set or null.
         */
        public EResult build() throws IllegalAccessException {
            if (type == null)
                throw new IllegalAccessException("You can not access results without setting its type!");

            if (type == ResultType.BOARD)
                throw new IllegalAccessException("Board based results is not available yet!");

            if (type == ResultType.CENTER) {
                throwNullPointerException(year, centerCode, districtCode, examType, boardType);
            }

            if (type == ResultType.DISTRICT) {
                throwNullPointerException(year, districtCode, examType, boardType);
            }

            if (type == ResultType.INSTITUTION) {
                throwNullPointerException(year, examType, boardType, eiinCode);
            }

            if (type == ResultType.INDIVIDUAL) {
                throwNullPointerException(year, studentRollNumber, registrationId, examType, boardType);
            }

            return new EResult(year, type, registrationId, studentRollNumber, boardType, examType, eiinCode, centerCode, districtCode);
        }
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