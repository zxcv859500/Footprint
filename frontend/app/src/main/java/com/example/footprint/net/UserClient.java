package com.example.footprint.net;

import com.example.footprint.model.User;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import java.io.UnsupportedEncodingException;

public class UserClient {
    private static final String url = "아직 몰라";

    private AsyncHttpClient client;
    private User user;

    public UserClient() {

    }

    public UserClient(User user) {
        this.user = user;
    }

    public void signIn(JsonHttpResponseHandler handler) {

        client.addHeader("x-access-token", user.getToken());
        client.get(url, handler);


    }


}
