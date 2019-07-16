package com.example.footprint.view.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.footprint.R;
import com.example.footprint.view.Fragment.NoticeBoardBlueFragment;
import com.example.footprint.view.Fragment.NoticeBoardRedFragment;
import com.example.footprint.view.Fragment.NoticeBoardYellowFragment;

public class NoticeBoardActivity extends AppCompatActivity {

    private Button btnNoticeRed, btnNoticeYellow, btnNoticeBlue;


    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
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

        btnNoticeRed.setOnClickListener(new BtnOnClickListener());
        btnNoticeYellow.setOnClickListener(new BtnOnClickListener());
        btnNoticeBlue.setOnClickListener(new BtnOnClickListener());

        noticeBoardRedFragment = new NoticeBoardRedFragment();
        noticeBoardYellowFragment = new NoticeBoardYellowFragment();
        noticeBoardBlueFragment = new NoticeBoardBlueFragment();

        Toolbar tb = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(tb) ;
        setFragment(0);


    }

    class BtnOnClickListener implements Button.OnClickListener{
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.btn_notice_red:
                    setFragment(0);
                    break;
                case R.id.btn_notice_yellow:
                    setFragment(1);
                    break;
                case R.id.btn_notice_blue:
                    setFragment(2);
                    break;
            }
        }
    }

    private void setFragment(int n){
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();

        switch (n){
            case 0:
                fragmentTransaction.replace(R.id.fragment,noticeBoardRedFragment);
                fragmentTransaction.commit();
                break;
            case 1:
                fragmentTransaction.replace(R.id.fragment,noticeBoardYellowFragment);
                fragmentTransaction.commit();
                break;
            case 2:
                fragmentTransaction.replace(R.id.fragment,noticeBoardBlueFragment);
                fragmentTransaction.commit();
                break;
        }

    }
}
