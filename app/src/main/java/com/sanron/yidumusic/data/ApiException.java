package com.sanron.yidumusic.data;

/**
 * Created by sanron on 16-7-16.
 */
public class ApiException extends Error {

    private int mErrorCode;

    public ApiException(int errorCode) {
        super("response errorcode : " + errorCode);
        mErrorCode = errorCode;
    }
}
