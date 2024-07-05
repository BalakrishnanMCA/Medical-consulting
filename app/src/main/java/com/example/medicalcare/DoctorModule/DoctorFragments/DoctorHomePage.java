package com.example.medicalcare.DoctorModule.DoctorFragments;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.medicalcare.R;
import com.example.medicalcare.StartActivity;
import com.example.medicalcare.UserModule.UserFragments.messageFragmentPackage.UserMessageFragment;
import com.example.medicalcare.databinding.ActivityDoctorHomePageBinding;
import com.google.android.material.navigation.NavigationBarView;

import org.aviran.cookiebar2.CookieBar;

public class DoctorHomePage extends AppCompatActivity {

    ActivityDoctorHomePageBinding binding;
    OnBackPressedDispatcher onBackPressedDispatcher;

    NetworkChangeReceiver networkChangeReceiver;

    boolean openmessagemodule;

    boolean isHome=false,isprofile=false,isMessagemod=false,isTodayAppointment=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityDoctorHomePageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        networkChangeReceiver = new NetworkChangeReceiver();
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);

        registerReceiver(networkChangeReceiver, intentFilter);

        onBackPressedDispatcher = this.getOnBackPressedDispatcher();

        onBackPressedDispatcher.addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Handle back button press
                if (isHome){
                    startActivity(new Intent(DoctorHomePage.this, StartActivity.class));
                    finish();
                }else{
                    replaceFragment(new DoctorHomeFragment());
                    binding.bottomNavigationView.getMenu().findItem(R.id.doctorhome).setChecked(true);
                    isHome=true;
                }

            }
        });
        if (getIntent().getBooleanExtra("openMessageModule",false)){
            replaceFragment(new MessageFragment());
            isMessagemod=true;
            isTodayAppointment=isHome=isprofile=false;
            binding.bottomNavigationView.getMenu().findItem(R.id.Docmessagemod).setChecked(true);

        }else {
            replaceFragment(new DoctorHomeFragment());
            isHome=true;
            isTodayAppointment=isMessagemod=isprofile=false;
        }

        binding.bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.doctorhome){
                    replaceFragment(new DoctorHomeFragment());
                    isHome=true;
                    isTodayAppointment=isMessagemod=isprofile=false;
                    return true;
                }
                if (item.getItemId() == R.id.Docmessagemod){
                    replaceFragment(new MessageFragment());
                    isMessagemod=true;
                    isTodayAppointment=isHome=isprofile=false;
                    return true;
                }
                if (item.getItemId()==R.id.doctortodayappointment){
                    replaceFragment(new TodayAppointmentFragment());
                    isTodayAppointment=true;
                    isprofile=isHome=isMessagemod=false;
                    return true;
                }
                if (item.getItemId()==R.id.doctorprofile){
                    replaceFragment(new DoctorProfileFragment());
                    isprofile=true;
                    isTodayAppointment=isHome=isMessagemod=false;

                    return true;
                }
                return false;
            }
        });
    }
    public void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager=getSupportFragmentManager();
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame,fragment);
        fragmentTransaction.commit();
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