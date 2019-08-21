package com.example.footprint.view.Activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.example.footprint.R;
import com.example.footprint.model.Token;
import com.example.footprint.net.JwtDecoder;
import com.example.footprint.net.RestAPI;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
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
    private static final int PICK_FROM_ALBUM = 1;
    private String imageFilePath;
    private Uri photoUri;
    private File photoFile;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_post);

        Intent intent = getIntent();
        type = intent.getExtras().getInt("type");
        lat = intent.getExtras().getDouble("lat");
        lng = intent.getExtras().getDouble("lng");
        thoroughfare = intent.getExtras().getString("thoroughfare");

        TedPermission.with(getApplicationContext())
                .setPermissionListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {

                    }

                    @Override
                    public void onPermissionDenied(ArrayList<String> deniedPermissions) {

                    }
                })
                .setPermissions(Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                .check();

        String token = Token.getTokenObject().getTokenKey();
        try {
            JSONObject json = new JSONObject(JwtDecoder.decode(token));
            nickname = json.getString("nickname");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        etTitle = findViewById(R.id.et_title);
        etContent = findViewById(R.id.et_content);
        ivImage = findViewById(R.id.iv_image);
        btnPost = findViewById(R.id.btn_post);

        ivImage.setOnClickListener(new BtnOnClickListener());
        btnPost.setOnClickListener(new BtnOnClickListener());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bitmap bitmap = BitmapFactory.decodeFile(imageFilePath);
            ExifInterface exif = null;

            try {
                if (imageFilePath != null) {
                    exif = new ExifInterface(imageFilePath);
                } else {
                    Log.e("error", "error");
                    return;
                }
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
        } else if (requestCode == PICK_FROM_ALBUM) {
            Uri photoUri = data.getData();
            Cursor cursor = null;

            try {
                String[] proj = {MediaStore.Images.Media.DATA};

                assert photoUri != null;
                cursor = getContentResolver().query(photoUri, proj, null, null, null);

                assert cursor != null;
                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

                cursor.moveToFirst();

                photoFile = new File(cursor.getString(columnIndex));
            } finally {
               if (cursor != null) {
                   cursor.close();
               }
            }

            BitmapFactory.Options options = new BitmapFactory.Options();
            Bitmap bitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath(), options);
            ivImage.setImageBitmap(bitmap);
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

        while (imageFilePath == null) {
            imageFilePath = image.getAbsolutePath();
        }
        return image;
    }

    private void imageLoad() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }

    private void uploadImageAction() {
        switch (type) {
            case 0:
                imageCapture();
                break;
            case 1:
                imageLoad();
                break;
            case 2:
                CharSequence[] charSequences = new CharSequence[] {"사진 찍기", "사진 가져오기"};
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setItems(charSequences, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                switch (i) {
                                    case 0:
                                        imageCapture();
                                        break;
                                    case 1:
                                        imageLoad();
                                        break;
                                }
                                dialogInterface.dismiss();
                            }
                        });
                builder.create().show();
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

                    String title = etTitle.getText().toString();
                    String content = etContent.getText().toString();
                    list.add(new BasicNameValuePair("title", title));
                    list.add(new BasicNameValuePair("content", content));
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

