package com.example.footprint.view.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

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
    FloatingActionButton floatingActionButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        //Intent intent = new Intent(MapActivity.this,NoticeBoardActivity.class);
        //startActivity(intent);

        floatingActionButton = findViewById(R.id.fab_main);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        try {
            MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.fragment);
            mapFragment.getMapAsync(this);
        } catch (NullPointerException e) {

        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        LatLng jeonJu = new LatLng(35.828521, 127.115604);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(jeonJu, (float)11.9));
    }
}
