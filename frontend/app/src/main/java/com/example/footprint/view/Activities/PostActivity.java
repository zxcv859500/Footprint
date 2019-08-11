package com.example.footprint.view.Activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.footprint.R;

public class PostActivity extends AppCompatActivity {
    private EditText etTitle, etContent;
    private ImageView ivImage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        etTitle = findViewById(R.id.et_title);
        etContent = findViewById(R.id.et_content);
        ivImage = findViewById(R.id.iv_image);

        ivImage.setImageResource(R.drawable.ic_plus);

        ivImage.setOnClickListener(new BtnOnClickListener());
    }

    class BtnOnClickListener implements Button.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.iv_image:
                    Log.e("button", "click");
                    break;
                case R.id.btn_post:
                    break;
            }
        }
    }
}
