package shafin.vision;

import android.*;
import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class  MapsActivity extends
        FragmentActivity implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private GoogleMap mMap;
    private ArrayList<MarkerOptions> markers;
    private DatabaseReference reference;
    private LocationManager locationManager;
    private DatabaseReference myRef;
    private Place selectedPlace;
    private boolean firstTime = true;
    private Location yourLocation;
    boolean paused;
    public static final int REQUEST_LOCATION_CODE = 99;
    private GoogleApiClient client;
    String myLocationMarkerName;
    Button b,bu;
    private final int REQ_CODE_SPEACH_OUTPUT=143;
    //Location location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        b=(Button)findViewById(R.id.voiceButton);
        bu=(Button)findViewById(R.id.button);
        Intent intent = getIntent();

        myLocationMarkerName = intent.getStringExtra(MapsStartPage.EXTRA_MESSAGE);
        if (myLocationMarkerName.length() == 0) myLocationMarkerName = "blind person";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }

        reference = FirebaseDatabase.getInstance().getReference();

        myRef = reference.child("Users").push();
        myRef.child("name").setValue(myLocationMarkerName);
        if (yourLocation != null) {

            myRef.child("lat").setValue(yourLocation.getLatitude());
            myRef.child("lng").setValue(yourLocation.getLongitude());
        }


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 200, 0, this);
        }


        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnToOpenMic();
            }
        });
        bu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //search();
                EditText editText = (EditText)findViewById(R.id.editText);
                String location =editText.getText().toString();
                editText.setText("");
                find(location);

            }
        });

    }




    //voice option starts........................
    private void btnToOpenMic(){

        Intent intent =new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Speak to search");

        try {
            startActivityForResult(intent,REQ_CODE_SPEACH_OUTPUT);
        }
        catch (ActivityNotFoundException e){
            Toast.makeText(getApplicationContext(),
                    "Sorry! Your device doesn\\'t support speech input",
                    Toast.LENGTH_SHORT).show();

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch ( requestCode){
            case REQ_CODE_SPEACH_OUTPUT:{
                if (resultCode == RESULT_OK && null != data){
                    ArrayList<String>voiceInText=data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    // String v=data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    // String d = voiceInText.toString();

                    String text =voiceInText.get(0);
                    find(text);

                }
                break;
            }
        }




    }
    //voice option ends........................


    public void find (String s){

        List<Address>addressList=null;
        MarkerOptions markerOptions = new MarkerOptions();
        if (!s.equals("")){
            Geocoder geocoder = new Geocoder(this);
            try {
                addressList=geocoder.getFromLocationName(s, 5);
            } catch (IOException e) {
                e.printStackTrace();
            }
            for (int i=0;i<addressList.size();i++){
                Address myAddress=addressList.get(i);
                LatLng latLng= new LatLng(myAddress.getLatitude(),myAddress.getLongitude());
                markerOptions.position(latLng);
                markerOptions.title(s);
                mMap.addMarker(markerOptions);
                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        if (client == null) {

                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_LONG).show();
                }
                return;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
        mMap.setMapType(mMap.MAP_TYPE_HYBRID);
    }

    public void refreshMarkers() {
        mMap.clear();
        for (MarkerOptions marker : markers) {
            //Add the marker
            mMap.addMarker(marker);
        }


        try {
            mMap.addMarker(new MarkerOptions().position(selectedPlace.getLatLng()).icon(BitmapDescriptorFactory.defaultMarker()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();



        reference.child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                try {
                    markers = new ArrayList<>();


                    for (DataSnapshot user : dataSnapshot.getChildren()) {

                        MarkerOptions marker = new MarkerOptions();

                        marker.position(new LatLng(Float.parseFloat(user.child("lat").getValue() + ""), Float.parseFloat(user.child("lng").getValue() + "")));

                        marker.title(user.child("name").getValue(String.class));

                        marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                        markers.add(marker);
                    }

                    refreshMarkers();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("PICKLES", "onPause!");
        paused = true;

        myRef.removeValue();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            locationManager.removeUpdates(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(myRef != null) {

            myRef.child("name").setValue(myLocationMarkerName);
            if (yourLocation != null) {
                myRef.child("lat").setValue(yourLocation.getLatitude());
                myRef.child("lng").setValue(yourLocation.getLongitude());
            }
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 200, 0, this);
            }
        }
    }
    protected synchronized void buildGoogleApiClient() {

        client = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        client.connect();
    }


    @Override
    public void onLocationChanged(Location location) {
        yourLocation = location;

        if(firstTime){
            mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(),location.getLongitude())));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
            firstTime = false;
        }
        if(mMap != null && !paused) {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            myRef.child("lat").setValue(latLng.latitude + "");
            myRef.child("lng").setValue(latLng.longitude + "");
        }
    }




    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_CODE);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_CODE);
            }
            return false;
        }

        return false;
    }




    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
