package com.example.footprint.view.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.footprint.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {
    GoogleMap googleMap;
    FloatingActionButton fabMain, fabCamera, fabHere;

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

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.fragment);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        LatLng jeonJu = new LatLng(35.828521, 127.115604);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(jeonJu, (float)11.9));
    }

    class BtnOnClickListener implements FloatingActionButton.OnClickListener {

        @Override
        public void onClick(View view) {
            switch(view.getId()) {
                case R.id.fab_main:
                    if (fabCamera.getVisibility() == View.VISIBLE) {
                        fabCamera.hide();
                        fabHere.hide();
                    } else {
                        fabCamera.show();
                        fabHere.show();
                    }
                    break;
                case R.id.fab_camera:
                    break;
                case R.id.fab_here:
                    break;
            }
        }
    }
}
