package com.foodies.app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.IntentService;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ResultReceiver;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class ProfileAdresseActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mGoogleMap;
    private final int FINAL_LOCATION_KEY = 1792;
    private CardView get_location_btn,validate_user_address_btn;
    private MaterialEditText profile_adresse_name;
    private DatabaseReference mAddressRef;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_profile_adresse);
        SupportMapFragment supportMapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(this);
        get_location_btn = findViewById(R.id.get_location_btn);
        profile_adresse_name = findViewById(R.id.profile_adresse_name);
        validate_user_address_btn = findViewById(R.id.validate_user_address_btn);
        if (mAuth.getCurrentUser() !=null){
            mAddressRef = FirebaseDatabase.getInstance().getReference("Users").child(mAuth.getCurrentUser().getUid());
            validate_user_address_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (profile_adresse_name.getText().toString().isEmpty()){
                        Toast.makeText(ProfileAdresseActivity.this, "Entrez le nom de l'adresse ou sélectionnez Détection automatique", Toast.LENGTH_LONG).show();
                    }else {
                        mAddressRef.child("Address").setValue(profile_adresse_name.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(ProfileAdresseActivity.this, "Adresse ajoutée avec succès", Toast.LENGTH_LONG).show();
                                finish();
                            }
                        });
                    }

                }
            });
        }

        get_location_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(ProfileAdresseActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},FINAL_LOCATION_KEY);
                    Log.i("Location Permission", "Permission not Granted");
                }else {
                    getShippingAdresseLocation();
                }
            }
        });
        //hide ActionBar
        getSupportActionBar().hide();
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        LatLng latLng = new LatLng(-34, 151);
        mGoogleMap.addMarker(new MarkerOptions().position(latLng).draggable(true));
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == FINAL_LOCATION_KEY && grantResults.length>0){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Log.d("Location Permission", "onRequestPermissionsResult: Granted");
                getShippingAdresseLocation();
            }
        }

    }

    private void getShippingAdresseLocation() {
        final LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        //Location Service Section
        LocationServices.getFusedLocationProviderClient(ProfileAdresseActivity.this).requestLocationUpdates(locationRequest,new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                LocationServices.getFusedLocationProviderClient(ProfileAdresseActivity.this).removeLocationUpdates(this);
                if (locationResult !=null && locationResult.getLocations().size() >0){
                    int locationindex = locationResult.getLocations().size() -1;
                    double latitude = locationResult.getLocations().get(locationindex).getLatitude();
                    double longtitude = locationResult.getLocations().get(locationindex).getLongitude();
                    refrech_map(latitude,longtitude);
                    Geocoder geocoder = new Geocoder(ProfileAdresseActivity.this, Locale.getDefault());
                    List<Address> addressList;
                    try {
                        addressList = geocoder.getFromLocation(latitude,longtitude,1);
                        profile_adresse_name.setText(addressList.get(0).getAddressLine(0));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }

        }, Looper.getMainLooper());
    }

    private void refrech_map(double latitude, double longtitude) {
        GoogleMap map = mGoogleMap;
        LatLng latLng = new LatLng(latitude, longtitude);
        mGoogleMap.addMarker(new MarkerOptions().position(latLng).draggable(true));
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,15.0f));
    }
    public class RetrieveAdresse extends ResultReceiver{

        public RetrieveAdresse(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            super.onReceiveResult(resultCode, resultData);
            if (resultCode == 1){
                Log.e("Adresse Name ", resultData.getString("ADRESSE_KEY"));
            }
        }
    }
}
