package com.eresult.sdk;

import com.eresult.sdk.data.Result;
import com.eresult.sdk.data.type.ResultType;

/**
 * Created by Anindya Das on 1/17/24 6:30 AM
 **/
public class EResult {
    private ResultType resultType;
    private String registrationId;
    private String studentRollNumber;

    private EResult(ResultType resultType) {
        this.resultType = resultType;
    }

    private EResult(ResultType resultType, String registrationId, String studentRollNumber) {
        this.resultType = resultType;
        this.registrationId = registrationId;
        this.studentRollNumber = studentRollNumber;
    }

    public void query(Callback<Result, Error> callback) {

    }

    static class Builder {
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
