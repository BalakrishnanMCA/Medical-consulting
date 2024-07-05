package com.example.medicalcare.UserModule.UserFragments.UserProfileComponentClasses.NearByClinicPackage;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.example.medicalcare.DoctorModule.DoctorFragments.DoctorProfileComponentClasses.ClinicLocationActivity;
import com.example.medicalcare.DoctorModule.DoctorFragments.DoctorProfileComponentClasses.UploadLocation;
import com.example.medicalcare.R;
import com.example.medicalcare.StartActivity;
import com.example.medicalcare.databinding.ActivityNearbyClinicBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.aviran.cookiebar2.CookieBar;

import java.util.ArrayList;
import java.util.Objects;

public class NearbyClinicActivity extends AppCompatActivity {

    LocationManager locationManager;
    Location currentLocation,shareLocation;
    FusedLocationProviderClient fusedLocationClient;
    ActivityNearbyClinicBinding views;
    ArrayList<UploadLocation> clinicLocationArrayList;
    NearByClinicsAdapter clinicsAdapter;
    UploadLocation uploadLocation;
    DatabaseReference locationRef= FirebaseDatabase.getInstance().getReference("ClinicLocation");
    DatabaseReference locationRefForDotorDetails= FirebaseDatabase.getInstance().getReference("DoctorDetails");

    NetworkChangeReceiver networkChangeReceiver;

