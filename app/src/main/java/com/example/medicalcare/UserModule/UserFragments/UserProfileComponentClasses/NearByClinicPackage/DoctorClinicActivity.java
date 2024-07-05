package com.example.medicalcare.UserModule.UserFragments.UserProfileComponentClasses.NearByClinicPackage;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

import com.example.medicalcare.DoctorModule.DoctorFragments.DoctorProfileComponentClasses.ClinicLocationActivity;
import com.example.medicalcare.R;
import com.example.medicalcare.StartActivity;
import com.example.medicalcare.databinding.ActivityDoctorClinicBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.aviran.cookiebar2.CookieBar;

import java.util.List;
import java.util.Locale;

public class DoctorClinicActivity extends AppCompatActivity {

    ActivityDoctorClinicBinding views;
    String refenrencenumber;
    String latitude,longitude,experience,profile,address,userlatitude,userlongitude;
    LocationManager locationManager;
    FusedLocationProviderClient fusedLocationClient;

    String sourceLocation,destinationLocation;

    DatabaseReference detailsRef= FirebaseDatabase.getInstance().getReference("DoctorDetails");
    NetworkChangeReceiver networkChangeReceiver;

    OnBackPressedDispatcher onBackPressedDispatcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        views=ActivityDoctorClinicBinding.inflate(getLayoutInflater());
        setContentView(views.getRoot());

        networkChangeReceiver = new NetworkChangeReceiver();
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);

        registerReceiver(networkChangeReceiver, intentFilter);

        onBackPressedDispatcher = this.getOnBackPressedDispatcher();

        onBackPressedDispatcher.addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Handle back button press
                finish();

            }
        });
        views.backButton.setOnClickListener(task->{
            onBackPressedDispatcher.onBackPressed();
        });
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        Intent intent=getIntent();
        refenrencenumber=intent.getStringExtra("ReferenceId");
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            // redirect to setting to enable the gps option in settings
            OnGPS();
        } else {
            //get the current location of the user
            getCurrentLocation();
        }
        detailsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(refenrencenumber)){
                    DoctorDetailRetriverClass data=snapshot.child(refenrencenumber).getValue(DoctorDetailRetriverClass.class);
                    if (data != null){
                        views.doctorName.setText(data.getName());
                        views.doctorSpecialization.setText(data.getDoctorSpecialization());
                        latitude=data.getLatitude();
                        longitude=data.getLongitude();
                        experience=data.getExperience()+" of Experience";
                        views.doctorExperience.setText(experience);
                        profile= data.getProfileImage();
                        setDoctorImage(profile);
                        getAddressOfLocation(latitude,longitude);
                        destinationLocation=latitude+","+longitude;
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        views.navigateLocation.setOnClickListener(task->{
            if (sourceLocation != null && destinationLocation != null){
                String uri = "https://www.google.com/maps/dir/?api=1&origin=" + sourceLocation + "&destination=" + destinationLocation;
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
            if (sourceLocation ==null) {
                if (destinationLocation==null){
                    Toast.makeText(this, "source and destination is cant be idenfied", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(this, "turn on the location", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }



    private void setDoctorImage(String profile) {
        if (profile!=null&& !profile.isEmpty()){
            Picasso.get().load(profile).into(views.profileImage);

        }
    }

    private void getAddressOfLocation( String latitudeStringvalue, String longitudeStringvalue) {

        double latitudevalue=Double.parseDouble(latitudeStringvalue);
        double longitudevalue=Double.parseDouble(longitudeStringvalue);

        try {

            Geocoder geocoder=new Geocoder(this, Locale.getDefault());
            List<Address> addresses=geocoder.getFromLocation(latitudevalue,longitudevalue,1);
            if (addresses!=null&&addresses.size()>0){
                address=addresses.get(0).getAddressLine(0);
            }
            views.doctorAddress.setText(address);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
        }else {

            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                // Logic to handle location object
                                double latitudeDoublevalue=location.getLatitude();
                                double longitudeDoublevalue=location.getLongitude();

                                //convert the double value to string value to store it in firebase
                                userlatitude=String.valueOf(latitudeDoublevalue);
                                userlongitude=String.valueOf(longitudeDoublevalue);
                                sourceLocation=userlatitude+","+userlongitude;

                            }
                        }
                    });

        }
    }


    private void OnGPS() {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setMessage("Enable GPS location").setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog alertDialog=builder.create();
        alertDialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Unregister NetworkChangeReceiver
        unregisterReceiver(networkChangeReceiver);
    }



    private class NetworkChangeReceiver extends BroadcastReceiver {
        private boolean wasConnected = true;

        @Override
        public void onReceive(Context context, Intent intent) {
            boolean isConnected = isNetworkConnected(context);

            if (isConnected != wasConnected) {
                if (isConnected) {
                    CookieBar.build((Activity) context)
                            .setTitle("Network Status")
                            .setMessage("Back to online")
                            .show();
                } else {
                    CookieBar.build((Activity) context)
                            .setTitle("Network Status")
                            .setMessage("No internet Connection")
                            .show();
                }
                wasConnected = isConnected;
            }
        }
    }

    // Method to check network connectivity
    private boolean isNetworkConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnected();
        }
        return false;
    }

}