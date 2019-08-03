package com.example.footprint.model;

import org.json.JSONException;
import org.json.JSONObject;

public class Marker {
    private String latitude;
    private String longitude;
    private Integer type;

    public Marker(JSONObject jsonObject) {
        try {
            this.latitude = (String) jsonObject.get("latitude");
            this.longitude = (String) jsonObject.get("longitude");
            this.type = (Integer) jsonObject.get("type");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public Integer getType() {
        return type;
    }
}
