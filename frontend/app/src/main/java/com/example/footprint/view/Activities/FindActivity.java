package com.example.footprint.view.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.footprint.R;
import com.example.footprint.view.Fragment.FindIdFragment;
import com.example.footprint.view.Fragment.FindPasswordFragment;

public class FindActivity extends AppCompatActivity {
    private Button btnFindId;
    private Button btnFindPassword;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find);

        btnFindId = findViewById(R.id.btn_find_id);
        btnFindPassword = findViewById(R.id.btn_find_password);

        btnFindId.setOnClickListener(new BtnOnClickListener());
        btnFindPassword.setOnClickListener(new BtnOnClickListener());

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        FindIdFragment findIdFragment = new FindIdFragment();
        fragmentTransaction.replace(R.id.fragment_find, findIdFragment);
        fragmentTransaction.commit();
    }

    class BtnOnClickListener implements Button.OnClickListener {
        FragmentTransaction fragmentTransaction;

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_find_id:
                    fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    FindIdFragment findIdFragment = new FindIdFragment();
                    fragmentTransaction.replace(R.id.fragment_find, findIdFragment);
                    fragmentTransaction.commit();
                    break;
                case R.id.btn_find_password:
                    fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    FindPasswordFragment findPasswordFragment = new FindPasswordFragment();
                    fragmentTransaction.replace(R.id.fragment_find, findPasswordFragment);
                    fragmentTransaction.commit();
                    break;
            }
        }
    }
}
