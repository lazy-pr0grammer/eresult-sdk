package com.eresult.sdk.data;

/**
 * ResultType enum represents different types of result inquiries that can be made through the eboardresults.com SDK.
 * <p>
 * Created by Anindya Das on 1/17/24 6:33 AM.
 */
public enum ResultType {
    BOARD,         // Result for an entire board
    CENTER,        // Result for a specific center
    DISTRICT,      // Result for a specific district
    INDIVIDUAL,    // Individual result
    INSTITUTION   // Result for an institution
}