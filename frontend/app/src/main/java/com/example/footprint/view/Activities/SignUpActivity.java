package com.example.footprint.view.Activities;

import android.content.Intent;
import android.icu.text.StringPrepParseException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.footprint.R;
import com.example.footprint.service.RestAPI;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Timer;
import java.util.TimerTask;

public class SignUpActivity extends AppCompatActivity {
    private EditText etSignUpId;
    private EditText etNickName;
    private EditText etSignUpPassword;
    private EditText etPasswordCheck;
    private EditText etPhone1, etPhone2, etPhone3;
    private EditText etVerification;
    private Button btnAuthentication;
    private Button btnVerification;
    private Button btnSignUp;
    private TextView tvTimer;

    private String phoneNumber = "";
    private boolean isVerified = false;

    private Timer timer = new Timer();
    private TimerTask timerTask;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        isVerified = false;

        etSignUpId = findViewById(R.id.et_sign_up_id);
        etNickName = findViewById(R.id.et_nickname);
        etSignUpPassword = findViewById(R.id.et_sign_up_password);
        etPasswordCheck = findViewById(R.id.et_password_check);
        etPhone1 = findViewById(R.id.et_phone1);
        etPhone2 = findViewById(R.id.et_phone2);
        etPhone3 = findViewById(R.id.et_phone3);
        etVerification = findViewById(R.id.et_verification);

        btnAuthentication = findViewById(R.id.btn_authentication);
        btnVerification = findViewById(R.id.btn_verification);
        btnSignUp = findViewById(R.id.btn_sign_up);

        tvTimer = findViewById(R.id.tv_timer);

        btnVerification.setOnClickListener(new BtnOnClickListener());
        btnAuthentication.setOnClickListener(new BtnOnClickListener());
        btnSignUp.setOnClickListener(new BtnOnClickListener());
    }

    @Override
    protected void onDestroy() {
        timer.cancel();
        super.onDestroy();
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

                tvTimer.post(new Runnable() {
                    @Override
                    public void run() {
                        if (m < 0) {
                            stopTimerTask();
                        } else {
                            tvTimer.setText(String.format("%d:%02d", m, s));
                        }
                    }
                });
            }
        };

        timer.schedule(timerTask, 0, 1000);
    }

    private void stopTimerTask() {
        if (timerTask != null) {
            tvTimer.setText("3:00");
            timerTask.cancel();
            timerTask = null;
        }
    }

    class BtnOnClickListener implements Button.OnClickListener {

        @Override
        public void onClick(View view) {
            JSONObject jsonParams = new JSONObject();
            switch(view.getId()) {
                case R.id.btn_authentication:
                    phoneNumber = etPhone1.getText().toString() + "-" +
                            etPhone2.getText().toString() + "-" +
                            etPhone3.getText().toString();
                    try {
                        jsonParams.put("phone", phoneNumber);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    RestAPI.post("/auth/cert", jsonParams, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            startTimerTask();
                        }
                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            Toast.makeText(SignUpActivity.this, "휴대폰번호를 잘못 입력하셨습니다.", Toast.LENGTH_SHORT).show();
                        }
                    });
                    startTimerTask();
                    break;
                case R.id.btn_verification:
                    try {
                        jsonParams.put("phone", phoneNumber)
                                  .put("verify", etVerification.getText().toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    RestAPI.post("/auth/verify", jsonParams, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            Toast.makeText(SignUpActivity.this, "인증되었습니다.", Toast.LENGTH_SHORT).show();
                            isVerified = true;
                            stopTimerTask();
                        }
                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            Toast.makeText(SignUpActivity.this, "인증에 실패하셨습니다.", Toast.LENGTH_SHORT).show();
                        }
                    });
                    break;
                case R.id.btn_sign_up:
                    if (!etSignUpPassword.getText().toString().equals(etPasswordCheck.getText().toString())) {
                        Toast.makeText(SignUpActivity.this, "패스워드가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                        break;
                    } else if (!isVerified) {
                        Toast.makeText(SignUpActivity.this, "휴대폰을 인증해주세요.", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    try {
                        jsonParams.put("username", etSignUpId.getText().toString())
                                  .put("password", etSignUpPassword.getText().toString())
                                  .put("nickname", etNickName.getText().toString())
                                  .put("phone", phoneNumber);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    RestAPI.post("/auth/register", jsonParams, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            Toast.makeText(SignUpActivity.this, "가입되었습니다.", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                            startActivity(intent);
                        }
                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            String msg = null;
                            try {
                                msg = errorResponse.get("error").toString();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            if (msg != null) {
                                if (msg.equals("Username, password, nickname, phone required")) {
                                    Toast.makeText(SignUpActivity.this, "빈 칸 없이 입력해 주세요.", Toast.LENGTH_SHORT).show();
                                } else if (msg.equals("Unsuccessful phone number")) {
                                    Toast.makeText(SignUpActivity.this, "휴대폰 번호가 올바르지 않습니다.", Toast.LENGTH_SHORT).show();
                                } else if (msg.equals("username or password too long")) {
                                    Toast.makeText(SignUpActivity.this, "아이디 또는 패스워드가 너무 깁니다.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
                    break;
            }
        }
    }
}
