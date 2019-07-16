package com.example.footprint.view.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.footprint.R;
import com.example.footprint.view.Fragment.NoticeBoardBlueFragment;
import com.example.footprint.view.Fragment.NoticeBoardRedFragment;
import com.example.footprint.view.Fragment.NoticeBoardYellowFragment;

public class NoticeBoardActivity extends AppCompatActivity {

    private Button btnNoticeRed, btnNoticeYellow, btnNoticeBlue;


    NoticeBoardRedFragment noticeBoardRedFragment;
    NoticeBoardYellowFragment noticeBoardYellowFragment;
    NoticeBoardBlueFragment noticeBoardBlueFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_board);

        btnNoticeRed = (Button)findViewById(R.id.btn_notice_red);
        btnNoticeYellow = (Button)findViewById(R.id.btn_notice_yellow);
        btnNoticeBlue = (Button) findViewById(R.id.btn_notice_blue);


    }

    class BtnOnClickListener implements Button.OnClickListener{
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.btn_notice_red:
                    break;
                case R.id.btn_notice_yellow:
                    break;
                case R.id.btn_notice_blue:
                    break;
            }
        }
    }
}
