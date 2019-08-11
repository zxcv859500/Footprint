package com.example.footprint.net;

import android.util.Base64;

import java.io.UnsupportedEncodingException;

public class JwtDecoder {
    public static String decode(String token) {
        String[] strings = token.split("\\.");
        String res = null;
        try {
            res = getJson(strings[1]);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return res;
    }

    private static String getJson(String token) throws UnsupportedEncodingException {
        byte[] bytes = Base64.decode(token, Base64.URL_SAFE);
        return new String(bytes, "UTF-8");
    }
}
