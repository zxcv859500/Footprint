package com.example.footprint.view.Fragment;

import android.content.Intent;
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
import com.example.footprint.view.Activities.SignInActivity;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

public class FindPasswordFragment extends Fragment {
    private EditText etFgPwId, etFgPwVerification, etFgNewPassword, etFgNewPasswordCheck;
    private EditText etFgPwPhone1, etFgPwPhone2, etFgPwPhone3;
    private Button btnFgPwAuthentication, btnFgPwVerification, btnFgModifyPassword;
    private TextView tvFgPwTimer;

    private Timer timer = new Timer();
    private TimerTask timerTask;
    private boolean isVerified;
    private String phoneNumber;

    public FindPasswordFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_find_password, container, false);

        isVerified = false;

        etFgPwId = view.findViewById(R.id.et_fg_pw_id);
        etFgPwVerification = view.findViewById(R.id.et_fg_pw_verification);
        etFgNewPassword = view.findViewById(R.id.et_fg_new_password);
        etFgPwPhone1 = view.findViewById(R.id.et_fg_pw_phone1);
        etFgPwPhone2 = view.findViewById(R.id.et_fg_pw_phone2);
        etFgPwPhone3 = view.findViewById(R.id.et_fg_pw_phone3);
        etFgNewPasswordCheck = view.findViewById(R.id.et_fg_new_password_check);
        btnFgPwAuthentication = view.findViewById(R.id.btn_fg_pw_authentication);
        btnFgPwVerification = view.findViewById(R.id.btn_fg_pw_verification);
        btnFgModifyPassword = view.findViewById(R.id.btn_fg_modify_password);
        tvFgPwTimer = view.findViewById(R.id.tv_fg_pw_timer);

        btnFgPwAuthentication.setOnClickListener(new BtnOnClickListener());
        btnFgPwVerification.setOnClickListener(new BtnOnClickListener());
        btnFgModifyPassword.setOnClickListener(new BtnOnClickListener());

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

                tvFgPwTimer.post(new Runnable() {
                    @Override
                    public void run() {
                        if (m < 0) {
                            stopTimerTask();
                        } else {
                            tvFgPwTimer.setText(String.format("%d:%02d", m, s));
                        }
                    }
                });
            }
        };

        timer.schedule(timerTask, 0, 1000);
    }

    private void stopTimerTask() {
        if (timerTask != null) {
            tvFgPwTimer.setText("3:00");
            timerTask.cancel();
            timerTask = null;
        }
    }

    class BtnOnClickListener implements Button.OnClickListener {
        JSONObject jsonParams = new JSONObject();
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_fg_pw_authentication:
                    try {
                        jsonParams.put("username", etFgPwId.getText());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    RestAPI.post("/auth/find/password/verify", jsonParams, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            Toast.makeText(getActivity(), "인증되었습니다.", Toast.LENGTH_SHORT).show();
                            try {
                                phoneNumber = (String) response.get("phone");
                                JSONObject jsonParams2 = new JSONObject();
                                try {
                                    jsonParams2.put("phone", phoneNumber);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                RestAPI.post("/auth/find/cert", jsonParams2, new JsonHttpResponseHandler() {
                                    @Override
                                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                        Toast.makeText(getActivity(), "인증번호를 입력해 주세요.", Toast.LENGTH_SHORT).show();
                                        isVerified = true;
                                        startTimerTask();
                                    }
                                    @Override
                                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                        //Toast.makeText(getActivity(), "아이디가 잘못되었습니다.", Toast.LENGTH_SHORT).show();
                                        //Log.e("실패", throwable.getMessage());
                                    }
                                });
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            Toast.makeText(getActivity(), "아이디가 잘못되었습니다.", Toast.LENGTH_SHORT).show();
                        }
                    });
                    break;
                case R.id.btn_fg_pw_verification:
                    phoneNumber = etFgPwPhone1.getText().toString() + "-" +
                            etFgPwPhone2.getText().toString() + "-" +
                            etFgPwPhone3.getText().toString();
                    try {
                        jsonParams.put("phone", phoneNumber)
                                .put("verify", etFgPwVerification.getText().toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    RestAPI.post("/auth/find/verify", jsonParams, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            Toast.makeText(getActivity(), "인증되었습니다.", Toast.LENGTH_SHORT).show();
                            isVerified = true;
                            etFgNewPassword.setVisibility(View.VISIBLE);
                            etFgNewPasswordCheck.setVisibility(View.VISIBLE);
                            btnFgModifyPassword.setVisibility(View.VISIBLE);
                            stopTimerTask();
                        }
                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            Toast.makeText(getActivity(), "인증에 실패하셨습니다.", Toast.LENGTH_SHORT).show();
                        }
                    });
                    break;
                case R.id.btn_fg_modify_password:
                    if (isVerified) {
                        if (etFgNewPassword.getText().toString().equals(etFgNewPasswordCheck.getText().toString())) {
                            try {
                                jsonParams.put("username", etFgPwId.getText().toString())
                                        .put("newPassword", etFgNewPassword.getText().toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            RestAPI.post("/auth/find/password", jsonParams, new JsonHttpResponseHandler() {
                                @Override
                                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                    Toast.makeText(getActivity(), "비밀번호가 변경되었습니다.", Toast.LENGTH_SHORT).show();
                                    Intent signInIntent = new Intent(getActivity(), SignInActivity.class);
                                    startActivity(signInIntent);
                                }
                                @Override
                                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                    Toast.makeText(getActivity(), "변경하지 못했습니다.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    } else {
                        Toast.makeText(getActivity(), "먼저 인증해 주세요.", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    }
}
