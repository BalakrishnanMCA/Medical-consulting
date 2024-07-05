package com.example.medicalcare.UserModule.UserFragments.UserProfileComponentClasses.HealthFilesPackage;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.medicalcare.DoctorModule.DoctorCardActivities.PriscriptonPackage.UploadPriscriptionData;
import com.example.medicalcare.R;
import com.example.medicalcare.StartActivity;
import com.example.medicalcare.databinding.ActivityHealthFilesBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.aviran.cookiebar2.CookieBar;

import java.util.ArrayList;

public class HealthFilesActivity extends AppCompatActivity {

    ActivityHealthFilesBinding views;
    HealthFilesAdapter filesAdapter;
    ArrayList<UploadPriscriptionData> priscriptionData;
    SharedPreferences preferences;
    String mobile;
    TextView noDataText;

    DatabaseReference healthFilesRef= FirebaseDatabase.getInstance().getReference("Priscription");

    NetworkChangeReceiver networkChangeReceiver;
    OnBackPressedDispatcher onBackPressedDispatcher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        views=ActivityHealthFilesBinding.inflate(getLayoutInflater());
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

        preferences =getSharedPreferences("UserProfile", MODE_PRIVATE);
        mobile=preferences.getString("UserMobile","");

        priscriptionData=new ArrayList<>();
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        filesAdapter=new HealthFilesAdapter(priscriptionData,this);
        views.healthFFilesRecycler.setLayoutManager(linearLayoutManager);
        views.healthFFilesRecycler.setAdapter(filesAdapter);

        views.backButton.setOnClickListener(task->{
            onBackPressedDispatcher.onBackPressed();
        });
        healthFilesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(mobile)){
                    for (DataSnapshot data : snapshot.child(mobile).getChildren()) {
                        UploadPriscriptionData filedata=data.getValue(UploadPriscriptionData.class);
                        priscriptionData.add(filedata);
                        filesAdapter.notifyDataSetChanged();
                    }
                }else{
                    views.healthFFilesRecycler.setVisibility(View.GONE);
                    views.noDataText.setVisibility(View.VISIBLE);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

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