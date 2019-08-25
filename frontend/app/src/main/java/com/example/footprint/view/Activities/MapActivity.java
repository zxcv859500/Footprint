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
import com.example.footprint.net.JwtDecoder;
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
    private FloatingActionButton fabMain, fabCamera, fabHere, fabMyPage, fabStatistics;
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
        fabStatistics = findViewById(R.id.fab_statistics);

        fabMain.setOnClickListener(new BtnOnClickListener());
        fabCamera.setOnClickListener(new BtnOnClickListener());
        fabHere.setOnClickListener(new BtnOnClickListener());
        fabMyPage.setOnClickListener(new BtnOnClickListener());
        fabStatistics.setOnClickListener(new BtnOnClickListener());

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
        BitmapDrawable[] bitmapdraw = {(BitmapDrawable) getResources().getDrawable(R.mipmap.ic_red),
                (BitmapDrawable) getResources().getDrawable(R.mipmap.ic_yellow),
                (BitmapDrawable) getResources().getDrawable(R.mipmap.ic_blue)};
        int size = markers.size();
        for (int i = 0; i < size; i++) {
            MarkerOptions markerOptions = new MarkerOptions();
            com.example.footprint.model.Marker marker = markers.get(i);
            markerOptions.position(new LatLng(Double.parseDouble(marker.getLatitude()),
                    Double.parseDouble(marker.getLongitude())));


            Bitmap b = bitmapdraw[marker.getType()].getBitmap();
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
                        fabStatistics.hide();
                        fabMain.setImageDrawable(getResources().getDrawable(R.drawable.ic_plus));
                    } else {
                        fabCamera.show();
                        fabHere.show();
                        fabMyPage.show();
                        fabStatistics.show();
                        if(decoderJwt()){
                            fabStatistics.hide();
                        }
                        fabMain.setImageDrawable(getResources().getDrawable(R.drawable.ic_minus));
                    }
                    break;
                case R.id.fab_camera:
                    Location current = whereAmI();
                    if (current != null) {
                        String thoroughfare = null;
                        String city = null;
                        com.example.footprint.net.Geocoder.getThoroughfare(current.getLatitude(), current.getLongitude());
                        thoroughfare = com.example.footprint.net.Geocoder.thoroughfare;
                        try {
                            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                            List<Address> addresses = geocoder.getFromLocation(current.getLatitude(), current.getLongitude(), 10);
                            Address address = addresses.get(0);
                            city = address.getLocality();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if (city.equals("전주시")) {
                            Intent postIntent = new Intent(MapActivity.this, PostActivity.class);
                            postIntent.putExtra("type", 0);
                            postIntent.putExtra("lat", current.getLatitude());
                            postIntent.putExtra("lng", current.getLongitude());
                            postIntent.putExtra("thoroughfare", thoroughfare);
                            startActivity(postIntent);
                        } else {
                            Toast.makeText(MapActivity.this, "전주시가 아닙니다.", Toast.LENGTH_SHORT).show();
                        }
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
                    }
                    else {
                        Log.e("location", "null");
                    }
                    break;
                case R.id.fab_my_page:
                    Intent myPageIntent = new Intent(MapActivity.this, MyPageActivity.class);
                    startActivity(myPageIntent);
                    break;
                case R.id.fab_statistics:
                    Intent statisticsIntent = new Intent(MapActivity.this, StatisticsActivity.class);
                    startActivity(statisticsIntent);
                    break;
            }
        }
    }

    private Boolean decoderJwt(){
        String previlage;
        Token token = Token.getTokenObject();
        String tmp;
        tmp = token.getTokenKey();
        try {
            JSONObject jsonObject = new JSONObject(JwtDecoder.decode(tmp));
            previlage = jsonObject.get("previlage").toString();
        } catch (JSONException e) {
            previlage = "99";
            e.printStackTrace();
        }



        Log.d("testToekn",JwtDecoder.decode(tmp));
        Log.d("testToekn",previlage);

        if(previlage.equals("0")){
            return true;
        }else {
            return false;
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
