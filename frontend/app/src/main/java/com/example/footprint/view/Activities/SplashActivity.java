package com.example.footprint.view.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.example.footprint.R;

public class SplashActivity extends Activity {

    Handler handler; // 헨들러 선언
    Loading loader; // 로더 클래스 선언

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        init();
        loader = new Loading(handler);
        new Thread(loader).start();

//        Handler handler = new Handler();
//        handler.postDelayed(new splashhandler(), 3000); // 1초 후에 hd handler 실행  3000ms = 3초
    }

    private class splashhandler implements Runnable {
        public void run() {
            Intent intent = new Intent(getApplication(), SignInActivity.class);
            startActivity(intent);
            finish();
        }
    }

    public void init(){

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
//                Toast.makeText(getApplicationContext(), "Loading Complete",Toast.LENGTH_LONG).show();
                finish();
            }
        };
    }


    @Override
    public void onBackPressed() {

    }
}
