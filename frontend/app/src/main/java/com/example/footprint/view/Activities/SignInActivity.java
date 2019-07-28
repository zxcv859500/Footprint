package com.example.footprint.view.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.footprint.R;
import com.example.footprint.model.User;
import com.example.footprint.service.RestAPI;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

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

        SharedPreferences sf = getSharedPreferences("sFile", MODE_PRIVATE);
        String email = sf.getString("email", "");
        Log.d("for email", email);
        String pass = sf.getString("pass", "");
        Log.d("for email", pass);

        if (email.equals("") || pass.equals("")) {
        } else {
            signin(email,pass);
        }

        super.onResume();

    }

    class BtnOnClickListener implements Button.OnClickListener {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_login:
                    signin(etId.getText().toString(),etPassword.getText().toString());

                    break;


                case R.id.btn_join:
                    Intent signUpIntent = new Intent(SignInActivity.this, SignUpActivity.class);
                    startActivity(signUpIntent);
                    break;

            }
        }
    }


    private void signin(String id, String password){
        JSONObject jsonParams = new JSONObject();
        try {
            jsonParams.put("username",id)
                    .put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RestAPI.post("/auth/login", jsonParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // TODO: 토큰 저장
                Toast.makeText(SignInActivity.this, "로그인 되었습니다.", Toast.LENGTH_SHORT).show();


                SharedPreferences sharedPreferences = getSharedPreferences("sFile", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                if (cbAutoLogin.isChecked() == true) {

                    String email = etId.getText().toString();
                    String pass = etPassword.getText().toString();

                    editor.putString("email", email);
                    editor.putString("pass", pass);

                    editor.commit();
                } else {
                    editor.putString("email", "");
                    editor.putString("pass", "");

                }

                Intent signInIntent = new Intent(SignInActivity.this, MapActivity.class);
                signInIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                signInIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(signInIntent);
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(SignInActivity.this, "아이디, 비밀번호 오류", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
