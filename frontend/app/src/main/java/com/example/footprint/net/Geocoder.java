package com.example.footprint.net;

import android.util.Log;

import com.example.footprint.model.Token;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Geocoder {
    public static String thoroughfare;
    public static void getThoroughfare(double lat, double lng) {
        String url1 = "https://naveropenapi.apigw.ntruss.com/map-reversegeocode/v2/gc?request=coordsToaddr&coords=";
        String url2 = "&sourcecrs=epsg:4326&output=json&orders=admcode";
        AsyncHttpClient client = new AsyncHttpClient();
        Token token = Token.getTokenObject();
        String tokenKey = token.getTokenKey();
        client.addHeader("X-NCP-APIGW-API-KEY-ID", "9vltm1bcl4");
        client.addHeader("X-NCP-APIGW-API-KEY", "6f38EWTV4XyEOhmtPQw6bBjtx9u0A0ApzXQ3Vieb");
        client.get(null, url1 + lng + "," + lat + url2, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONArray results = response.getJSONArray("results");
                    JSONObject object = results.getJSONObject(0).getJSONObject("region").getJSONObject("area3");
                    thoroughfare = object.getString("name");
                    //Log.e("thoroughfare", thoroughfare);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
