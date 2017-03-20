package com.example.jayti.hometowns;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener, View.OnClickListener {

    private GoogleMap mMap;
    Button setLoc;
    Double iLatitude,iLongitude;
    LatLng clickedpos;
    int change =0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Bundle b = getIntent().getExtras();
        iLatitude = b.getDouble("Latitude");
        iLongitude = b.getDouble("Longitude");
        Log.i("rew","Latitude" + iLatitude);
        Log.i("rew","Longitude" + iLongitude);


        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        setLoc = (Button) findViewById(R.id.setLocation);
        setLoc.setOnClickListener(this);


    }
    @Override
    public void onClick(View v) {
        if(change == 1) {
            Intent returnIntent = new Intent();
            returnIntent.putExtra("latitude", iLatitude);
            returnIntent.putExtra("longitude", iLongitude);
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
        }
        else {
            Intent returnIntent = new Intent();
            setResult(Activity.RESULT_CANCELED, returnIntent);
            finish();
        }
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if((iLatitude==0.0) && (iLongitude==0.0)) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
            mMap.setOnMapClickListener(this);
        }
        else {
            LatLng area = new LatLng(iLatitude,iLongitude);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(area));
            mMap.setOnMapClickListener(this);
        }

    }


    @Override
    public void onMapClick(LatLng location) {
        mMap.clear();
        change = 1;
        clickedpos = new LatLng(location.latitude,location.longitude);
        iLatitude = location.latitude;
        iLongitude = location.longitude;
        Log.i("rew", "new Location " + iLatitude + " longitude " + iLongitude );
        mMap.addMarker(new MarkerOptions().position(clickedpos).title("Clicked here"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(clickedpos));
    }


}
