package com.example.footprint.view.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.footprint.R;
import com.example.footprint.model.User;
import com.example.footprint.net.UserClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;

public class SignInActivity extends AppCompatActivity {
    EditText etEmail, etPassword;
    CheckBox cbAutoLogin;
    Button btnLogin, btnJoin;
    User user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        etEmail = (EditText) findViewById(R.id.et_email);
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
//                    user.setUserName(etEmail.getText().toString());
//                    user.setPassword(etPassword.getText().toString());
//
//                    UserClient userClient = new UserClient(user);


                    // 에러 분류 관리 추가 미완
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



                    Intent signInIntent = new Intent(SignInActivity.this, MapActivity.class);
                    startActivity(signInIntent);
                    break;


                case R.id.btn_join:
                    Intent signUpIntent = new Intent(SignInActivity.this, SignUpActivity.class);
                    startActivity(signUpIntent);
                    break;

            }
        }
    }

}
