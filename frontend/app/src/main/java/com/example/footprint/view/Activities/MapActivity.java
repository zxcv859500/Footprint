package com.example.footprint.view.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.footprint.R;
import com.example.footprint.model.Token;
import com.example.footprint.net.RestAPI;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    private GoogleMap googleMap;
    private FloatingActionButton fabMain, fabCamera, fabHere, fabMyPage;
    private LocationManager locationManager;
    private Marker marker;
    private ArrayList<com.example.footprint.model.Marker> markerArrayList;

    @Override
    protected void onStart() {
        super.onStart();
        markerArrayList = new ArrayList<>();
        RestAPI.get("/marker/list", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                if (response != null) {
                    int size = response.length();
                    for (int i = 0; i < size; i++) {
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = (JSONObject) response.get(i);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (jsonObject != null) {
                            com.example.footprint.model.Marker marker = new com.example.footprint.model.Marker(jsonObject);
                            markerArrayList.add(marker);
                        }
                    }
                    loadMarker(markerArrayList);
                }
            }
        });
    }

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

        markerArrayList = new ArrayList<>();
        RestAPI.get("/marker/list", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                if (response != null) {
                    int size = response.length();
                    for (int i = 0; i < size; i++) {
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = (JSONObject) response.get(i);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (jsonObject != null) {
                            com.example.footprint.model.Marker marker = new com.example.footprint.model.Marker(jsonObject);
                            markerArrayList.add(marker);
                        }
                    }
                    loadMarker(markerArrayList);
                }
            }
        });
    }

    public void loadMarker(ArrayList<com.example.footprint.model.Marker> markers){
        int size = markers.size();
        for (int i = 0; i < size; i++) {
            MarkerOptions markerOptions = new MarkerOptions();
            com.example.footprint.model.Marker marker = markers.get(i);
            markerOptions.position(new LatLng(Double.parseDouble(marker.getLatitude()),
                    Double.parseDouble(marker.getLongitude())));

            BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.black);
            Bitmap b = bitmapdraw.getBitmap();
            Bitmap smallMarker = Bitmap.createScaledBitmap(b, 150, 150, false);
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));

            googleMap.addMarker(markerOptions);
            googleMap.setOnMarkerClickListener(this);
        }
    }

    public Location whereAmI() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MapActivity.this,
                        new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
                        0);
                return whereAmI();
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
                    Location current = whereAmI();
                    if (current != null) {
                        String thoroughfare = null;
                        try {
                            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                            Log.e("lat", Double.toString(current.getLatitude()));
                            Log.e("lng", Double.toString(current.getLongitude()));
                            List<Address> addresses = geocoder.getFromLocation(current.getLatitude(), current.getLongitude(), 10);
                            Address address = addresses.get(0);
                            thoroughfare = address.getThoroughfare();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Intent postIntent = new Intent(MapActivity.this, PostActivity.class);
                        postIntent.putExtra("type", 0);
                        postIntent.putExtra("lat", current.getLatitude());
                        postIntent.putExtra("lng", current.getLongitude());
                        postIntent.putExtra("thoroughfare", thoroughfare);
                        startActivity(postIntent);
                    }
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
                    Intent myPageIntent = new Intent(MapActivity.this, MyPageActivity.class);
                    startActivity(myPageIntent);
                    break;
            }
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        double lat,lng;
        lat = marker.getPosition().latitude;
        lng = marker.getPosition().longitude;


        Intent intent = new Intent(MapActivity.this, NoticeBoardActivity.class);
        intent.putExtra("lat",lat);
        intent.putExtra("lng",lng);
        startActivity(intent);

        return true;
    }
}
