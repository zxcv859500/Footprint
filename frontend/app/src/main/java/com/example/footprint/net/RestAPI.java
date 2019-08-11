package com.example.footprint.net;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class RestAPI {
    final private static String url = "http://203.254.143.185:3000/api";

    public static void post(String uri, JSONObject jsonParams, JsonHttpResponseHandler jsonHttpResponseHandler) {
        AsyncHttpClient client = new AsyncHttpClient();

        try {
            StringEntity entity = new StringEntity(jsonParams.toString(), "UTF-8");
            entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            client.post(null, url + uri, entity, "application/json", jsonHttpResponseHandler);
        } catch (UnsupportedEncodingException e) {

        }
    }

    public static void post(String uri, JSONObject jsonParams, String token, JsonHttpResponseHandler jsonHttpResponseHandler) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("x-access-token", token);

        try {
            StringEntity entity = new StringEntity(jsonParams.toString(), "UTF-8");
            entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            client.post(null, url + uri, entity, "application/json", jsonHttpResponseHandler);
        } catch (UnsupportedEncodingException e) {

        }
    }

    public static void get(String uri, JsonHttpResponseHandler jsonHttpResponseHandler) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(null, url + uri, jsonHttpResponseHandler);
    }

    public static void get(String uri, String token, JsonHttpResponseHandler jsonHttpResponseHandler) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("x-access-token", token);
        client.get(null, url + uri, jsonHttpResponseHandler);
    }
}
