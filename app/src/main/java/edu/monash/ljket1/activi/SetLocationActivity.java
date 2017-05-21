package edu.monash.ljket1.activi;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;
import java.util.Locale;

public class SetLocationActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LatLng mPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_location);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.setLocationMap);
        mapFragment.getMapAsync(this);

        Button setLocationButton = (Button) findViewById(R.id.setLocationButton);
        setLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("latitude", String.valueOf(mPosition.latitude));
                intent.putExtra("longitude", String.valueOf(mPosition.longitude));
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMinZoomPreference(13.0f);
        mMap.moveCamera(CameraUpdateFactory.zoomTo(16.0f));

        mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                TextView textView = (TextView) findViewById(R.id.setLocationText);
                textView.setText("" + mMap.getCameraPosition().target.longitude + ", " + mMap.getCameraPosition().target.latitude);
            }
        });

        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                TextView textView = (TextView) findViewById(R.id.setLocationText);
                mPosition = mMap.getCameraPosition().target;
                try {
                    Geocoder geo = new Geocoder(getApplicationContext(), Locale.getDefault());
                    List<Address> addresses = geo.getFromLocation(mPosition.latitude, mPosition.longitude, 1);
                    if (addresses.size() > 0) {
                        textView.setText("" + mMap.getCameraPosition().target.longitude + ", " + mMap.getCameraPosition().target.latitude + "\n" + addresses.get(0).getAddressLine(0));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        LatLng pos = new LatLng(-37.8136, 144.9631);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(pos));
    }
}
