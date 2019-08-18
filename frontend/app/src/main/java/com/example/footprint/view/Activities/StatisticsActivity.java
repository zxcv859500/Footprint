package com.example.footprint.view.Activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.footprint.R;
import com.example.footprint.net.RestAPI;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class StatisticsActivity extends AppCompatActivity {
    private Spinner spThoroughfare;
    private ImageView ivRed, ivYellow, ivBlue;
    private TextView tvRed, tvYellow, tvBlue;
    private ArrayList<String> number;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        spThoroughfare = findViewById(R.id.sp_thoroughfare);
        ivRed = findViewById(R.id.iv_red);
        ivYellow = findViewById(R.id.iv_yellow);
        ivBlue = findViewById(R.id.iv_blue);
        tvRed = findViewById(R.id.tv_red);
        tvYellow = findViewById(R.id.tv_yellow);
        tvBlue = findViewById(R.id.tv_blue);
    }

    @Override
    protected void onStart() {
        super.onStart();
        number = new ArrayList<>();
        setSpinner();
    }

    public void setSpinner() {
        RestAPI.get("/statistics/list", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.e("size", Integer.toString(response.length()));
                ArrayList<String> thoroughfare = new ArrayList<>();
                for (int i = 0; i < response.length(); i++) {
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = (JSONObject) response.get(i);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.e("json", jsonObject.toString());
                    try {
                        String num = jsonObject.getString("typeA") + "," +
                                jsonObject.get("typeB") + "," +
                                jsonObject.get("typeC");
                        thoroughfare.add(jsonObject.getString("road"));
                        number.add(num);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getApplicationContext(),
                        R.layout.support_simple_spinner_dropdown_item,
                        thoroughfare);
                spThoroughfare.setAdapter(arrayAdapter);
                spThoroughfare.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        String s = number.get(i);
                        String[] split = s.split(",");
                        tvRed.setText(split[0]);
                        tvYellow.setText(split[1]);
                        tvBlue.setText(split[2]);
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e("error", "통신실패");
            }
        });

    }
}
