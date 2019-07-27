package com.example.footprint.view.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.footprint.R;
import com.example.footprint.model.User;
import com.example.footprint.net.UserClient;
import com.example.footprint.service.RestAPI;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class SignInActivity extends AppCompatActivity {
    EditText etId, etPassword;
    CheckBox cbAutoLogin;
    Button btnLogin, btnJoin;
    User user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        etId = (EditText) findViewById(R.id.et_id);
        etPassword = (EditText) findViewById(R.id.et_password);
        cbAutoLogin = (CheckBox) findViewById(R.id.cb_auto_login);
        btnLogin = (Button) findViewById(R.id.btn_login);
        btnJoin = (Button) findViewById(R.id.btn_join);

        btnLogin.setOnClickListener(new BtnOnClickListener());
        btnJoin.setOnClickListener(new BtnOnClickListener());
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    class BtnOnClickListener implements Button.OnClickListener {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_login:
//                    User user = new User();
//                    user.setUserName(etId.getText().toString());
//                    user.setPassword(etPassword.getText().toString());
//
//                    UserClient userClient = new UserClient(user);
//
//                    userClient.signIn(new JsonHttpResponseHandler() {
//                        @Override
//                        public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
//
//
//                        }
//
//                        @Override
//                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//
//                        }
//                    });
                    JSONObject jsonParams = new JSONObject();
                    try {
                        jsonParams.put("username", etId.getText().toString())
                                  .put("password", etPassword.getText().toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    RestAPI.post("/auth/login", jsonParams, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            // TODO: 토큰 저장
                            Toast.makeText(SignInActivity.this, "로그인 되었습니다.", Toast.LENGTH_SHORT).show();

                            Intent signInIntent = new Intent(SignInActivity.this, MapActivity.class);
                            startActivity(signInIntent);
                        }
                    });


                    break;


                case R.id.btn_join:
                    Intent signUpIntent = new Intent(SignInActivity.this, SignUpActivity.class);
                    startActivity(signUpIntent);
                    break;

            }
        }
    }

}
