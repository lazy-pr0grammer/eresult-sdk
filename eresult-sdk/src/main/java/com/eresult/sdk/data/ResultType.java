package com.eresult.sdk.data;

/**
 * ResultType enum represents different types of result inquiries that can be made through the eboardresults.com SDK.
 * <p>
 * Created by Anindya Das on 1/17/24 6:33 AM.
 */
public enum ResultType {
    BOARD(null),               // Result for an entire board
    INDIVIDUAL("1"),         // Individual result (associated value: "1")
    INSTITUTION("2"),      // Result for an institution (associated value: "2")
    CENTER("3"),         // Result for a specific center (associated value: "3")
    DISTRICT("4");     // Result for a specific district (associated value: "4")

    private final String result; // Associated result for certain constants

    // Constructor for constants with associated results
    ResultType(String result) {
        this.result = result;
    }

    // Getter for associated results
    public String getResult() {
        return result;
    }
}
