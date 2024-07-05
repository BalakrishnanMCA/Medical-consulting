package com.example.medicalcare.UserModule.UserFragments;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.medicalcare.DoctorModule.DoctorFragments.MessageFragment;
import com.example.medicalcare.R;
import com.example.medicalcare.StartActivity;
import com.example.medicalcare.UserModule.UserFragments.FavoriteDoctorPackage.FavoriteFragment;
import com.example.medicalcare.UserModule.UserFragments.MyDoctorPackage.MyDoctorFragment;
import com.example.medicalcare.UserModule.UserFragments.messageFragmentPackage.UserMessageFragment;
import com.example.medicalcare.databinding.ActivityHomePageBinding;

import org.aviran.cookiebar2.CookieBar;

public class HomePage extends AppCompatActivity {


    ActivityHomePageBinding binding;

    boolean isHome=false,isMenuFav=false,isMenuProfile=false,isMyDoc=false,isMessagemod=false;

    NetworkChangeReceiver networkChangeReceiver;

    boolean backToOnline=false;
    OnBackPressedDispatcher onBackPressedDispatcher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityHomePageBinding.inflate(getLayoutInflater());
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
                    startActivity(new Intent(HomePage.this, StartActivity.class));
                    finish();
                }else{
                    replaceFragment(new HomeFragment());
                    binding.bottomNavigationView.getMenu().findItem(R.id.menuhome).setChecked(true);
                    isHome=true;
                }

            }
        });





        if (getIntent().getBooleanExtra("openMessageModule",false)){
            replaceFragment(new UserMessageFragment());
            isMessagemod=true;
            isMenuFav=isHome=isMenuProfile=isMyDoc=false;
            binding.bottomNavigationView.getMenu().findItem(R.id.messagemod).setChecked(true);

        }else if (getIntent().getBooleanExtra("openMyDoctor", false)) {
            replaceFragment(new MyDoctorFragment());
            binding.bottomNavigationView.getMenu().findItem(R.id.mydoc).setChecked(true);
            isMyDoc=true;
            isMenuFav=isMenuProfile=isHome=false;
        }else{
            binding.bottomNavigationView.setItemIconSize(46);
            replaceFragment(new HomeFragment());
            isHome = true;
        }

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {

            if (item.getItemId() == R.id.menuhome){
                replaceFragment(new HomeFragment());
                isHome=true;
                isMenuFav=isMenuProfile=isMyDoc=false;
                return true;
            }
            if (item.getItemId()==R.id.menufav){
                replaceFragment(new FavoriteFragment());
                isMenuFav=true;
                isHome=isMenuProfile=isMyDoc=false;
                return true;
            }
            if (item.getItemId() == R.id.messagemod){
                replaceFragment(new UserMessageFragment());
                isMessagemod=true;
                isMenuFav=isHome=isMenuProfile=isMyDoc=false;
                return true;
            }
            if (item.getItemId()==R.id.menuprofile){
                replaceFragment(new UserProfileFragment());
                isMenuProfile=true;
                isMenuFav=isHome=isMyDoc=false;
                return true;
            }
            if (item.getItemId()==R.id.mydoc){
                replaceFragment(new MyDoctorFragment());
                isMyDoc=true;
                isMenuFav=isMenuProfile=isHome=false;
                return true;
            }
            return false;
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