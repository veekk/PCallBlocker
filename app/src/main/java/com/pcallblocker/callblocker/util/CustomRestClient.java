package com.pcallblocker.callblocker.util;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * Crafted by veek on 08.08.16 with love ♥
 */
public class CustomRestClient {

    private static final String BASE_URL = "http://pcallblocker.com/pcallphp/";

    private static AsyncHttpClient client= new AsyncHttpClient();

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler handler){
        client.post(getAbsoluteUrl(url), params, handler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
}
