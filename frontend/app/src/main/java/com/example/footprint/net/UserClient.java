package com.example.footprint.net;

import com.example.footprint.R;
import com.example.footprint.model.Token;
import com.example.footprint.model.User;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import java.io.UnsupportedEncodingException;

public class UserClient {
    private static final String url = "http://13.209.117.111:3000/api";

    private AsyncHttpClient client;
    private Token token;

    public UserClient(){
        token = Token.getTokenObject();
    }

    public void write(JsonHttpResponseHandler handler) {

        client.addHeader("x-access-token", token.getTokenKey());
        client.post(url, handler);

    }


}