    OnBackPressedDispatcher onBackPressedDispatcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        views=ActivityNearbyClinicBinding.inflate(getLayoutInflater());
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


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        clinicLocationArrayList=new ArrayList<>();
        uploadLocation=new UploadLocation();
        //now get the current location the user
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            // redirect to setting to enable the gps option in settings
            OnGPS();

        } else {
            //get the current location of the user
            getCurrentLocation();
        }

        views.backButton.setOnClickListener(task->{
            onBackPressedDispatcher.onBackPressed();
        });
        views.citydropdown.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                views.citydropdown.setError(null);
                String data = (String) adapterView.getItemAtPosition(i);
                views.towndropdown.setText("");
                setTownDropDownList(data);


            }
        });
        views.towndropdown.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                views.towndropdown.setError(null);
                clinicLocationArrayList.clear();

            }
        });
        views.showClinics.setOnClickListener(task ->{
            if (views.citydropdown.getText().toString().isEmpty() || views.towndropdown.getText().toString().isEmpty()) {
                if (views.citydropdown.getText().toString().isEmpty()) {
                    views.citydropdown.setError("select your city");
                }
                if (views.towndropdown.getText().toString().isEmpty()) {
                    views.towndropdown.setError("select your town");
                }
            }else {
                String city=views.citydropdown.getText().toString();
                String town=views.towndropdown.getText().toString();
                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    // redirect to setting to enable the gps option in settings
                    OnGPS();

                } else {
                    //get the current location of the user
                    getCurrentLocation();
                    loadClinicLocation(city,town);

                }


            }
        });
    }

    private void loadClinicLocation(String city,String town) {
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        clinicsAdapter=new NearByClinicsAdapter(clinicLocationArrayList,this,currentLocation);
        views.clinicLocationRecycler.setLayoutManager(linearLayoutManager);
        views.clinicLocationRecycler.setAdapter(clinicsAdapter);

        locationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                clinicLocationArrayList.clear();
                if (snapshot.hasChild(city)){
                    if (snapshot.child(city).hasChild(town)){
                        for (DataSnapshot data: snapshot.child(city).child(town).getChildren()) {
                            uploadLocation=data.getValue(UploadLocation.class);
                            clinicLocationArrayList.add(uploadLocation);
                            clinicsAdapter.notifyDataSetChanged();
                        }
                    }else{
                        Toast.makeText(NearbyClinicActivity.this, "No doctor has present in this town", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(NearbyClinicActivity.this, "No doctor has present in this city", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void setTownDropDownList(String data) {
        if (Objects.equals(data, "Ariyalur")){
            views.towndropdown.setSimpleItems(R.array.Ariyalur);

        }else if (Objects.equals(data, "Chengalpattu")){
            views.towndropdown.setSimpleItems(R.array.Chengalpattu);

        }else if (Objects.equals(data, "Chennai")){
            views.towndropdown.setSimpleItems(R.array.Chennai);

        }else if (Objects.equals(data, "Coimbatore")){
            views.towndropdown.setSimpleItems(R.array.Coimbatore);

        }else if (Objects.equals(data, "Cuddalore")){
            views.towndropdown.setSimpleItems(R.array.Cuddalore);

        }else if (Objects.equals(data, "Dharmapuri")){
            views.towndropdown.setSimpleItems(R.array.Dharmapuri);

        }else if (Objects.equals(data, "Dindigul")){
            views.towndropdown.setSimpleItems(R.array.Dindigul);

        }else if (Objects.equals(data, "Erode")){
            views.towndropdown.setSimpleItems(R.array.Erode);

        }else if (Objects.equals(data, "Kallakurichi")){
            views.towndropdown.setSimpleItems(R.array.Kallakurichi);

        }else if (Objects.equals(data, "Kancheepuram")){
            views.towndropdown.setSimpleItems(R.array.Kancheepuram);

        }else if (Objects.equals(data, "Karur")){
            views.towndropdown.setSimpleItems(R.array.Karur);

        }else if (Objects.equals(data, "Krishnagiri")){
            views.towndropdown.setSimpleItems(R.array.Krishnagiri);

        }else if (Objects.equals(data, "Madurai")){
            views.towndropdown.setSimpleItems(R.array.Madurai);

        }else if (Objects.equals(data, "Mayiladuthurai")){
            views.towndropdown.setSimpleItems(R.array.Mayiladuthurai);

        }else if (Objects.equals(data, "Nagapattinam")){
            views.towndropdown.setSimpleItems(R.array.Nagapattinam);

        }else if (Objects.equals(data, "Kanniyakumari")){
            views.towndropdown.setSimpleItems(R.array.Kanniyakumari);

        }else if (Objects.equals(data, "Namakkal")){
            views.towndropdown.setSimpleItems(R.array.Namakkal);

        }else if (Objects.equals(data, "Perambalur")){
            views.towndropdown.setSimpleItems(R.array.Perambalur);

        }else if (Objects.equals(data, "Pudukottai")){
            views.towndropdown.setSimpleItems(R.array.Pudukottai);

        }else if (Objects.equals(data, "Ramanathapuram")){
            views.towndropdown.setSimpleItems(R.array.Ramanathapuram);

        }else if (Objects.equals(data, "Ranipet")){
            views.towndropdown.setSimpleItems(R.array.Ranipet);

        }else if (Objects.equals(data, "Salem")){
            views.towndropdown.setSimpleItems(R.array.Salem);

        }else if (Objects.equals(data, "Sivagangai")){
            views.towndropdown.setSimpleItems(R.array.Sivagangai);

        }else if (Objects.equals(data, "Tenkasi")){
            views.towndropdown.setSimpleItems(R.array.Tenkasi);

        }else if (Objects.equals(data, "Thanjavur")){
            views.towndropdown.setSimpleItems(R.array.Thanjavur);

        }else if (Objects.equals(data, "Theni")){
            views.towndropdown.setSimpleItems(R.array.Theni);

        }else if (Objects.equals(data, "Thiruvallur")){
            views.towndropdown.setSimpleItems(R.array.Thiruvallur);

        }else if (Objects.equals(data, "Thiruvarur")){
            views.towndropdown.setSimpleItems(R.array.Thiruvarur);

        }else if (Objects.equals(data, "Thoothukudi")){
            views.towndropdown.setSimpleItems(R.array.Thoothukudi);

        }else if (Objects.equals(data, "Trichirappalli")){
            views.towndropdown.setSimpleItems(R.array.Trichirappalli);

        }else if (Objects.equals(data, "Thirunelveli")){
            views.towndropdown.setSimpleItems(R.array.Thirunelveli);

        }else if (Objects.equals(data, "Tirupathur")){
            views.towndropdown.setSimpleItems(R.array.Tirupathur);

        }else if (Objects.equals(data, "Tiruppur")){
            views.towndropdown.setSimpleItems(R.array.Tiruppur);

        }else if (Objects.equals(data, "Tiruvannamalai")){
            views.towndropdown.setSimpleItems(R.array.Tiruvannamalai);

        }else if (Objects.equals(data, "Nilgiris")){
            views.towndropdown.setSimpleItems(R.array.Nilgiris);

        }else if (Objects.equals(data, "Vellore")){
            views.towndropdown.setSimpleItems(R.array.Vellore);

        }else if (Objects.equals(data, "Viluppuram")){
            views.towndropdown.setSimpleItems(R.array.Viluppuram);

        }else if (Objects.equals(data, "Virudhunagar")){
            views.towndropdown.setSimpleItems(R.array.Virudhunagar);

        }
    }
    private Location getCurrentLocation() {

        if (ActivityCompat.checkSelfPermission(NearbyClinicActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(NearbyClinicActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
        }else {

            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                // Logic to handle location object
                                currentLocation =location;

                            }
                        }
                    });


        }
        return currentLocation;
    }
    private void OnGPS() {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setMessage("Enable GPS location").setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                dialogInterface.dismiss();
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
    protected void onResume() {
        super.onResume();
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            // redirect to setting to enable the gps option in settings
            OnGPS();

        } else {
            //get the current location of the user
            getCurrentLocation();
        }
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