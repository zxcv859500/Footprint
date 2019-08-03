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
import com.example.footprint.model.Token;
import com.example.footprint.model.User;
import com.example.footprint.net.RestAPI;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

public class SignInActivity extends AppCompatActivity {
    EditText etId, etPassword;
    CheckBox cbAutoLogin;
    Button btnLogin, btnJoin;
    User user = new User();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);


        Intent intent = new Intent(this, SplashActivity.class); // 로딩화면 준비
        startActivity(intent); // 스플레시 엑티비티 시작


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

        if (email.equals("") && pass.equals("")) {
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
                    user.setUserName(etId.getText().toString());
                    user.setPassword(etPassword.getText().toString());
                    signin(etId.getText().toString(),etPassword.getText().toString());
                    break;


                case R.id.btn_join:
                    Intent signUpIntent = new Intent(SignInActivity.this, SignUpActivity.class);
                    startActivity(signUpIntent);
                    break;

            }
        }
    }


    private void signin(String id, final String password){
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

                Toast.makeText(SignInActivity.this, "로그인 되었습니다.", Toast.LENGTH_SHORT).show();
                JSONObject docs= null;
                Log.d("test response",""+ response);
                if(response != null){
                    try {
                        String tokenkey;

                        docs = response;
                        tokenkey = docs.getString("token");
                        Log.d("test response",tokenkey);
                        Token token = Token.getTokenObject();
                        token.setTokenKey(tokenkey);
                        Log.d("Token",tokenkey.toString());
                        token.setUser(user);
                    }catch (JSONException e){
                        Log.d("Token","Fail for Exception");
                    }
                }else{
                    Log.d("Token","Fail");
                }

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
