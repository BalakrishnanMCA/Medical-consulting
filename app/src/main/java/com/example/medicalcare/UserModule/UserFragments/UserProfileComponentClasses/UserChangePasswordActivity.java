package com.example.medicalcare.UserModule.UserFragments.UserProfileComponentClasses;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Toast;

import com.example.medicalcare.DoctorModule.DoctorFragments.DoctorProfileComponentClasses.ChangePasswordActivity;
import com.example.medicalcare.R;
import com.example.medicalcare.StartActivity;
import com.example.medicalcare.databinding.ActivityUserChangePasswordBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.aviran.cookiebar2.CookieBar;

import java.util.Objects;

public class UserChangePasswordActivity extends AppCompatActivity {

    NetworkChangeReceiver networkChangeReceiver;
    OnBackPressedDispatcher onBackPressedDispatcher;
    ActivityUserChangePasswordBinding views;
    SharedPreferences preferences;
    DatabaseReference ref= FirebaseDatabase.getInstance().getReference("UserDetails");

    String mobile,retrivedPassword,storePassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        views=ActivityUserChangePasswordBinding.inflate(getLayoutInflater());
        setContentView(views.getRoot());
        preferences = getSharedPreferences("UserProfile", MODE_PRIVATE);
        mobile=preferences.getString("UserMobile","");
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
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(mobile)){
                    retrivedPassword=snapshot.child(mobile).child("userpassword").getValue(String.class);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        views.saveButton.setOnClickListener(task ->{
            if (views.currentPassword.getText().toString().isEmpty() && views.newPassword.getText().toString().isEmpty() && views.confirmPassword.getText().toString().isEmpty()){
                Toast.makeText(this, "Enter the details", Toast.LENGTH_SHORT).show();
            } else if (views.currentPassword.getText().toString().isEmpty() || views.newPassword.getText().toString().isEmpty() || views.confirmPassword.getText().toString().isEmpty()) {
                if (views.currentPassword.getText().toString().isEmpty()){
                    views.currentPassword.setError("Enter the current password");
                }
                if (views.newPassword.getText().toString().isEmpty()){
                    views.newPassword.setError("enter the new password");
                }
                if (views.confirmPassword.getText().toString().isEmpty()){
                    views.confirmPassword.setError("enter confirm password");
                }
            }else {
                if (!Objects.equals(retrivedPassword, views.currentPassword.getText().toString())){
                    views.currentPassword.setError("password not valid");
                }else {
                    if (views.newPassword.getText().toString().equals(views.confirmPassword.getText().toString())){
                        storePassword=views.newPassword.getText().toString();
                        ref.child(mobile).child("password").setValue(storePassword).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(UserChangePasswordActivity.this, "Password changed", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }else {
                        views.confirmPassword.setError("confirm password didn't match with new password");
                    }
                }
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