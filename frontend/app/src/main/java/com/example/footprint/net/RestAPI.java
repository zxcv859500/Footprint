package com.example.footprint.net;

import android.os.AsyncTask;
import android.util.Log;

import com.example.footprint.model.Token;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

public class RestAPI {
    final private static String url = "http://203.254.143.185:3000/api";

    public static void post(String uri, JSONObject jsonParams, JsonHttpResponseHandler jsonHttpResponseHandler) {
        AsyncHttpClient client = new AsyncHttpClient();
        Token token = Token.getTokenObject();
        client.addHeader("x-access-token",token.getTokenKey());
        try {
            StringEntity entity = new StringEntity(jsonParams.toString(), "UTF-8");
            entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            client.post(null, url + uri, entity,"application/json", jsonHttpResponseHandler);
        } catch (UnsupportedEncodingException e) {

        }
    }

    public static void post(String uri, List<BasicNameValuePair> list, File file) {
        MyAsyncTask myAsyncTask = new MyAsyncTask();
        HttpPost httpPost = new HttpPost(url + uri);

        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        for (BasicNameValuePair pair : list) {
            builder.addTextBody(pair.getName(), pair.getValue(), ContentType.MULTIPART_FORM_DATA.withCharset("utf-8"));
            Log.e(pair.getName(), pair.getValue());
        }
        builder.addPart("picture", new FileBody(file));

        httpPost.setEntity(builder.build());
        httpPost.setHeader("x-access-token", Token.getTokenObject().getTokenKey());

        myAsyncTask.execute(httpPost);
    }

    public static void get(String uri, JsonHttpResponseHandler jsonHttpResponseHandler) {
        AsyncHttpClient client = new AsyncHttpClient();
        Token token = Token.getTokenObject();
        String tokenKey = token.getTokenKey();
        client.addHeader("x-access-token", tokenKey);
        client.get(null, url + uri, jsonHttpResponseHandler);
    }


    private static class MyAsyncTask extends AsyncTask<HttpPost, String, String> {
        @Override
        protected String doInBackground(HttpPost... httpPosts) {
            HttpClient httpClient = new DefaultHttpClient();
            try {
                HttpResponse httpResponse = httpClient.execute(httpPosts[0]);
                String json;
                BufferedReader reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent(), "UTF-8"));
                json = reader.readLine();
                Log.e("error", json);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
