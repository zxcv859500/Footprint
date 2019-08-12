package com.example.footprint.view.Activities;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.example.footprint.R;
import com.example.footprint.model.Token;
import com.example.footprint.net.JwtDecoder;
import com.example.footprint.net.RestAPI;
import com.gun0912.tedpermission.TedPermission;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PostActivity extends AppCompatActivity {
    private EditText etTitle, etContent;
    private ImageView ivImage;
    private Button btnPost;

    private int type;
    private double lat, lng;
    private String thoroughfare, nickname;

    private static final int REQUEST_IMAGE_CAPTURE = 672;
    private String imageFilePath;
    private Uri photoUri;
    private File photoFile;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        Intent intent = getIntent();
        type = intent.getExtras().getInt("type");
        lat = intent.getExtras().getDouble("lat");
        lng = intent.getExtras().getDouble("lng");
        thoroughfare = intent.getExtras().getString("thoroughfare");

        String token = Token.getTokenObject().getTokenKey();
        try {
            JSONObject json = new JSONObject(JwtDecoder.decode(token));
            nickname = json.getString("nickname");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        TedPermission.with(getApplicationContext())
                .setPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        etTitle = findViewById(R.id.et_title);
        etContent = findViewById(R.id.et_content);
        ivImage = findViewById(R.id.iv_image);
        btnPost = findViewById(R.id.btn_post);

        ivImage.setImageResource(R.drawable.ic_plus);

        ivImage.setOnClickListener(new BtnOnClickListener());
        btnPost.setOnClickListener(new BtnOnClickListener());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bitmap bitmap = BitmapFactory.decodeFile(imageFilePath);
            ExifInterface exif = null;

            try {
                exif = new ExifInterface(imageFilePath);
            } catch (IOException e) {
                e.printStackTrace();
            }

            int exifOrientation;
            int exifDegree = 0;

            if (exif != null) {
                exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                exifDegree = exifOrientationToDegree(exifOrientation);
            }

            ivImage.setImageBitmap(rotate(bitmap, exifDegree));
        }
    }

    private int exifOrientationToDegree(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    private Bitmap rotate(Bitmap bitmap, float degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    private void imageCapture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            try {
                photoFile = createImageFile();
            } catch (IOException e) {

            }

            if (photoFile != null) {
                photoUri = FileProvider.getUriForFile(getApplicationContext(), getPackageName(), photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "TEST_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        imageFilePath = image.getAbsolutePath();
        return image;
    }

    private void uploadImageAction() {
        switch (type) {
            case 0:
                imageCapture();
                break;
            case 1:
                break;
            case 2:
                break;
        }
    }

    class BtnOnClickListener implements Button.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.iv_image:
                    uploadImageAction();
                    break;
                case R.id.btn_post:
                    List<BasicNameValuePair> list = new ArrayList<>();

                    try {
                        list.add(new BasicNameValuePair("title", new String(etTitle.getText().toString().getBytes(), "UTF-8")));
                        list.add(new BasicNameValuePair("content", new String(etContent.getText().toString().getBytes(), "UTF-8")));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    list.add(new BasicNameValuePair("latitude", Double.toString(lat)));
                    list.add(new BasicNameValuePair("longitude", Double.toString(lng)));
                    list.add(new BasicNameValuePair("road", thoroughfare));
                    list.add(new BasicNameValuePair("type", Integer.toString(type)));

                    RestAPI.post("/post/write", list, photoFile);

                    Intent mapIntent = new Intent(PostActivity.this, MapActivity.class);
                    mapIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |
                        Intent.FLAG_ACTIVITY_CLEAR_TOP |
                        Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(mapIntent);
                    break;
            }
        }
    }
}
