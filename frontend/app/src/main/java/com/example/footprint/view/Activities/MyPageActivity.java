package com.example.footprint.view.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.footprint.R;
import com.example.footprint.model.Token;
import com.example.footprint.net.JwtDecoder;
import com.example.footprint.net.RestAPI;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

public class MyPageActivity extends AppCompatActivity {
    private EditText etNickName, etPassword, etPasswordCheck;
    private Button btnModify, btnLogout, btnWithdrawal;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_page);

        etNickName = findViewById(R.id.et_nickname);
        etPassword = findViewById(R.id.et_password);
        etPasswordCheck = findViewById(R.id.et_password_check);
        btnModify = findViewById(R.id.btn_modify);
        btnLogout = findViewById(R.id.btn_logout);
        btnWithdrawal = findViewById(R.id.btn_withdrawal);

        btnModify.setOnClickListener(new BtnOnClickListener());
        btnLogout.setOnClickListener(new BtnOnClickListener());
        btnWithdrawal.setOnClickListener(new BtnOnClickListener());

        String token = Token.getTokenObject().getTokenKey();
        try {
            JSONObject json = new JSONObject(JwtDecoder.decode(token));
            etNickName.setText(json.get("nickname").toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    class BtnOnClickListener implements Button.OnClickListener {
        JSONObject jsonParams = new JSONObject();

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_modify:
                    if (etPassword.getText().toString().equals(etPasswordCheck.getText().toString())) {
                        try {
                            jsonParams.put("password", etPassword.getText().toString())
                                    .put("nickname", etNickName.getText().toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        RestAPI.post("/auth/edit", jsonParams, Token.getTokenObject().getTokenKey(), new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                Toast.makeText(MyPageActivity.this, "변경되었습니다.", Toast.LENGTH_SHORT).show();
                                Token token = Token.getTokenObject();
                                Intent intent = new Intent(MyPageActivity.this, SignInActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |
                                        Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                        Intent.FLAG_ACTIVITY_NEW_TASK);
                                token.setTokenKey(null);
                                removeAutoLogin();
                                startActivity(intent);
                            }
                        });
                    } else {
                        Toast.makeText(MyPageActivity.this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.btn_logout:
                    Token token = Token.getTokenObject();
                    Intent intent = new Intent(MyPageActivity.this, SignInActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |
                            Intent.FLAG_ACTIVITY_CLEAR_TOP |
                            Intent.FLAG_ACTIVITY_NEW_TASK);
                    token.setTokenKey(null);
                    removeAutoLogin();
                    startActivity(intent);
                    break;
                case R.id.btn_withdrawal:
                    AlertDialog.Builder builder = new AlertDialog.Builder(MyPageActivity.this);
                    builder.setTitle("회원탈퇴");
                    builder.setMessage("정말로 탈퇴하시겠습니까?")
                            .setCancelable(false)
                            .setPositiveButton("탈퇴",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            RestAPI.get("/auth/secession", Token.getTokenObject().getTokenKey(), new JsonHttpResponseHandler() {
                                                @Override
                                                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                                    Toast.makeText(MyPageActivity.this, "탈퇴되었습니다.", Toast.LENGTH_SHORT).show();
                                                    Token token = Token.getTokenObject();
                                                    Intent intent = new Intent(MyPageActivity.this, SignInActivity.class);
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |
                                                            Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                                            Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    token.setTokenKey(null);
                                                    removeAutoLogin();
                                                    startActivity(intent);

                                                }
                                            });
                                        }
                                    })
                            .setNegativeButton("취소",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.cancel();
                                        }
                                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                    break;
            }
        }
    }

    private void removeAutoLogin() {

        SharedPreferences sharedPreferences = getSharedPreferences("sFile", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("email", "");
        editor.putString("pass", "");
        editor.commit();

    }
}
