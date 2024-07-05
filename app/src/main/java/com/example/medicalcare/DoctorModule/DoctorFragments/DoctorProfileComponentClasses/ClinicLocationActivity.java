package com.example.medicalcare.DoctorModule.DoctorFragments.DoctorProfileComponentClasses;

import static androidx.core.location.LocationManagerCompat.getCurrentLocation;

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
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.example.medicalcare.DoctorModule.DoctorFragments.DoctorProfileComponentClasses.SentPriscriptionPackage.SentPrescriptionActivity;
import com.example.medicalcare.R;
import com.example.medicalcare.databinding.ActivityClinicLocationBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.aviran.cookiebar2.CookieBar;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class ClinicLocationActivity extends AppCompatActivity {

    ActivityClinicLocationBinding views;
    LocationManager locationManager;
    FusedLocationProviderClient fusedLocationClient;

    String city, town,address,mobile,name,latitude,longitude;
    SharedPreferences preferences;
    boolean doctorDetailsLongitude=false,doctorDetailsLatitude=false;
    DatabaseReference locationRef= FirebaseDatabase.getInstance().getReference("ClinicLocation");
    DatabaseReference locationRefForDotorDetails= FirebaseDatabase.getInstance().getReference("DoctorDetails");

    OnBackPressedDispatcher onBackPressedDispatcher;

    NetworkChangeReceiver networkChangeReceiver;

    boolean backToOnline=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        views = ActivityClinicLocationBinding.inflate(getLayoutInflater());
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
        preferences=getSharedPreferences("DoctorProfile", MODE_PRIVATE);
        mobile=preferences.getString("DoctorMobile","");

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
            }
        });
        views.saveLocation.setOnClickListener(task -> {


                city = views.citydropdown.getText().toString();
                town = views.towndropdown.getText().toString();
                name=views.clinicName.getText().toString();

                if (latitude != null && longitude != null && !views.clinicName.getText().toString().isEmpty()){
                    upoadClinicLocationDetail(latitude,longitude,mobile,city,town,name,address);
                }else{
                    Toast.makeText(this, "enable the switch button to get the location ", Toast.LENGTH_SHORT).show();
                }

        });
        views.locationSwitchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (views.citydropdown.getText().toString().isEmpty() || views.towndropdown.getText().toString().isEmpty()) {
                    if (views.citydropdown.getText().toString().isEmpty()) {
                        views.citydropdown.setError("select your city");
                    }
                    if (views.towndropdown.getText().toString().isEmpty()) {
                        views.towndropdown.setError("select your town");
                    }
                    views.locationSwitchButton.setChecked(false);
                }else if (views.locationSwitchButton.isChecked()) {


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    }
                    if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        // redirect to setting to enable the gps option in settings
                        OnGPS();
                        views.locationSwitchButton.setChecked(false);
                    } else {
                        //get the current location of the user
                        getCurrentLocation();
                        views.clinicName.setVisibility(View.VISIBLE);
                    }
                }else if(!views.locationSwitchButton.isChecked()){
                    views.locationText.setText("check the button to add location");
                }
            }
        });
    }

    private void upoadClinicLocationDetail(String latitude, String longitude, String mobile, String city, String town,String name,String address) {
        UploadLocation uploadLocation=new UploadLocation(mobile,latitude,longitude,name,address);
        locationRefForDotorDetails.child(mobile).child("latitude").setValue(latitude).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                doctorDetailsLatitude=true;
            }
        });
        locationRefForDotorDetails.child(mobile).child("longitude").setValue(longitude).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                doctorDetailsLongitude=true;
            }
        });
        locationRef.child(city).child(town).child(mobile).setValue(uploadLocation).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (doctorDetailsLongitude && doctorDetailsLatitude){

                    views.citydropdown.setText("");
                    views.towndropdown.setText("");
                    views.locationSwitchButton.setChecked(false);
                    views.locationText.setText("");

                    Toast.makeText(ClinicLocationActivity.this, "Location updated successfully", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(ClinicLocationActivity.this, "Location updated faild", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(ClinicLocationActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ClinicLocationActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

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
                                 latitude=String.valueOf(latitudeDoublevalue);
                                 longitude=String.valueOf(longitudeDoublevalue);
                                getAddressOfLocation(ClinicLocationActivity.this,latitudeDoublevalue,longitudeDoublevalue);
                            }
                        }
                    });

        }
    }



    private void getAddressOfLocation(Context context, double latitudeDoublevalue, double longitudeDoublevalue) {
        try {

            Geocoder geocoder=new Geocoder(context, Locale.getDefault());
            List<Address> addresses=geocoder.getFromLocation(latitudeDoublevalue,longitudeDoublevalue,1);
            if (addresses!=null&&addresses.size()>0){
                address=addresses.get(0).getAddressLine(0);
            }
            views.locationText.setText(address);
            views.locationText.setSelected(true);
        }catch (Exception e){
            e.printStackTrace();
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
                views.locationSwitchButton.setChecked(false);
            }
        });
        AlertDialog alertDialog=builder.create();
        alertDialog.show();
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