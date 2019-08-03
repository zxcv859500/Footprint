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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;
import java.util.function.ToDoubleBiFunction;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final int REQUEST_IMAGE_CAPTURE = 672;
    private GoogleMap googleMap;
    private FloatingActionButton fabMain, fabCamera, fabHere, fabMyPage;
    private LocationManager locationManager;
    private Marker marker;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        fabMain = findViewById(R.id.fab_main);
        fabCamera = findViewById(R.id.fab_camera);
        fabHere = findViewById(R.id.fab_here);
        fabMyPage = findViewById(R.id.fab_my_page);

        fabMain.setOnClickListener(new BtnOnClickListener());
        fabCamera.setOnClickListener(new BtnOnClickListener());
        fabHere.setOnClickListener(new BtnOnClickListener());
        fabMyPage.setOnClickListener(new BtnOnClickListener());

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
                        fabMyPage.hide();
                        fabMain.setImageDrawable(getResources().getDrawable(R.drawable.ic_plus));
                    } else {
                        fabCamera.show();
                        fabHere.show();
                        fabMyPage.show();
                        fabMain.setImageDrawable(getResources().getDrawable(R.drawable.ic_minus));
                    }
                    break;
                case R.id.fab_camera:
                    break;
                case R.id.fab_here:
                    Location location = whereAmI();
                    if (location != null) {
                        if (marker != null) {
                            marker.remove();
                        }
                        double lat = location.getLatitude();
                        double lng = location.getLongitude();
                        LatLng here = new LatLng(lat, lng);
                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(here);
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(here, 16));
                        marker = googleMap.addMarker(markerOptions);
                        break;
                    }
                case R.id.fab_my_page:
                    break;
            }
        }
    }
}
