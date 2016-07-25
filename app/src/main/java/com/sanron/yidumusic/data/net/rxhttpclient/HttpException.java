
package com.sanron.yidumusic.data.net.rxhttpclient;

public final class HttpException extends Exception {

    public HttpException(int responseCode) {
        super("HTTP responsecode " + responseCode);
    }
}