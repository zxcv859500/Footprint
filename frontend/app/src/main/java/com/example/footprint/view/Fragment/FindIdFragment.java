package com.example.footprint.view.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.footprint.R;
import com.example.footprint.net.RestAPI;
import com.example.footprint.view.Activities.SignUpActivity;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

public class FindIdFragment extends Fragment {
    private EditText etFgPhone1, etFgPhone2, etFgPhone3;
    private EditText etFgVerification;
    private Button btnFgAuthentication, btnFgVerification, btnFgFindId;
    private TextView tvId, tvFgTimer;

    private Timer timer = new Timer();
    private TimerTask timerTask;
    private String phoneNumber;
    private boolean isVerified;

    public FindIdFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_find_id, container, false);

        isVerified = false;

        etFgPhone1 = view.findViewById(R.id.et_fg_phone1);
        etFgPhone2 = view.findViewById(R.id.et_fg_phone2);
        etFgPhone3 = view.findViewById(R.id.et_fg_phone3);
        etFgVerification = view.findViewById(R.id.et_fg_verification);
        btnFgAuthentication = view.findViewById(R.id.btn_fg_authentication);
        btnFgVerification = view.findViewById(R.id.btn_fg_verification);
        btnFgFindId = view.findViewById(R.id.btn_fg_find_id);
        tvId = view.findViewById(R.id.tv_id);
        tvFgTimer = view.findViewById(R.id.tv_fg_timer);

        btnFgAuthentication.setOnClickListener(new BtnOnClickListener());
        btnFgVerification.setOnClickListener(new BtnOnClickListener());
        btnFgFindId.setOnClickListener(new BtnOnClickListener());

        return view;
    }

    private void startTimerTask() {
        stopTimerTask();

        timerTask = new TimerTask() {
            int m = 3;
            int s = 0;
            @Override
            public void run() {
                m = (s == 0 ? m - 1 : m);
                s = (s == 0 ? 59 : s - 1);

                tvFgTimer.post(new Runnable() {
                    @Override
                    public void run() {
                        if (m < 0) {
                            stopTimerTask();
                        } else {
                            tvFgTimer.setText(String.format("%d:%02d", m, s));
                        }
                    }
                });
            }
        };

        timer.schedule(timerTask, 0, 1000);
    }

    private void stopTimerTask() {
        if (timerTask != null) {
            tvFgTimer.setText("3:00");
            timerTask.cancel();
            timerTask = null;
        }
    }

    class BtnOnClickListener implements Button.OnClickListener {
        JSONObject jsonParams = new JSONObject();
        @Override
        public void onClick(View view) {
            switch(view.getId()) {
                case R.id.btn_fg_authentication:
                    Log.e("버튼눌림", "버튼눌림");
                    phoneNumber = etFgPhone1.getText().toString() + "-" +
                            etFgPhone2.getText().toString() + "-" +
                            etFgPhone3.getText().toString();
                    Log.e("폰 번호", phoneNumber);
                    try {
                        jsonParams.put("phone", phoneNumber);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    RestAPI.post("/auth/find/cert", jsonParams, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            startTimerTask();
                        }
                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            Toast.makeText(getActivity(), "휴대폰번호를 잘못 입력하셨습니다.", Toast.LENGTH_SHORT).show();
                        }
                    });
                    break;
                case R.id.btn_fg_verification:
                    try {
                        jsonParams.put("phone", phoneNumber)
                                .put("verify", etFgVerification.getText().toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    RestAPI.post("/auth/find/verify", jsonParams, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            Toast.makeText(getActivity(), "인증되었습니다.", Toast.LENGTH_SHORT).show();
                            isVerified = true;
                            stopTimerTask();
                        }
                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            Toast.makeText(getActivity(), "인증에 실패하셨습니다.", Toast.LENGTH_SHORT).show();
                        }
                    });
                    break;
                case R.id.btn_fg_find_id:
                    if (isVerified) {
                        try {
                            jsonParams.put("phone", phoneNumber);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        RestAPI.post("/auth/find/username", jsonParams, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                try {
                                    String str = "아이디는 " + response.get("username") + " 입니다.";
                                    tvId.setText(str);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                stopTimerTask();
                            }
                        });
                    } else {
                        Toast.makeText(getActivity(), "휴대폰을 인증해 주세요.", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    }
}
