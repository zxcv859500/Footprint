package com.example.footprint.view.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.footprint.R;
import com.example.footprint.model.Token;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;
import java.util.function.ToDoubleBiFunction;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final int REQUEST_IMAGE_CAPTURE = 672;
    private GoogleMap googleMap;
    private FloatingActionButton fabMain, fabCamera, fabHere;
    private LocationManager locationManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        fabMain = findViewById(R.id.fab_main);
        fabCamera = findViewById(R.id.fab_camera);
        fabHere = findViewById(R.id.fab_here);

        fabMain.setOnClickListener(new BtnOnClickListener());
        fabCamera.setOnClickListener(new BtnOnClickListener());
        fabHere.setOnClickListener(new BtnOnClickListener());

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.fragment);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        LatLng jeonJu = new LatLng(35.828521, 127.115604);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(jeonJu, (float)11.9));
    }

    public Location whereAmI() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MapActivity.this,
                        new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
                        0);
                return null;
            }
        }

        Location location = null;
        List<String> providers = locationManager.getAllProviders();
        for (String provider : providers) {
            if (provider == null) {
                continue;
            }
            if (location == null) {
                location = locationManager.getLastKnownLocation(provider);
            }
        }

        return location;
    }

    public void imageCapture() {
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {
                Toast.makeText(this,"000부분 사용을 위해 카메라 권한이 필요합니다.",Toast.LENGTH_LONG).show();
            }

        }

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
//        TODO:
//        찍은 사진 파일을 가져올 것
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    class BtnOnClickListener implements FloatingActionButton.OnClickListener {

        @Override
        public void onClick(View view) {
            switch(view.getId()) {
                case R.id.fab_main:
                    if (fabCamera.getVisibility() == View.VISIBLE) {
                        fabCamera.hide();
                        fabHere.hide();
                        fabMain.setImageDrawable(getResources().getDrawable(R.drawable.ic_plus));
                    } else {
                        fabCamera.show();
                        fabHere.show();
                        fabMain.setImageDrawable(getResources().getDrawable(R.drawable.ic_minus));
                    }
                    break;
                case R.id.fab_camera:
                    imageCapture();
                    // TODO:
                    // 찍은 이미지와 현위치 정보를 서버에 보내고
                    // 마커 찍기
                    break;
                case R.id.fab_here:
                    Location location = whereAmI();
                    if (location != null) {
                        double lat = location.getLatitude();
                        double lng = location.getLongitude();
                        LatLng here = new LatLng(lat, lng);
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(here, 16));
                        break;
                    }
            }
        }
    }
}
